# E2E Demo Scenarios - OrderFlow AI MVP

Tài liệu này mô tả các kịch bản end-to-end dùng để demo và kiểm thử MVP OrderFlow AI. Bộ scenario bám sát:

- `local/OrderFlow AI MVP.pdf`: luồng MVP, API, trạng thái đơn, review workbench, rule check và Definition of Done.
- `local/Bộ dữ liệu sẵn sàng cho ML về từ lóng ống nước và vật liệu nhựa trong xây dựng Việt Nam.pdf`: nguyên tắc normalize từ lóng theo 4 tầng `surface form -> canonical technical term -> material family -> use-case`.
- `local/dataset_tu_long_ong_nhua_xay_dung_vn.xlsx`: 856 alias, 154 canonical terms, đặc biệt các alias mơ hồ như `ống cuộn đen`, `nối thăm`, `kẹp đỡ ống`.

## 1. Mục Tiêu

MVP được coi là pass E2E demo khi chứng minh được:

- Sale Admin có thể tạo đơn nháp từ text thô.
- AI extraction bóc được dòng hàng, số lượng, đơn vị và thuộc tính quan trọng.
- SKU matching trả về candidate có lý do, không tự đoán khi thiếu thông tin.
- Rule engine kiểm giá, tồn kho, công nợ bằng logic deterministic.
- Review Workbench cho phép Sale Admin sửa dòng hàng, chọn SKU, xử lý hold, approve.
- Hệ thống tạo được HTML preview báo giá hoặc phiếu lấy hàng.
- `processing_events`, `review_actions`, `audit_events` ghi lại toàn bộ bước quan trọng.

## 2. Seed Data Chung Cho Demo

### 2.1. Users và role

| Code | Role | Ghi chú |
|---|---|---|
| `sale_admin_01` | `SALE_ADMIN` | Người tạo, review, approve đơn thường |
| `manager_01` | `MANAGER` | Người release hold nghiêm trọng nếu demo cần |
| `system` | `SYSTEM` | Ghi processing/audit event |

### 2.2. Customers

| Customer | Price tier | Credit profile | Ghi chú |
|---|---|---|---|
| `MINH_ANH` | `TIER_A` | Hạn mức đủ, không quá hạn | Dùng cho đơn pass |
| `AN_PHAT` | `TIER_B` | Hạn mức thấp hoặc có nợ quá hạn | Dùng cho `CREDIT_HOLD` |
| `THANH_DAT` | `TIER_SPECIAL` | Có giá đặc biệt cần duyệt | Dùng cho `PRICE_HOLD` |

### 2.3. Warehouse

| Warehouse | Ghi chú |
|---|---|
| `KHO_CHINH` | 1 kho chính cho MVP |

### 2.4. SKU seed tối thiểu

MVP có thể dùng 20-50 SKU. Để chạy các scenario dưới đây, seed cần có ít nhất các nhóm SKU sau:

| Nhóm SKU | Alias/canonical liên quan từ dataset | Mục đích test |
|---|---|---|
| Ống PVC-U/PVC trắng D21/D27/D34/D90/D110 | `C0001`, `C0141`, `C0142`, `C0143`, `C0146`, `C0147` | Ống cấp/thoát, size phổ biến |
| Ống PP-R nóng/lạnh, PN10/PN16/PN20 | `C0007`, `C0148` | `ống nóng`, `ống nhiệt`, PN |
| Ống HDPE/PE cuộn | `C0009`, `C0150` | Case `ống cuộn đen` mơ hồ |
| Co 90, co 45/cút lơi | `C0035`, `C0038` | `co`, `cút`, `lơi`, `chếch` |
| Tê đều, tê giảm | `C0042`, `C0045` | `tê`, `chữ T`, `ba chạc`, `tê giảm` |
| Nối ren trong/ren ngoài | `C0028`, `C0029`, `C0030`, `C0031` | `ren trong`, `ren ngoài`, `đầu cái`, `đầu đực` |
| Rắc co/zắc co | `C0053`, `C0054`, `C0055` | Từ lóng tháo lắp |
| Nối thông tắc/nối thẳng thăm | `C0066`, `C0070` | Case `nối thăm` mơ hồ |
| Van bi/van khóa/khóa nước | `C0077`, `C0080`, `C0081` | Case van/vòi |
| Kẹp/cùm ống | `C0106`, `C0114` | Case `kẹp đỡ ống` mơ hồ |

