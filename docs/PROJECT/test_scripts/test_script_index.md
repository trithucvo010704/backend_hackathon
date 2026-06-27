# Test Script Index - OrderFlow AI MVP

Tài liệu này liệt kê các **test script** được tạo từ bộ tài liệu test hiện có và schema PostgreSQL của OrderFlow AI MVP.

## 1. Số File Script Đã Tạo

Tạo mới **7 file** trong folder `specs/PROJECT/test_scripts/`:

| # | Script file | Dựa trên tài liệu nguồn | Mục tiêu |
|---:|---|---|---|
| 1 | `test_script_index.md` | Tất cả test docs + DB schema | Index, thứ tự chạy, convention chung |
| 2 | `e2e_demo_test_script.md` | `e2e_demo_scenarios.md` | Script chạy end-to-end từ raw text tới approve/export |
| 3 | `ai_extraction_test_script.md` | `ai_extraction_test_cases.md` | Script test AI extraction và 856 alias atomic |
| 4 | `sku_matching_alias_test_script.md` | `sku_matching_alias_test_cases.md` | Script test alias/SKU candidates, duplicate alias |
| 5 | `rule_engine_test_script.md` | `rule_engine_test_cases.md` | Script test price/stock/credit/hold/reservation |
| 6 | `agent_customer_question_test_script.md` | `agent_customer_question_test_cases.md` | Script test agent hỏi đáp, báo giá, tư vấn, guardrail GPT |
| 7 | `mvp_user_journey_agent_test_script.md` | `mvp_user_journey_and_agent_test_plan.md` | Script test journey theo role/màn hình và liên kết agent scenarios |

## 2. DB Tables Dùng Trong Script

Các script dùng đúng bảng từ schema PostgreSQL đã cung cấp:

| Nhóm | Tables |
|---|---|
| Tenant/user/customer | `organizations`, `app_users`, `customers`, `customer_projects`, `customer_credit_profiles`, `warehouses` |
| Catalog/map | `product_skus`, `product_aliases` |
| Price/stock | `price_lists`, `sku_prices`, `inventory_balances` |
| Draft order | `raw_order_texts`, `draft_orders`, `draft_order_lines`, `sku_candidates` |
| Rules | `price_checks`, `inventory_checks`, `credit_checks`, `order_holds` |
| Review/output | `review_actions`, `inventory_reservations`, `draft_order_documents` |
| Trace | `processing_events`, `audit_events` |

## 3. Canonical ID Convention

Excel có `canonical_id`, nhưng DB schema hiện tại chưa có bảng/cột `canonical_id` riêng. Vì vậy script dùng convention MVP:

- `canonical_id` là **test fixture ID** từ Excel, dùng để assert expected mapping.
- Trong DB, mapping chính được assert bằng `product_aliases.alias_text`, `product_aliases.normalized_alias`, `product_aliases.product_family`, `material`, `brand`, `diameter_mm`, `pressure_class`, `fitting_type`, `thread_type`, và `sku_candidates`.
- Nếu implementation lưu `canonical_id` vào JSON, assert trong `draft_order_lines.extracted_attributes` hoặc `raw_order_texts.extraction_result`.
- Nếu implementation lưu `canonical_id` trong `product_aliases.note`, test runner có thể parse JSON/text note, nhưng không bắt buộc cho MVP.

## 4. Thứ Tự Chạy Khuyến Nghị

1. `ai_extraction_test_script.md`
2. `sku_matching_alias_test_script.md`
3. `rule_engine_test_script.md`
4. `agent_customer_question_test_script.md`
5. `e2e_demo_test_script.md`
6. `mvp_user_journey_agent_test_script.md`

Lý do: extraction và matching là nền. Rule engine cần line đã có `selected_sku_id`. Agent và E2E gom các lớp này lại.

## 5. API Naming Convention

Các endpoint dưới đây là contract test theo MVP docs. Nếu implementation đặt tên khác, test runner map sang endpoint tương đương:

| Operation | Endpoint contract |
|---|---|
| Create draft from raw text | `POST /api/draft-orders/from-text` |
| Get draft detail | `GET /api/draft-orders/{orderId}` |
| Match SKU for one line | `POST /api/draft-order-lines/{lineId}/match-skus` |
| Select SKU | `POST /api/draft-order-lines/{lineId}/select-sku` |
| Update line | `PATCH /api/draft-order-lines/{lineId}` |
| Run rule checks | `POST /api/draft-orders/{orderId}/run-checks` |
| Release hold | `POST /api/order-holds/{holdId}/release` |
| Approve order | `POST /api/draft-orders/{orderId}/approve` |
| Generate quote | `POST /api/draft-orders/{orderId}/documents/quote` |
| Generate pick list | `POST /api/draft-orders/{orderId}/documents/pick-list` |
| Agent interpret/question | `POST /api/agent/interpret` or equivalent internal endpoint |

## 6. Common SQL Assertion Template

```sql
-- draft header
select id, status, customer_id, project_id, warehouse_id, total_amount, clarification_question
from draft_orders
where id = :draft_order_id;

-- lines
select id, line_no, raw_line_text, quantity, requested_unit, selected_sku_id,
       unit_price, line_amount, status, clarification_question, extracted_attributes
from draft_order_lines
where draft_order_id = :draft_order_id
order by line_no;

-- candidates
select c.draft_order_line_id, c.rank_no, s.sku_code, s.product_name,
       c.confidence_score, c.match_reason, c.matched_attributes, c.missing_attributes
from sku_candidates c
join product_skus s on s.id = c.sku_id
where c.draft_order_line_id in (
  select id from draft_order_lines where draft_order_id = :draft_order_id
)
order by c.draft_order_line_id, c.rank_no;

-- holds
select hold_type, status, severity, reason, payload
from order_holds
where draft_order_id = :draft_order_id
order by created_at;

-- trace
select stage, status, metadata
from processing_events
where draft_order_id = :draft_order_id
order by started_at;

select event_type, actor_type, metadata
from audit_events
where draft_order_id = :draft_order_id
order by created_at;
```

## 7. Global Fail Conditions

- Agent/LLM trả số tiền cụ thể nhưng không có `price_checks`, `sku_prices`, hoặc pricing response.
- Agent/LLM nói còn hàng nhưng không có `inventory_checks`.
- Agent/LLM nói công nợ được duyệt nhưng không có `credit_checks` và release hợp lệ.
- Có line `PENDING_MATCH` hoặc `NEEDS_CLARIFICATION` nhưng vẫn approve/export.
- Duplicate alias bị auto-select khi chưa đủ thuộc tính.
- Không có `processing_events`/`audit_events` cho các bước chính.
