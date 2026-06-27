# SKU Matching Alias Test Script - OrderFlow AI MVP

Nguồn: `specs/PROJECT/sku_matching_alias_test_cases.md`

Mục tiêu: test `draft_order_lines -> product_aliases/product_skus -> sku_candidates -> selected_sku_id only when safe`.

## 1. Scope

SKU matching được phép:

- Normalize alias/từ lóng.
- Tìm candidates trong `product_aliases` và `product_skus`.
- Ghi `sku_candidates` với rank, confidence, reason.
- Đặt line `MATCHED` khi top candidate đủ rõ theo threshold.

SKU matching không được:

- Tự tính giá/tồn/công nợ.
- Tự approve/export.
- Auto-select duplicate alias khi thiếu thuộc tính.

## 2. Base API Script

```http
POST /api/draft-order-lines/{lineId}/match-skus
Content-Type: application/json

{
  "organization_code": "ORDERFLOW_DEMO",
  "force_rematch": true
}
```

Then:

```http
GET /api/draft-order-lines/{lineId}/sku-candidates
```

## 3. Common DB Assertions

```sql
select l.id, l.raw_line_text, l.status, l.selected_sku_id, l.extracted_attributes
from draft_order_lines l
where l.id = :line_id;

select c.rank_no, s.sku_code, s.product_name, c.confidence_score,
       c.match_reason, c.matched_attributes, c.missing_attributes
from sku_candidates c
join product_skus s on s.id = c.sku_id
where c.draft_order_line_id = :line_id
order by c.rank_no;
```

## 4. Main Matching Scripts

| Script ID | Source case | Raw line | Expected |
|---|---|---|---|
| SKUM-SCRIPT-001 | SKUM-E2E-001 | `10 cây ống nhựa trắng phi 21 Bình Minh` | BM PVC-U D21 Top-1 |
| SKUM-SCRIPT-002 | SKUM-E2E-002 | `10 cay ong nhua trang phi 21 BM` | Same as above, accent-insensitive |
| SKUM-SCRIPT-003 | SKUM-E2E-003 | `20 cây ống nóng 25 PN20` | PP-R D25 PN20 Top-1 |
| SKUM-SCRIPT-004 | SKUM-E2E-004 | `20 cây ống nóng 25` | Multiple PN candidates, no auto-select |
| SKUM-SCRIPT-005 | SKUM-E2E-005 | `5 cút vuông 27` | Co 90 D27 Top-1/Top-3 |
| SKUM-SCRIPT-006 | SKUM-E2E-006 | `10 cút lơi 27` | Co 45 D27, not Co 90 |
| SKUM-SCRIPT-007 | SKUM-E2E-007 | `8 tê giảm 34 xuống 27` | Tee reducer 34-27 |
| SKUM-SCRIPT-008 | SKUM-E2E-008 | `6 nối răng trong 27` | Nối ren trong, not ren ngoài |
| SKUM-SCRIPT-009 | SKUM-E2E-009 | `2 bộ zắc co ren ngoài 27` | Rắc co ren ngoài, not normal coupling |
| SKUM-SCRIPT-010 | SKUM-E2E-010 | `20 khóa nước 27` | Multiple valve subtype if seed has many |
| SKUM-SCRIPT-011 | SKUM-E2E-011 | `100m ống ghen cứng D20` | Electrical conduit, not water pipe |
| SKUM-SCRIPT-012 | SKUM-E2E-012 | `20 máng gen nhựa` | Cable trunking, not round conduit |
| SKUM-SCRIPT-013 | SKUM-E2E-013 | `2 cuộn ống cuộn đen` | Duplicate alias, no auto-select |
| SKUM-SCRIPT-014 | SKUM-E2E-014 | `4 nối thăm 90` | Duplicate alias, no auto-select |
| SKUM-SCRIPT-015 | SKUM-E2E-015 | `50 kẹp đỡ ống` | Duplicate alias, no auto-select |
| SKUM-SCRIPT-016 | SKUM-E2E-016 | `8 cây ống NTP phi 34` | Tiền Phong D34 Top-1 |
| SKUM-SCRIPT-017 | SKUM-E2E-017 | `5 cây ống PVC DN32` | DN32 maps according to catalog conversion |
| SKUM-SCRIPT-018 | SKUM-E2E-018 | `1 cuộn băng tan` | PTFE/băng tan Top-1 |
| SKUM-SCRIPT-019 | SKUM-E2E-019 | `hàn nhiệt ppr nóng pn20` | Action context, no forced SKU |
| SKUM-SCRIPT-020 | SKUM-E2E-020 | `nhà bị búa nước cần test áp` | Issue/action context, no product SKU |

