# MVP User Journey & Agent Test Script - OrderFlow AI MVP

Nguồn: `specs/PROJECT/mvp_user_journey_and_agent_test_plan.md`

Mục tiêu: test theo vai trò người dùng và màn hình MVP, liên kết với bộ agent/customer scenarios.

## 1. Roles

| Role | User seed | Journey focus |
|---|---|---|
| `SALE_ADMIN` | `sale_admin_01` | Create draft, review, select SKU, fix holds, approve/export |
| `MANAGER` | `manager_01` | Release price/credit hold |
| `WAREHOUSE` | `warehouse_01` | View pick list after approved |
| `SYSTEM` | system actor | Processing/audit events |

## 2. User Journey Scripts

| Script ID | Source journey | UI/API flow | Must assert |
|---|---|---|---|
| UJ-SCRIPT-001 | UJ-001 | Create clear order and approve | Status flow, lines, candidates, checks, quote |
| UJ-SCRIPT-002 | UJ-002 | Clarification handling | Holds/questions, edit line, rerun |
| UJ-SCRIPT-003 | UJ-003 | Ambiguous SKU selection | Multiple candidates, human select, review action |
| UJ-SCRIPT-004 | UJ-004 | Stock hold | Stock fail, edit/reject line, rerun |
| UJ-SCRIPT-005 | UJ-005 | Credit hold | Sale Admin blocked, Manager release |
| UJ-SCRIPT-006 | UJ-006 | Price hold | Below-floor price, manager release/fix price |
| UJ-SCRIPT-007 | UJ-007 | Repeat order | History lookup, override, review |
| UJ-SCRIPT-008 | UJ-008 | Audit debug | Processing events, review actions, audit events visible |

## 3. Script UJ-SCRIPT-001 - Sale Admin Happy Path

**UI steps**

1. Login as `sale_admin_01`.
2. Open `/orders/new`.
3. Select `MINH_ANH`, project `Quận 7`, warehouse `KHO_CHINH`.
4. Paste E2E-01 raw text.
5. Submit.
6. Open review screen.
7. Confirm SKU candidates.
8. Run checks.
9. Approve.
10. Generate quote.

**DB assertions**

```sql
select status, approved_by_user_id, approved_at
from draft_orders
where id = :draft_order_id;

select count(*) as line_count
from draft_order_lines
where draft_order_id = :draft_order_id;

select count(*) as reservation_count
from inventory_reservations
where draft_order_line_id in (
  select id from draft_order_lines where draft_order_id = :draft_order_id
);

select document_type, status
from draft_order_documents
where draft_order_id = :draft_order_id;
```

## 4. Script UJ-SCRIPT-003 - Human Selects Ambiguous SKU

**Precondition**

Draft from E2E-03 or AGQ duplicate alias scenario.

**UI/API steps**

1. Open review workbench.
2. Select one candidate for `ống cuộn đen` after clarification.
3. Select one candidate for `nối thăm`.
4. Select one candidate for `kẹp đỡ ống`.
5. Run checks.

**DB assertions**

```sql
select raw_line_text, selected_sku_id, selected_by_user_id, selected_at, status
from draft_order_lines
where draft_order_id = :draft_order_id
order by line_no;

select action_type, draft_order_line_id, actor_user_id, before_data, after_data
from review_actions
where draft_order_id = :draft_order_id
  and action_type = 'SELECT_SKU'
order by created_at;
```

Pass:

- Selection is by `SALE_ADMIN`, not AI auto-select.
- `review_actions.SELECT_SKU` exists for each manually selected line.

## 5. Script UJ-SCRIPT-005 - Manager Releases Credit Hold

**Steps**

1. Sale Admin creates order for `AN_PHAT`.
2. Run checks -> `CREDIT_HOLD`.
3. Sale Admin attempts approve -> rejected.
4. Manager opens hold detail.
5. Manager releases hold with note.
6. Sale Admin reruns/approves.

**DB assertions**

```sql
select hold_type, status, released_by_user_id, release_note
from order_holds
where draft_order_id = :draft_order_id
  and hold_type = 'CREDIT_HOLD';

select action_type, actor_user_id, comment
from review_actions
where draft_order_id = :draft_order_id
order by created_at;
```

Pass:

- Release user has role `MANAGER`.
- Release note is not empty.

## 6. Agent Scenario Link

For agent/customer questions, this journey suite does not duplicate all 104 cases. It links to:

- `agent_customer_question_test_script.md`
- Curated IDs `AGQ-001..AGQ-104`
- Atomic alias IDs `AGQ-ALIAS-{canonical_id}-{row_number}`

Minimum UI smoke subset:

| UI smoke ID | Agent case | Purpose |
|---|---|---|
| UJ-AGENT-001 | AGQ-001 | Clear raw order creates reviewable draft |
| UJ-AGENT-002 | AGQ-053 | Duplicate alias creates clarification |
| UJ-AGENT-003 | AGQ-083 | Slang order asking total uses price API only |
| UJ-AGENT-004 | AGQ-088 | Ambiguous order asking total refuses total |
| UJ-AGENT-005 | AGQ-097 | Quote + credit uses rule engine |

## 7. Audit Debug Script

For every journey, run:

```sql
select stage, status, started_at, finished_at, duration_ms, metadata
from processing_events
where draft_order_id = :draft_order_id
order by started_at;

select action_type, actor_user_id, comment, before_data, after_data
from review_actions
where draft_order_id = :draft_order_id
order by created_at;

select event_type, actor_type, actor_user_id, metadata
from audit_events
where draft_order_id = :draft_order_id
order by created_at;
```

Pass:

- AI extraction, SKU matching, rule checks are traceable.
- Human edits/selections/releases/approvals are traceable.
- The reviewer can explain why each hold exists.

## 8. Exit Criteria

- 8 user journey scripts pass.
- 5 agent UI smoke scripts pass.
- 104 agent/customer scenario IDs are covered by linked agent script.
- No journey requires OCR, voice, Zalo auto-send, ERP sync, or invoice automation.
