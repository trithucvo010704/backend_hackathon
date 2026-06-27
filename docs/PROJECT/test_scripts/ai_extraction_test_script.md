# AI Extraction Test Script - OrderFlow AI MVP

Nguồn: `specs/PROJECT/ai_extraction_test_cases.md`

Mục tiêu: test `RawOrderText -> raw_order_texts -> draft_orders -> draft_order_lines -> extracted_attributes/missing clarification`.

## 1. Scope

AI extraction chỉ được:

- Tách line.
- Bóc quantity/unit.
- Bóc alias/surface forms.
- Bóc attributes: material, brand, diameter, PN, fitting type, angle, thread, unit/package, use-case.
- Ghi missing attributes và clarification question.

AI extraction không được:

- Tự chọn SKU cuối cùng.
- Tự tính giá/tồn/công nợ.
- Tự approve/export.

## 2. Base API Script

```http
POST /api/draft-orders/from-text
Content-Type: application/json

{
  "organization_code": "ORDERFLOW_DEMO",
  "customer_code": "MINH_ANH",
  "warehouse_code": "KHO_CHINH",
  "source_channel": "MANUAL_TEXT",
  "raw_text": "<RawOrderText>"
}
```

Expected side effects:

- Insert `raw_order_texts`.
- Insert `draft_orders`.
- Insert `draft_order_lines`.
- Save AI JSON in `raw_order_texts.extraction_result` and/or `draft_order_lines.extracted_attributes`.
- Create `processing_events` for extraction.
- Create `audit_events` for draft creation.

## 3. Common DB Assertions

```sql
select source_channel, raw_text, extraction_result
from raw_order_texts
where id = (select raw_order_text_id from draft_orders where id = :draft_order_id);

select line_no, raw_line_text, item_description, quantity, requested_unit,
       extracted_attributes, clarification_question, status
from draft_order_lines
where draft_order_id = :draft_order_id
order by line_no;

select stage, status, metadata
from processing_events
where draft_order_id = :draft_order_id
order by started_at;
```

## 4. Main Extraction Scripts

| Script ID | Source case | Raw text focus | Must assert |
|---|---|---|---|
| AIEX-SCRIPT-001 | AIEX-E2E-001 | Clear PVC/fittings order | 4 lines; PVC-U/Bình Minh/D21; cút vuông 90; chữ T; ren trong |
| AIEX-SCRIPT-002 | AIEX-E2E-002 | PPR nóng + co/tê giảm thiếu info | `ống nóng` -> PP-R, PN/diameter if present; `co` missing angle/diameter; `tê giảm` missing reducer size if no history |
| AIEX-SCRIPT-003 | AIEX-E2E-003 | Duplicate aliases | `ống cuộn đen`, `nối thăm`, `kẹp đỡ ống` flagged ambiguous |
| AIEX-SCRIPT-004 | AIEX-E2E-004 | Missing quantity/unit | No default quantity = 1 |
| AIEX-SCRIPT-005 | AIEX-E2E-005 | Ren trong/ren ngoài | Thread gender not swapped |
| AIEX-SCRIPT-006 | AIEX-E2E-006 | Tê giảm/côn thu | Preserve from/to diameters |
| AIEX-SCRIPT-007 | AIEX-E2E-007 | Co/cút angle | cút vuông = 90; cút lơi/chếch = 45; co cong 88 = 88 |
| AIEX-SCRIPT-008 | AIEX-E2E-008 | Van/vòi | khóa nước may need subtype clarification; van bi tay gạt exact |
| AIEX-SCRIPT-009 | AIEX-E2E-009 | Ống điện/gen/ghen | Electrical use-case, not water pipe |
| AIEX-SCRIPT-010 | AIEX-E2E-010 | Issue + real items | `xì nước` is note/context, not SKU line |
| AIEX-SCRIPT-011 | AIEX-E2E-011 | Size symbols | phi/D/DN/fi normalized |
| AIEX-SCRIPT-012 | AIEX-E2E-012 | PN/độ dày | PN10/PN20 retained, two lines not merged |
| AIEX-SCRIPT-013 | AIEX-E2E-013 | Brand aliases | BM/NTP/Dekko extracted as brand |
| AIEX-SCRIPT-014 | AIEX-E2E-014 | Unit/package | cây/cuộn/mét/bộ preserved |
| AIEX-SCRIPT-015 | AIEX-E2E-015 | Delivery/project note | Delivery note not product line |
| AIEX-SCRIPT-016 | AIEX-E2E-016 | Repeat history | history reference captured, not invented |
| AIEX-SCRIPT-017 | AIEX-E2E-017 | Downstream hold input | Extraction clean; no credit hold at extraction stage |
| AIEX-SCRIPT-018 | AIEX-E2E-018 | Price note | Price request in order note, no unit price set |

