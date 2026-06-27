# Task Todo: OrderFlow AI Backend Full MVP

## Current Implementation Snapshot

- DONE: DBML-backed Flyway migrations are added under `src/main/resources/db/migration`.
- DONE: 24/24 DBML tables have OrderFlow JPA entities and repositories under `modules/orderflow/common`.
- DONE: Local simple login is implemented with `app_users.password_hash` and JWT bearer tokens.
- DONE: OrderFlow MVP APIs are scaffolded for auth, master data, AI extraction, draft orders, line review, holds, documents, processing events, audit events, and review actions.
- DONE: OpenAI extraction gateway is wired through `OPENAI_API_KEY`, `ORDERFLOW_OPENAI_MODEL`, and `OPENAI_BASE_URL`.
- DONE: MAS seed records are represented in existing AI core tables for orchestrator, extraction, matching, rule check, review workbench, and document agents.
- DONE: Automated tests pass with `.\mvnw.cmd test`, including a DBML entity/repository coverage test.
- BLOCKED FOR REAL HTTP SMOKE: local Postgres is reachable on `localhost:5432`, but the default `postgres/postgres` credential is rejected on this machine.
- READY WITH SECRET HANDLING: the OpenAI key was provided in chat, but it must be injected through the process environment or updated into local `ai_envs.api_key`; it is not committed to repo files or printed in logs.
- READY: `scripts/orderflow-smoke.ps1` can run login/master-data smoke and optional OpenAI/full draft-order smoke once backend is running with valid DB credentials and OpenAI key.

## 0. Locked Understanding

- Product: OrderFlow AI is an AI Safe Order-Entry Workbench for Sale Admin in plumbing/building-material distribution.
- Main flow: raw order text -> OpenAI structured extraction -> SKU candidate matching -> deterministic price/inventory/credit checks -> human review -> approve -> quote or pick-list HTML preview.
- AI is not allowed to approve orders, override price, override stock, override credit, or silently choose SKU when the line is ambiguous.
- Full backend MVP must be implemented from `docs/db.dbml`, `docs/OrderFlow AI MVP.pdf`, and `docs/overview_v01 (1).md`.
- Current backend base is reusable, but it is still a skeleton/gateway-style app. It does not yet contain OrderFlow domain entities, APIs, OpenAI order extraction, rule checks, holds, review workflow, or document output.
- Prompt quality can be improved later. First goal is a backend that runs locally, calls OpenAI with a real API key, persists all MVP data, and passes backend tests.

## 1. What We Can Reuse From The Current Base

- Keep:
  - Maven wrapper, Spring Boot application entrypoint, Java 23 setup.
  - PostgreSQL datasource setup.
  - JPA auditing and repository scanning.
  - Jackson/ObjectMapper configuration.
  - `BaseResponse`, existing exception handling style, validation handling.
  - CORS/security skeleton, but add `/api/**` MVP security separately.
  - Existing AI core tables/classes as a MAS registry:
    - `ai_models`
    - `ai_envs`
    - `assistant_agents`
    - `agent_brains`
    - `agent_responses`
  - `JsonConverters` for map/list JSON conversion.
- Refactor or bypass:
  - Existing `/app/auth/exchange` ID-system flow is not the MVP login.
  - Existing Redis token auth should not be required for local MVP.
  - Existing `AgentService` is hard-coded to Gemini and cannot be used as-is for OpenAI.
  - Existing `users/roles` tables do not match `app_users/organizations` in DBML.
  - Existing Chroma/Gemini/Google properties are not the MVP AI path.
- Disable or leave unused for MVP:
  - Kafka, Redis, S3, Chroma, MCP, Bitbucket, dbdiagram.
  - They should not be required for local startup or API tests.

## 2. Target Folder Structure

Create new MVP code under the current package:

```text
src/main/java/vn/ezisolutions/cloud/hackathon/modules/orderflow/
  identity/
    api/
    application/
    domain/
    infrastructure/
  masterdata/
    api/
    application/
    domain/
    infrastructure/
  order/
    api/
    application/
    domain/
    infrastructure/
  ai/
    application/
    domain/
    infrastructure/
    prompt/
  matching/
    application/
    domain/
  rules/
    application/
    domain/
  review/
    api/
    application/
  document/
    api/
    application/
  audit/
    api/
    application/
    domain/
  common/
    api/
    security/
    time/
```

Create tests under:

```text
src/test/java/vn/ezisolutions/cloud/hackathon/modules/orderflow/
```

Create test fixtures under:

