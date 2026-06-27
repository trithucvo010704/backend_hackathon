# SKU Matching Alias Test Cases - OrderFlow AI MVP

Tài liệu này định nghĩa test cho bước **SKU Matching** trong OrderFlow AI MVP:

`draft_order_lines + extracted_attributes + product_aliases/product_skus -> sku_candidates -> MATCHED / PENDING_MATCH / CLARIFICATION_HOLD`

Nguồn bám sát:

- `local/OrderFlow AI MVP.pdf`: module `SKU Matching`, API `POST /api/draft-order-lines/{lineId}/match-skus`, `GET /api/draft-order-lines/{lineId}/sku-candidates`, `POST /api/draft-order-lines/{lineId}/select-sku`.
- `local/dataset_tu_long_ong_nhua_xay_dung_vn.xlsx`: sheet `Alias_Map` có 856 alias rows, 154 canonical terms, 853 unique alias strings, 43 categories.
- `specs/PROJECT/e2e_demo_scenarios.md`: các luồng E2E cần matching đi qua.
- `specs/PROJECT/ai_extraction_test_cases.md`: extraction output đầu vào cho matching.

## 1. Mục Tiêu

SKU Matching được coi là pass khi:

- Nhận `draft_order_line` đã được AI extraction bóc ra.
- Normalize alias/từ lóng, size, brand, material, PN, thread, angle và unit.
- Tìm candidate đúng từ `product_aliases` và `product_skus`.
- Candidate đúng nằm trong Top-3 cho case rõ.
- Không auto-select khi alias/canonical còn mơ hồ hoặc thiếu thuộc tính quan trọng.
- Lưu `sku_candidates` với `rank_no`, `confidence_score`, `match_reason`.
- Cập nhật line status đúng:
  - `MATCHED` nếu một candidate rõ ràng.
  - `PENDING_MATCH` nếu nhiều candidate gần nhau.
  - `NEEDS_CLARIFICATION` nếu thiếu thuộc tính cần hỏi.

## 2. Input và Output Kỳ Vọng

### 2.1. Input từ AI Extraction

```json
{
  "draft_order_line_id": "uuid",
  "raw_line_text": "5 cút vuông 27",
  "item_description": "cút vuông 27",
  "quantity": 5,
  "requested_unit": "cái",
  "surface_forms": ["cút vuông", "27"],
  "canonical_hints": [
    {
      "canonical_id": "C0035",
      "canonical_term": "Co 90°",
      "category": "Phụ kiện đổi hướng"
    },
    {
      "canonical_id": "C0142",
      "canonical_term": "Ống phi 27",
      "category": "Ký hiệu/kích cỡ"
    }
  ],
  "extracted_attributes": {
    "brand": null,
    "material": null,
    "diameter": "27",
    "pressure_class": null,
    "fitting_type": "co",
    "angle": "90",
    "thread_gender": null,
    "connection_type": null
  }
}
```

### 2.2. Output `sku_candidates`

```json
[
  {
    "rank_no": 1,
    "sku_id": "uuid",
    "sku_code": "BM-PVC-CO90-D27",
    "product_name": "Co 90 PVC-U D27 Bình Minh",
    "canonical_id": "C0035",
    "confidence_score": 0.91,
    "match_reason": "Alias exact match cút vuông -> Co 90; diameter 27 matched; category fitting matched.",
    "missing_attributes": ["brand"],
    "requires_review": false
  }
]
```

## 3. Matching Rules Cần Test

### 3.1. Normalize rule

Matcher phải xử lý:

- Lowercase, trim spaces, bỏ dấu tiếng Việt khi cần.
- Normalize hyphen/space: `măng-xông`, `măng xông`, `măngsông`.
- Normalize ký hiệu: `phi`, `fi`, `Φ`, `D`, `DN`.
- Normalize angle: `90`, `90°`, `vuông`; `45`, `lơi`, `chếch`.
- Normalize thread gender: `ren trong`, `răng trong`, `ren cái`, `đầu cái`; `ren ngoài`, `răng ngoài`, `ren đực`, `đầu đực`.
- Normalize brand alias: `BM` -> Bình Minh, `NTP` -> Tiền Phong.

