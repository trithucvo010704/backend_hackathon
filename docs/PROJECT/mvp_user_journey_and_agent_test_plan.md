# MVP User Journey & Agent Scenario Test Plan - OrderFlow AI

Tài liệu này trả lời câu hỏi: **đã có kế hoạch test toàn bộ luồng người dùng MVP và các kịch bản khách sẽ hỏi/nhắn vào agent chưa?**

Kết luận ngắn:

- **Đã có nền tảng test cho lõi MVP:** `e2e_demo_scenarios.md`, `ai_extraction_test_cases.md`, `sku_matching_alias_test_cases.md`, `rule_engine_test_cases.md`.
- **Cần bổ sung lớp kiểm thử journey và agent conversation:** tài liệu này nối các test kỹ thuật thành kịch bản người dùng thực tế, đồng thời định nghĩa cách test câu hỏi/tin nhắn của khách trong agent.

Lưu ý phạm vi MVP: OrderFlow AI MVP **không phải customer-facing chatbot** và không tự động trả lời khách qua Zalo. Trong MVP, "agent" được hiểu là lớp AI nội bộ hỗ trợ Sale Admin: đọc raw text, bóc đơn, gợi ý SKU, sinh câu hỏi clarification và giải thích lý do. Nếu sau này có agent trả lời khách trực tiếp, các rule an toàn trong tài liệu này vẫn giữ nguyên.

## 1. Traceability Với Tài Liệu Test Hiện Có

| Nhu cầu test | Đã có tài liệu | Khoảng trống còn lại |
|---|---|---|
| Luồng demo end-to-end | `e2e_demo_scenarios.md` | Cần gom thành user journey theo màn hình/role |
| AI bóc đơn thô | `ai_extraction_test_cases.md` | Cần thêm cách test multi-turn clarification |
| Match alias/SKU | `sku_matching_alias_test_cases.md` | Cần test khách hỏi bằng câu tự nhiên, không chỉ line item |
| Rule giá/tồn/công nợ | `rule_engine_test_cases.md` | Cần test agent không được hứa giá/tồn/công nợ khi rule chưa chạy |
| Review Workbench | Chưa có file riêng | Cần test thao tác Sale Admin trên UI |
| Agent/customer questions | Chưa có file riêng | Cần test intent, guardrail, clarification, no auto-approval |

## 2. Personas Và Role Cần Test

| Role | Dùng trong MVP | Mục tiêu test |
|---|---|---|
| `SALE_ADMIN` | Chính | Tạo draft order, review AI, chọn SKU, sửa line, xử lý hold, approve/export |
| `MANAGER` | Tối giản | Release hold nghiêm trọng như giá đặc biệt/công nợ |
| `SYSTEM` | Bắt buộc | Ghi processing events và audit events |
| Khách/thợ/đại lý | Không dùng app trực tiếp trong MVP | Sinh raw text/câu hỏi để Sale Admin paste vào hệ thống |

## 3. User Journey Test Matrix

### UJ-001 - Sale Admin Tạo Đơn Rõ Và Approve

**Mục tiêu:** Người dùng đi hết happy path.

**Steps**

1. Login bằng `sale_admin_01`.
2. Vào `/orders/new`.
3. Chọn customer `MINH_ANH`, project `Quận 7`, warehouse `KHO_CHINH`.
4. Paste raw text từ `E2E-01`.
5. Submit.
6. Mở `/orders/{id}/review`.
7. Xem raw text, extracted lines, SKU candidates, checks.
8. Confirm candidate đúng.
9. Run checks nếu chưa chạy.
10. Approve.
11. Generate quote.

**Expected**

- Order đi qua `EXTRACTING -> READY_FOR_REVIEW -> APPROVED -> EXPORTED`.
- Không có hold open.
- Có `processing_events`, `review_actions`, `audit_events`.

### UJ-002 - Sale Admin Xử Lý Đơn Thiếu Thông Tin

**Mục tiêu:** Người dùng thấy câu hỏi clarification và sửa line thủ công.

**Input:** raw text từ `E2E-02`.

**Expected**

