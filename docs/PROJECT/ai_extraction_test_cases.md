# AI Extraction Test Cases - OrderFlow AI MVP

Tài liệu này định nghĩa test cho bước **AI Extraction** trong luồng chính của OrderFlow AI MVP:

`RawOrderText -> AI Extraction -> draft_order_lines -> missing attributes / clarification -> SKU Matching`

Nguồn bám sát:

- `local/OrderFlow AI MVP.pdf`: module `AI Extraction`, API `POST /api/draft-orders/from-text`, lifecycle `EXTRACTING -> READY_FOR_REVIEW / NEEDS_CLARIFICATION / ON_HOLD`.
- `local/dataset_tu_long_ong_nhua_xay_dung_vn.xlsx`: sheet `Alias_Map` có 856 alias, 154 canonical terms, 43 category.
- `local/Bộ dữ liệu sẵn sàng cho ML về từ lóng ống nước và vật liệu nhựa trong xây dựng Việt Nam.pdf`: nguyên tắc normalize 4 tầng `surface form -> canonical technical term -> material family -> use-case`.

## 1. Mục Tiêu Test

AI Extraction được coi là pass khi:

- Tách đúng từng dòng hàng từ đơn thô.
- Bóc đúng số lượng, đơn vị, ngày giao, ghi chú giao hàng nếu có.
- Nhận diện được alias/từ lóng trong text và map về canonical direction phù hợp với dataset.
- Tách được thuộc tính quan trọng: material, brand, diameter, PN/pressure class, fitting type, thread gender, angle, package form, application/use-case.
- Không tự thêm thông tin không có trong text.
- Nếu thiếu thông tin quan trọng, tạo `missing_attributes` và `clarification_question`.
- Với alias mơ hồ, không ép thành một SKU/canonical duy nhất; extraction phải đánh dấu ambiguity để SKU Matching/Review xử lý.

## 2. Output Schema Kỳ Vọng

AI extraction nên trả về object có cấu trúc tương đương:

```json
{
  "customer_hint": "string|null",
  "project_hint": "string|null",
  "requested_delivery_date": "string|null",
  "delivery_note": "string|null",
  "order_note": "string|null",
  "lines": [
    {
      "raw_line_text": "string",
      "item_description": "string",
      "quantity": 10,
      "requested_unit": "cây|cái|cuộn|m|bộ|null",
      "surface_forms": ["ống nhựa trắng", "phi 21"],
      "canonical_hints": [
        {
          "canonical_id": "C0001",
          "canonical_term": "Ống PVC-U",
          "category": "Ống/Vật liệu",
          "material": "PVC-U/uPVC",
          "application": "cấp nước, thoát nước, tuyến cáp ngầm",
          "confidence": 0.0,
          "reason": "string"
        }
      ],
      "extracted_attributes": {
        "brand": "Bình Minh|null",
        "material": "PVC-U|PP-R|HDPE|null",
        "diameter": "21|27|34|90|110|null",
        "diameter_system": "phi|D|DN|inch|null",
        "pressure_class": "PN10|PN16|PN20|null",
        "fitting_type": "co|tee|reducer|coupling|valve|null",
        "angle": "90|45|88|null",
        "thread_gender": "ren trong|ren ngoài|null",
        "connection_type": "trơn|ren|gioăng|hàn nhiệt|dán keo|null",
        "package_form": "cây|cuộn|null",
        "use_case": "cấp nước|thoát nước|luồn dây điện|null"
      },
      "missing_attributes": ["diameter", "material"],
      "clarification_question": "string|null",
      "confidence_score": 0.0
    }
  ]
}
```

## 3. Coverage Bắt Buộc Từ Excel

### 3.1. Atomic alias coverage

Sheet `Alias_Map` có:

| Metric | Count |
|---|---:|
| Alias rows | 856 |
| Canonical terms | 154 |
| Categories | 43 |

Test suite tự động sau này phải sinh **856 atomic alias extraction tests**, mỗi dòng trong `Alias_Map` là một test case.

ID format đề xuất:

```text
AIEX-ALIAS-{canonical_id}-{row_number}
```

Prompt template:

```text
Khách cần {quantity} {unit} {alias} {optional_size_or_brand}, giao trong hôm nay.
```

Expected cho mỗi atomic alias:

- `surface_forms` chứa đúng `alias`.
- `canonical_hints[0].canonical_id` hoặc Top-N có `canonical_id` đúng từ Excel.
- `canonical_term`, `category`, `material`, `application` khớp với `Alias_Map`.
- Nếu `alias` có độ mơ hồ cao hoặc trùng ở nhiều canonical, không được return một kết quả duy nhất với confidence tuyệt đối.

### 3.2. Category coverage tối thiểu

Tài liệu test phải có ít nhất 1 case luồng chính cho các nhóm sau:

| Category từ Excel | Vì sao cần test |
|---|---|
| `Ống/Vật liệu` | Core domain: PVC-U, PP-R, HDPE |
| `Ký hiệu/kích cỡ` | `phi`, `D`, `DN`, `PN` là nguồn nhầm SKU |
| `Phụ kiện nối ren` | `ren trong`, `ren ngoài`, `đầu cái`, `đầu đực` |
| `Phụ kiện đổi hướng` | `co`, `cút`, `lơi`, `chếch`, angle |
| `Phụ kiện chia nhánh` | `tê`, `chữ T`, `ba chạc`, `Y` |
| `Phụ kiện nối giảm` | `côn thu`, `tê giảm`, nhiều đường kính |
| `Van/Vòi` | `khóa nước`, `van bi`, `van chặn` |
| `Ống điện/luồn dây` | Tránh nhầm `ống gen/ghen` với ống nước |
| `Thi công/cách nói thợ` | `lấy giống đơn trước`, `đi âm`, `đầu chờ` thường là note/context |
| `Sự cố/vận hành` | `xì`, `rò`, `tắc`, `búa nước` thường là service/repair context |

### 3.3. Alias mơ hồ bắt buộc

3 alias lặp trong Excel phải có test riêng:

| Alias | Candidate hợp lý | Expected extraction behavior |
|---|---|---|
| `ống cuộn đen` | `C0009 - Ống HDPE`; `C0150 - Ống cuộn` | Mark ambiguous; hỏi vật liệu/cỡ/chiều dài nếu thiếu |
| `nối thăm` | `C0066 - Nối thông tắc`; `C0070 - Nối thẳng thăm` | Mark ambiguous; không tự chọn |
| `kẹp đỡ ống` | `C0106 - Kẹp đỡ ống điện`; `C0114 - Cùm ống` | Hỏi dùng cho ống điện hay treo/cố định ống nước |

## 4. Test Case Format

Mỗi test case trong tài liệu này dùng format:

| Field | Ý nghĩa |
|---|---|
| `ID` | Mã test |
| `Purpose` | Mục tiêu extraction |
| `RawOrderText` | Text Sale Admin paste |
| `Excel aliases covered` | Alias/canonical_id liên quan |
| `Expected order-level extraction` | Customer/project/date/note |
| `Expected lines` | Dòng hàng, số lượng, đơn vị, attributes |
| `Expected missing/clarification` | Thuộc tính thiếu và câu hỏi cần hỏi |
| `Must not` | Điều AI không được làm |

## 5. Test Cases Luồng Chính

### AIEX-E2E-001 - Đơn rõ, nhiều dòng hàng, đủ size

**Purpose:** Happy path extraction cho đơn rõ, dùng alias đời thường nhưng đủ thuộc tính.

**RawOrderText**

```text
Anh lấy cho công trình Quận 7:
10 cây ống nhựa trắng phi 21 Bình Minh,
5 cút vuông 27,
3 chữ T 27,
2 nối ren trong 21,
giao sáng mai.
```

**Excel aliases covered**

| Alias | Canonical |
|---|---|
| `ống nhựa trắng` | `C0001 - Ống PVC-U` |
| `phi 21` | `C0141 - Ống phi 21` |
| `cút vuông` | `C0035 - Co 90°` |
| `chữ T` | `C0042 - Tê 90°` |
| `nối ren trong` | `C0028 - Nối thẳng ren trong` |

**Expected order-level extraction**