### 3.2. Ranking signal

Ưu tiên ranking đề xuất:

| Signal | Ý nghĩa |
|---|---|
| Exact alias match | Alias trong `Alias_Map.alias` hoặc `normalized_alias` khớp trực tiếp |
| Canonical hint match | `canonical_id` từ extraction khớp với SKU/canonical |
| Attribute match | material, diameter, PN, angle, thread, brand, unit |
| Context match | cấp nước/thoát nước/luồn dây điện/tưới/PCCC |
| Missing critical attribute penalty | Thiếu brand/diameter/material/PN/thread khi cần |
| Ambiguity penalty | Alias trùng nhiều canonical hoặc nhiều SKU tương đương |

### 3.3. Auto-match threshold đề xuất

| Condition | Expected status |
|---|---|
| Top-1 score >= 0.85, margin với Top-2 >= 0.15, không thiếu critical attribute | `MATCHED` |
| Top-1 score >= 0.75 nhưng margin < 0.15 | `PENDING_MATCH` |
| Có nhiều candidate hợp lý hoặc alias trùng canonical | `PENDING_MATCH` + `CLARIFICATION_HOLD` nếu cần |
| Thiếu diameter/material/thread/PN quan trọng | `NEEDS_CLARIFICATION` |
| Không có candidate phù hợp | `PENDING_MATCH` hoặc `NEEDS_CLARIFICATION`, không invent SKU |

## 4. Coverage Bắt Buộc Từ Excel

### 4.1. Atomic alias matching coverage

Mỗi dòng trong `Alias_Map` phải sinh 1 test atomic:

```text
SKUM-ALIAS-{canonical_id}-{row_number}
```

Input atomic:

```json
{
  "raw_line_text": "{quantity} {unit} {alias}",
  "surface_forms": ["{alias}"],
  "canonical_hints": [
    {
      "canonical_id": "{canonical_id}",
      "canonical_term": "{canonical_term}",
      "category": "{category}",
      "material": "{material}",
      "application": "{application}"
    }
  ]
}
```

Expected atomic:

- Candidate đúng có `canonical_id` từ Excel trong Top-3.
- `match_reason` nhắc ít nhất một signal: alias/canonical/category/material/diameter.
- Nếu category không phải hàng bán trực tiếp như `Thi công/cách nói thợ`, `Sự cố/vận hành`, `Kiểm tra nghiệm thu`, matcher không được ép thành SKU nếu seed SKU không có item tương ứng.

### 4.2. Category coverage

Top category trong Excel cần được cover ở scenario chính:

| Category | Alias rows | Test focus |
|---|---:|---|
| `Ống/Vật liệu` | 100 | PVC-U, PP-R, HDPE, LDPE, package form |
| `Phụ kiện ống điện` | 67 | Không nhầm điện với nước |
| `Ký hiệu/kích cỡ` | 65 | phi/D/DN/PN/độ dày |
| `Van/Vòi` | 55 | Van chặn, van bi, khóa nước |
| `Thi công/cách nói thợ` | 48 | Context, không phải SKU |
| `Thi công/kết nối` | 43 | Dán keo/hàn nhiệt là action/context |
| `Phụ kiện kiểm tra/thông tắc` | 34 | `nối thăm`, `con thỏ`, thông tắc |
| `Ống điện/luồn dây` | 26 | ống gen/ghen, ruột gà điện |
| `Vật tư phụ` | 26 | keo, băng tan, primer |
| `Phụ kiện nối ren` | 24 | ren trong/ren ngoài |
| `Sự cố/vận hành` | 24 | rò/tắc/búa nước là issue context |

### 4.3. Duplicate alias coverage

Các alias trùng trong Excel là regression tests bắt buộc:

| Alias | Canonical candidates | Expected |
|---|---|---|
| `ống cuộn đen` | `C0009 - Ống HDPE`; `C0150 - Ống cuộn` | Top candidates có cả material và package; không auto-select nếu thiếu size/length |
| `nối thăm` | `C0066 - Nối thông tắc`; `C0070 - Nối thẳng thăm` | Top candidates có cả hai; cần hỏi loại thăm/kiểu nối |
| `kẹp đỡ ống` | `C0106 - Kẹp đỡ ống điện`; `C0114 - Cùm ống` | Hỏi dùng cho ống điện hay treo/cố định ống |

## 5. Test Case Format

| Field | Ý nghĩa |
|---|---|
| `ID` | Mã test |
| `Purpose` | Điều matcher cần chứng minh |
| `Input draft_order_line` | Dữ liệu sau AI extraction |
| `Seed SKU assumptions` | SKU cần có trong seed |
| `Expected candidates` | Top-N expected |
| `Expected status` | `MATCHED`, `PENDING_MATCH`, `NEEDS_CLARIFICATION` |
| `Must not` | Điều matcher không được làm |

## 6. Test Cases Luồng Chính

### SKUM-E2E-001 - Exact alias: `ống nhựa trắng phi 21 Bình Minh`

**Purpose:** Match alias rõ từ Excel sang SKU ống PVC-U đúng brand/diameter.

**Input draft_order_line**

```json
{
  "raw_line_text": "10 cây ống nhựa trắng phi 21 Bình Minh",
  "item_description": "ống nhựa trắng phi 21 Bình Minh",
  "quantity": 10,
  "requested_unit": "cây",
  "surface_forms": ["ống nhựa trắng", "phi 21", "Bình Minh"],
  "canonical_hints": ["C0001", "C0141", "C0152"],
  "extracted_attributes": {
    "brand": "Bình Minh",
    "material": "PVC-U",
    "diameter": "21"
  }
}
```

**Seed SKU assumptions**

- `BM-PVCU-PIPE-D21` exists.
- Similar SKUs exist: D27, D34, non-Bình Minh.

**Expected candidates**

| Rank | Expected candidate | Reason |
|---:|---|---|
| 1 | Bình Minh PVC-U pipe D21 | Alias `ống nhựa trắng`; brand Bình Minh; diameter 21 |
| 2-3 | Other PVC-U D21 or Bình Minh nearby sizes | Lower because brand/diameter mismatch |

**Expected status:** `MATCHED`

**Must not**

- Không match sang PP-R/HDPE.
- Không chọn D27/D34.

### SKUM-E2E-002 - Normalized alias không dấu: `ong nhua trang phi 21 BM`

**Purpose:** Accent-insensitive matching.

**Input draft_order_line**

```json
{
  "raw_line_text": "10 cay ong nhua trang phi 21 BM",
  "item_description": "ong nhua trang phi 21 BM",
  "quantity": 10,
  "requested_unit": "cay",
  "surface_forms": ["ong nhua trang", "phi 21", "BM"],
  "canonical_hints": ["C0001", "C0141", "C0152"],
  "extracted_attributes": {
    "brand": "Bình Minh",
    "material": "PVC-U",
    "diameter": "21"
  }
}
```

**Expected candidates:** same as `SKUM-E2E-001`.

**Expected status:** `MATCHED`

**Must not**

- Không fail vì thiếu dấu.

### SKUM-E2E-003 - PP-R nóng PN20

**Purpose:** Match `ống nóng`, `PPR`, `PN20`, diameter.

**Input draft_order_line**

```json
{
  "raw_line_text": "20 cây ống nóng 25 PN20",
  "item_description": "ống nóng 25 PN20",
  "quantity": 20,
  "requested_unit": "cây",
  "surface_forms": ["ống nóng", "25", "PN20"],
  "canonical_hints": ["C0007", "C0148"],
  "extracted_attributes": {
    "material": "PP-R",
    "diameter": "25",
    "pressure_class": "PN20",
    "use_case": "nước nóng"
  }
}
```

**Seed SKU assumptions**