## 5. Duplicate Alias Regression SQL

```sql
-- no auto-selected SKU for ambiguous duplicate alias lines
select raw_line_text, selected_sku_id, status, clarification_question
from draft_order_lines
where id in (:ong_cuon_den_line_id, :noi_tham_line_id, :kep_do_ong_line_id);

-- multiple candidates expected
select draft_order_line_id, count(*) as candidate_count
from sku_candidates
where draft_order_line_id in (:ong_cuon_den_line_id, :noi_tham_line_id, :kep_do_ong_line_id)
group by draft_order_line_id;
```

Pass:

- `selected_sku_id is null`.
- `status in ('PENDING_MATCH', 'NEEDS_CLARIFICATION')`.
- `candidate_count >= 2`.

## 6. API Contract Scripts

### SKUM-API-SCRIPT-001 - Match One Line

1. Create draft line with extraction attributes.
2. `POST /api/draft-order-lines/{lineId}/match-skus`.
3. Assert candidates inserted.
4. Assert `processing_events` stage contains SKU matching.

### SKUM-API-SCRIPT-002 - Get Candidates

1. `GET /api/draft-order-lines/{lineId}/sku-candidates`.
2. Assert fields: `rank_no`, `sku_code`, `product_name`, `confidence_score`, `match_reason`.

### SKUM-API-SCRIPT-003 - Select SKU

```http
POST /api/draft-order-lines/{lineId}/select-sku
Content-Type: application/json

{
  "sku_id": "<candidate_sku_id>",
  "actor_user_id": "<sale_admin_user_id>",
  "comment": "Selected after review"
}
```

DB assertions:

```sql
select selected_sku_id, selected_by_user_id, selected_at, status
from draft_order_lines
where id = :line_id;

select action_type, actor_user_id, comment
from review_actions
where draft_order_line_id = :line_id
order by created_at desc
limit 1;
```

## 7. Negative Scripts

| Script ID | Input | Expected |
|---|---|---|
| SKUM-NEG-SCRIPT-001 | Clear item but missing quantity | Candidates allowed; no quantity invented |
| SKUM-NEG-SCRIPT-002 | `ống siêu chịu lực D999` | No high-confidence invented SKU |
| SKUM-NEG-SCRIPT-003 | Matching step | No price/stock/credit checks created |
| SKUM-NEG-SCRIPT-004 | `co 90 27` missing brand/material | Candidates returned; missing attributes captured |

## 8. 856 Atomic Alias Matching Generator

For each Excel `Alias_Map` row:

```json
{
  "id": "SKUM-ALIAS-{canonical_id}-{row_number}",
  "raw_line_text": "{qty} {unit} {alias}",
  "expected_normalized_alias": "{normalized_alias}",
  "expected_fixture_canonical_id": "{canonical_id}",
  "expected_category": "{category}"
}
```

DB assertions:

- `product_aliases.normalized_alias = expected_normalized_alias` exists.
- If the category is purchasable and seed SKU exists, expected candidate Top-3.
- If category is action/issue/context, no forced SKU.

## 9. Exit Criteria

- 20 main matching scripts pass.
- 3 API contract scripts pass.
- 4 negative scripts pass.
- 856 generated alias matching scripts pass according to category.
- Duplicate aliases never auto-select without enough attributes.
