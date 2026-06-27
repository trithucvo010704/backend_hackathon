# Rule Engine Test Cases - OrderFlow AI MVP

Tài liệu này định nghĩa test cho **Rule Engine** trong OrderFlow AI MVP:

`draft_order_lines đã chọn SKU -> Price Check -> Inventory Check -> Credit Check -> order_holds -> READY_FOR_REVIEW / ON_HOLD`

Nguồn bám sát:

- `local/OrderFlow AI MVP.pdf`: module `Rule Check`, `PriceCheckService`, `InventoryCheckService`, `CreditCheckService`, `HoldService`, `Inventory Reservation`.
- `specs/PROJECT/e2e_demo_scenarios.md`: các luồng `STOCK_HOLD`, `CREDIT_HOLD`, `PRICE_HOLD`, approve và document output.
- `specs/PROJECT/sku_matching_alias_test_cases.md`: đầu vào của rule engine là line đã có `selected_sku_id`.
- `local/dataset_tu_long_ong_nhua_xay_dung_vn.xlsx`: dùng để chọn nhóm hàng test đại diện như ống PVC-U, PP-R/PN, HDPE, co/cút, tê, nối ren, van/vòi, vật tư phụ.

## 1. Ranh Giới Trách Nhiệm

Rule Engine là logic deterministic, không phải AI agent.

Rule Engine được phép:

- Tính đơn giá theo khách hàng, bảng giá, SKU và số lượng.
- Tính thành tiền từng dòng và tổng đơn.
- Kiểm tồn khả dụng.
- Kiểm công nợ và hạn mức.
- Tạo `price_checks`, `inventory_checks`, `credit_checks`.
- Tạo hoặc đóng `order_holds`.
- Cập nhật `draft_orders.status`.
- Khi approve, tạo `inventory_reservations` và tăng `reserved_quantity`.

Rule Engine không được:

- Tự chọn SKU.
- Tự sửa quantity/unit/price nếu không có review action.
- Dùng LLM để quyết định giá, tồn kho hoặc công nợ.
- Cho approve/export pick list khi còn hold mở.
- Tự release hold nếu không có user/action hợp lệ.

## 2. Input và Output Kỳ Vọng

### 2.1. Input

Rule Engine nhận `draft_order` có các line đã match SKU:

```json
{
  "draft_order_id": "uuid",
  "customer_id": "MINH_ANH",
  "warehouse_id": "KHO_CHINH",
  "status": "READY_FOR_REVIEW",
  "lines": [
    {
      "line_id": "L1",
      "selected_sku_id": "BM-PVCU-PIPE-D21",
      "quantity": 10,
      "requested_unit": "cây",
      "manual_unit_price": null,
      "status": "MATCHED"
    }
  ]
}
```

### 2.2. Output

Rule Engine tạo snapshot:

```json
{
  "price_checks": [
    {
      "line_id": "L1",
      "sku_id": "BM-PVCU-PIPE-D21",
      "price_tier": "TIER_A",
      "unit_price": 42000,
      "approval_floor_price": 39000,
      "result": "PASS"
    }
  ],
  "inventory_checks": [
    {
      "line_id": "L1",
      "warehouse_id": "KHO_CHINH",
      "requested_quantity": 10,
      "on_hand_quantity": 100,
      "reserved_quantity": 20,
      "available_quantity": 80,
      "result": "PASS"
    }
  ],
  "credit_checks": [
    {
      "customer_id": "MINH_ANH",
      "current_debt": 12000000,
      "order_amount": 420000,
      "projected_debt": 12420000,
      "credit_limit": 50000000,
      "overdue_amount": 0,
      "result": "PASS"
    }
  ],
  "order_holds": [],
  "draft_order_status": "READY_FOR_REVIEW"
}
```

## 3. Seed Data Chung

### 3.1. Customers và credit profile

| Customer | Price tier | Current debt | Credit limit | Overdue amount | Mục đích |
|---|---|---:|---:|---:|---|
| `MINH_ANH` | `TIER_A` | 12,000,000 | 50,000,000 | 0 | Happy path |
| `AN_PHAT` | `TIER_B` | 28,500,000 | 30,000,000 | 0 | Vượt hạn mức |
| `HOA_PHAT_OVERDUE` | `TIER_B` | 5,000,000 | 80,000,000 | 2,000,000 | Nợ quá hạn |
| `THANH_DAT` | `TIER_SPECIAL` | 10,000,000 | 100,000,000 | 0 | Giá đặc biệt |