## 3. Quy Ước Pass/Fail

| Nhóm | Pass khi | Fail khi |
|---|---|---|
| Extraction | Bóc đúng dòng hàng, quantity, unit, alias chính và missing attributes | Gộp sai dòng, mất quantity, tự thêm thuộc tính không có trong text |
| Matching | Candidate đúng nằm trong Top-3; có `match_reason` rõ | Không trả candidate đúng hoặc match sang category sai |
| Ambiguity | Case mơ hồ tạo `PENDING_MATCH` hoặc `NEEDS_CLARIFICATION` | Auto-select SKU duy nhất khi dữ liệu chưa đủ |
| Rule check | Tạo hold đúng: `STOCK_HOLD`, `CREDIT_HOLD`, `PRICE_HOLD` | Bỏ sót hold hoặc để đơn có hold xuống kho |
| Review | Sale Admin chọn/sửa/release/approve được và có log | Thay đổi không ghi `review_actions`/`audit_events` |
| Output | Chỉ tạo quote/pick list khi đơn đủ điều kiện | Tạo pick list cho đơn còn hold mở |

## 4. API Sequence Chuẩn

Áp dụng cho hầu hết scenario:

1. `POST /api/auth/login`
2. `GET /api/customers`
3. `GET /api/customers/{id}/projects`
4. `GET /api/warehouses`
5. `POST /api/draft-orders/from-text`
6. Backend tạo `raw_order_texts`, `draft_orders`, `processing_events`
7. Backend chạy AI extraction, tạo `draft_order_lines`
8. Backend chạy SKU matching, tạo `sku_candidates`
9. `POST /api/draft-orders/{id}/run-checks`
10. `GET /api/draft-orders/{id}`
11. Sale Admin review: `PATCH`, `select-sku`, `release hold` nếu cần
12. `POST /api/draft-orders/{id}/approve`
13. `POST /api/draft-orders/{id}/documents/quote` hoặc `pick-list`
14. `GET /api/draft-orders/{id}/processing-events`
15. `GET /api/draft-orders/{id}/audit-events`
16. `GET /api/draft-orders/{id}/review-actions`

## 5. Scenario E2E-01 - Đơn Rõ, Match Tốt, Không Hold

**Mục tiêu:** Chứng minh happy path: từ text thô tới đơn nháp sẵn sàng review, approve và tạo báo giá.

**Customer:** `MINH_ANH`

**Raw order text:**

```text
Anh lấy cho công trình Quận 7:
10 cây ống nhựa trắng phi 21 Bình Minh,
5 cút vuông 27,
3 chữ T 27,
2 nối ren trong 21,
giao sáng mai.
```

**Alias cần nhận diện:**

| Surface form | Expected canonical direction |
|---|---|
| `ống nhựa trắng` | `C0001 - Ống PVC-U` |
| `phi 21` | `C0141 - Ống phi 21` |
| `cút vuông` | `C0035 - Co 90°` |
| `27` | `C0142 - Ống phi 27` / diameter D27 |
| `chữ T` | `C0042 - Tê 90°` |
| `nối ren trong` | `C0028 - Nối thẳng ren trong` |

**Expected extraction:**

| Line | item_description | quantity | unit | extracted attributes |
|---|---|---:|---|---|
| 1 | ống nhựa trắng phi 21 Bình Minh | 10 | cây | material/category PVC-U, brand Bình Minh, diameter 21 |
| 2 | cút vuông 27 | 5 | cái | fitting type co 90, diameter 27 |
| 3 | chữ T 27 | 3 | cái | fitting type tê/T 90, diameter 27 |
| 4 | nối ren trong 21 | 2 | cái | fitting type nối ren trong, diameter 21 |

**Expected SKU behavior:**

- Mỗi line có candidate đúng trong Top-3.
- Với seed đủ rõ, hệ thống có thể auto mark `MATCHED`.
- Không được tự invent brand cho line 2-4 nếu text không nói rõ; có thể kế thừa brand từ context nếu prompt/rule được thiết kế rõ, nhưng phải ghi `match_reason`.

**Expected rules:**