- PP-R D25 PN10, PN16, PN20 exist.

**Expected candidates**

| Rank | Expected candidate | Reason |
|---:|---|---|
| 1 | PP-R D25 PN20 hot-water pipe | `ống nóng` + D25 + PN20 |
| 2 | PP-R D25 PN16 | Same material/diameter but PN mismatch |
| 3 | PP-R D25 PN10 | Same material/diameter but PN mismatch |

**Expected status:** `MATCHED`

**Must not**

- Không bỏ mất PN và chọn PN10/PN16.

### SKUM-E2E-004 - Missing PN cho PP-R, nhiều candidate gần nhau

**Purpose:** Không auto-select khi PP-R thiếu PN.

**Input draft_order_line**

```json
{
  "raw_line_text": "20 cây ống nóng 25",
  "item_description": "ống nóng 25",
  "quantity": 20,
  "requested_unit": "cây",
  "surface_forms": ["ống nóng", "25"],
  "canonical_hints": ["C0007"],
  "extracted_attributes": {
    "material": "PP-R",
    "diameter": "25",
    "pressure_class": null
  }
}
```

**Expected candidates**

- PP-R D25 PN10.
- PP-R D25 PN16.
- PP-R D25 PN20.

**Expected status:** `NEEDS_CLARIFICATION` or `PENDING_MATCH`

**Expected clarification:** "Ống nóng PPR D25 cần PN10, PN16 hay PN20?"

**Must not**

- Không tự chọn PN20 vì `ống nóng`.

### SKUM-E2E-005 - Co/cút vuông 27

**Purpose:** Match alias `cút vuông` -> Co 90° D27.

**Input draft_order_line**

```json
{
  "raw_line_text": "5 cút vuông 27",
  "item_description": "cút vuông 27",
  "quantity": 5,
  "requested_unit": "cái",
  "surface_forms": ["cút vuông", "27"],
  "canonical_hints": ["C0035", "C0142"],
  "extracted_attributes": {
    "fitting_type": "co",
    "angle": "90",
    "diameter": "27"
  }
}
```

**Expected candidates**

| Rank | Expected candidate | Reason |
|---:|---|---|
| 1 | Co 90 D27 | `cút vuông` means 90°, diameter 27 |
| 2 | Co 90 nearby size | Size mismatch penalty |
| 3 | Co 45 D27 | Angle mismatch penalty |

**Expected status:** `MATCHED` if material/brand defaults are acceptable; otherwise candidate Top-3 + review.

**Must not**

- Không chọn co 45/cút lơi.

### SKUM-E2E-006 - Cút lơi/chếch 45

**Purpose:** Match 45-degree aliases.

**Input draft_order_line**

```json
{
  "raw_line_text": "10 cút lơi 27",
  "item_description": "cút lơi 27",
  "quantity": 10,
  "requested_unit": "cái",
  "surface_forms": ["cút lơi", "27"],
  "canonical_hints": ["C0038", "C0142"],
  "extracted_attributes": {
    "fitting_type": "co",
    "angle": "45",
    "diameter": "27"
  }
}
```

**Expected candidate rank 1:** Co 45 D27.

**Expected status:** `MATCHED` if material/brand resolved; otherwise `PENDING_MATCH` with Co 45 D27 Top-1.

**Must not**

- Không chọn co 90.

### SKUM-E2E-007 - Tê giảm nhiều đường kính

**Purpose:** Match fitting reducer, không mất reduced diameter.

**Input draft_order_line**

```json
{
  "raw_line_text": "8 tê giảm 34 xuống 27",
  "item_description": "tê giảm 34 xuống 27",
  "quantity": 8,
  "requested_unit": "cái",
  "surface_forms": ["tê giảm", "34", "27"],
  "canonical_hints": ["C0045", "C0143", "C0142"],
  "extracted_attributes": {
    "fitting_type": "tee_reducer",
    "main_diameter": "34",
    "branch_diameter": "27"
  }
}
```

**Expected candidate rank 1:** Tê giảm 34-27.