```text
src/test/resources/orderflow/
  openai-fixtures/
  sql/
  http/
```

## 3. Phase 0 - Backend Boot Safety

- Add missing test dependencies:
  - `spring-boot-starter-test`
  - `spring-security-test`
  - Testcontainers PostgreSQL
  - WireMock or MockWebServer for OpenAI gateway tests
- Add Flyway or a clear migration runner. Prefer Flyway for a reliable fresh database.
- Ensure local app can start without Redis/Kafka/S3/Chroma/MCP.
- Add local properties:
  - `OPENAI_API_KEY`
  - `ORDERFLOW_OPENAI_MODEL`
  - `APP_JWT_SECRET`
  - PostgreSQL connection variables
- Do not commit any API key.
- Add masked HTTP logging for Authorization/API key/body fields before real OpenAI calls.

## 4. Phase 1 - Generate Entities From DBML

Create JPA entities matching `docs/db.dbml` exactly, with only one MVP extension:

- Add `password_hash` to `app_users` for local user/password login.

Entity groups:

- Organization and user:
  - `OrganizationEntity`
  - `AppUserEntity`
- Customer:
  - `CustomerEntity`
  - `CustomerProjectEntity`
  - `CustomerCreditProfileEntity`
- Catalog and stock:
  - `WarehouseEntity`
  - `ProductSkuEntity`
  - `ProductAliasEntity`
  - `PriceListEntity`
  - `SkuPriceEntity`
  - `InventoryBalanceEntity`
- Intake/order:
  - `RawOrderTextEntity`
  - `DraftOrderEntity`
  - `DraftOrderLineEntity`
  - `SkuCandidateEntity`
- Rule snapshots and holds:
  - `PriceCheckEntity`
  - `InventoryCheckEntity`
  - `CreditCheckEntity`
  - `OrderHoldEntity`
- Review/output/audit:
  - `ReviewActionEntity`
  - `InventoryReservationEntity`
  - `ProcessingEventEntity`
  - `AuditEventEntity`
  - `DraftOrderDocumentEntity`

Create enum classes from DBML:

- `UserRole`
- `CustomerType`
- `DraftOrderStatus`
- `DraftOrderLineStatus`
- `HoldType`
- `HoldStatus`
- `RuleCheckStatus`
- `ActorType`
- `DocumentType`
- `DocumentStatus`
- `ReservationStatus`

Entity rules:

- Use UUID primary keys.
- Use `BigDecimal` for decimal columns.
- Use `OffsetDateTime` or `LocalDateTime` consistently for `timestamptz`; prefer `OffsetDateTime` if schema uses timezone semantics.
- Use `LocalDate` for date-only fields.
- Use PostgreSQL `jsonb` for:
  - `raw_order_texts.extraction_result`
  - `draft_order_lines.extracted_attributes`
  - `sku_candidates.matched_attributes`
  - `sku_candidates.missing_attributes`
  - `order_holds.payload`
  - audit/review before/after metadata
- Keep relationships simple. Use IDs for write paths when object graphs would make service logic fragile.

## 5. Phase 2 - Database Migration And Seed Data

- Create Flyway migration from DBML:
  - `V1__orderflow_schema.sql`
  - include indexes for common filters: organization, draft order status, customer, line order, open holds.
- Create seed migration:
  - `V2__orderflow_seed_demo.sql`
- Seed data must support real demo/test:
  - 1 organization.
  - 3 users:
    - Sale Admin
    - Manager
    - System user
  - 3 to 5 customers.
  - Customer projects and delivery addresses.
  - 1 active warehouse.
  - About 50 product SKUs:
    - PVC pipes
    - PP-R hot/cold pipe variants
    - HDPE variants
    - co 90/co 45
    - co ren trong/ren ngoài
    - tê đều/tê giảm
    - nối, lơi, phụ kiện common cases
  - Product aliases for slang:
    - `ong nong`, `ống nóng`
    - `co`, `cút`
    - `phi 25`, `phi 27`, `D25`
    - `ren trong`, `ren ngoài`
    - `te giam`, `tê giảm`
  - Price lists and SKU prices with floor price cases.
  - Inventory balances with enough stock and low-stock cases.
  - Credit profiles with pass and fail cases.
- Seed MAS records in AI core tables:
  - `ai_models`: OpenAI model row.
  - `ai_envs`: OpenAI env row using environment placeholder, not literal key.
  - `assistant_agents`: OrderFlow MAS agent rows.
  - `agent_brains`: prompt/template rows per agent skill.

## 6. Phase 3 - MAS Agent Registry