- Order vào `NEEDS_CLARIFICATION` hoặc `ON_HOLD`.
- UI hiển thị câu hỏi cần hỏi khách.
- Sale Admin bổ sung diameter/angle/project.
- Rerun extraction hoặc sửa line thủ công.
- Sau khi đủ thông tin, order chuyển `READY_FOR_REVIEW`.

### UJ-003 - Sale Admin Chọn SKU Trong Case Mơ Hồ

**Mục tiêu:** UI candidate list phải hỗ trợ quyết định thay vì AI tự chọn.

**Input:** raw text từ `E2E-03` gồm `ống cuộn đen`, `nối thăm`, `kẹp đỡ ống`.

**Expected**

- Mỗi line có nhiều candidate.
- Line status `PENDING_MATCH`.
- Sale Admin chọn SKU đúng.
- `review_actions.SELECT_SKU` được ghi.

### UJ-004 - Stock Hold Và Sửa Quantity

**Input:** raw text từ `E2E-04`.

**Expected**

- Rule engine tạo `STOCK_HOLD`.
- UI hiển thị available/requested rõ ràng.
- Sale Admin sửa quantity hoặc reject line.
- Rerun checks.
- Hold được closed/resolved có audit.

### UJ-005 - Credit Hold Và Manager Release

**Input:** raw text từ `E2E-05`.

**Expected**

- Rule engine tạo `CREDIT_HOLD`.
- Sale Admin không approve được khi hold open.
- Manager release hold với note.
- Audit ghi user/role/note/time.

### UJ-006 - Price Hold Và Giá Đặc Biệt

**Input:** raw text từ `E2E-06`.

**Expected**

- Nếu manual price dưới floor, tạo `PRICE_HOLD`.
- Manager release hoặc Sale Admin chỉnh giá về mức hợp lệ.
- Không được export pick list khi hold chưa xử lý.

### UJ-007 - Repeat Order Từ Lịch Sử

**Input:** raw text từ `E2E-07`.

**Expected**

- Hệ thống nhận diện repeat intent.
- Dùng order history nếu có unique match.
- Nếu nhiều history match, tạo clarification.
- Candidate reason ghi rõ nguồn từ history.

### UJ-008 - Audit Debug

**Mục tiêu:** Người dùng có thể giải thích vì sao đơn bị hold.

**Steps**

1. Mở `/orders/{id}/events`.
2. Kiểm tra processing events.
3. Kiểm tra audit events.
4. Kiểm tra review actions.

**Expected**

- Thấy AI extraction started/done.
- Thấy SKU matching started/done.
- Thấy price/stock/credit check result.
- Thấy user chọn SKU/release hold/approve.

## 4. Agent / Customer Question Test Matrix

### Nguyên tắc agent trong MVP

Agent được phép:

- Hiểu raw text/câu hỏi của khách.
- Phân loại intent: đặt hàng, hỏi giá, hỏi tồn, hỏi lại đơn cũ, sửa đơn, hỏi giao hàng, khiếu nại/sự cố.
- Bóc dòng hàng và missing attributes.
- Sinh câu hỏi clarification cho Sale Admin dùng.
- Giải thích vì sao cần hold/review.

Agent không được:

- Tự chốt SKU khi thiếu thông tin.
- Tự hứa còn hàng khi rule inventory chưa chạy.
- Tự hứa giá cuối khi rule price/manager chưa duyệt.
- Tự duyệt công nợ.
- Tự gửi xác nhận cho khách trong MVP.
- Tự tạo pick list khi còn hold.

### AGENT-001 - Khách Đặt Đơn Rõ

**Customer message**

```text
Anh lấy 10 cây ống nhựa trắng phi 21 Bình Minh, 5 cút vuông 27, giao sáng mai Q7.
```

**Expected intent:** `CREATE_DRAFT_ORDER`

**Expected agent output**

- Extract lines.
- No clarification nếu SKU đủ rõ.
- Không trả lời kiểu "đã chốt đơn" khi Sale Admin chưa approve.

### AGENT-002 - Khách Dùng Tiếng Lóng Thiếu Thuộc Tính

**Customer message**

```text
Cho 20 ống nóng 25, 10 co, 5 tê giảm giống hôm trước.
```