### 3.2. SKU groups từ Excel alias dataset

| Nhóm | Canonical/alias từ Excel | Dùng cho rule tests |
|---|---|---|
| PVC-U pipe | `C0001 - Ống PVC-U`, `C0141/C0142/C0147 - phi 21/27/110` | Price, stock, reservation |
| PP-R hot pipe | `C0007 - Ống PP-R`, `C0148 - PN áp lực` | Price by PN, floor price |
| HDPE/PE | `C0009 - Ống HDPE`, `C0010 - HDPE PE100`, `C0150 - Ống cuộn` | High-value order, stock |
| Co/cút | `C0035 - Co 90°`, `C0038 - Co 45°` | Fitting inventory |
| Tê/tê giảm | `C0042 - Tê 90°`, `C0045 - Tê giảm` | Fitting inventory and price |
| Nối ren | `C0028 - Nối ren trong`, `C0029 - Nối ren ngoài` | Unit price/fitting |
| Rắc co | `C0053/C0054/C0055` | Higher-price fitting |
| Van/vòi | `C0077 - Van chặn`, `C0080 - Van bi tay gạt` | Credit/price impact |
| Vật tư phụ | `C0115 - Băng tan`, `C0108 - Keo dán PVC` | Low-value item, pass rules |

### 3.3. Price seed tối thiểu

| SKU code | Product | Tier A | Tier B | Floor price | Notes |
|---|---|---:|---:|---:|---|
| `BM-PVCU-PIPE-D21` | Ống PVC-U D21 Bình Minh | 42,000 | 45,000 | 39,000 | cây |
| `BM-PVCU-PIPE-D110` | Ống thoát PVC-U D110 Bình Minh | 210,000 | 225,000 | 198,000 | cây |
| `PPR-HOT-D25-PN20` | Ống PP-R nóng D25 PN20 | 120,000 | 130,000 | 112,000 | cây |
| `HDPE-PE100-D60` | Ống HDPE PE100 D60 | 350,000 | 370,000 | 330,000 | cây/cuộn theo seed |
| `PVC-CO90-D27` | Co 90 D27 | 8,000 | 9,000 | 7,200 | cái |
| `PVC-TEE-D27` | Tê D27 | 11,000 | 12,000 | 10,000 | cái |
| `PVC-NOI-REN-TRONG-D21` | Nối ren trong D21 | 12,000 | 13,000 | 11,000 | cái |
| `PPR-RACCO-REN-TRONG-D27` | Rắc co ren trong D27 | 65,000 | 70,000 | 60,000 | bộ |
| `VALVE-KHOA-D27` | Van khóa nước D27 | 95,000 | 105,000 | 88,000 | cái |
| `PTFE-BANG-TAN` | Băng tan | 5,000 | 6,000 | 4,000 | cuộn |

### 3.4. Inventory seed tối thiểu

| SKU code | On hand | Reserved | Available | Mục đích |
|---|---:|---:|---:|---|
| `BM-PVCU-PIPE-D21` | 100 | 20 | 80 | Pass |
| `BM-PVCU-PIPE-D110` | 12 | 4 | 8 | Stock hold nếu request 20 |
| `PPR-HOT-D25-PN20` | 50 | 0 | 50 | Pass |
| `HDPE-PE100-D60` | 35 | 0 | 35 | Credit high-value |
| `PVC-CO90-D27` | 100 | 10 | 90 | Pass |
| `PVC-TEE-D27` | 10 | 0 | 10 | Boundary |
| `PVC-NOI-REN-TRONG-D21` | 20 | 0 | 20 | Pass |
| `VALVE-KHOA-D27` | 25 | 0 | 25 | Pass |
| `PTFE-BANG-TAN` | 200 | 0 | 200 | Pass |

## 4. Rule Definitions

### 4.1. Price Check

Input:

- `customer.default_price_tier`
- `sku_prices`
- `draft_order_lines.quantity`
- `draft_order_lines.selected_sku_id`
- `draft_order_lines.manual_unit_price` nếu Sale Admin sửa giá