| Field | Expected |
|---|---|
| `project_hint` | `Quận 7` |
| `requested_delivery_date` | relative date: `sáng mai` |
| `delivery_note` | `giao sáng mai` |

**Expected lines**

| Line | quantity | unit | expected attributes |
|---|---:|---|---|
| ống nhựa trắng phi 21 Bình Minh | 10 | cây | material PVC-U/uPVC, brand Bình Minh, diameter 21 |
| cút vuông 27 | 5 | cái | fitting_type co/cút, angle 90, diameter 27 |
| chữ T 27 | 3 | cái | fitting_type tee/T, diameter 27 |
| nối ren trong 21 | 2 | cái | fitting_type coupling/adaptor, thread_gender ren trong, diameter 21 |

**Expected missing/clarification:** none for extraction.

**Must not**

- Không gộp `cút vuông`, `chữ T`, `nối ren trong` thành một dòng.
- Không đổi `cút vuông` thành co 45.

### AIEX-E2E-002 - PPR/ống nóng, PN, thiếu thông tin phụ kiện

**Purpose:** Test `ống nóng`, `PN`, và phụ kiện thiếu diameter/type.

**RawOrderText**

```text
Cho anh 20 cây ống nóng 25 PN20, 10 co, 5 tê giảm giống hôm trước.
Giao công trình cũ chiều nay nếu kịp.
```

**Excel aliases covered**

| Alias | Canonical |
|---|---|
| `ống nóng` | `C0007 - Ống PP-R` |
| `PN20` | `C0148 - PN áp lực` |
| `co` | `C0035/C0038/...` ambiguous co family |
| `tê giảm` | `C0045 - Tê giảm` |
| `giống hôm trước` | repeat/history intent |

**Expected lines**

| Line | quantity | unit | expected attributes |
|---|---:|---|---|
| ống nóng 25 PN20 | 20 | cây | material PP-R, hot-water/use-case, diameter 25, pressure_class PN20 |
| co | 10 | cái | fitting_type co, missing diameter/angle/thread/material |
| tê giảm giống hôm trước | 5 | cái | fitting_type tee reducer, history_reference true, missing reducer sizes if history unavailable |

**Expected missing/clarification**

- `co`: missing `diameter`, `angle`, `connection_type/thread_gender`.
- `tê giảm`: missing reducer dimensions unless history lookup resolves it.
- `công trình cũ`: missing unique project if customer has multiple projects.

**Must not**

- Không tự biến `co` thành `Co 90 D25`.
- Không tự tạo project nếu `công trình cũ` không resolve duy nhất.

### AIEX-E2E-003 - Alias mơ hồ: `ống cuộn đen`, `nối thăm`, `kẹp đỡ ống`

**Purpose:** Test các alias lặp trong Excel, bắt AI đánh dấu ambiguity.

**RawOrderText**

```text
Lấy giúp anh 2 cuộn ống cuộn đen, 4 nối thăm 90, 50 kẹp đỡ ống.
Giao kho chính trong hôm nay.
```

**Excel aliases covered**

| Alias | Possible canonical |
|---|---|
| `ống cuộn đen` | `C0009 - Ống HDPE`; `C0150 - Ống cuộn` |
| `nối thăm` | `C0066 - Nối thông tắc`; `C0070 - Nối thẳng thăm` |
| `kẹp đỡ ống` | `C0106 - Kẹp đỡ ống điện`; `C0114 - Cùm ống` |

**Expected lines**

| Line | quantity | unit | expected extraction |
|---|---:|---|---|
| ống cuộn đen | 2 | cuộn | ambiguous canonical hints: HDPE material and package form |
| nối thăm 90 | 4 | cái | ambiguous inspection/cleanout fitting, diameter/angle 90 may be size or angle depending seed |
| kẹp đỡ ống | 50 | cái | ambiguous pipe support/electrical clamp |

**Expected missing/clarification**

- `ống cuộn đen`: hỏi đường kính/chiều dài/cuộn và xác nhận HDPE hay chỉ quy cách cuộn.
- `nối thăm 90`: hỏi D90 hay góc 90; nối thông tắc hay nối thẳng thăm.
- `kẹp đỡ ống`: hỏi dùng cho ống điện hay treo/cố định ống nước, size nào.