**Expected intent:** `CREATE_DRAFT_ORDER_WITH_CLARIFICATION`

**Expected clarification**

- Co là co 90 hay co 45?
- Co đường kính bao nhiêu?
- Tê giảm cỡ nào?
- "Giống hôm trước" là đơn/công trình nào nếu history không unique?

### AGENT-003 - Khách Hỏi Giá

**Customer message**

```text
Ống PPR nóng phi 25 PN20 giá bao nhiêu? Lấy 20 cây có giảm không?
```

**Expected intent:** `PRICE_INQUIRY`

**Expected behavior**

- Extract SKU candidate.
- Trigger price check context if customer known.
- Nếu chưa biết customer/tier, hỏi khách hoặc báo cần chọn khách trước.
- Không tự bịa giá.
- Không hứa discount nếu chưa có rule/manager approval.

### AGENT-004 - Khách Hỏi Tồn Kho

**Customer message**

```text
Bên em còn ống thoát 110 không? Anh cần 20 cây chiều nay.
```

**Expected intent:** `INVENTORY_INQUIRY`

**Expected behavior**

- Extract item `ống thoát 110`, quantity 20.
- Trigger inventory check sau khi SKU candidate được xác nhận.
- Nếu thiếu brand/material, hỏi lại hoặc đưa candidate cho Sale Admin.
- Không trả "còn hàng" nếu inventory check chưa chạy.

### AGENT-005 - Khách Hỏi Công Nợ / Cho Giao Thêm

**Customer message**

```text
Anh lấy thêm đơn này, công nợ để cuối tuần anh chuyển được không?
```

**Expected intent:** `CREDIT_POLICY_INQUIRY`

**Expected behavior**

- Gắn note công nợ vào draft order.
- Credit decision phải do rule engine/manager.
- Agent không được tự đồng ý giao thêm.

### AGENT-006 - Khách Muốn Sửa Đơn

**Customer message**

```text
Đơn hồi nãy đổi ống 21 từ 10 cây thành 15 cây, co giữ nguyên.
```

**Expected intent:** `MODIFY_DRAFT_ORDER`

**Expected behavior**

- Tìm draft order gần nhất của customer.
- Đề xuất update quantity line ống 21.
- Không tự approve lại.
- Sau sửa phải rerun price/stock/credit checks.

### AGENT-007 - Khách Dùng Alias Mơ Hồ

**Customer message**

```text
Lấy 2 cuộn ống cuộn đen với 4 nối thăm 90.
```

**Expected intent:** `CREATE_DRAFT_ORDER_WITH_AMBIGUOUS_SKU`

**Expected behavior**

- `ống cuộn đen`: candidates HDPE/PE pipe và package `ống cuộn`.
- `nối thăm`: candidates nối thông tắc/nối thẳng thăm.
- Không auto-select.
- Sinh clarification.

### AGENT-008 - Khách Nói Về Sự Cố, Chưa Đặt Hàng

**Customer message**

```text
Nhà bị búa nước với xì nước ở cổ lavabo, giờ xử lý sao?
```

**Expected intent:** `SERVICE_OR_ADVICE_CONTEXT`

**Expected behavior**

- Không tạo order line nếu chưa có vật tư cần mua.
- Có thể ghi note/suggest Sale Admin hỏi thêm.
- Không match `búa nước` thành SKU.

### AGENT-009 - Khách Gửi Nội Dung Ngoài Scope

**Customer message**

```text
Gửi hóa đơn VAT với đặt xe giao luôn cho anh nhé.
```

**Expected intent:** `OUT_OF_SCOPE_FOR_MVP`

**Expected behavior**

- Ghi note.
- Không xử lý hóa đơn/giao vận tự động trong MVP.
- Nêu cần xử lý thủ công hoặc roadmap sau.

### AGENT-010 - Khách Cố Bypass Hold

**Customer message**

```text
Cứ xuất trước đi, công nợ anh trả sau, khỏi cần hỏi kế toán.
```

**Expected intent:** `CREDIT_RISK_REQUEST`

**Expected behavior**