## 5. Example Script Detail - AIEX-SCRIPT-001

**Input**

```text
Anh lấy cho công trình Quận 7:
10 cây ống nhựa trắng phi 21 Bình Minh,
5 cút vuông 27,
3 chữ T 27,
2 nối ren trong 21,
giao sáng mai.
```

**Assertions**

```sql
select count(*) = 4 as has_four_lines
from draft_order_lines
where draft_order_id = :draft_order_id;

select raw_line_text, quantity, requested_unit, extracted_attributes
from draft_order_lines
where draft_order_id = :draft_order_id
order by line_no;
```

Expected:

- Line 1 quantity 10, unit `cây`, brand Bình Minh, material PVC-U, diameter 21.
- Line 2 quantity 5, unit `cái`, fitting type co/cút, angle 90, diameter 27.
- Line 3 quantity 3, unit `cái`, fitting type tê/T, diameter 27.
- Line 4 quantity 2, unit `cái`, thread type ren trong, diameter 21.

## 6. Negative Scripts

| Script ID | Input | Expected |
|---|---|---|
| AIEX-NEG-SCRIPT-001 | `Cho 10 cây ống PVC phi 21, 5 co 90 phi 21.` | Brand null/missing; no hallucinated Bình Minh |
| AIEX-NEG-SCRIPT-002 | `Cho anh ống nóng phi 25 và co 25.` | Quantity missing for both; no default 1 |
| AIEX-NEG-SCRIPT-003 | `Nhà bị búa nước, cần test áp lại tuyến ống.` | Context/action only; no product line if no item to buy |
| AIEX-NEG-SCRIPT-004 | `Đi ống gen mềm âm tường cho dây điện.` | Electrical context; not water pipe |

## 7. 856 Atomic Alias Extraction Generator

For each row in Excel `Alias_Map`, generate:

```json
{
  "id": "AIEX-ALIAS-{canonical_id}-{row_number}",
  "raw_text": "Khách cần 10 cây/cái {alias} phi 27, giao hôm nay.",
  "expected_alias": "{alias}",
  "expected_normalized_alias": "{normalized_alias}",
  "expected_fixture_canonical_id": "{canonical_id}",
  "expected_category": "{category}",
  "expected_material": "{material}",
  "expected_application": "{application}"
}
```

DB assertion:

```sql
select *
from product_aliases
where organization_id = :org_id
  and active = true
  and normalized_alias = :expected_normalized_alias;
```

Extraction assertion:

- `draft_order_lines.extracted_attributes` contains the surface form or normalized alias.
- If implementation stores canonical fixture IDs, expected `canonical_id` appears in JSON.
- If category is action/issue/context, line may be a note/clarification instead of product line.

## 8. Exit Criteria

- 18 main extraction scripts pass.
- 4 negative scripts pass.
- 856 generated alias scripts pass or produce expected context/no-SKU behavior.
- No extraction script creates price, inventory, credit, approval, or document side effects.