**Must not**

- Không auto-select canonical duy nhất với confidence cao.
- Không bỏ qua `cuộn` như một unit/package signal.

### AIEX-E2E-004 - Không đủ thông tin quantity/unit

**Purpose:** Test dòng hàng thiếu số lượng hoặc đơn vị.

**RawOrderText**

```text
Khách cần ống PVC trắng 34 Bình Minh, thêm co 90 34 và băng tan.
Mai giao qua công trình Thủ Đức.
```

**Excel aliases covered**

| Alias | Canonical |
|---|---|
| `PVC trắng` | `C0001 - Ống PVC-U` |
| `34` | `C0143 - Ống phi 34` |
| `co 90` | `C0035 - Co 90°` |
| `băng tan` | `C0115 - Băng tan` |

**Expected lines**

| Line | quantity | unit | expected attributes |
|---|---:|---|---|
| ống PVC trắng 34 Bình Minh | null | null/cây | material PVC-U, brand Bình Minh, diameter 34, missing quantity |
| co 90 34 | null | cái | fitting_type co, angle 90, diameter 34, missing quantity |
| băng tan | null | cuộn/cái nullable | material PTFE, missing quantity |

**Expected missing/clarification**

- Hỏi số lượng cho từng dòng.

**Must not**

- Không default số lượng = 1 nếu text không nói rõ.

### AIEX-E2E-005 - Ren trong/ren ngoài và đầu cái/đầu đực

**Purpose:** Test thread gender extraction, tránh nhầm ren trong/ngoài.

**RawOrderText**

```text
Lấy 6 nối răng trong 27, 6 đầu đực 27, 4 co ren cái 21, 4 co ren đực 21.
```

**Excel aliases covered**

| Alias | Canonical |
|---|---|
| `nối răng trong` | `C0028 - Nối thẳng ren trong` |
| `đầu đực` | `C0029 - Nối thẳng ren ngoài` |
| `co ren cái` | `C0036 - Co 90° ren trong` |
| `co ren đực` | `C0037 - Co 90° ren ngoài` |

**Expected lines**

| Line | quantity | unit | expected attributes |
|---|---:|---|---|
| nối răng trong 27 | 6 | cái | thread_gender ren trong/female, diameter 27 |
| đầu đực 27 | 6 | cái | thread_gender ren ngoài/male, diameter 27 |
| co ren cái 21 | 4 | cái | fitting_type co, angle default 90 if alias says co 90 in seed else missing angle, thread_gender ren trong, diameter 21 |
| co ren đực 21 | 4 | cái | fitting_type co, thread_gender ren ngoài, diameter 21 |

**Must not**

- Không đảo `ren trong` và `ren ngoài`.
- Không map `đầu đực` sang ren trong.

### AIEX-E2E-006 - Tê giảm/côn thu nhiều đường kính

**Purpose:** Test reducer dimensions và fitting reducer.

**RawOrderText**

```text
Cho 8 tê giảm 34 xuống 27, 10 côn thu 27-21, 5 bạc giảm 34/21.
```

**Excel aliases covered**

| Alias | Canonical |
|---|---|
| `tê giảm` | `C0045 - Tê giảm` |
| `côn thu` | `C0032 - Nối giảm` |
| `bạc giảm` | `C0033 - Bạc chuyển bậc` |

**Expected lines**

| Line | quantity | unit | expected attributes |
|---|---:|---|---|
| tê giảm 34 xuống 27 | 8 | cái | fitting_type tee reducer, main_diameter 34, branch/reduced_diameter 27 |
| côn thu 27-21 | 10 | cái | fitting_type reducer/coupling, from_diameter 27, to_diameter 21 |
| bạc giảm 34/21 | 5 | cái | fitting_type bushing/reducer, from_diameter 34, to_diameter 21 |

**Must not**

- Không chỉ giữ một diameter.
- Không map `tê giảm` thành `tê đều`.

### AIEX-E2E-007 - Co/cút/lơi/chếch và angle

**Purpose:** Test alias đổi hướng và angle 90/45.

**RawOrderText**