**Expected status:** `MATCHED` if SKU exists.

**Must not**

- Không match sang Tê đều 34.
- Không chỉ dùng một diameter.

### SKUM-E2E-008 - Ren trong/ren ngoài

**Purpose:** Thread gender exactness.

**Input draft_order_line**

```json
{
  "raw_line_text": "6 nối răng trong 27",
  "item_description": "nối răng trong 27",
  "quantity": 6,
  "requested_unit": "cái",
  "surface_forms": ["nối răng trong", "27"],
  "canonical_hints": ["C0028", "C0142"],
  "extracted_attributes": {
    "fitting_type": "coupling_threaded",
    "thread_gender": "ren trong",
    "diameter": "27"
  }
}
```

**Expected candidate rank 1:** Nối thẳng ren trong D27.

**Expected status:** `MATCHED`

**Must not**

- Không match sang ren ngoài/đầu đực.

### SKUM-E2E-009 - Rắc co/zắc co/union

**Purpose:** Match alias tháo lắp.

**Input draft_order_line**

```json
{
  "raw_line_text": "2 bộ zắc co ren ngoài 27",
  "item_description": "zắc co ren ngoài 27",
  "quantity": 2,
  "requested_unit": "bộ",
  "surface_forms": ["zắc co", "ren ngoài", "27"],
  "canonical_hints": ["C0053", "C0055", "C0142"],
  "extracted_attributes": {
    "fitting_type": "union",
    "thread_gender": "ren ngoài",
    "diameter": "27"
  }
}
```

**Expected candidate rank 1:** Rắc co ren ngoài D27.

**Expected status:** `MATCHED`

**Must not**

- Không match sang nối ren ngoài thường nếu union SKU tồn tại.

### SKUM-E2E-010 - Van khóa nước 27 mơ hồ subtype

**Purpose:** `khóa nước` có thể là van chặn/van bi tùy SKU, cần candidate review nếu seed có nhiều loại.

**Input draft_order_line**

```json
{
  "raw_line_text": "20 khóa nước 27",
  "item_description": "khóa nước 27",
  "quantity": 20,
  "requested_unit": "cái",
  "surface_forms": ["khóa nước", "27"],
  "canonical_hints": ["C0077", "C0142"],
  "extracted_attributes": {
    "fitting_type": "valve",
    "diameter": "27"
  }
}
```

**Expected candidates**

- Van chặn D27.
- Van bi tay gạt D27 nếu seed có.
- Van bi tay xoay D27 nếu seed có.

**Expected status:** `PENDING_MATCH` if multiple valve subtypes exist.

**Expected clarification:** "Khóa nước D27 là van chặn, van bi tay gạt hay loại khác?"

**Must not**

- Không auto-select nếu nhiều valve subtype cùng hợp lý.

### SKUM-E2E-011 - Ống gen/ghen điện không nhầm ống nước

**Purpose:** Electrical conduit context.

**Input draft_order_line**

```json
{
  "raw_line_text": "100m ống ghen cứng D20",
  "item_description": "ống ghen cứng D20",
  "quantity": 100,
  "requested_unit": "m",
  "surface_forms": ["ống ghen cứng", "D20"],
  "canonical_hints": ["C0021"],
  "extracted_attributes": {
    "use_case": "luồn dây điện",
    "diameter": "20",
    "connection_type": "cứng"
  }
}
```

**Expected candidate rank 1:** Ống cứng luồn dây điện PVC D20.

**Expected status:** `MATCHED`

**Must not**

- Không match sang ống PVC cấp/thoát nước D20/D21.

### SKUM-E2E-012 - Máng gen không phải conduit tròn

**Purpose:** Cable trunking vs round conduit.

**Input draft_order_line**

```json
{
  "raw_line_text": "20 máng gen nhựa",
  "item_description": "máng gen nhựa",
  "quantity": 20,
  "requested_unit": "cái",
  "surface_forms": ["máng gen"],
  "canonical_hints": ["C0023"],
  "extracted_attributes": {
    "use_case": "luồn dây điện",
    "shape": "máng/hộp"
  }
}
```

