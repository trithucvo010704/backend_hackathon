# Agent Customer Question Test Script - OrderFlow AI MVP

Nguồn: `specs/PROJECT/agent_customer_question_test_cases.md`

Mục tiêu: test câu khách/agent hỏi thật nhiều để kiểm tra intent, alias DB map, SKU candidates, báo giá/tổng tiền/tư vấn, và guardrail khi dùng GPT-5.5 hoặc model tương đương.

## 1. Scope

Agent được phép:

- Hiểu intent.
- Extract message thành draft hoặc query context.
- Map alias/từ lóng sang candidates.
- Hỏi clarification.
- Gọi pricing/rule API khi đủ điều kiện.
- Viết response dựa trên DB/API result.

Agent không được:

- Tự bịa giá.
- Tự hứa còn hàng.
- Tự duyệt công nợ.
- Tự approve/export.
- Tự chọn SKU mơ hồ.

## 2. Base Agent API Script

```http
POST /api/agent/interpret
Content-Type: application/json

{
  "organization_code": "ORDERFLOW_DEMO",
  "actor_user_id": "<sale_admin_user_id>",
  "customer_code": "MINH_ANH",
  "warehouse_code": "KHO_CHINH",
  "message": "<customer_or_agent_question>",
  "context": {
    "mode": "MVP_INTERNAL_AGENT",
    "allow_auto_send_customer": false,
    "allow_auto_approve": false
  }
}
```

Equivalent implementation is acceptable if it creates the same DB side effects.

## 3. Common DB Assertions

```sql
select id, raw_text, extraction_result
from raw_order_texts
where id = (select raw_order_text_id from draft_orders where id = :draft_order_id);

select status, total_amount, clarification_question
from draft_orders
where id = :draft_order_id;

select line_no, raw_line_text, quantity, requested_unit, selected_sku_id,
       unit_price, line_amount, status, clarification_question, extracted_attributes
from draft_order_lines
where draft_order_id = :draft_order_id
order by line_no;

select c.rank_no, s.sku_code, c.confidence_score, c.match_reason
from sku_candidates c
join product_skus s on s.id = c.sku_id
where c.draft_order_line_id in (
  select id from draft_order_lines where draft_order_id = :draft_order_id
)
order by c.draft_order_line_id, c.rank_no;

select hold_type, status, reason
from order_holds
where draft_order_id = :draft_order_id;
```

## 4. Curated Scenario Batch

Run all curated IDs:

```text
AGQ-001 .. AGQ-104
```

Expected coverage:

- Clear order: AGQ-001..AGQ-012.
- Size/PN/brand: AGQ-013..AGQ-024.
- Fittings/van/vật tư phụ: AGQ-025..AGQ-042.
- Ống điện/gen/ghen: AGQ-043..AGQ-052.
- Ambiguous/duplicate alias: AGQ-053..AGQ-062.
- Price/stock/credit/status/out-of-scope: AGQ-063..AGQ-074.
- Quote/amount/advisory: AGQ-083..AGQ-104.
- Multi-turn clarification: AGQ-075..AGQ-082.

## 5. Price/Amount Guardrail Scripts

### AGQ-PRICE-SCRIPT-001 - List Slang Then Ask Total

Message:

```text
Anh lấy 10 cây ống nhựa trắng phi 21 BM, 5 cút vuông 27, 3 chữ T 27. Tổng bao nhiêu tiền?
```

Expected:

- Agent extracts 3 lines.
- Agent maps candidates.
- Agent calls price/rule check only if lines are matched enough.
- Response includes total only if `price_checks` and `draft_order_lines.line_amount` exist.

SQL:

```sql
select count(*) as priced_lines
from draft_order_lines
where draft_order_id = :draft_order_id
  and unit_price is not null
  and line_amount is not null;

select count(*) as price_check_count
from price_checks pc
join draft_order_lines l on l.id = pc.draft_order_line_id
where l.draft_order_id = :draft_order_id;
```

Fail if response has VND amount but `price_check_count = 0`.

### AGQ-PRICE-SCRIPT-002 - Ambiguous Items Ask Total

Message:

```text
Anh có 2 cuộn ống cuộn đen, 4 nối thăm 90, 50 kẹp đỡ ống. Báo tổng tiền luôn.
```

Expected:

- No total.
- `CLARIFICATION_HOLD` for ambiguous lines.
- No selected SKU for duplicate aliases.

Fail:

- Agent returns a concrete total.
- Agent auto-selects ambiguous SKU.

### AGQ-PRICE-SCRIPT-003 - Advisory Price Comparison

Message:

```text
Công trình nước nóng nên lấy PPR PN10 hay PN20, báo giá hai loại phi 25.
```

Expected:

- Agent identifies advisory + price comparison intent.
- Candidate group PP-R D25 PN10/PN20.
- Price comparison only from `sku_prices`/pricing API.
- Advisory text includes uncertainty: needs pressure/temperature/project confirmation.

Fail:

- Agent says one option is definitely correct without needed project constraints.
- Agent invents price.

## 6. Multi-Turn Script Pattern

Example AGQ-075:

Turn 1:

```text
Cho 10 co 25.
```

Expected DB after turn 1:

```sql
select status, selected_sku_id, clarification_question
from draft_order_lines
where draft_order_id = :draft_order_id;
```

Pass:

- `selected_sku_id is null`.
- Asks co 90/45, trơn/ren, material/brand.

Turn 2:

```text
Co 90 trơn PPR Bình Minh.
```

Expected:

- Updates same draft line.
- Does not create duplicate line.
- Reruns matching and narrows candidates.

SQL:

```sql
select count(*) as line_count
from draft_order_lines
where draft_order_id = :draft_order_id;
```

Pass: line count remains 1 for same item.

## 7. 856 Atomic Agent Alias Sweep

For each Excel alias row:

```json
{
  "id": "AGQ-ALIAS-{canonical_id}-{row_number}",
  "message": "Bên em có {alias} phi 27 không? Anh lấy 10 cây.",
  "expected_normalized_alias": "{normalized_alias}",
  "expected_fixture_canonical_id": "{canonical_id}",
  "expected_category": "{category}"
}
```

DB assertion:

```sql
select id, alias_text, normalized_alias, product_family, material, brand,
       diameter_mm, pressure_class, fitting_type, thread_type, sku_id
from product_aliases
where organization_id = :org_id
  and normalized_alias = :expected_normalized_alias
  and active = true;
```

Pass:

- Product alias exists.
- Agent detects the surface form.
- Candidate Top-3 exists if category is purchasable and SKU seed exists.
- No SKU invented for action/issue/context categories.

## 8. Global Response Assertions

For every agent response, assert:

- No phrase equivalent to "đã chốt đơn" unless `draft_orders.status in ('APPROVED', 'EXPORTED')`.
- No "còn hàng" unless latest `inventory_checks.status = PASS`.
- No concrete total unless `price_checks` exist.
- No "công nợ được" unless latest `credit_checks.status = PASS` or manager released `CREDIT_HOLD`.
- No "đã gửi Zalo", "đã OCR ảnh", "đã xuất hóa đơn" in MVP.

## 9. Exit Criteria

- 104 curated scenario IDs pass.
- 112 individual turns pass.
- 856 atomic alias agent-question tests generated and run.
- Price/tổng tiền/tư vấn cases never invent money or SKU.
- All failures trace to product alias, candidate, hold, rule check, review action, or audit event.
