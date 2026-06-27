# Acceptance Criteria: OrderFlow AI Backend Full MVP

## 1. Planning And Scope

- `task-todo.md` describes the full backend MVP plan by phase.
- Plan explicitly uses `docs/db.dbml`, `docs/OrderFlow AI MVP.pdf`, and `docs/overview_v01 (1).md`.
- No business rule is invented outside those documents unless it is marked as an implementation assumption.
- Prompt optimization is not required for done; prompt structure must be maintainable and testable.

## 2. Database And Entity Coverage

- All MVP tables from `docs/db.dbml` have matching PostgreSQL schema.
- All MVP tables have matching JPA entities or documented read/write projections.
- Required enums from DBML exist as Java enums.
- `app_users` supports simple local login with `password_hash`.
- JSONB fields persist and load valid structured JSON.
- Migrations can build a fresh local database from zero.
- Seed data includes organization, users, customers, projects, warehouse, SKUs, aliases, prices, inventory, credit profiles, and MAS agent records.

## 3. MAS Agent Table Coverage

- Existing AI core tables are reused as MAS registry tables.
- Seeded MAS records exist for:
  - `ORDERFLOW_ORCHESTRATOR`
  - `ORDER_EXTRACTION_AGENT`
  - `SKU_MATCHING_AGENT`
  - `RULE_CHECK_AGENT`
  - `REVIEW_WORKBENCH_AGENT`
  - `DOCUMENT_AGENT`
- OpenAI-capable agent/brain records point to an OpenAI provider/model/env.
- Deterministic rule/review/document nodes are represented in the registry but do not rely on LLM decisions.
- Agent outputs/errors are saved to `agent_responses` or equivalent processing/audit records.

## 4. Local Runtime

- Backend starts with the local profile and PostgreSQL.
- Redis, Kafka, S3, Chroma, MCP, Bitbucket, and dbdiagram are not required for MVP local startup.
- No API key or secret is committed.
- Required local environment variables are documented:
  - `SPRING_PROFILES_ACTIVE`
  - `POSTGRES_URL`
  - `POSTGRES_USERNAME`
  - `POSTGRES_PASSWORD`
  - `APP_JWT_SECRET`
  - `OPENAI_API_KEY`
  - `ORDERFLOW_OPENAI_MODEL`
- Logs do not print raw OpenAI API keys, bearer tokens, passwords, or sensitive request bodies.

## 5. Auth

- `POST /api/auth/login` authenticates an active `app_users` record with email/password.
- Login returns a bearer token and current user payload.
- `GET /api/auth/me` returns current user, organization, and role.
- Protected `/api/**` endpoints reject missing or invalid tokens.
- MVP roles support at least `SALE_ADMIN`, `MANAGER`, and `SYSTEM`.

## 6. Master Data APIs

- The following APIs work and filter by current organization:
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

## 7. OpenAI Extraction

- `POST /api/ai/extract-order` calls OpenAI when `OPENAI_API_KEY` is configured.
- OpenAI output is validated against backend DTO/schema before persistence.
- Invalid or incomplete AI output does not create an approved or silently matched line.
- Raw extraction result is stored in `raw_order_texts.extraction_result`.
- Processing events record extraction start/completion/failure.
- Normal automated tests can run without a real OpenAI key by using a mock gateway.
- A separate local smoke test can call OpenAI for real when the key exists.

## 8. Draft Order Flow

- `POST /api/draft-orders/from-text` creates:
  - `raw_order_texts`
  - `draft_orders`
  - `draft_order_lines`
  - `processing_events`
  - `audit_events`
- The create flow runs extraction, matching, and checks unless a controlled failure occurs.
- `GET /api/draft-orders` lists orders with filters for status/customer/date where implemented.
- `GET /api/draft-orders/{id}` returns header, raw text, lines, candidates, checks, holds, documents, and events needed by Review Workbench.
- `POST /api/draft-orders/{id}/rerun-extraction` refreshes extraction and logs audit.