Expected:

- Nếu không có giá cho SKU/tier: tạo `PRICE_HOLD`.
- Nếu `manual_unit_price < approval_floor_price`: tạo `PRICE_HOLD`.
- Nếu giá hợp lệ: lưu `price_checks`, set `unit_price`, `line_amount`.

### 4.2. Inventory Check

Input:

- `warehouse_id`
- `selected_sku_id`
- `requested_quantity`
- `inventory_balances.on_hand_quantity`
- `inventory_balances.reserved_quantity`

Formula:

```text
available_quantity = on_hand_quantity - reserved_quantity
```

Expected:

- Nếu `available_quantity >= requested_quantity`: PASS.
- Nếu `available_quantity < requested_quantity`: tạo `STOCK_HOLD`.
- Khi approve: tạo `inventory_reservations` và tăng `reserved_quantity`.

### 4.3. Credit Check

Input:

- `customer_credit_profiles.current_debt`
- `customer_credit_profiles.credit_limit`
- `customer_credit_profiles.overdue_amount`
- `draft_orders.total_amount`

Formula:

```text
projected_debt = current_debt + draft_order.total_amount
```

Expected:

- Nếu `overdue_amount > 0`: tạo `CREDIT_HOLD`.
- Nếu `projected_debt > credit_limit`: tạo `CREDIT_HOLD`.
- Nếu không vi phạm: PASS.

### 4.4. Hold State

| Hold type | Khi tạo | Ai xử lý |
|---|---|---|
| `PRICE_HOLD` | Giá thiếu, giá dưới sàn, giá đặc biệt cần duyệt | Manager/Sale Admin theo policy demo |
| `STOCK_HOLD` | Không đủ tồn khả dụng | Sale Admin sửa qty/đổi SKU/reject line |
| `CREDIT_HOLD` | Vượt hạn mức hoặc có nợ quá hạn | Manager/Kế toán |
| `CLARIFICATION_HOLD` | Thiếu SKU/thuộc tính từ bước trước | Sale Admin, không thuộc Rule Engine chính |

## 5. Test Case Format

| Field | Ý nghĩa |
|---|---|
| `ID` | Mã test |
| `Purpose` | Rule cần chứng minh |
| `Precondition` | Draft order, selected SKU, seed data |
| `Action` | API hoặc thao tác |
| `Expected checks` | Price/inventory/credit result |
| `Expected holds` | Hold tạo/không tạo |
| `Expected status` | Status order/line |
| `Must not` | Điều không được xảy ra |

## 6. Test Cases Luồng Chính

### RULE-E2E-001 - Happy Path Pass Cả 3 Rule

**Purpose:** Đơn đã match SKU rõ, đủ tồn, không vượt công nợ, giá hợp lệ.

**Precondition**

Customer: `MINH_ANH`

| Line | SKU | Quantity | Unit |
|---|---|---:|---|
| ống nhựa trắng phi 21 Bình Minh | `BM-PVCU-PIPE-D21` | 10 | cây |
| cút vuông 27 | `PVC-CO90-D27` | 5 | cái |
| chữ T 27 | `PVC-TEE-D27` | 3 | cái |
| nối ren trong 21 | `PVC-NOI-REN-TRONG-D21` | 2 | cái |

**Action**

`POST /api/draft-orders/{id}/run-checks`

**Expected checks**

| Check | Expected |
|---|---|
| Price | PASS, unit price theo `TIER_A` |
| Inventory | PASS cho tất cả line |
| Credit | PASS |

**Expected holds:** none.

**Expected status:** `READY_FOR_REVIEW`.

**Must not**

- Không tạo hold nếu tất cả rule pass.
- Không tạo inventory reservation trước khi approve.

### RULE-PRICE-001 - Giá Theo Tier Khách Hàng

**Purpose:** Kiểm tra hệ thống lấy đúng bảng giá theo `customer.default_price_tier`.

**Precondition**

Same SKU `BM-PVCU-PIPE-D21`:

| Customer | Tier | Expected unit price |
|---|---|---:|
| `MINH_ANH` | `TIER_A` | 42,000 |
| `AN_PHAT` | `TIER_B` | 45,000 |