```text
Lấy 10 cút vuông 27, 10 cút lơi 27, 5 chếch 45 phi 34, 3 co cong 88 D90.
```

**Excel aliases covered**

| Alias | Canonical |
|---|---|
| `cút vuông` | `C0035 - Co 90°` |
| `cút lơi` | `C0038 - Co 45°` |
| `chếch 45` | `C0038 - Co 45°` |
| `co cong 88` | `C0040 - Co cong 88°` |

**Expected lines**

| Line | quantity | unit | expected attributes |
|---|---:|---|---|
| cút vuông 27 | 10 | cái | angle 90, diameter 27 |
| cút lơi 27 | 10 | cái | angle 45, diameter 27 |
| chếch 45 phi 34 | 5 | cái | angle 45, diameter 34 |
| co cong 88 D90 | 3 | cái | angle 88, diameter 90 |

**Must not**

- Không map `cút lơi` hoặc `chếch 45` sang co 90.

### AIEX-E2E-008 - Van/vòi/khóa nước

**Purpose:** Test nhóm Van/Vòi và cảnh báo thiếu loại van.

**RawOrderText**

```text
Cho 20 khóa nước 27, 5 van bi tay gạt 34, 3 phao cơ bồn nước.
```

**Excel aliases covered**

| Alias | Canonical |
|---|---|
| `khóa nước` | `C0077 - Van chặn` |
| `van bi tay gạt` | `C0080 - Van bi tay gạt` |
| `phao cơ` | `C0084 - Van phao` |

**Expected lines**

| Line | quantity | unit | expected attributes |
|---|---:|---|---|
| khóa nước 27 | 20 | cái | valve/general shutoff, diameter 27, possibly missing valve subtype/material |
| van bi tay gạt 34 | 5 | cái | valve ball, handle lever, diameter 34 |
| phao cơ bồn nước | 3 | cái | float valve, application water tank |

**Expected missing/clarification**

- `khóa nước 27` có thể cần hỏi van chặn hay van bi, nhựa/thau/kim loại, ren hay trơn nếu seed có nhiều SKU.

**Must not**

- Không map `phao cơ` thành phụ kiện ống thường.

### AIEX-E2E-009 - Ống điện/ghen/gen không được nhầm sang ống nước

**Purpose:** Test nhóm ống luồn dây điện, vì `ống gen/ghen` dễ bị nhầm.

**RawOrderText**

```text
Công trình cần 100m ống ghen cứng D20, 50m ruột gà điện, 20 máng gen nhựa.
```

**Excel aliases covered**

| Alias | Canonical |
|---|---|
| `ống ghen cứng` | `C0021 - Ống cứng luồn dây điện PVC` |
| `ruột gà điện` | `C0022 - Ống gen mềm luồn dây điện` |
| `máng gen` | `C0023 - Máng luồn dây điện uPVC` |

**Expected lines**

| Line | quantity | unit | expected attributes |
|---|---:|---|---|
| ống ghen cứng D20 | 100 | m | use_case luồn dây điện, rigid conduit, diameter 20 |
| ruột gà điện | 50 | m | flexible conduit, electrical use-case |
| máng gen nhựa | 20 | cái/thanh nullable | cable trunking, not round pipe |

**Must not**

- Không map `ống ghen` sang ống cấp/thoát nước.
- Không map `máng gen` sang conduit tròn.

### AIEX-E2E-010 - Sự cố/vận hành là context, không phải luôn là line hàng

**Purpose:** Test text có issue/repair context lẫn vật tư cần đặt.

**RawOrderText**

```text
Nhà khách bị xì nước ở cổ lavabo, lấy 2 xi phông U, 1 cuộn băng tan, 3 nối ren ngoài 21 để thay.
```

**Excel aliases covered**

| Alias | Canonical |
|---|---|
| `xì nước` | `C0127 - Rò nước` |
| `cổ lavabo` | `C0139 - Cổ ống` |
| `xi phông U` | `C0074 - Xi-phông U` |
| `băng tan` | `C0115 - Băng tan` |
| `nối ren ngoài` | `C0029 - Nối thẳng ren ngoài` |

**Expected extraction**

