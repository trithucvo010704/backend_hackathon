# Rule Engine Test Script - OrderFlow AI MVP

Nguồn: `specs/PROJECT/rule_engine_test_cases.md`

Mục tiêu: test deterministic rules trên DB thật: price, inventory, credit, hold, approval, reservation, document gate.

## 1. Scope

Input của rule engine là `draft_orders` có `draft_order_lines.selected_sku_id` và quantity hợp lệ.

Rule engine được phép:

- Tạo `price_checks`, `inventory_checks`, `credit_checks`.
- Set `unit_price`, `line_amount`, `total_amount`.
- Tạo `order_holds`.
- Chặn approve/export khi còn hold.
- Tạo `inventory_reservations` khi approve.

Rule engine không được chọn SKU hoặc dùng LLM.

## 2. Base API Script

```http
POST /api/draft-orders/{orderId}/run-checks
Content-Type: application/json

{
  "actor_user_id": "<sale_admin_user_id>"
}
```

## 3. Common DB Assertions

```sql
select status, total_amount
from draft_orders
where id = :draft_order_id;

select l.line_no, s.sku_code, l.quantity, l.unit_price, l.line_amount, l.status
from draft_order_lines l
join product_skus s on s.id = l.selected_sku_id
where l.draft_order_id = :draft_order_id
order by l.line_no;

select l.line_no, pc.proposed_unit_price, pc.reference_unit_price,
       pc.approval_floor_price, pc.status, pc.reason
from price_checks pc
join draft_order_lines l on l.id = pc.draft_order_line_id
where l.draft_order_id = :draft_order_id
order by pc.checked_at;

select l.line_no, ic.requested_quantity, ic.on_hand_quantity,
       ic.reserved_quantity, ic.available_quantity, ic.status, ic.reason
from inventory_checks ic
join draft_order_lines l on l.id = ic.draft_order_line_id
where l.draft_order_id = :draft_order_id
order by ic.checked_at;

select order_amount, credit_limit, current_debt, overdue_debt,
       pending_approved_order_amount, projected_debt, status, reason
from credit_checks
where draft_order_id = :draft_order_id
order by checked_at;

select hold_type, status, severity, reason, payload
from order_holds
where draft_order_id = :draft_order_id
order by created_at;
```

## 4. Rule Scripts

| Script ID | Source case | Focus | Expected |
|---|---|---|---|
| RULE-SCRIPT-001 | RULE-E2E-001 | Happy path | Price, inventory, credit pass; no hold |
| RULE-SCRIPT-002 | RULE-PRICE-001 | Tier price | TIER_A/TIER_B price differs |
| RULE-SCRIPT-003 | RULE-PRICE-002 | Below floor | `PRICE_HOLD` |
| RULE-SCRIPT-004 | RULE-PRICE-003 | Missing price | `PRICE_HOLD` |
| RULE-SCRIPT-005 | RULE-STOCK-001 | Available equals requested | PASS |
| RULE-SCRIPT-006 | RULE-STOCK-002 | Available < requested | `STOCK_HOLD` |
| RULE-SCRIPT-007 | RULE-STOCK-003 | Reserved reduces available | `STOCK_HOLD` based on available, not on-hand |
| RULE-SCRIPT-008 | RULE-STOCK-004 | Edit quantity rerun | Old hold resolved/released, new snapshot PASS |
| RULE-SCRIPT-009 | RULE-CREDIT-001 | Credit within limit | PASS |
| RULE-SCRIPT-010 | RULE-CREDIT-002 | Projected debt > limit | `CREDIT_HOLD` |
| RULE-SCRIPT-011 | RULE-CREDIT-003 | Overdue debt | `CREDIT_HOLD` |
| RULE-SCRIPT-012 | RULE-MULTI-001 | Stock + credit | Both holds created |
| RULE-SCRIPT-013 | RULE-MULTI-002 | Price + stock | Both holds created |
| RULE-SCRIPT-014 | RULE-REVIEW-001 | Release hold | Requires user and note |
| RULE-SCRIPT-015 | RULE-APPROVAL-001 | Approve with hold | Reject |
| RULE-SCRIPT-016 | RULE-APPROVAL-002 | Approve pass order | Reservations created |
| RULE-SCRIPT-017 | RULE-APPROVAL-003 | Approve idempotency | No duplicate reservation |
| RULE-SCRIPT-018 | RULE-DOC-001 | Pick list with hold | Reject |
| RULE-SCRIPT-019 | RULE-DOC-002 | Quote after valid review | Document generated |
| RULE-SCRIPT-020 | RULE-DATA-001 | No selected SKU | No price/inventory check, clarification hold |
| RULE-SCRIPT-021 | RULE-DATA-002 | Quantity invalid | Validation/clarification, no reservation |
| RULE-SCRIPT-022 | RULE-UNIT-001 | Unit conversion missing | Clarification hold |
| RULE-SCRIPT-023 | RULE-REGRESSION-001 | Băng tan low-value SKU | Normal rules pass |
| RULE-SCRIPT-024 | RULE-REGRESSION-002 | Action/issue no SKU | No rule checks |