**Expected candidate rank 1:** Máng luồn dây điện uPVC / trunking.

**Must not**

- Không match sang ống gen mềm hoặc ống cứng tròn.

### SKUM-E2E-013 - Ống cuộn đen mơ hồ

**Purpose:** Duplicate alias regression.

**Input draft_order_line**

```json
{
  "raw_line_text": "2 cuộn ống cuộn đen",
  "item_description": "ống cuộn đen",
  "quantity": 2,
  "requested_unit": "cuộn",
  "surface_forms": ["ống cuộn đen"],
  "canonical_hints": ["C0009", "C0150"],
  "extracted_attributes": {
    "package_form": "cuộn",
    "color": "đen"
  }
}
```

**Expected candidates**

| Rank | Candidate direction | Reason |
|---:|---|---|
| 1-2 | HDPE/PE pipe | `ống đen`, likely HDPE/PE |
| 1-2 | Generic ống cuộn | requested_unit/package form cuộn |

**Expected status:** `NEEDS_CLARIFICATION`

**Expected clarification:** "Ống cuộn đen là HDPE/PE đường kính bao nhiêu, chiều dài/cuộn bao nhiêu?"

**Must not**

- Không auto-select một SKU khi thiếu diameter/length.

### SKUM-E2E-014 - Nối thăm mơ hồ

**Purpose:** Duplicate alias regression.

**Input draft_order_line**

```json
{
  "raw_line_text": "4 nối thăm 90",
  "item_description": "nối thăm 90",
  "quantity": 4,
  "requested_unit": "cái",
  "surface_forms": ["nối thăm", "90"],
  "canonical_hints": ["C0066", "C0070"],
  "extracted_attributes": {
    "diameter": "90"
  }
}
```

**Expected candidates**

- Nối thông tắc D90.
- Nối thẳng thăm D90.

**Expected status:** `PENDING_MATCH` or `NEEDS_CLARIFICATION`

**Must not**

- Không auto-select một loại nối thăm nếu seed có cả hai.

### SKUM-E2E-015 - Kẹp đỡ ống mơ hồ

**Purpose:** Duplicate alias regression.

**Input draft_order_line**

```json
{
  "raw_line_text": "50 kẹp đỡ ống",
  "item_description": "kẹp đỡ ống",
  "quantity": 50,
  "requested_unit": "cái",
  "surface_forms": ["kẹp đỡ ống"],
  "canonical_hints": ["C0106", "C0114"],
  "extracted_attributes": {}
}
```

**Expected candidates**

- Kẹp đỡ ống điện.
- Cùm ống / đai treo ống.

**Expected status:** `NEEDS_CLARIFICATION`

**Expected clarification:** "Kẹp dùng cho ống điện hay cùm treo/cố định ống nước, size bao nhiêu?"

**Must not**

- Không auto-select.

### SKUM-E2E-016 - Brand alias BM/NTP/Dekko

**Purpose:** Brand signal ranking.

**Input draft_order_line**

```json
{
  "raw_line_text": "8 cây ống NTP phi 34",
  "item_description": "ống NTP phi 34",
  "quantity": 8,
  "requested_unit": "cây",
  "surface_forms": ["NTP", "phi 34"],
  "canonical_hints": ["C0153", "C0143"],
  "extracted_attributes": {
    "brand": "Tiền Phong",
    "diameter": "34"
  }
}
```

**Expected candidate rank 1:** Tiền Phong pipe D34.

**Must not**

- Không chọn Bình Minh/Dekko nếu NTP SKU tồn tại.

### SKUM-E2E-017 - DN mapping

**Purpose:** Size-system matching.

**Input draft_order_line**

```json
{
  "raw_line_text": "5 cây ống PVC DN32",
  "item_description": "ống PVC DN32",
  "quantity": 5,
  "requested_unit": "cây",
  "surface_forms": ["PVC", "DN32"],
  "canonical_hints": ["C0001", "C0143"],
  "extracted_attributes": {
    "material": "PVC-U",
    "diameter_system": "DN",
    "diameter": "32"
  }
}
```