Represent each MVP node as a row-backed agent/brain so the system matches the MAS table design.

Agents to seed:

- `ORDERFLOW_ORCHESTRATOR`
  - Type: workflow coordinator.
  - Responsibility: logs stage order and calls internal services.
  - Runtime: Java service, not direct LLM decision maker.
- `ORDER_EXTRACTION_AGENT`
  - Type: OpenAI agent.
  - Responsibility: extract raw text into structured order JSON.
  - Stores response in `raw_order_texts.extraction_result` and `agent_responses`.
- `SKU_MATCHING_AGENT`
  - Type: hybrid AI/rule node.
  - Responsibility: use extracted attributes, aliases, and SKU catalogue to produce candidates and reasons.
  - MVP runtime: deterministic matcher first, optional OpenAI explanation second.
- `RULE_CHECK_AGENT`
  - Type: deterministic rule node.
  - Responsibility: run price, inventory, and credit checks.
  - Important: this is not LLM authority.
- `REVIEW_WORKBENCH_AGENT`
  - Type: workflow/action logger.
  - Responsibility: record user edits, SKU selection, hold release, approve/reject.
- `DOCUMENT_AGENT`
  - Type: deterministic document generator.
  - Responsibility: generate quote and pick-list HTML snapshots.

The code should load agent/brain config from AI core tables, but business services must still own the final rule decisions.

## 7. Phase 4 - OpenAI Real Integration

- Add an `OpenAiOrderExtractionGateway` behind an interface:
  - `OrderExtractionGateway`
  - `extract(OrderExtractionCommand command): OrderExtractionResult`
- Use OpenAI API key from environment:
  - `OPENAI_API_KEY`
- Use model from environment:
  - `ORDERFLOW_OPENAI_MODEL`
- Add a local debug endpoint:
  - `POST /api/ai/extract-order`
- Use structured JSON output:
  - header fields: customer/project hint, delivery date, delivery note, missing information.
  - line fields: raw line, description, quantity, unit, attributes, confidence, clarification question.
- Validate OpenAI output before saving:
  - quantity must exist and be positive.
  - each line must have raw text.
  - confidence must be within 0 to 1.
  - missing critical attributes create clarification hold instead of auto approval.
- Save:
  - raw OpenAI structured result in `raw_order_texts.extraction_result`.
  - agent response/audit in `agent_responses` and `processing_events`.
- Add retry with short timeout, but avoid infinite retry.
- If OpenAI fails in local testing, API should return a clear business error. Test fixtures can still run without the key.

## 8. Phase 5 - Auth/User Password Login

Implement simple MVP auth:

- `POST /api/auth/login`
  - input: email, password.
  - checks `app_users.active = true`.
  - verifies BCrypt `password_hash`.
  - returns bearer token and current user payload.
- `GET /api/auth/me`
  - returns current user, organization, role.
- `POST /api/auth/logout`
  - for JWT MVP: return success; no server state required.

Security:

- Protect `/api/**` except `/api/auth/login`.
- Use stateless bearer JWT.
- Claims:
  - user id
  - organization id
  - role
  - display name
- Keep `/app/**` legacy security untouched unless it blocks startup.

## 9. Phase 6 - Master Data APIs

Implement read-first APIs:

- `GET /api/customers`
- `GET /api/customers/{id}`
- `GET /api/customers/{id}/projects`
- `GET /api/products/skus`
- `GET /api/products/skus/{id}`
- `GET /api/products/aliases`
- `GET /api/warehouses`
- `GET /api/inventory/balances`
- `GET /api/price-lists`
- `GET /api/credit-profiles/{customerId}`

MVP constraints:

- Filter by current user's organization.
- Keep CRUD write APIs out unless needed for demo.
- Return compact DTOs for frontend, not entity graphs.

## 10. Phase 7 - Draft Order Intake

Implement:

- `POST /api/draft-orders/from-text`
- `GET /api/draft-orders`
- `GET /api/draft-orders/{id}`
- `PATCH /api/draft-orders/{id}`
- `POST /api/draft-orders/{id}/rerun-extraction`

Create-from-text flow:

1. Validate customer, project, warehouse belong to current organization.
2. Insert `raw_order_texts`.
3. Insert `draft_orders` with status `EXTRACTING`.
4. Log `RAW_TEXT_RECEIVED`.
5. Call `ORDER_EXTRACTION_AGENT`.
6. Persist extracted lines.
7. Create clarification holds if required.
8. Call SKU matching.
9. Call rule checks for matched lines.
10. Set final status:
    - `NEEDS_CLARIFICATION` if missing info.
    - `ON_HOLD` if blocking holds exist.
    - `READY_FOR_REVIEW` if no blocking hold.