## 5. Approval Script

```http
POST /api/draft-orders/{orderId}/approve
Content-Type: application/json

{
  "actor_user_id": "<sale_admin_user_id>"
}
```

DB assertions:

```sql
select status, approved_by_user_id, approved_at
from draft_orders
where id = :draft_order_id;

select r.sku_id, r.draft_order_line_id, r.quantity, r.status
from inventory_reservations r
where r.draft_order_line_id in (
  select id from draft_order_lines where draft_order_id = :draft_order_id
);

select action_type, actor_user_id
from review_actions
where draft_order_id = :draft_order_id
order by created_at desc;
```

Pass:

- No open hold before approve.
- Reservation count equals approved purchasable line count.
- Calling approve again does not duplicate reservation.

## 6. Hold Release Script

```http
POST /api/order-holds/{holdId}/release
Content-Type: application/json

{
  "actor_user_id": "<manager_user_id>",
  "release_note": "Đã duyệt theo policy demo"
}
```

DB assertions:

```sql
select status, released_by_user_id, released_at, release_note
from order_holds
where id = :hold_id;

select action_type, comment
from review_actions
where draft_order_id = :draft_order_id
  and action_type in ('RELEASE_HOLD', 'RELEASE_PRICE_HOLD', 'RELEASE_CREDIT_HOLD')
order by created_at desc;
```

Fail if release note is empty for `PRICE_HOLD` or `CREDIT_HOLD`.

## 7. API Contract Scripts

| Script ID | Endpoint | Must assert |
|---|---|---|
| RULE-API-SCRIPT-001 | `POST /api/draft-orders/{id}/run-checks` | Creates fresh snapshots, updates total/status, writes processing events |
| RULE-API-SCRIPT-002 | `GET /api/draft-orders/{id}/checks` | Returns price/inventory/credit latest or history |
| RULE-API-SCRIPT-003 | `GET /api/draft-orders/{id}/holds` | Returns hold id/type/status/reason/release fields |

## 8. Negative Scripts

| Script ID | Expected |
|---|---|
| RULE-NEG-SCRIPT-001 | AI service disabled but rule checks still work for selected SKUs |
| RULE-NEG-SCRIPT-002 | Rerun does not release hold if failure remains |
| RULE-NEG-SCRIPT-003 | Rerun credit check does not double-count order amount |
| RULE-NEG-SCRIPT-004 | Draft/run-check does not reduce on-hand; approve creates reservation only |

## 9. Exit Criteria

- 24 main rule scripts pass.
- 3 API scripts pass.
- 4 negative scripts pass.
- No approval/export while any `order_holds.status = OPEN`.
- Rule engine never mutates `selected_sku_id` unless explicit review action exists.