**Expected candidate direction:** PVC-U pipe equivalent to dataset mapping `C0143 - Ống phi 34`.

**Expected status:** `MATCHED` if conversion table exists, otherwise `PENDING_MATCH` with explanation.

**Must not**

- Không chọn D32 raw if catalogue uses D34 for DN32 mapping.

### SKUM-E2E-018 - Vật tư phụ: băng tan

**Purpose:** Match non-pipe consumable.

**Input draft_order_line**

```json
{
  "raw_line_text": "1 cuộn băng tan",
  "item_description": "băng tan",
  "quantity": 1,
  "requested_unit": "cuộn",
  "surface_forms": ["băng tan"],
  "canonical_hints": ["C0115"],
  "extracted_attributes": {
    "material": "PTFE"
  }
}
```

**Expected candidate rank 1:** Băng tan/PTFE tape.

**Expected status:** `MATCHED`

**Must not**

- Không match sang gioăng/ron.

### SKUM-E2E-019 - Thi công/kết nối là action, không phải SKU

**Purpose:** Context category should not force SKU.

**Input draft_order_line**

```json
{
  "raw_line_text": "hàn nhiệt ppr nóng pn20",
  "item_description": "hàn nhiệt ppr nóng pn20",
  "quantity": null,
  "requested_unit": null,
  "surface_forms": ["hàn nhiệt", "ppr", "nóng", "pn20"],
  "canonical_hints": ["C0130", "C0007", "C0148"],
  "extracted_attributes": {
    "action": "hàn nhiệt",
    "material": "PP-R",
    "pressure_class": "PN20"
  }
}
```

**Expected behavior:**

- `hàn nhiệt` is installation action, not a SKU candidate by itself.
- If no purchasable item/quantity, status should be `NEEDS_CLARIFICATION`, not `MATCHED`.

**Must not**

- Không match action `hàn nhiệt` sang máy hàn hoặc đầu hàn nếu khách không nói cần mua dụng cụ.

### SKUM-E2E-020 - Sự cố/vận hành là issue, không phải SKU

**Purpose:** Avoid matching issue terms to products.

**Input draft_order_line**

```json
{
  "raw_line_text": "nhà bị búa nước cần test áp",
  "item_description": "nhà bị búa nước cần test áp",
  "quantity": null,
  "requested_unit": null,
  "surface_forms": ["búa nước", "test áp"],
  "canonical_hints": ["C0126", "C0123"],
  "extracted_attributes": {
    "issue": "búa nước",
    "action": "test áp"
  }
}
```

**Expected behavior:**

- Không tạo SKU candidate hàng hóa.
- Line should be note/service context or `NEEDS_CLARIFICATION` if order line expected.

**Must not**

- Không match `test áp` sang bơm/máy hoặc van giảm áp nếu text không yêu cầu mua.

## 7. API Contract Tests Cho Matching

### SKUM-API-001 - Match SKU cho một line

**Endpoint:** `POST /api/draft-order-lines/{lineId}/match-skus`

**Precondition:** `draft_order_line.status = EXTRACTED` hoặc `PENDING_MATCH`.

**Expected:**

- Tạo `sku_candidates`.
- Ghi `processing_events`.
- Ghi `audit_events` loại system matching.
- Cập nhật line status theo rule threshold.

### SKUM-API-002 - Lấy candidates

**Endpoint:** `GET /api/draft-order-lines/{lineId}/sku-candidates`

**Expected response fields:**

| Field | Required |
|---|---|
| `rank_no` | Yes |
| `sku_id` | Yes |
| `sku_code` | Yes |
| `product_name` | Yes |
| `confidence_score` | Yes |
| `match_reason` | Yes |
| `missing_attributes` | Recommended |

### SKUM-API-003 - Sale Admin chọn SKU