**Action**

Run checks cho hai draft order giống nhau, khác customer.

**Expected**

- `price_checks.price_tier` đúng từng customer.
- `draft_order_lines.unit_price` khác nhau theo tier.

**Must not**

- Không dùng giá mặc định cố định cho mọi khách.

### RULE-PRICE-002 - Giá Dưới Sàn Tạo `PRICE_HOLD`

**Purpose:** Nếu Sale Admin sửa giá dưới floor, hệ thống phải chặn.

**Precondition**

Customer: `THANH_DAT`

| Line | SKU | Quantity | Manual unit price | Floor |
|---|---|---:|---:|---:|
| ống PPR nóng D25 PN20 | `PPR-HOT-D25-PN20` | 15 | 105,000 | 112,000 |

**Action**

1. `PATCH /api/draft-order-lines/{lineId}` set `unit_price = 105000`.
2. `POST /api/draft-orders/{id}/run-checks`.

**Expected checks**

| Check | Expected |
|---|---|
| Price | FAIL |
| Inventory | PASS if stock enough |
| Credit | PASS if customer within limit |

**Expected hold**

| Field | Expected |
|---|---|
| `hold_type` | `PRICE_HOLD` |
| `reason` | manual price below approval floor |
| `status` | `OPEN` |

**Expected status:** `ON_HOLD`.

**Must not**

- Không approve nếu `PRICE_HOLD` còn open.

### RULE-PRICE-003 - Thiếu Giá Cho SKU/Tier

**Purpose:** Không có giá thì không được tự đoán.

**Precondition**

`sku_prices` thiếu giá cho `VALVE-KHOA-D27` ở `TIER_SPECIAL`.

**Action**

Run checks cho customer `THANH_DAT`, line `VALVE-KHOA-D27`.

**Expected**

- `price_checks.result = FAIL`.
- Tạo `PRICE_HOLD` với reason `missing price for tier`.
- Không dùng giá từ tier khác nếu không có policy fallback rõ.

**Must not**

- Không tự lấy giá `TIER_A` hoặc `TIER_B` thay thế.

### RULE-STOCK-001 - Tồn Khả Dụng Bằng Số Lượng Yêu Cầu

**Purpose:** Boundary condition: available equals requested thì pass.

**Precondition**

SKU `PVC-TEE-D27`: on hand 10, reserved 0, available 10.

Line request: 10 cái `PVC-TEE-D27`.

**Expected**

- `inventory_checks.result = PASS`.
- Không tạo `STOCK_HOLD`.

### RULE-STOCK-002 - Không Đủ Tồn Tạo `STOCK_HOLD`

**Purpose:** Chặn đơn thiếu tồn.

**Precondition**

SKU `BM-PVCU-PIPE-D110`: on hand 12, reserved 4, available 8.

Line request: 20 cây.

**Action**

Run checks.

**Expected**

| Field | Expected |
|---|---|
| `inventory_checks.result` | FAIL |
| `requested_quantity` | 20 |
| `available_quantity` | 8 |
| `order_holds.hold_type` | `STOCK_HOLD` |
| `draft_orders.status` | `ON_HOLD` |

**Must not**

- Không cho generate pick list khi hold còn open.

### RULE-STOCK-003 - Reserved Quantity Phải Trừ Khỏi Available

**Purpose:** Tồn khả dụng không được chỉ dùng on-hand.

**Precondition**

SKU `BM-PVCU-PIPE-D21`: on hand 100, reserved 95, available 5.

Line request: 10 cây.

**Expected**

- FAIL với `STOCK_HOLD`.
- Reason phải nêu available 5, không phải on hand 100.

**Must not**

- Không pass chỉ vì on-hand đủ.

### RULE-STOCK-004 - Sửa Quantity Rồi Rerun Clears Hold

**Purpose:** Sau khi Sale Admin sửa line, rule check tạo snapshot mới và có thể đóng hold.

**Precondition**

Từ `RULE-STOCK-002`, order đang có `STOCK_HOLD`.

**Action**

1. Sale Admin sửa quantity từ 20 xuống 8.
2. `POST /api/draft-orders/{id}/run-checks`.

**Expected**