- `price_checks`: PASS.
- `inventory_checks`: PASS.
- `credit_checks`: PASS.
- Không có open hold.

**Expected final states:**

| Entity | Expected |
|---|---|
| `draft_orders.status` | `READY_FOR_REVIEW` -> `APPROVED` -> `EXPORTED` nếu tạo document |
| `draft_order_lines.status` | `MATCHED` -> `APPROVED` |
| `order_holds` | Không có hold mở |
| `inventory_reservations` | Tạo khi approve |
| `draft_order_documents` | Có quote HTML preview |

**Audit expectations:**

- Có event `AI_EXTRACTION_STARTED/DONE`.
- Có event `SKU_MATCHING_STARTED/DONE`.
- Có event `RULE_CHECK_DONE`.
- Có review action `APPROVE_ORDER`.
- Có audit event khi tạo quote.

## 6. Scenario E2E-02 - Thiếu Thuộc Tính, Cần Clarification

**Mục tiêu:** Chứng minh AI không đoán bừa khi khách đặt kiểu thiếu thông tin.

**Customer:** `MINH_ANH`

**Raw order text:**

```text
Cho anh 20 cây ống nóng 25, 10 co, 5 tê giảm giống hôm trước.
Giao công trình cũ, chiều nay nếu kịp.
```

**Alias cần nhận diện:**

| Surface form | Expected canonical direction |
|---|---|
| `ống nóng` | `C0007 - Ống PP-R` hoặc subtype hot-water PP-R |
| `co` | Có thể là `C0035 - Co 90°`, `C0038 - Co 45°`, hoặc co ren/giảm tùy context |
| `tê giảm` | `C0045 - Tê giảm` |
| `giống hôm trước` | Cần lookup lịch sử đơn mẫu theo customer/project |
| `công trình cũ` | Cần map sang project gần nhất hoặc hỏi lại |

**Expected extraction:**

| Line | Expected result |
|---|---|
| 1 | Bóc `ống nóng 25`, quantity 20, unit cây, material PP-R/hot-water, diameter 25 nếu seed hỗ trợ |
| 2 | Bóc `co`, quantity 10, unit cái, thiếu diameter/type |
| 3 | Bóc `tê giảm giống hôm trước`, quantity 5, unit cái, fitting type tê giảm, thiếu cỡ đầu lớn/đầu nhỏ nếu không có history |

**Expected holds:**

| Hold | Reason |
|---|---|
| `CLARIFICATION_HOLD` | `co` thiếu đường kính, góc 90/45, ren/trơn |
| `CLARIFICATION_HOLD` | `tê giảm` thiếu cỡ nhánh giảm nếu history không đủ |
| `CLARIFICATION_HOLD` | `công trình cũ` không xác định được project duy nhất |

**Expected clarification questions:**

- "`co` là co 90 hay co 45, đường kính bao nhiêu, trơn hay ren?"
- "`tê giảm` cần cỡ nào, ví dụ 34-27 hay 27-21?"
- "`công trình cũ` là công trình nào của khách Minh Anh?"

**Expected final states before user fixes:**

| Entity | Expected |
|---|---|
| `draft_orders.status` | `NEEDS_CLARIFICATION` hoặc `ON_HOLD` |
| `draft_order_lines.status` | Có line `NEEDS_CLARIFICATION` hoặc `PENDING_MATCH` |
| `order_holds.hold_type` | `CLARIFICATION_HOLD` |

**Review action to complete demo:**

Sale Admin bổ sung:

```text
co 90 phi 25 trơn; tê giảm 34-25; công trình Quận 7
```

Sau khi sửa hoặc rerun:

- Lines chuyển sang `MATCHED`.
- Rule checks chạy lại.
- Nếu pass, order chuyển `READY_FOR_REVIEW`.

## 7. Scenario E2E-03 - SKU Mơ Hồ, Candidate Top-3, Không Auto-Select

**Mục tiêu:** Chứng minh hệ thống xử lý alias mơ hồ từ Excel dataset mà không tự tin quá mức.

**Customer:** `MINH_ANH`

**Raw order text:**

```text
Lấy giúp anh:
2 cuộn ống cuộn đen,
4 nối thăm 90,
50 kẹp đỡ ống,
giao kho chính trong hôm nay.
```

