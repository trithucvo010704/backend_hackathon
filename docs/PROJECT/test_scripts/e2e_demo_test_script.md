# E2E Demo Test Script - OrderFlow AI MVP

Nguồn: `specs/PROJECT/e2e_demo_scenarios.md`

Mục tiêu: biến 8 scenario E2E thành script chạy được bằng API + DB assertion.

## 1. Setup Chung

Seed tối thiểu:

- `organizations`: 1 organization `ORDERFLOW_DEMO`.
- `app_users`: `sale_admin_01`, `manager_01`, `warehouse_01`.
- `customers`: `MINH_ANH`, `AN_PHAT`, `THANH_DAT`.
- `warehouses`: `KHO_CHINH`.
- `product_skus`, `product_aliases`, `price_lists`, `sku_prices`, `inventory_balances` theo seed trong E2E doc.
- `customer_credit_profiles` cho happy path, stock hold, credit hold, price hold.

Common variables:

```text
:org_id
:sale_admin_user_id
:manager_user_id
:warehouse_id
:customer_id
:project_id
:draft_order_id
```

## 2. Script E2E-01 - Happy Path

**Input**

```json
{
  "customer_code": "MINH_ANH",
  "project_hint": "Quận 7",
  "warehouse_code": "KHO_CHINH",
  "raw_text": "Anh lấy cho công trình Quận 7:\n10 cây ống nhựa trắng phi 21 Bình Minh,\n5 cút vuông 27,\n3 chữ T 27,\n2 nối ren trong 21,\ngiao sáng mai."
}
```

**Steps**

1. `POST /api/draft-orders/from-text`.
2. Poll or wait until extraction/matching complete.
3. `GET /api/draft-orders/{orderId}`.
4. `POST /api/draft-orders/{orderId}/run-checks`.
5. If all lines `MATCHED`, `POST /api/draft-orders/{orderId}/approve`.
6. `POST /api/draft-orders/{orderId}/documents/quote`.

**DB assertions**

```sql
select source_channel, raw_text
from raw_order_texts
where id = (select raw_order_text_id from draft_orders where id = :draft_order_id);

select status, total_amount
from draft_orders
where id = :draft_order_id;

select count(*) as line_count
from draft_order_lines
where draft_order_id = :draft_order_id;

select count(*) as open_holds
from order_holds
where draft_order_id = :draft_order_id and status = 'OPEN';

select document_type, status
from draft_order_documents
where draft_order_id = :draft_order_id;
```

**Pass**

- Có 4 `draft_order_lines`.
- Candidate đúng Top-3 cho từng line.
- `price_checks`, `inventory_checks`, `credit_checks` đều `PASS`.
- Không có hold open.
- Order đi được `READY_FOR_REVIEW -> APPROVED -> EXPORTED` nếu document generation set status exported.

## 3. Script E2E-02 - Clarification Hold

**Input**

```json
{
  "customer_code": "MINH_ANH",
  "warehouse_code": "KHO_CHINH",
  "raw_text": "Cho anh 20 cây ống nóng 25, 10 co, 5 tê giảm giống hôm trước. Giao công trình cũ, chiều nay nếu kịp."
}
```

**Steps**

1. Create draft from text.
2. Read lines and holds.
3. Verify system asks clarification.
4. Patch line/project clarification:

```json
{
  "line_updates": [
    {"raw_match": "co", "attributes": {"angle_degree": 90, "diameter_mm": 25, "connection_type": "trơn"}},
    {"raw_match": "tê giảm", "attributes": {"reducer_from_mm": 34, "reducer_to_mm": 25}}
  ],
  "project_hint": "Quận 7"
}
```

5. Rerun matching and rule checks.

**DB assertions**

```sql
select line_no, status, clarification_question, extracted_attributes
from draft_order_lines
where draft_order_id = :draft_order_id
order by line_no;

select hold_type, status, reason
from order_holds
where draft_order_id = :draft_order_id;

select action_type, before_data, after_data
from review_actions
where draft_order_id = :draft_order_id
order by created_at;
```

**Pass**

- Before fix: order `NEEDS_CLARIFICATION` hoặc `ON_HOLD`; có `CLARIFICATION_HOLD`.
- After fix: clarification hold resolved/released, lines có candidates rõ hơn.

## 4. Script E2E-03 - Ambiguous Alias

**Input**

```json
{
  "customer_code": "MINH_ANH",
  "warehouse_code": "KHO_CHINH",
  "raw_text": "Lấy giúp anh 2 cuộn ống cuộn đen, 4 nối thăm 90, 50 kẹp đỡ ống. Giao kho chính trong hôm nay."
}
```

**DB assertions**

```sql
select l.raw_line_text, l.status, l.selected_sku_id, l.clarification_question,
       count(c.id) as candidate_count
from draft_order_lines l
left join sku_candidates c on c.draft_order_line_id = l.id
where l.draft_order_id = :draft_order_id
group by l.id
order by l.line_no;
```