- Inventory check mới PASS.
- `STOCK_HOLD` cũ được `RESOLVED` hoặc `CLOSED`.
- `review_actions` có `EDIT_QTY`.
- `audit_events` ghi quantity change và hold resolution.

### RULE-CREDIT-001 - Trong Hạn Mức Công Nợ

**Purpose:** Customer good standing pass credit.

**Precondition**

Customer `MINH_ANH`:

| current debt | order amount | credit limit | overdue |
|---:|---:|---:|---:|
| 12,000,000 | 1,000,000 | 50,000,000 | 0 |

**Expected**

- `projected_debt = 13,000,000`.
- `credit_checks.result = PASS`.
- Không `CREDIT_HOLD`.

### RULE-CREDIT-002 - Vượt Hạn Mức Tạo `CREDIT_HOLD`

**Purpose:** Chặn rủi ro tín dụng.

**Precondition**

Customer `AN_PHAT`:

| current debt | order amount | credit limit | overdue |
|---:|---:|---:|---:|
| 28,500,000 | 3,000,000 | 30,000,000 | 0 |

**Expected**

- `projected_debt = 31,500,000`.
- `credit_checks.result = FAIL`.
- Tạo `CREDIT_HOLD`.
- Order status `ON_HOLD`.

**Must not**

- Không approve nếu `CREDIT_HOLD` còn open.

### RULE-CREDIT-003 - Nợ Quá Hạn Tạo `CREDIT_HOLD` Dù Chưa Vượt Hạn Mức

**Purpose:** Overdue là điều kiện chặn riêng.

**Precondition**

Customer `HOA_PHAT_OVERDUE`:

| current debt | order amount | credit limit | overdue |
|---:|---:|---:|---:|
| 5,000,000 | 1,000,000 | 80,000,000 | 2,000,000 |

**Expected**

- `projected_debt = 6,000,000`, vẫn dưới limit.
- `credit_checks.result = FAIL`.
- Tạo `CREDIT_HOLD` reason `overdue_amount > 0`.

### RULE-MULTI-001 - Nhiều Hold Cùng Lúc

**Purpose:** Một đơn có thể vừa thiếu tồn vừa vượt công nợ.

**Precondition**

Customer `AN_PHAT`.

| Line | SKU | Quantity | Issue |
|---|---|---:|---|
| Ống thoát D110 | `BM-PVCU-PIPE-D110` | 20 | available only 8 |
| Van khóa D27 | `VALVE-KHOA-D27` | 20 | pushes debt above limit |

**Expected**

- Tạo `STOCK_HOLD`.
- Tạo `CREDIT_HOLD`.
- Không được chỉ tạo hold đầu tiên rồi bỏ qua hold còn lại.
- Order status `ON_HOLD`.

**Must not**

- Không cho approve nếu còn bất kỳ hold open nào.

### RULE-MULTI-002 - Price Hold và Stock Hold Cùng Lúc

**Purpose:** Giá dưới sàn và thiếu tồn đều phải được ghi nhận.

**Precondition**

Line `PPR-HOT-D25-PN20`:

- Quantity 60, available 50.
- Manual price 105,000, floor 112,000.

**Expected**

- `PRICE_HOLD`.
- `STOCK_HOLD`.
- Cả hai check snapshot được lưu.

### RULE-REVIEW-001 - Release Hold Cần User Và Note

**Purpose:** Hold release phải có audit trail.

**Precondition**

Order có `PRICE_HOLD` open.

**Action**

`POST /api/order-holds/{holdId}/release`

Payload:

```json
{
  "released_by": "manager_01",
  "note": "Đã duyệt giá đặc biệt cho khách Thành Đạt"
}
```

**Expected**

- Hold status `RELEASED`.
- `review_actions` có `RELEASE_HOLD`.
- `audit_events` ghi user, role, note, timestamp.

**Must not**

- Không cho release nếu note trống với hold nghiêm trọng.

### RULE-APPROVAL-001 - Không Approve Khi Còn Hold Open

**Purpose:** Bảo vệ nguyên tắc "đơn có hold không xuống kho".

**Precondition**

Order có `STOCK_HOLD` open.

**Action**

`POST /api/draft-orders/{id}/approve`

**Expected**