- `xì nước ở cổ lavabo` đi vào `order_note` hoặc line note, không nhất thiết là item cần mua.
- Tạo 3 item lines: `xi phông U`, `băng tan`, `nối ren ngoài 21`.

**Must not**

- Không tạo dòng hàng "xì nước" với SKU.
- Không bỏ mất context sửa chữa nếu cần dùng cho ghi chú.

### AIEX-E2E-011 - Ký hiệu kích cỡ: phi/D/DN/fi

**Purpose:** Test nhiều cách gọi size cùng nghĩa.

**RawOrderText**

```text
Lấy 5 ống D21, 5 ống phi 27, 5 ống DN32, 5 ống fi 60, đều là PVC Bình Minh.
```

**Excel aliases covered**

| Alias | Canonical |
|---|---|
| `D21` | `C0141 - Ống phi 21` |
| `phi 27` | `C0142 - Ống phi 27` |
| `DN32` | `C0143 - Ống phi 34` in dataset mapping |
| `fi 60` | `C0145 - Ống phi 60` |

**Expected lines**

| Line | quantity | unit | expected diameter |
|---|---:|---|---|
| ống D21 | 5 | cây nullable | 21 |
| ống phi 27 | 5 | cây nullable | 27 |
| ống DN32 | 5 | cây nullable | 34/DN32 mapping according to seed |
| ống fi 60 | 5 | cây nullable | 60 |

**Expected missing/clarification**

- Nếu unit không rõ, hỏi cây/mét/cuộn.

**Must not**

- Không coi DN32 là diameter 32 mm nếu taxonomy seed map DN32 -> phi 34.

### AIEX-E2E-012 - PN/độ dày/áp lực

**Purpose:** Test pressure class và độ dày.

**RawOrderText**

```text
Cho 10 cây ống PPR nóng phi 25 PN20, 10 cây ống PPR lạnh phi 25 PN10, lấy loại thành dày.
```

**Excel aliases covered**

| Alias | Canonical |
|---|---|
| `PPR` / `ống PPR` | `C0007 - Ống PP-R` |
| `ống nóng` | `C0007 - Ống PP-R` hot subtype |
| `PN20`, `PN10` | `C0148 - PN áp lực` |
| `thành dày` | `C0149 - Ống dày/mỏng` |

**Expected lines**

| Line | quantity | unit | expected attributes |
|---|---:|---|---|
| ống PPR nóng phi 25 PN20 | 10 | cây | PP-R, hot-water, diameter 25, PN20, thick wall signal |
| ống PPR lạnh phi 25 PN10 | 10 | cây | PP-R, cold-water, diameter 25, PN10, thick wall signal if "loại thành dày" applies to both |

**Must not**

- Không bỏ mất PN.
- Không gộp hai line PN10/PN20 thành một.

### AIEX-E2E-013 - Brand và tên thị trường

**Purpose:** Test brand extraction từ alias thương mại.

**RawOrderText**

```text
Lấy 10 cây ống BM phi 21, 5 co Bình Minh 27, 8 cây ống NTP phi 34, 3 phụ kiện Dekko ren trong 21.
```

**Excel aliases covered**

| Alias | Canonical |
|---|---|
| `ống BM`, `Bình Minh` | `C0152 - Ống Bình Minh` |
| `NTP`, `Tiền Phong` | `C0153 - Ống Tiền Phong` |
| `Dekko` | `C0154 - Ống Dekko` |
| `ren trong` | `C0028 - Nối thẳng ren trong` |

**Expected extraction**

- Brand hints:
  - `BM`, `Bình Minh` -> brand Bình Minh.
  - `NTP` -> brand Tiền Phong.
  - `Dekko` -> brand Dekko.
- `phụ kiện Dekko ren trong 21` thiếu fitting subtype nếu chỉ có "phụ kiện", cần clarification.

**Must not**

- Không coi `BM` là material.
- Không tự chọn loại phụ kiện cho `phụ kiện Dekko ren trong 21`.

### AIEX-E2E-014 - Unit và packaging: cây/cuộn/mét/bộ

**Purpose:** Test đơn vị và quy cách đóng gói.

**RawOrderText**