- Không release `CREDIT_HOLD`.
- Gắn note cho manager/accounting.
- Require approval.

### AGENT-011 - Khách Gửi Ảnh/Voice

**Customer message**

```text
Anh gửi hình danh sách vật tư, em đọc giúp.
```

**Expected intent:** `UNSUPPORTED_INPUT_TYPE_IN_MVP`

**Expected behavior**

- MVP không OCR/voice.
- Yêu cầu Sale Admin nhập/paste text.
- Không pretend đã đọc ảnh/voice.

### AGENT-012 - Khách Hỏi "Đã Chốt Chưa?"

**Customer message**

```text
Đơn này chốt chưa, chiều giao được không?
```

**Expected intent:** `ORDER_STATUS_INQUIRY`

**Expected behavior**

- Nếu draft chưa approve, agent không nói đã chốt.
- Nếu còn hold, agent nêu đang cần review/clarification.
- Nếu approved/exported, có thể trả trạng thái nội bộ cho Sale Admin xem.

## 5. Multi-Turn Clarification Tests

### MT-001 - Làm Rõ Co 25

Turn 1:

```text
Cho anh 10 co 25.
```

Expected:

- Ask: co 90 hay co 45, trơn hay ren, vật liệu/brand?

Turn 2:

```text
Co 90 trơn PPR Bình Minh.
```

Expected:

- Update same draft line.
- Candidate SKU narrowed.
- Run checks after SKU selected.

### MT-002 - Làm Rõ Tê Giảm

Turn 1:

```text
Lấy 5 tê giảm.
```

Expected:

- Ask cỡ đầu lớn/đầu nhỏ/nhánh.

Turn 2:

```text
34 xuống 27, PVC.
```

Expected:

- Update attributes: main diameter 34, reduced diameter 27, material PVC.

### MT-003 - Làm Rõ Ống Cuộn Đen

Turn 1:

```text
2 cuộn ống cuộn đen.
```

Expected:

- Ask HDPE/PE hay chỉ quy cách cuộn, đường kính và chiều dài/cuộn.

Turn 2:

```text
HDPE D60, cuộn 100m.
```

Expected:

- Candidate HDPE D60 package cuộn.

## 6. Pass/Fail Cho Agent

| Nhóm | Pass | Fail |
|---|---|---|
| Intent | Phân loại đúng create/price/stock/credit/modify/status/out-of-scope | Tất cả đều biến thành order |
| Extraction | Bóc đúng line và attributes | Mất quantity/diameter/PN/thread |
| Clarification | Hỏi đúng thông tin thiếu | Tự đoán khi thiếu |
| Safety | Không hứa giá/tồn/công nợ khi rule chưa chạy | Tự chốt giá/còn hàng/cho nợ |
| MVP scope | Từ chối nhẹ OCR/voice/Zalo auto/ERP sync | Pretend đã xử lý ngoài scope |
| Audit | Mọi update draft/review đều có event | Không có trace |

## 7. Tài Liệu Cần Có Tiếp Theo

Để bộ test hoàn chỉnh hơn, nên tạo thêm:

- `review_workbench_test_cases.md`: test UI/thao tác Sale Admin chi tiết.
- `agent_customer_question_test_cases.md`: đã bổ sung bộ câu hỏi agent/customer chuyên sâu, gồm DB alias mapping, báo giá/tổng tiền, tư vấn, và sweep 856 alias từ Excel.
- `test_data_seed_matrix.md` hoặc `.xlsx`: seed customers/SKUs/prices/inventory/credit theo từng scenario.

## 8. Exit Criteria

- [ ] 8 user journey tests pass.
- [ ] 104 curated agent/customer scenario IDs pass trong `agent_customer_question_test_cases.md`.
- [ ] 3 multi-turn clarification tests pass.
- [ ] Agent không tự approve, không tự chọn SKU mơ hồ, không tự hứa giá/tồn/công nợ.
- [ ] Mọi journey quan trọng đều trace được sang E2E, extraction, matching và rule test.
- [ ] Không có scenario nào yêu cầu tính năng ngoài scope MVP như OCR, voice, Zalo OA/API, ERP sync thật.