- API trả lỗi nghiệp vụ.
- `draft_orders.status` vẫn `ON_HOLD`.
- Không tạo `inventory_reservations`.

### RULE-APPROVAL-002 - Approve Tạo Reservation

**Purpose:** Khi đơn pass hết rule, approve phải giữ hàng.

**Precondition**

Order từ `RULE-E2E-001`, không hold open.

**Action**

`POST /api/draft-orders/{id}/approve`

**Expected**

- `draft_orders.status = APPROVED`.
- Tạo `inventory_reservations` status `ACTIVE` cho từng line.
- `inventory_balances.reserved_quantity` tăng đúng bằng quantity approved.
- `audit_events` ghi approve và reserve.

**Must not**

- Không trừ `on_hand_quantity` thực tế trong MVP nếu spec chỉ reserve.

### RULE-APPROVAL-003 - Idempotency Khi Approve Lại

**Purpose:** Gọi approve hai lần không được double reserve.

**Precondition**

Order đã `APPROVED`.

**Action**

Gọi lại `POST /api/draft-orders/{id}/approve`.

**Expected**

- API trả `already approved` hoặc no-op an toàn.
- Không tạo reservation duplicate.
- `reserved_quantity` không tăng lần hai.

### RULE-DOC-001 - Pick List Bị Chặn Khi Còn Hold

**Purpose:** Document output phụ thuộc rule state.

**Precondition**

Order có `CREDIT_HOLD` open.

**Action**

`POST /api/draft-orders/{id}/documents/pick-list`

**Expected**

- API reject.
- Không tạo `draft_order_documents` loại pick list.

### RULE-DOC-002 - Quote Có Thể Tạo Sau Review Hợp Lệ

**Purpose:** Quote/pick list chỉ sinh khi đơn hợp lệ theo policy MVP.

**Precondition**

Order `APPROVED`, không hold open.

**Action**

`POST /api/draft-orders/{id}/documents/quote`

**Expected**

- Tạo HTML snapshot trong `draft_order_documents`.
- Nội dung dùng giá và line amount từ `price_checks`/`draft_order_lines`.

### RULE-DATA-001 - Line Chưa Có Selected SKU Không Được Rule Check Như Hàng Hợp Lệ

**Purpose:** Rule engine không thay SKU matching.

**Precondition**

Line status `PENDING_MATCH`, `selected_sku_id = null`.

**Action**

Run checks.

**Expected**

- Không tạo price/inventory check cho line thiếu SKU.
- Tạo hoặc giữ `CLARIFICATION_HOLD`.
- Order không `READY_FOR_REVIEW`.

**Must not**

- Không tự chọn SKU.

### RULE-DATA-002 - Quantity Null Hoặc <= 0

**Purpose:** Không chạy rule tài chính/tồn kho trên số lượng không hợp lệ.

**Precondition**

Line có SKU nhưng quantity null, 0 hoặc âm.

**Expected**

- Tạo `CLARIFICATION_HOLD` hoặc validation error.
- Không tính line_amount âm.
- Không tạo reservation.

### RULE-UNIT-001 - Unit Không Khớp Sell Unit

**Purpose:** Nếu SKU bán theo cây nhưng line request theo mét/cuộn chưa có conversion, cần review.

**Precondition**

SKU `BM-PVCU-PIPE-D21` sell unit `cây`; line request `20 mét`.

**Expected**

- Nếu MVP chưa có unit conversion: tạo `CLARIFICATION_HOLD` reason `unit conversion required`.
- Nếu có conversion seed: convert và ghi rõ trong check/audit.

**Must not**

- Không tự coi `20 mét = 20 cây`.

### RULE-REGRESSION-001 - Vật Tư Phụ Low-Value Pass

**Purpose:** Vật tư phụ như băng tan vẫn đi qua price/inventory/credit như SKU thường.

**Precondition**

Line `1 cuộn băng tan`, SKU `PTFE-BANG-TAN`.

**Expected**

- Price PASS.
- Inventory PASS.
- Credit PASS.
- Không bỏ qua line vì không phải ống/phụ kiện chính.

### RULE-REGRESSION-002 - Action/Issue Không Phải SKU Không Đi Qua Rule Engine