11. Return a full draft order detail DTO.

## 11. Phase 8 - SKU Matching

Implement:

- `POST /api/draft-order-lines/{lineId}/match-skus`
- `GET /api/draft-order-lines/{lineId}/sku-candidates`
- `POST /api/draft-order-lines/{lineId}/select-sku`

Matching logic:

- Normalize Vietnamese text:
  - lowercase.
  - remove duplicate spaces.
  - normalize `phi`, `d`, `ø`.
  - keep both accent and accentless matching paths.
- Score sources:
  - alias exact/contains match.
  - material match.
  - brand match.
  - diameter match.
  - fitting type match.
  - pressure class/PN match.
  - sell unit compatibility.
- Persist top candidates.
- Auto mark `MATCHED` only if one candidate is clearly above threshold.
- Otherwise mark `PENDING_MATCH` and create `CLARIFICATION_HOLD`.
- `select-sku` logs `review_actions` and `audit_events`.

## 12. Phase 9 - Rule Check And Holds

Implement:

- `POST /api/draft-orders/{id}/run-checks`
- `GET /api/draft-orders/{id}/checks`
- `GET /api/draft-orders/{id}/holds`
- `POST /api/order-holds/{holdId}/release`
- `POST /api/order-holds/{holdId}/reject`

Rule services:

- `PriceCheckService`
  - choose best active price list by customer-specific first, then price tier, priority, valid date.
  - set line unit price and line amount.
  - create `PRICE_HOLD` if price is below approval floor or missing.
- `InventoryCheckService`
  - compare requested quantity with available quantity.
  - create `STOCK_HOLD` when unavailable.
- `CreditCheckService`
  - projected debt = current debt + overdue debt + pending approved amount + draft total.
  - create `CREDIT_HOLD` if over limit or overdue debt exists.
- `HoldService`
  - one open hold per rule/line when possible.
  - release/reject hold with audit.

Order status rules:

- Any blocking open hold -> `ON_HOLD`.
- Any clarification hold -> `NEEDS_CLARIFICATION`.
- No blocking holds after checks -> `READY_FOR_REVIEW`.

## 13. Phase 10 - Review, Approve, Reservation

Implement:

- `PATCH /api/draft-order-lines/{lineId}`
- `POST /api/draft-order-lines/{lineId}/reject`
- `POST /api/draft-orders/{id}/approve`
- `POST /api/draft-orders/{id}/reject`
- `POST /api/draft-orders/{id}/reserve-inventory`
- `POST /api/inventory-reservations/{id}/release`

Approve rules:

- Cannot approve rejected order.
- Cannot approve if required lines are unresolved.
- Cannot approve if blocking holds remain open.
- On approve:
  - set order `APPROVED`.
  - set approved user/time.
  - create active reservations for matched approved lines.
  - increase reserved quantity.
  - log review and audit.

## 14. Phase 11 - Document Output

Implement:

- `POST /api/draft-orders/{id}/documents/quote`
- `POST /api/draft-orders/{id}/documents/pick-list`
- `GET /api/draft-orders/{id}/documents`
- `GET /api/draft-order-documents/{id}`

MVP output:

- HTML snapshot only.
- Quote includes customer, project, delivery note, SKU, product name, quantity, unit price, line amount, total.
- Pick list includes warehouse, SKU, product name, quantity, unit, location/notes if available.
- Store HTML in `draft_order_documents.html_snapshot`.
- Set status `GENERATED`.
- Optionally set order `EXPORTED` after document generation if this matches frontend demo flow.

## 15. Phase 12 - Audit And Processing Events

Implement:

- `GET /api/draft-orders/{id}/processing-events`
- `GET /api/draft-orders/{id}/audit-events`
- `GET /api/draft-orders/{id}/review-actions`

Log stages:

- `RAW_TEXT_RECEIVED`
- `AI_EXTRACTION_STARTED`
- `AI_EXTRACTION_COMPLETED`
- `SKU_MATCHING_STARTED`
- `SKU_MATCHING_COMPLETED`
- `RULE_CHECK_STARTED`
- `RULE_CHECK_COMPLETED`
- `HOLD_CREATED`
- `USER_SELECTED_SKU`
- `USER_EDITED_LINE`
- `HOLD_RELEASED`
- `ORDER_APPROVED`
- `DOCUMENT_GENERATED`

## 16. Phase 13 - Real Local Run