## 9. SKU Matching

- `POST /api/draft-order-lines/{lineId}/match-skus` creates ranked candidates.
- `GET /api/draft-order-lines/{lineId}/sku-candidates` returns candidate list with confidence and reason.
- `POST /api/draft-order-lines/{lineId}/select-sku` sets final SKU, updates line status, and logs review/audit.
- Clear top match can be marked `MATCHED`.
- Ambiguous match must remain `PENDING_MATCH` or `NEEDS_CLARIFICATION` and create/open an appropriate hold.

## 10. Rule Checks And Holds

- `POST /api/draft-orders/{id}/run-checks` runs price, inventory, and credit checks.
- `GET /api/draft-orders/{id}/checks` returns price, inventory, and credit snapshots.
- `GET /api/draft-orders/{id}/holds` returns open and historical holds.
- Missing price or below-floor price creates `PRICE_HOLD`.
- Insufficient stock creates `STOCK_HOLD`.
- Over credit limit or overdue debt creates `CREDIT_HOLD`.
- Blocking open holds prevent approval.
- Releasing/rejecting a hold requires an authenticated user and writes audit/review records.

## 11. Review And Approval

- `PATCH /api/draft-order-lines/{lineId}` updates editable line fields and logs before/after.
- `PATCH /api/draft-orders/{id}` updates editable header fields and logs before/after.
- `POST /api/draft-orders/{id}/approve` rejects approval when blocking holds or unresolved lines remain.
- Successful approval updates status, approver, approved time, and creates inventory reservations.
- `POST /api/draft-orders/{id}/reject` rejects an order and writes audit.
- Inventory reservation updates reserved quantity without subtracting on-hand quantity.

## 12. Document Output

- `POST /api/draft-orders/{id}/documents/quote` generates quote HTML snapshot.
- `POST /api/draft-orders/{id}/documents/pick-list` generates pick-list HTML snapshot.
- `GET /api/draft-orders/{id}/documents` lists generated documents.
- `GET /api/draft-order-documents/{id}` returns stored document detail.
- HTML includes enough data for demo: customer, order, SKU, product, quantity, price, total, warehouse/delivery notes where applicable.

## 13. Audit And Processing Events

- Processing events exist for each major stage.
- Audit events exist for AI extraction, SKU selection, line/header edit, hold release/reject, approval/rejection, reservation, and document generation.
- Review actions exist for user-facing review operations.
- APIs expose processing events, audit events, and review actions for a draft order.

## 14. Automated Test Acceptance

- `.\mvnw.cmd test` runs real unit/controller tests, not an empty suite.
- Unit tests cover:
  - auth/JWT
  - OpenAI output validation
  - normalization
  - SKU matching
  - price check
  - inventory check
  - credit check
  - hold lifecycle
  - order status transitions
  - document HTML generation
- Controller tests cover key `/api/**` endpoints with mock AI.
- Integration tests can run with Testcontainers PostgreSQL and validate migrations plus seed data.
- Real OpenAI tests are opt-in and skipped when `OPENAI_API_KEY` is missing.

## 15. Real API Smoke Acceptance

With PostgreSQL running and `OPENAI_API_KEY` configured:

- Login succeeds with seeded Sale Admin.
- Creating a draft order from Vietnamese raw text calls OpenAI successfully.
- Extraction result is persisted.
- Draft lines are persisted.
- SKU candidates are created.
- Rule checks create pass/warn/fail snapshots.
- Holds are visible when expected.
- Sale Admin can select SKU and release allowed holds.
- Approval succeeds only after blocking issues are resolved.
- Quote or pick-list document is generated.
- Audit and processing events show the full flow.

## 16. Build Done

- Backend compiles cleanly.
- Backend starts locally.
- No committed secret is introduced.
- MVP backend can support the frontend screens described in the PDF:
  - Login
  - Orders list
  - Create draft order
  - Review Workbench
  - Document preview
  - Product/customer lookup
  - Processing/audit events