**Purpose:** Các category `Thi công/cách nói thợ`, `Sự cố/vận hành` không nên tạo rule check như hàng bán.

**Precondition**

Line note: `nhà bị búa nước cần test áp`, no `selected_sku_id`.

**Expected**

- Không price/inventory/credit check.
- Nếu tồn tại trong order, line ở dạng note/context hoặc clarification.

## 7. API Contract Tests

### RULE-API-001 - Run Checks

**Endpoint:** `POST /api/draft-orders/{id}/run-checks`

**Expected**

- Tạo mới check snapshots cho order hiện tại.
- Không overwrite snapshot cũ nếu cần audit; có thể mark latest.
- Cập nhật `draft_orders.total_amount`.
- Cập nhật `draft_orders.status = ON_HOLD` nếu có hold open, else `READY_FOR_REVIEW`.
- Ghi `processing_events`.

### RULE-API-002 - Get Checks

**Endpoint:** `GET /api/draft-orders/{id}/checks`

**Expected response includes:**

- `price_checks`
- `inventory_checks`
- `credit_checks`
- latest/check history flag nếu có

### RULE-API-003 - Get Holds

**Endpoint:** `GET /api/draft-orders/{id}/holds`

**Expected fields:**

| Field | Required |
|---|---|
| `hold_id` | Yes |
| `hold_type` | Yes |
| `status` | Yes |
| `reason` | Yes |
| `line_id` | Optional for order-level credit hold |
| `created_at` | Yes |
| `released_by` | If released |
| `release_note` | If released |

## 8. Database Assertions

| Table | Assertion |
|---|---|
| `price_checks` | Có snapshot cho từng line có SKU; lưu tier, unit price, floor, result |
| `inventory_checks` | Có snapshot requested/on-hand/reserved/available/result |
| `credit_checks` | Có snapshot current debt, order amount, projected debt, limit, overdue |
| `order_holds` | Hold đúng type, status, reason, line/order scope |
| `draft_orders` | Status đúng sau run checks/approve |
| `draft_order_lines` | `unit_price`, `line_amount`, status không bị sửa ngoài rule |
| `inventory_reservations` | Tạo khi approve, không duplicate |
| `processing_events` | Có `RULE_CHECK_STARTED`, `PRICE_CHECK_DONE`, `INVENTORY_CHECK_DONE`, `CREDIT_CHECK_DONE`, `RULE_CHECK_DONE` |
| `review_actions` | Có khi user sửa qty/price/release hold/approve |
| `audit_events` | Ghi thay đổi quan trọng và hold transitions |

## 9. Negative Tests

### RULE-NEG-001 - Không Dùng LLM Để Tính Rule

Rule checks phải chạy được khi AI service tắt, miễn là order đã có selected SKU và seed data.

### RULE-NEG-002 - Không Tự Release Hold Sau Rerun Nếu Chưa Hết Lỗi

Nếu rerun checks nhưng điều kiện vẫn fail, hold phải vẫn open hoặc tạo hold mới tương đương.

### RULE-NEG-003 - Không Double Count Credit

Rerun checks nhiều lần không được cộng order amount nhiều lần vào current debt. `projected_debt` luôn là snapshot tính từ current debt + current order total.

### RULE-NEG-004 - Không Trừ Tồn Thật Khi Mới Tạo Draft

Tạo draft/rule check chỉ kiểm tồn. Chỉ approve mới tạo reservation.

## 10. Exit Criteria

- [ ] Happy path pass cả Price, Inventory, Credit.
- [ ] `PRICE_HOLD` tạo đúng khi giá dưới sàn hoặc thiếu giá.
- [ ] `STOCK_HOLD` tạo đúng khi available < requested.
- [ ] `CREDIT_HOLD` tạo đúng khi projected debt vượt limit hoặc có overdue.
- [ ] Nhiều hold cùng lúc được ghi đầy đủ.
- [ ] Không approve/export pick list khi còn hold open.
- [ ] Release hold yêu cầu user/note và có audit.
- [ ] Approve tạo reservation đúng, không double reserve.
- [ ] Rerun checks tạo snapshot mới và không làm mất audit trail.
- [ ] Rule engine không tự chọn SKU, không tự sửa quantity, không dùng LLM.