```text
Cho 3 cuộn HDPE đen D60, 20 mét ống tưới mềm, 15 cây PVC 90, 2 bộ rắc co ren ngoài 27.
```

**Excel aliases covered**

| Alias | Canonical |
|---|---|
| `cuộn HDPE`, `ống cuộn đen` | `C0009`, `C0150` |
| `ống tưới mềm` | `C0013 - Ống LDPE` |
| `PVC` | `C0001 - Ống PVC-U` |
| `rắc co ren ngoài` | `C0055 - Rắc co ren ngoài` |

**Expected lines**

| Line | quantity | unit | expected attributes |
|---|---:|---|---|
| cuộn HDPE đen D60 | 3 | cuộn | HDPE/PE, diameter 60, package_form cuộn |
| ống tưới mềm | 20 | mét | LDPE/PE, use-case tưới |
| PVC 90 | 15 | cây | PVC-U/PVC, diameter 90 |
| rắc co ren ngoài 27 | 2 | bộ | union/rắc co, thread_gender ren ngoài, diameter 27 |

**Must not**

- Không mất unit `cuộn`/`mét`/`cây`/`bộ`.

### AIEX-E2E-015 - Ngày giao, project, delivery note

**Purpose:** Test extraction order-level fields không bị lẫn vào line item.

**RawOrderText**

```text
Khách Minh Anh - công trình Q7 cần 10 cây ống PVC D21, 5 co 90 D21.
Giao trước 9h sáng mai, xe nhỏ vào hẻm, gọi anh Nam trước khi tới.
```

**Expected order-level extraction**

| Field | Expected |
|---|---|
| `customer_hint` | Minh Anh |
| `project_hint` | Q7 / Quận 7 |
| `requested_delivery_date` | sáng mai before 9h |
| `delivery_note` | xe nhỏ vào hẻm; gọi anh Nam trước khi tới |

**Expected lines**

| Line | quantity | unit |
|---|---:|---|
| ống PVC D21 | 10 | cây |
| co 90 D21 | 5 | cái |

**Must not**

- Không tạo item line từ "xe nhỏ", "hẻm", "gọi anh Nam".

### AIEX-E2E-016 - Repeat order/historical reference

**Purpose:** Test AI nhận diện reference tới lịch sử đơn, không tự bịa line nếu thiếu history.

**RawOrderText**

```text
Anh lấy lại giống đơn Quận 7 hôm trước, nhưng tăng ống lên 15 cây, co giữ nguyên, tê giảm lấy 5 cái.
Giao sáng mai.
```

**Expected extraction**

| Field | Expected |
|---|---|
| `history_reference` | true |
| `project_hint` | Quận 7 |
| `requested_delivery_date` | sáng mai |
| `overrides` | ống quantity 15; co keep previous; tê giảm quantity 5 |

**Expected missing/clarification**

- Nếu không có history lookup result, hỏi chọn đơn lịch sử nào.
- Nếu có nhiều đơn Quận 7 gần đây, hỏi xác nhận.

**Must not**

- Không tự tạo danh sách SKU đầy đủ nếu history không được cung cấp.

### AIEX-E2E-017 - Đơn có nhiều hold downstream nhưng extraction vẫn phải sạch

**Purpose:** Test raw text phục vụ `STOCK_HOLD`/`CREDIT_HOLD` downstream, nhưng extraction chỉ làm đúng phần bóc dữ liệu.

**RawOrderText**

```text
An Phát lấy 30 cây ống HDPE PE100, 20 van khóa nước 27, 10 rắc co ren trong 27.
Giao công trình Long An tuần này.
```

**Excel aliases covered**

| Alias | Canonical |
|---|---|
| `HDPE PE100` | `C0010 - Ống HDPE PE100` |
| `van khóa nước` | `C0077 - Van chặn` |
| `rắc co ren trong` | `C0054 - Rắc co ren trong` |

**Expected extraction**

- Customer hint: An Phát.
- Project hint: Long An.
- Delivery hint: tuần này.
- 3 item lines with quantities.

**Must not**

- Không tạo `CREDIT_HOLD` trong extraction output; credit hold thuộc rule engine.
- Không bỏ qua line `van khóa nước` vì có thể mơ hồ subtype.