**Alias mơ hồ bắt buộc test:**

| Alias | Candidate hợp lý |
|---|---|
| `ống cuộn đen` | `C0009 - Ống HDPE`; `C0150 - Ống cuộn` |
| `nối thăm` | `C0066 - Nối thông tắc`; `C0070 - Nối thẳng thăm` |
| `kẹp đỡ ống` | `C0106 - Kẹp đỡ ống điện`; `C0114 - Cùm ống` |

**Expected SKU behavior:**

- Không line nào trong 3 line trên được auto-select nếu thiếu material/application/diameter.
- Mỗi line phải có ít nhất 2 candidate.
- `match_reason` phải giải thích vì sao mơ hồ:
  - `ống cuộn đen`: vừa là vật liệu HDPE/PE vừa là quy cách đóng gói.
  - `nối thăm`: có thể là phụ kiện kiểm tra/thông tắc hoặc nối thẳng thăm.
  - `kẹp đỡ ống`: có thể là phụ kiện ống điện hoặc cùm treo/cố định ống.

**Expected holds:**

| Line | Hold |
|---|---|
| ống cuộn đen | `CLARIFICATION_HOLD` |
| nối thăm 90 | `CLARIFICATION_HOLD` nếu không xác định kiểu nối/thông tắc |
| kẹp đỡ ống | `CLARIFICATION_HOLD` |

**Expected final states:**

| Entity | Expected |
|---|---|
| `draft_orders.status` | `NEEDS_CLARIFICATION` |
| `draft_order_lines.status` | `PENDING_MATCH` |
| `sku_candidates` | Có Top-3 hoặc ít nhất Top-2 cho từng line |

**Pass condition đặc biệt:**

Nếu hệ thống auto chọn một SKU duy nhất cho `ống cuộn đen`, `nối thăm`, hoặc `kẹp đỡ ống`, scenario fail.

## 8. Scenario E2E-04 - Không Đủ Tồn Kho, Tạo `STOCK_HOLD`

**Mục tiêu:** Chứng minh rule engine chặn đơn trước khi xuống kho.

**Customer:** `MINH_ANH`

**Seed inventory:** SKU ống thoát/PVC D110 chỉ có `available_quantity = 8 cây`.

**Raw order text:**

```text
Công trình Nhà Bè cần gấp:
20 cây ống thoát 110,
10 co cong 88,
5 đầu thông tắc 110.
Giao trong chiều nay.
```

**Alias cần nhận diện:**

| Surface form | Expected canonical direction |
|---|---|
| `ống thoát 110` | `C0017 - Ống thoát nước` + `C0147 - Ống phi 110` |
| `co cong 88` | `C0040 - Co cong 88°` |
| `đầu thông tắc` | `C0066 - Nối thông tắc` |

**Expected extraction and matching:**

- Candidate đúng nằm trong Top-3 cho cả 3 line.
- Nếu thiếu brand, có thể dùng candidate theo default catalogue nhưng không được mất size D110.

**Expected rules:**

| Check | Expected |
|---|---|
| Price | PASS |
| Inventory | FAIL cho line `20 cây ống thoát 110` |
| Credit | PASS |

**Expected hold:**

| Field | Expected |
|---|---|
| `order_holds.hold_type` | `STOCK_HOLD` |
| `reason` | Available 8 < requested 20 |
| `draft_orders.status` | `ON_HOLD` |

**Review paths:**

Demo có thể chọn một trong ba cách:

1. Sale Admin sửa quantity từ 20 xuống 8, rerun checks.
2. Sale Admin đổi SKU sang SKU thay thế đủ tồn, rerun checks.
3. Sale Admin reject line thiếu tồn.

**Expected after fix:**

- Hold được release hoặc closed có lý do.
- `inventory_checks` được tạo snapshot mới.
- `review_actions` ghi `EDIT_QTY` hoặc `SELECT_SKU` hoặc `REJECT_LINE`.

## 9. Scenario E2E-05 - Vượt Công Nợ, Tạo `CREDIT_HOLD`

**Mục tiêu:** Chứng minh đơn có rủi ro tín dụng không được approve/export.

**Customer:** `AN_PHAT`

**Seed credit profile:**