**Endpoint:** `POST /api/draft-order-lines/{lineId}/select-sku`

**Expected:**

- Set `selected_sku_id`.
- Set line status `MATCHED`.
- Create `review_actions.SELECT_SKU`.
- Create `audit_events`.
- Does not run price/stock/credit automatically unless endpoint explicitly chains to rule check.

## 8. Database Assertions

| Table | Assertion |
|---|---|
| `product_aliases` | Có normalized alias cho Excel alias |
| `sku_candidates` | Top-N candidates được lưu, rank không trùng |
| `draft_order_lines` | Status đúng sau matching |
| `order_holds` | Tạo `CLARIFICATION_HOLD` khi cần |
| `processing_events` | Có stage `SKU_MATCHING_STARTED/DONE/FAILED` |
| `review_actions` | Chỉ ghi khi user chọn/sửa, không ghi cho system matching |
| `audit_events` | Ghi matching result và user selection |

## 9. Negative Tests

### SKUM-NEG-001 - Không match nếu thiếu quantity nhưng item rõ

Matcher có thể trả candidate nhưng không được tự set quantity.

Input: `ống PVC trắng 34 Bình Minh`, quantity null.

Expected:

- Candidate PVC-U D34 Bình Minh Top-1.
- Line vẫn có missing quantity để extraction/review xử lý.

### SKUM-NEG-002 - Không invent SKU ngoài catalogue

Input: `ống siêu chịu lực D999`.

Expected:

- Không có candidate confidence cao.
- Status `PENDING_MATCH` hoặc `NEEDS_CLARIFICATION`.
- Không tạo SKU mới.

### SKUM-NEG-003 - Không dùng LLM tính giá/tồn/công nợ

Matching chỉ chọn candidate. Không trả price/stock/credit decision.

### SKUM-NEG-004 - Không bỏ qua candidate vì brand thiếu

Input: `co 90 27`.

Expected:

- Vẫn trả candidates theo co 90 D27.
- Missing brand/material có thể giảm confidence hoặc yêu cầu review.

## 10. Atomic Alias Test Generation Rules

Để không bỏ sót Excel:

1. Đọc `Alias_Map`.
2. Với mỗi row, sinh `SKUM-ALIAS-{canonical_id}-{row_number}`.
3. Seed `product_aliases` từ các cột:
   - `alias`
   - `normalized_alias`
   - `canonical_id`
   - `canonical_term`
   - `category`
   - `material`
   - `application`
4. Nếu canonical/category có SKU seed tương ứng:
   - Expected candidate đúng Top-3.
5. Nếu category là context/action/issue:
   - Expected no forced SKU unless seed có item bán được tương ứng.
6. Với duplicate alias:
   - Expected multiple candidates and no auto-select.

Prompt line template theo category:

| Category group | Template |
|---|---|
| `Ống/Vật liệu`, `Ống/Công năng` | `{qty} cây {alias} phi 27` |
| `Phụ kiện*`, `Van/Vòi`, `Đồng hồ nước` | `{qty} cái {alias} phi 27` |
| `Ký hiệu/kích cỡ` | `{qty} cây ống PVC {alias}` |
| `Vật tư phụ`, `Vật tư làm kín`, `Dụng cụ thi công` | `{qty} cái/cuộn {alias}` |
| `Thi công*`, `Sự cố*`, `Kiểm tra*` | `ghi chú: {alias} tuyến ống này` |

## 11. Exit Criteria

- [ ] 20 main-flow matching cases pass.
- [ ] 4 negative tests pass.
- [ ] 3 duplicate alias regression tests pass.
- [ ] 856 atomic alias matching tests generated from Excel.
- [ ] 154 canonical ids covered.
- [ ] 43 categories covered.
- [ ] Candidate đúng Top-3 cho all clear purchasable aliases.
- [ ] Ambiguous aliases never auto-select without enough attributes.
- [ ] Matching does not calculate price, stock, credit.
- [ ] `sku_candidates`, `processing_events`, `audit_events`, `review_actions` assertions pass.