### AIEX-E2E-018 - Giá đặc biệt là note, không phải attribute SKU

**Purpose:** Test câu "giá như lần trước giảm sâu" được đưa vào note/price intent.

**RawOrderText**

```text
Khách Thành Đạt hỏi 15 cây ống PPR nóng PN20 phi 21, 10 co ren cái 21,
giá như lần trước giảm sâu giúp anh.
```

**Expected extraction**

| Field | Expected |
|---|---|
| `customer_hint` | Thành Đạt |
| `order_note` | giá như lần trước giảm sâu |
| `price_intent` | special/discount requested |

**Expected lines**

| Line | quantity | unit | expected attributes |
|---|---:|---|---|
| ống PPR nóng PN20 phi 21 | 15 | cây | PP-R, hot-water, PN20, diameter 21 |
| co ren cái 21 | 10 | cái | co, ren trong, diameter 21, missing angle if seed does not default |

**Must not**

- Không biến "giá như lần trước" thành product line.
- Không tự set unit price trong extraction.

## 6. Negative Tests

### AIEX-NEG-001 - Không được hallucinate brand

**RawOrderText**

```text
Cho 10 cây ống PVC phi 21, 5 co 90 phi 21.
```

**Expected:** brand = null hoặc missing brand. Không tự thêm Bình Minh/Tiền Phong/Dekko.

### AIEX-NEG-002 - Không được hallucinate quantity

**RawOrderText**

```text
Cho anh ống nóng phi 25 và co 25.
```

**Expected:** quantity missing cho cả hai line. Không default quantity = 1.

### AIEX-NEG-003 - Không được gộp service issue thành SKU

**RawOrderText**

```text
Nhà bị búa nước, cần test áp lại tuyến ống.
```

**Expected:** issue/action context `búa nước`, `test áp`; không tạo product line nếu không có vật tư cần mua.

### AIEX-NEG-004 - Không được nhầm ống điện với ống nước

**RawOrderText**

```text
Đi ống gen mềm âm tường cho dây điện.
```

**Expected:** electrical conduit context; không map sang ống cấp/thoát nước.

## 7. Atomic Alias Test Generation Rules

Khi tự động sinh test từ Excel:

1. Đọc sheet `Alias_Map`.
2. Với mỗi row, tạo 1 test atomic.
3. Dùng `alias`, `canonical_id`, `canonical_term`, `category`, `material`, `application`, `confidence`, `example_context`.
4. Nếu `example_context` có sẵn, dùng làm prompt chính.
5. Nếu không, dùng template theo category:

| Category pattern | Prompt template |
|---|---|
| `Ống/Vật liệu`, `Ống/Công năng` | `Khách cần 10 cây {alias} phi 27, giao hôm nay.` |
| `Phụ kiện*` | `Khách cần 5 cái {alias} phi 27.` |
| `Van/Vòi` | `Khách cần 3 cái {alias} phi 27.` |
| `Ký hiệu/kích cỡ` | `Khách cần 10 cây ống PVC {alias}.` |
| `Thi công*`, `Sự cố*`, `Kiểm tra*` | `Ghi chú công trình: cần {alias} cho tuyến ống này.` |
| `Vật tư phụ`, `Dụng cụ thi công` | `Khách cần 2 cái/cuộn {alias}.` |

6. Expected output:
   - `surface_forms` contains alias.
   - `canonical_hints` contains expected canonical_id.
   - `category/material/application` match Excel row where applicable.
   - For generic or ambiguous category, require lower confidence or clarification.

## 8. Exit Criteria

AI Extraction test suite đạt yêu cầu khi:

- [ ] 18 main-flow test cases pass.
- [ ] 4 negative tests pass.
- [ ] 856 atomic alias cases được sinh từ Excel.
- [ ] 154 canonical terms đều xuất hiện ít nhất 1 lần trong atomic suite.
- [ ] 43 categories đều có coverage.
- [ ] 3 duplicate alias cases bắt buộc không auto-resolve.
- [ ] Không có test nào yêu cầu LLM tính giá/tồn/công nợ.
- [ ] Tất cả downstream hold chỉ là expected later-stage behavior, không nằm trong extraction responsibility.