| Field | Value |
|---|---:|
| credit_limit | 30,000,000 |
| current_debt | 28,500,000 |
| overdue_amount | 0 hoặc > 0 tùy demo |
| projected_order_amount | > 1,500,000 |

**Raw order text:**

```text
Anh An Phát lấy:
30 cây ống HDPE PE100,
20 van khóa nước 27,
10 rắc co ren trong 27,
giao công trình Long An tuần này.
```

**Alias cần nhận diện:**

| Surface form | Expected canonical direction |
|---|---|
| `ống HDPE PE100` | `C0010 - Ống HDPE PE100` |
| `van khóa nước` | `C0077 - Van chặn` hoặc `C0080/C0081 - Van bi` tùy seed |
| `rắc co ren trong` | `C0054 - Rắc co ren trong` |
| `27` | `C0142 - Ống phi 27` / diameter D27 |

**Expected rules:**

| Check | Expected |
|---|---|
| Price | PASS |
| Inventory | PASS |
| Credit | FAIL |

**Expected hold:**

| Field | Expected |
|---|---|
| `order_holds.hold_type` | `CREDIT_HOLD` |
| `reason` | projected debt > credit limit hoặc customer có overdue |
| `draft_orders.status` | `ON_HOLD` |

**Expected restriction:**

- `POST /api/draft-orders/{id}/approve` phải fail hoặc trả lỗi nghiệp vụ nếu `CREDIT_HOLD` còn open.
- Không được tạo `inventory_reservations`.
- Không được tạo pick list.

**Review paths:**

- Manager release hold có note: "Đã xác nhận thanh toán trước khi giao".
- Hoặc Sale Admin reject order.

**Audit expectations:**

- Có `CREDIT_CHECK_FAILED`.
- Có `RELEASE_HOLD` hoặc `REJECT_ORDER`.
- Nếu release, audit phải ghi user role và note.

## 10. Scenario E2E-06 - Giá Đặc Biệt, Tạo `PRICE_HOLD`

**Mục tiêu:** Chứng minh rule giá không để Sale Admin tự chốt giá dưới sàn mà không có review.

**Customer:** `THANH_DAT`

**Seed pricing:**

| Field | Value |
|---|---|
| default price tier | `TIER_SPECIAL` |
| approval_floor_price | Giá sàn theo SKU |
| requested/manual price | Thấp hơn floor |

**Raw order text:**

```text
Khách Thành Đạt hỏi:
15 cây ống PPR nóng PN20 phi 21,
10 co ren cái 21,
giá như lần trước giảm sâu giúp anh.
```

**Alias cần nhận diện:**

| Surface form | Expected canonical direction |
|---|---|
| `ống PPR nóng` | `C0007 - Ống PP-R` + hot-water subtype |
| `PN20` | `C0148 - PN áp lực` |
| `phi 21` | `C0141 - Ống phi 21` |
| `co ren cái` | `C0036 - Co 90° ren trong` nếu góc 90 default/seed rõ, nếu không thì clarification |
| `giá như lần trước giảm sâu` | cần check price tier/lịch sử giá, có thể trigger price review |

**Expected behavior:**

- Extraction phải bóc `PN20`, `phi 21`, `PPR nóng`.
- Nếu `co ren cái` thiếu góc hoặc material thì có thể tạo `CLARIFICATION_HOLD`.
- Khi Sale Admin nhập/sửa unit price thấp hơn floor, rule tạo `PRICE_HOLD`.

**Expected hold:**

| Field | Expected |
|---|---|
| `order_holds.hold_type` | `PRICE_HOLD` |
| `reason` | unit price < approval floor hoặc giá đặc biệt cần manager approve |
| `draft_orders.status` | `ON_HOLD` |

**Review paths:**

- Manager release `PRICE_HOLD` với note.
- Sale Admin chỉnh giá về mức hợp lệ và rerun checks.

**Pass condition:**

Đơn không được approve khi `PRICE_HOLD` còn open.

## 11. Scenario E2E-07 - Repeat Order Dựa Trên Lịch Sử Mẫu

**Mục tiêu:** Chứng minh scope "lấy giống đơn trước" ở mức proof-of-flow.

**Customer:** `MINH_ANH`

**Seed order history:**

Đơn gần nhất của `MINH_ANH` tại `CT_Q7` có:

- 10 cây ống PVC-U D21 Bình Minh.
- 5 co 90 D27.
- 3 tê giảm 34-27.

**Raw order text:**

```text
Anh lấy lại giống đơn Quận 7 hôm trước,
nhưng tăng ống lên 15 cây, co giữ nguyên,
tê giảm lấy 5 cái.
Giao sáng mai.
```

**Expected extraction:**

- Nhận diện intent repeat order.
- Nhận diện project/history reference: `Quận 7 hôm trước`.
- Bóc override quantity:
  - ống: 15 cây.
  - co: giữ nguyên 5 cái.
  - tê giảm: 5 cái.

**Expected SKU behavior:**

- SKU lấy từ history phải được đánh dấu `source = HISTORY`.
- Candidate vẫn cần hiển thị để Sale Admin review.
- Nếu nhiều lịch sử phù hợp, tạo `CLARIFICATION_HOLD`.

**Expected final states:**

| Entity | Expected |
|---|---|
| `draft_order_lines` | Tạo từ history + override |
| `sku_candidates.match_reason` | "Matched from previous order CT_Q7" hoặc tương đương |
| `processing_events` | Có event lookup lịch sử |

**Pass condition đặc biệt:**

Không được tự tạo repeat order nếu không tìm được đúng lịch sử đơn theo customer/project/time hint.

## 12. Scenario E2E-08 - Output Document Chỉ Khi Đơn Hợp Lệ

**Mục tiêu:** Chứng minh quote/pick list không được sinh tùy tiện khi đơn còn hold.

**Precondition:**

Dùng lại một đơn từ `E2E-04`, `E2E-05`, hoặc `E2E-06` đang có hold open.

**Actions:**

1. Gọi `POST /api/draft-orders/{id}/documents/pick-list` khi hold còn open.
2. Gọi `POST /api/draft-orders/{id}/approve` khi hold còn open.
3. Release/fix hold hợp lệ.
4. Approve order.
5. Generate quote.
6. Generate pick list.

**Expected behavior:**

| Action | Expected |
|---|---|
| Generate pick list while hold open | Reject với business error |
| Approve while hold open | Reject với business error |
| Generate quote after approve | PASS |
| Generate pick list after approve | PASS |

**Expected document content:**

- Header có order no, customer, project, warehouse.
- Table có SKU code, product name, quantity, unit, unit price, amount.
- Pick list có warehouse, SKU, quantity to pick.
- Document HTML snapshot được lưu trong `draft_order_documents`.

## 13. Demo Run Order Khuyến Nghị

Thứ tự demo nên chạy để kể chuyện mượt:

1. `E2E-01` - Happy path: cho thấy tốc độ.
2. `E2E-03` - Alias mơ hồ: cho thấy AI không đoán bừa.
3. `E2E-04` - Stock hold: cho thấy rule engine bảo vệ vận hành.
4. `E2E-05` - Credit hold: cho thấy kiểm soát rủi ro tài chính.
5. `E2E-07` - Repeat order: cho thấy khả năng mở rộng workflow thực tế.

Nếu thời gian chỉ có 5 phút, dùng 3 scenario:

1. `E2E-01`
2. `E2E-03`
3. `E2E-04` hoặc `E2E-05`

## 14. Checklist Definition Of Done Cho E2E Demo

- [ ] Login được bằng `SALE_ADMIN`.
- [ ] Tạo draft order từ raw text.
- [ ] `raw_order_texts` lưu text gốc.
- [ ] AI extraction tạo `draft_order_lines`.
- [ ] SKU matching tạo `sku_candidates`.
- [ ] Case rõ có candidate đúng Top-3.
- [ ] Case mơ hồ không auto-select.
- [ ] Rule checks tạo đúng snapshot.
- [ ] Holds tạo đúng loại và lý do.
- [ ] Review Workbench sửa line/chọn SKU/release hold được.
- [ ] Approve tạo `inventory_reservations`.
- [ ] Đơn còn hold không được approve hoặc generate pick list.
- [ ] Generate quote/pick list HTML được sau khi hợp lệ.
- [ ] `processing_events` ghi stage AI/matching/rules.
- [ ] `review_actions` ghi thao tác người dùng.
- [ ] `audit_events` ghi thay đổi quan trọng.