**Pass**

- `ống cuộn đen`, `nối thăm`, `kẹp đỡ ống` đều không có `selected_sku_id`.
- Mỗi line có nhiều candidates.
- Có `CLARIFICATION_HOLD`.

**Fail**

- Bất kỳ line duplicate alias nào auto-select SKU khi thiếu size/use-case/material.

## 5. Script E2E-04 - Stock Hold

**Input**

```json
{
  "customer_code": "MINH_ANH",
  "warehouse_code": "KHO_CHINH",
  "raw_text": "Công trình Nhà Bè cần gấp: 20 cây ống thoát 110, 10 co cong 88, 5 đầu thông tắc 110. Giao trong chiều nay."
}
```

**Expected seed**

`BM-PVCU-PIPE-D110.available_quantity = 8`, requested = 20.

**DB assertions**

```sql
select requested_quantity, available_quantity, status, reason
from inventory_checks
where draft_order_line_id = :d110_line_id
order by checked_at desc
limit 1;

select hold_type, status, reason, payload
from order_holds
where draft_order_id = :draft_order_id and hold_type = 'STOCK_HOLD';
```

**Pass**

- Inventory check `FAIL`.
- Có `STOCK_HOLD`.
- Approve/pick-list bị reject khi hold open.

## 6. Script E2E-05 - Credit Hold

**Input**

```json
{
  "customer_code": "AN_PHAT",
  "warehouse_code": "KHO_CHINH",
  "raw_text": "Anh An Phát lấy 30 cây ống HDPE PE100, 20 van khóa nước 27, 10 rắc co ren trong 27, giao công trình Long An tuần này."
}
```

**DB assertions**

```sql
select order_amount, credit_limit, current_debt, overdue_debt, projected_debt, status, reason
from credit_checks
where draft_order_id = :draft_order_id
order by checked_at desc
limit 1;

select hold_type, status, reason
from order_holds
where draft_order_id = :draft_order_id and hold_type = 'CREDIT_HOLD';
```

**Pass**

- `credit_checks.status = FAIL`.
- Có `CREDIT_HOLD`.
- Approve bị chặn nếu hold open.
- Manager release cần `release_note`.

## 7. Script E2E-06 - Price Hold

**Input**

```json
{
  "customer_code": "THANH_DAT",
  "warehouse_code": "KHO_CHINH",
  "raw_text": "Khách Thành Đạt hỏi 15 cây ống PPR nóng PN20 phi 21, 10 co ren cái 21, giá như lần trước giảm sâu giúp anh."
}
```

**Steps**

1. Create draft and match SKUs.
2. Patch line price below floor.
3. Run checks.

**DB assertions**

```sql
select proposed_unit_price, approval_floor_price, status, reason
from price_checks
where draft_order_line_id = :ppr_line_id
order by checked_at desc
limit 1;

select hold_type, status, reason
from order_holds
where draft_order_id = :draft_order_id and hold_type = 'PRICE_HOLD';
```

**Pass**

- `PRICE_HOLD` open khi unit price dưới sàn.
- Không approve được khi hold open.

## 8. Script E2E-07 - Repeat Order

**Input**

```json
{
  "customer_code": "MINH_ANH",
  "project_hint": "Quận 7",
  "raw_text": "Anh lấy lại giống đơn Quận 7 hôm trước, nhưng tăng ống lên 15 cây, co giữ nguyên, tê giảm lấy 5 cái. Giao sáng mai."
}
```

**DB assertions**

```sql
select raw_line_text, quantity, selected_sku_id, extracted_attributes
from draft_order_lines
where draft_order_id = :draft_order_id
order by line_no;

select stage, status, metadata
from processing_events
where draft_order_id = :draft_order_id
  and stage ilike '%HISTORY%';
```

**Pass**

- Nếu history unique: lines được tạo từ history + override.
- Nếu nhiều history match: tạo clarification, không invent SKU/line.

## 9. Script E2E-08 - Output Document Gate

**Precondition**

Order có open hold từ E2E-04, E2E-05 hoặc E2E-06.

**Steps**

1. Try `POST /documents/pick-list`.
2. Try approve.
3. Resolve hold.
4. Approve.
5. Generate quote and pick list.

**DB assertions**

```sql
select count(*) as open_holds
from order_holds
where draft_order_id = :draft_order_id and status = 'OPEN';

select document_type, status, generated_by_user_id, generated_at
from draft_order_documents
where draft_order_id = :draft_order_id
order by created_at;

select status
from inventory_reservations
where draft_order_line_id in (
  select id from draft_order_lines where draft_order_id = :draft_order_id
);
```

**Pass**

- Khi hold open: approve/export bị chặn.
- Sau khi hold resolved và approve: quote/pick list được tạo, có HTML snapshot.