Local prerequisites:

- PostgreSQL running.
- Java 23 available.
- Environment variables:
  - `SPRING_PROFILES_ACTIVE=local`
  - `POSTGRES_URL=jdbc:postgresql://localhost:5432/hackathon`
  - `POSTGRES_USERNAME=postgres`
  - `POSTGRES_PASSWORD=postgres`
  - `APP_JWT_SECRET=<local-secret>`
  - `OPENAI_API_KEY=<provided-by-user>`
  - `ORDERFLOW_OPENAI_MODEL=<model>`

Run:

```powershell
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local
```

Real API smoke flow:

1. Login as Sale Admin.
2. Call master data list APIs.
3. Create draft order from Vietnamese raw text.
4. Verify OpenAI extraction result persisted.
5. Verify SKU candidates created.
6. Run checks.
7. Release or resolve holds if needed.
8. Approve order.
9. Generate quote.
10. Read audit and processing events.

## 17. Backend Test Plan

Unit tests:

- Password hashing and JWT generation/validation.
- OpenAI response validator with fixtures.
- Vietnamese text normalization.
- SKU matching score and top-3 behavior.
- Price list selection.
- Inventory availability check.
- Credit projected debt check.
- Hold creation/release.
- Draft order status transitions.
- HTML document generation.

Controller tests:

- `/api/auth/login` success/failure.
- `/api/auth/me` authorized/unauthorized.
- Create draft from text with mock extraction gateway.
- Get draft order detail.
- Select SKU.
- Run checks.
- Approve order.
- Generate quote.

Integration tests:

- Testcontainers PostgreSQL.
- Flyway migration from empty DB.
- Seed data exists and is queryable.
- End-to-end happy path with mock OpenAI.
- Hold path:
  - ambiguous SKU.
  - insufficient stock.
  - over credit.
  - price hold.

Real OpenAI smoke test:

- Kept separate from normal CI.
- Enabled only when `OPENAI_API_KEY` is present.
- Calls `POST /api/ai/extract-order` and `POST /api/draft-orders/from-text`.
- Verifies schema validity, not exact wording.

## 18. Demo Scenarios To Prove MVP

Scenario 1 - clear order:

- Text: customer asks for PVC/PP-R pipe and fittings with enough stock.
- Expected: extraction succeeds, SKU candidates match, checks pass, order ready for review, approve, quote generated.

Scenario 2 - ambiguous SKU:

- Text includes `co 27` or `ống 25 nóng` without enough attributes.
- Expected: candidate list exists, line pending match or needs clarification, clarification hold created.

Scenario 3 - stock hold:

- Text requests quantity higher than available.
- Expected: inventory check fail, stock hold created, order on hold.

Scenario 4 - credit hold:

- Customer already near or over credit limit.
- Expected: credit hold created, approve blocked until release/reject.

Scenario 5 - price hold:

- Price below floor or missing price.
- Expected: price hold created and visible in checks/holds.

## 19. Files To Update First

- `pom.xml`
  - test dependencies.
  - Flyway.
  - JWT dependency if needed.
- `src/main/resources/application.properties`
- `src/main/resources/application-local.properties`
- `src/main/resources/db/migration/V1__orderflow_schema.sql`
- `src/main/resources/db/migration/V2__orderflow_seed_demo.sql`
- new `modules/orderflow/**` packages.
- `task-todo.md`
- `acceptance_criteria.md`

## 20. Information Needed From User For Real Testing

- OpenAI API key supplied as environment variable, not pasted into committed files.
- Preferred OpenAI model if Bob has already selected one; otherwise use configurable default.
- Local PostgreSQL credential if different from `postgres/postgres`.
- If the OpenAI key must be stored in the local MAS table, run once with `ORDERFLOW_OPENAI_PERSIST_API_KEY=true`; the app will copy `OPENAI_API_KEY` into the enabled `ai_envs` row after Flyway seed.
- Whether frontend expects `/api/**` exactly or wants `/app/**` compatibility aliases.

## 21. Definition Of Done

- Backend starts locally with PostgreSQL and OpenAI key.
- Fresh database can be created from migrations and seed data.
- Entity model covers DBML tables.
- Login user/password works.
- Real OpenAI extraction endpoint works with Vietnamese raw order text.
- Create draft order flow persists raw text, extraction, lines, candidates, checks, holds, events, and audit.
- Review actions work.
- Approve creates inventory reservations.
- Quote/pick-list HTML is generated.
- Unit/controller/integration tests pass.
- Real API smoke test can be run locally with the user's OpenAI key.
