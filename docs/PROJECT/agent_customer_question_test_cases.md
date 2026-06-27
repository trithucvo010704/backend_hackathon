# Agent Customer Question Test Cases - OrderFlow AI MVP

Tài liệu này tập trung vào lớp test **khách hỏi / agent hiểu / DB map đúng không** cho OrderFlow AI MVP.

Trong MVP, agent không phải chatbot tự động trả lời khách qua Zalo. Agent là lớp AI nội bộ hỗ trợ Sale Admin: đọc raw text/câu hỏi khách, xác định intent, bóc dòng hàng, map alias/từ lóng sang canonical/SKU candidates, sinh câu hỏi clarification, và không tự chốt đơn.

Nguồn bám sát:

- `local/OrderFlow AI MVP.pdf`: raw order input, AI extraction, SKU matching, rule engine, review workbench, human approval.
- `local/dataset_tu_long_ong_nhua_xay_dung_vn.xlsx`: `Alias_Map` có 856 alias rows, 154 canonical terms, 43 categories.
- `specs/PROJECT/ai_extraction_test_cases.md`
- `specs/PROJECT/sku_matching_alias_test_cases.md`
- `specs/PROJECT/rule_engine_test_cases.md`
- `specs/PROJECT/e2e_demo_scenarios.md`

## 1. Mục Tiêu

Test này trả lời 5 câu hỏi:

1. Agent có hiểu đúng intent của câu khách không?
2. Agent có nhận ra đúng alias/từ lóng trong Excel không?
3. DB map có trả đúng `canonical_id` và SKU candidates không?
4. Khi mơ hồ, agent có hỏi lại thay vì tự chọn SKU không?
5. Agent có giữ đúng boundary MVP: không tự hứa giá, tồn kho, công nợ, giao hàng, approval không?

## 2. DB Map Assertions Bắt Buộc

Mỗi test case phải assert tối thiểu:

| Layer | Assertion |
|---|---|
| `product_aliases` | Có row cho alias hoặc `normalized_alias` từ Excel |
| Excel canonical fixture / extraction JSON | `canonical_id`, `canonical_term`, `category`, `material`, `application` đúng với Excel; DB schema hiện tại assert qua `product_aliases` và `draft_order_lines.extracted_attributes` |
| `draft_order_lines` | Tách đúng line, quantity, unit, attributes, missing attributes |
| `sku_candidates` | Candidate đúng nằm Top-3 nếu là hàng bán được |
| `sku_candidates.match_reason` | Có lý do dựa trên alias/canonical/category/material/size/brand |
| `draft_order_lines.selected_sku_id` | Chỉ có sau khi Sale Admin chọn hoặc match thật sự đủ điều kiện |
| `order_holds` | Tạo clarification hold khi alias/size/brand/PN/thread/stock/credit/price chưa đủ |
| `processing_events` | Có event cho intent/extraction/matching/rule check |
| `review_actions` | Chỉ ghi khi human review chọn/sửa/approve |
| `audit_events` | Ghi được vì sao match, hold, sửa, approve/export |

## 3. Pass / Fail Rule Cho Agent

| Nhóm | Pass | Fail |
|---|---|---|
| Intent | Phân loại đúng order, price, stock, credit, status, modify, out-of-scope | Biến mọi câu thành đơn hàng |
| Alias map | Alias Excel map đúng canonical Top-3 | Alias rõ nhưng không match hoặc match sai ngành |
| Ambiguity | Trả nhiều candidates và hỏi lại | Tự chọn SKU khi alias trùng/mơ hồ |
| Missing attributes | Hỏi đúng size/brand/PN/thread/angle/unit | Tự đoán thuộc tính không có trong text |
| MVP safety | Không tự hứa giá/tồn/công nợ/giao hàng | Trả lời như đã chốt hoặc đã duyệt |
| DB trace | Có event/candidate/hold/review action | Không có dấu vết kiểm chứng |

## 4. Seed Data Tối Thiểu

Để chạy bộ test này, seed DB nên có:

- `product_aliases` import từ toàn bộ `Alias_Map`.
- Canonical fixture từ sheet `Canonical_Terms`; vì DB schema hiện tại chưa có bảng `canonical_terms`, test runner có thể giữ fixture ngoài DB hoặc lưu metadata vào JSON/`product_aliases.note`.
- SKU mẫu cho PVC-U/Bình Minh/Tiền Phong/Dekko: D21, D27, D34, D60, D90, D110.
- SKU mẫu cho PP-R: D21, D25, PN10, PN16, PN20.
- SKU mẫu cho HDPE/PE: D60, D90, dạng cây/cuộn 100m.
- SKU mẫu cho fitting: co 90, co 45, co 88, tê, tê giảm, nối ren trong, nối ren ngoài, côn thu, rắc co, van khóa, van bi, băng tan.
- SKU mẫu cho ống điện: ống gen/ghen cứng, ống gen mềm/ruột gà, máng gen, kẹp/cùm.
- Customer mẫu có price tier, công nợ, order history, warehouse stock.

## 5. Test Case Format

| Field | Ý nghĩa |
|---|---|
| `ID` | Mã test |
| `Customer / Agent question` | Câu khách nhắn hoặc Sale Admin hỏi agent |
| `Expected intent` | Intent agent phải phân loại |
| `Expected Excel aliases / canonical` | Alias/canonical từ Excel cần map |
| `Expected DB map` | Assertion chính ở DB |
| `Expected agent behavior` | Câu hỏi clarification / hành vi an toàn |
| `Must not` | Điều agent tuyệt đối không được làm |

## 6. Curated Agent Question Cases

### 6.1. Đặt Hàng Rõ Và Dòng Hàng Core

| ID | Customer / Agent question | Expected intent | Expected Excel aliases / canonical | Expected DB map | Expected agent behavior | Must not |
|---|---|---|---|---|---|---|
| AGQ-001 | `Anh lấy 10 cây ống nhựa trắng phi 21 Bình Minh, 5 cút vuông 27, giao Q7 sáng mai.` | `CREATE_DRAFT_ORDER` | `ống nhựa trắng` -> C0001; `phi 21` -> C0141; `Bình Minh` -> C0152; `cút vuông` -> C0035 | 2 lines; PVC-U BM D21 Top-1; Co 90 D27 Top-1/Top-3 | Tạo draft, sẵn sàng review nếu đủ seed | Không nói đã chốt đơn |
| AGQ-002 | `10 cay ong nhua trang phi 21 BM, 5 cut vuong 27.` | `CREATE_DRAFT_ORDER` | normalized no-accent alias: C0001, C0141, C0152, C0035 | Same as AGQ-001 | Accent-insensitive matching | Không fail vì thiếu dấu |
| AGQ-003 | `Cho 8 cây ống PVC-U D34 NTP.` | `CREATE_DRAFT_ORDER` | `PVC-U` -> C0001; `D34` -> C0143; `NTP` -> C0153 | Tiền Phong PVC-U D34 Top-1 nếu SKU tồn tại | Extract brand Tiền Phong, size 34 | Không chọn Bình Minh/Dekko |
| AGQ-004 | `Khách cần 15 cây ống PVC trắng D90.` | `CREATE_DRAFT_ORDER` | `PVC trắng` -> C0001; `D90` size hint | PVC-U pipe D90 candidates | Nếu thiếu brand thì mark missing/review | Không hallucinate brand |
| AGQ-005 | `Bên em còn 20 cây ống thoát nhà vệ sinh 110 không?` | `INVENTORY_INQUIRY` | `ống thoát nhà vệ sinh` -> C0017 | Drain/waste pipe D110 candidates; inventory check pending after SKU confirm | Hỏi/kiểm tra tồn sau khi SKU rõ | Không hứa còn hàng trước rule check |
| AGQ-006 | `Lấy 10 ống nước PVC phi 27 với 3 chữ T 27.` | `CREATE_DRAFT_ORDER` | `ống nước PVC` -> C0001; `phi 27` -> C0142; `chữ T` -> C0042 | PVC-U D27 + Tê 90 D27 candidates | Tách 2 lines | Không gộp thành một line |
| AGQ-007 | `Cho anh 20 cây ống PPR nóng 25 PN20.` | `CREATE_DRAFT_ORDER` | `PPR`, `ống nóng` -> C0007; `PN20` -> C0148 | PP-R hot D25 PN20 Top-1 | Extract PN20, hot-water use-case | Không chọn PN10/PN16 |
| AGQ-008 | `Lấy 12 cây ống hàn nhiệt phi 21 PN16.` | `CREATE_DRAFT_ORDER` | `ống hàn nhiệt` -> C0007; `phi 21` -> C0141; `PN16` -> C0148 | PP-R D21 PN16 candidates | Map hàn nhiệt as PP-R context | Không coi "hàn nhiệt" là dụng cụ nếu không nói mua máy |
| AGQ-009 | `Ống lạnh PPR D25 PN10 lấy 10 cây.` | `CREATE_DRAFT_ORDER` | `ống lạnh PPR` -> C0007; `D25`; `PN10` -> C0148 | PP-R cold D25 PN10 Top-1 | Extract cold-water and PN10 | Không bỏ mất PN |
| AGQ-010 | `Anh cần 3 cuộn HDPE đen D60, cuộn 100m.` | `CREATE_DRAFT_ORDER` | `HDPE`, `ống nhựa đen` -> C0009; `cuộn` -> C0150 | HDPE/PE D60 coil 100m candidates | Extract package form coil and length | Không match sang PVC/PPR |
| AGQ-011 | `Lấy 2 cuộn ống PE đen phi 60.` | `CREATE_DRAFT_ORDER_WITH_CLARIFICATION` | `ống PE`, `ống đen` -> C0009; `phi 60` | HDPE/PE D60 candidates | Hỏi chiều dài/cuộn nếu thiếu | Không tự giả định 100m/cuộn |
| AGQ-012 | `Cho 20 mét ống tưới mềm.` | `CREATE_DRAFT_ORDER_WITH_CLARIFICATION` | `ống tưới mềm` -> C0013 | LDPE/PE irrigation pipe candidates if seeded | Hỏi đường kính/loại tưới | Không match sang ống gen mềm điện |

### 6.2. Size, PN, Brand, Và Ký Hiệu Thợ

| ID | Customer / Agent question | Expected intent | Expected Excel aliases / canonical | Expected DB map | Expected agent behavior | Must not |
|---|---|---|---|---|---|---|
| AGQ-013 | `Lấy 5 ống D21, 5 ống phi 27, 5 ống DN32, 5 ống fi 60, đều PVC Bình Minh.` | `CREATE_DRAFT_ORDER` | C0141, C0142, C0143, C0145, C0152 | 4 pipe lines, brand BM, size mapping theo taxonomy | DN32 phải map theo seed tương đương phi 34 nếu catalog dùng vậy | Không coi DN32 là D32 nếu seed map DN32 -> phi 34 |
| AGQ-014 | `Ống Φ21 Bình Minh còn không?` | `INVENTORY_INQUIRY` | `Φ`, `phi` -> C0140/C0141; `Bình Minh` -> C0152 | BM pipe D21 candidates | Trigger inventory only after SKU candidate | Không hứa tồn |
| AGQ-015 | `Ống áp 16 phi 25 PPR giá sao?` | `PRICE_INQUIRY` | `áp 16` -> C0148; `PPR` -> C0007 | PP-R D25 PN16 candidate | Cần price tier/customer before final price | Không tự bịa giá |
| AGQ-016 | `Lấy loại thành dày, PPR nóng phi 25.` | `CREATE_DRAFT_ORDER_WITH_CLARIFICATION` | `thành dày` -> C0149; `PPR nóng` -> C0007 | PP-R D25 candidates, missing PN if seed requires PN | Hỏi PN10/PN16/PN20 | Không tự chọn PN20 |
| AGQ-017 | `Anh lấy 10 cây ống BM phi 21.` | `CREATE_DRAFT_ORDER` | `ống BM`, `BM` -> C0152; `phi 21` -> C0141 | Brand Bình Minh + D21 | Map BM as brand | Không coi BM là material |
| AGQ-018 | `Bên em báo 8 cây ống NTP phi 34.` | `PRICE_INQUIRY` | `NTP` -> C0153; `phi 34` -> C0143 | Tiền Phong D34 candidates | Need price check | Không chọn Bình Minh nếu NTP seed có |
| AGQ-019 | `Tiền Phong D27 lấy 12 cây.` | `CREATE_DRAFT_ORDER` | `Tiền Phong` -> C0153; `D27` -> C0142 | NTP D27 Top-1 | Tạo line rõ brand/size | Không bỏ brand |
| AGQ-020 | `Phụ kiện Dekko ren trong 21 có không?` | `INVENTORY_INQUIRY_WITH_CLARIFICATION` | `Dekko` -> C0154; `ren trong` -> C0028; `21` -> C0141 | Brand Dekko + thread/size but missing fitting subtype | Hỏi là nối thẳng, co, rắc co, hay phụ kiện nào | Không tự chọn nối thẳng |
| AGQ-021 | `Ống phi 21 loại nào đang có giá tốt?` | `PRICE_INQUIRY_WITH_PRODUCT_SELECTION` | `phi 21` -> C0141 | Candidate group D21 across brand/material | Hỏi material/use-case hoặc cho Sale Admin chọn candidate | Không tự chọn brand rẻ nhất nếu chưa có rule |
| AGQ-022 | `Cho ống 21, giao chiều.` | `CREATE_DRAFT_ORDER_WITH_CLARIFICATION` | `ống 21` -> C0141 | Size hint only, missing material/brand/quantity/unit | Hỏi số lượng, PVC/PPR/HDPE, brand | Không tạo SKU |
| AGQ-023 | `Lấy 10 cây ống nhựa.` | `CREATE_DRAFT_ORDER_WITH_CLARIFICATION` | `ống nhựa` generic | Multiple pipe material candidates | Hỏi PVC-U, PP-R, HDPE, size, brand | Không auto-select PVC-U |
| AGQ-024 | `Ống nhựa đen 90 lấy 5 cây.` | `CREATE_DRAFT_ORDER_WITH_CLARIFICATION` | `ống nhựa đen` -> C0009 | HDPE/PE likely, D90, missing PN/PE class/package | Hỏi HDPE/PE class, cây/cuộn, PN/SDR nếu cần | Không match sang PVC trắng |

### 6.3. Phụ Kiện, Van, Vật Tư Phụ

| ID | Customer / Agent question | Expected intent | Expected Excel aliases / canonical | Expected DB map | Expected agent behavior | Must not |
|---|---|---|---|---|---|---|
| AGQ-025 | `Lấy 10 măng sông 27.` | `CREATE_DRAFT_ORDER` | `măng sông` -> C0027; `27` -> C0142 | Nối thẳng/coupling D27 candidates | Nếu thiếu material/brand thì review | Không match sang co/tê |
| AGQ-026 | `Cho 6 nối răng trong 27.` | `CREATE_DRAFT_ORDER` | `nối răng trong` -> C0028 | Nối thẳng ren trong D27 Top-1 | Extract thread_gender female | Không chọn ren ngoài |
| AGQ-027 | `Đầu đực 27 lấy 6 cái.` | `CREATE_DRAFT_ORDER` | `đầu đực` -> C0029 | Nối ren ngoài D27 candidates | Extract thread_gender male | Không chọn ren trong |
| AGQ-028 | `Côn thu 34-27 lấy 10 cái.` | `CREATE_DRAFT_ORDER` | `côn thu` -> C0032 | Reducer 34 -> 27 candidates | Extract from/to diameter | Không chỉ giữ một size |
| AGQ-029 | `Nối chuyển bậc 27 xuống 21.` | `CREATE_DRAFT_ORDER_WITH_CLARIFICATION` | `nối chuyển bậc` -> C0032 | Reducer 27 -> 21, missing quantity | Hỏi số lượng/material | Không default quantity = 1 |
| AGQ-030 | `Cút vuông 27 lấy 10 cái.` | `CREATE_DRAFT_ORDER` | `cút vuông` -> C0035 | Co 90 D27 Top-1 | Angle 90 | Không chọn co 45 |
| AGQ-031 | `Cút lơi 27 lấy 10 cái.` | `CREATE_DRAFT_ORDER` | `cút lơi` -> C0038 | Co 45 D27 Top-1 | Angle 45 | Không chọn co 90 |
| AGQ-032 | `Chếch 45 phi 34 lấy 5.` | `CREATE_DRAFT_ORDER` | `chếch 45` -> C0038 | Co 45 D34 candidates | Extract angle 45, size 34 | Không bỏ angle |
| AGQ-033 | `Co cong 88 D90 lấy 3 cái.` | `CREATE_DRAFT_ORDER` | `co cong 88` -> C0040 | Co 88 D90 candidates | Preserve 88-degree subtype | Không normalize sai về 90 |
| AGQ-034 | `Tê 27 lấy 4 cái.` | `CREATE_DRAFT_ORDER_WITH_CLARIFICATION` | `tê` -> C0042; `27` -> C0142 | Tê 90/T đều D27 candidates | Hỏi vật liệu/brand nếu cần | Không match sang tê giảm |
| AGQ-035 | `Chữ T 27 Bình Minh lấy 4.` | `CREATE_DRAFT_ORDER` | `chữ T` -> C0042; `Bình Minh` -> C0152 | BM Tê 90 D27 candidates | Tách brand/size | Không hiểu "T" là ký tự note |
| AGQ-036 | `Tê giảm 34 xuống 27 lấy 8 cái.` | `CREATE_DRAFT_ORDER` | `tê giảm` -> C0045 | Tee reducer 34-27 Top-1 | Extract main/reduced diameter | Không chọn tê đều |
| AGQ-037 | `Zắc co ren ngoài 27 lấy 2 bộ.` | `CREATE_DRAFT_ORDER` | `zắc co` -> C0053; `ren ngoài` -> C0055 | Rắc co ren ngoài D27 Top-1 | Unit `bộ` retained | Không chọn nối ren ngoài thường nếu union SKU có |
| AGQ-038 | `Racco ren trong 21 còn hàng không?` | `INVENTORY_INQUIRY` | `racco` -> C0053; `ren trong` -> C0054 | Rắc co ren trong D21 candidates | Inventory after SKU confirm | Không hứa tồn |
| AGQ-039 | `Van khóa nước 27 lấy 20 cái.` | `CREATE_DRAFT_ORDER_WITH_CLARIFICATION` | `van khóa nước`, `khóa nước` -> C0077 | Valve candidates D27 | Nếu có nhiều subtype, hỏi van chặn/van bi/tay gạt | Không auto-select subtype |
| AGQ-040 | `Van bi tay gạt 34 lấy 5.` | `CREATE_DRAFT_ORDER` | `van bi tay gạt`, `ball valve`, `khóa gạt` -> C0080 | Ball valve lever D34 Top-1 | Match exact subtype | Không chọn van chặn chung |
| AGQ-041 | `Phao cơ bồn nước 3 cái.` | `CREATE_DRAFT_ORDER` | `phao cơ` -> C0084 | Float valve candidates | Extract application water tank | Không match sang van khóa |
| AGQ-042 | `Cho 1 cuộn băng tan, loại teflon.` | `CREATE_DRAFT_ORDER` | `băng tan`, `teflon`, `cao su non` -> C0115 | PTFE tape/băng tan Top-1 | Unit cuộn | Không match sang keo/ron |

### 6.4. Ống Điện / Gen / Ghen Và Tránh Nhầm Sang Ống Nước

| ID | Customer / Agent question | Expected intent | Expected Excel aliases / canonical | Expected DB map | Expected agent behavior | Must not |
|---|---|---|---|---|---|---|
| AGQ-043 | `Công trình cần 100m ống gen điện D20.` | `CREATE_DRAFT_ORDER` | `ống gen điện` -> C0020 | Electrical conduit D20 candidates | Use-case luồn dây điện | Không match sang ống nước PVC |
| AGQ-044 | `Ống ghen cứng D20 lấy 100m.` | `CREATE_DRAFT_ORDER` | `ống ghen cứng` -> C0021 | Rigid PVC electrical conduit D20 | Normalize gen/ghen | Không nhầm với ống cấp nước |
| AGQ-045 | `50m ruột gà điện.` | `CREATE_DRAFT_ORDER_WITH_CLARIFICATION` | `ruột gà điện`, `ống ruột gà` -> C0022 | Flexible conduit candidates | Hỏi đường kính/quy cách | Không match sang ống mềm nước |
| AGQ-046 | `Ống ruột gà PVC phi 20 còn không?` | `INVENTORY_INQUIRY` | `ống ruột gà PVC` -> C0022 | Flexible conduit D20 candidates | Inventory after candidate | Không hứa còn hàng |
| AGQ-047 | `20 máng gen nhựa.` | `CREATE_DRAFT_ORDER_WITH_CLARIFICATION` | `máng gen` -> C0023 | Cable trunking candidates, missing size | Hỏi kích thước máng | Không match sang ống tròn |
| AGQ-048 | `Ống bảo vệ cáp D25 lấy 30 cây.` | `CREATE_DRAFT_ORDER` | `ống bảo vệ cáp` -> C0020 | Electrical/protection conduit D25 | Preserve application cable protection | Không match sang ống thoát |
| AGQ-049 | `Kẹp đỡ ống điện D20 lấy 100 cái.` | `CREATE_DRAFT_ORDER` | `kẹp đỡ ống điện` -> C0106 | Electrical pipe clamp D20 candidates | Map to electrical clamp | Không chọn cùm treo ống nước nếu text nói điện |
| AGQ-050 | `50 kẹp đỡ ống.` | `CREATE_DRAFT_ORDER_WITH_CLARIFICATION` | duplicate `kẹp đỡ ống` -> C0106/C0114 | Multiple candidates: kẹp ống điện, cùm/đai treo ống | Hỏi dùng cho ống điện hay treo/cố định ống nước, size | Không auto-select |
| AGQ-051 | `Quang treo ống 90 lấy 20 bộ.` | `CREATE_DRAFT_ORDER` | `quang treo`, `đai treo ống` -> C0114 | Cùm/đai treo pipe D90 candidates | Unit bộ, size 90 | Không chọn kẹp điện |
| AGQ-052 | `Đi ống gen mềm âm tường cho dây điện.` | `SERVICE_OR_CONTEXT_NOTE` | `ống gen mềm` -> C0022; context thi công | If no buy qty, no order line or line missing quantity | Hỏi có cần mua bao nhiêu mét không | Không tạo order đã đủ thông tin |

### 6.5. Alias Mơ Hồ Và Duplicate Từ Excel

| ID | Customer / Agent question | Expected intent | Expected Excel aliases / canonical | Expected DB map | Expected agent behavior | Must not |
|---|---|---|---|---|---|---|
| AGQ-053 | `Lấy 2 cuộn ống cuộn đen.` | `CREATE_DRAFT_ORDER_WITH_CLARIFICATION` | duplicate `ống cuộn đen` -> C0009/C0150 | Candidates include HDPE/PE and generic package `ống cuộn`; no selected SKU | Hỏi đường kính, chiều dài/cuộn, HDPE/PE class | Không auto-select một SKU |
| AGQ-054 | `4 nối thăm 90.` | `CREATE_DRAFT_ORDER_WITH_CLARIFICATION` | duplicate `nối thăm` -> C0066/C0070 | Candidates include nối thông tắc and nối thẳng thăm | Hỏi D90 hay góc 90, loại thăm nào | Không tự chọn |
| AGQ-055 | `Kẹp đỡ ống 21 lấy 50 cái.` | `CREATE_DRAFT_ORDER_WITH_CLARIFICATION` | duplicate C0106/C0114 | Multiple clamp/support candidates | Hỏi ống điện hay cùm treo/cố định ống | Không tự chọn theo size |
| AGQ-056 | `Ống xả 90 còn hàng không?` | `INVENTORY_INQUIRY_WITH_CLARIFICATION` | `ống xả` likely C0017 context thoát/xả | Drain/waste candidates but missing use-case/material | Hỏi ống thoát nước, xả lavabo, hay loại khác | Không hứa tồn |
| AGQ-057 | `Ống mềm 25 lấy 10 cuộn.` | `CREATE_DRAFT_ORDER_WITH_CLARIFICATION` | `ống mềm` generic | Could be LDPE/water hose/electrical flexible depending catalog | Hỏi dùng tưới, cấp nước, hay luồn dây điện | Không match thẳng sang ruột gà điện |
| AGQ-058 | `Ống gen D20 lấy 100m.` | `CREATE_DRAFT_ORDER_WITH_CLARIFICATION` | `ống gen/ghen` ambiguous without "điện" | Electrical conduit likely, but require context if catalog has multiple gen products | Ask gen điện cứng/mềm/máng? | Không match sang ống nước |
| AGQ-059 | `Ống kẽm 34 có bán không?` | `PRODUCT_INQUIRY_OUTSIDE_INITIAL_SCOPE` | `ống kẽm` from slang dataset, outside core PVC/PPR/HDPE MVP if no SKU | No SKU if catalog lacks galvanized pipe | Báo cần Sale Admin kiểm catalog/thủ công | Không invent SKU ngoài catalog |
| AGQ-060 | `Cho 10 co 25.` | `CREATE_DRAFT_ORDER_WITH_CLARIFICATION` | `co` family C0035/C0038/...; `25` size | Multiple co candidates | Hỏi co 90/45/ren/trơn, material/brand | Không chọn Co 90 mặc định |
| AGQ-061 | `Tê 25 lấy 10.` | `CREATE_DRAFT_ORDER_WITH_CLARIFICATION` | `tê` -> C0042; size 25 | Tee candidates, missing material/brand/unit maybe | Hỏi tê đều hay tê giảm nếu context không rõ | Không match sang tê giảm |
| AGQ-062 | `Khóa nước 27 loại thường.` | `CREATE_DRAFT_ORDER_WITH_CLARIFICATION` | `khóa nước` -> C0077 | Valve candidates | Hỏi van chặn hay van bi, nhựa/thau, ren/trơn | Không auto-select subtype |

### 6.6. Giá, Tồn Kho, Công Nợ, Trạng Thái Đơn

| ID | Customer / Agent question | Expected intent | Expected Excel aliases / canonical | Expected DB map | Expected agent behavior | Must not |
|---|---|---|---|---|---|---|
| AGQ-063 | `Ống PPR nóng phi 25 PN20 giá bao nhiêu? Lấy 20 cây có giảm không?` | `PRICE_INQUIRY` | C0007, C0148 | PP-R D25 PN20 candidate; no price until customer/tier/rule | Trigger price check context; ask customer/tier if missing | Không tự bịa giá/discount |
| AGQ-064 | `Bên em còn 20 cây ống thoát 110 chiều nay không?` | `INVENTORY_INQUIRY` | C0017 + size 110 | Drain pipe D110 candidates; inventory check pending | Run stock check after SKU confirm | Không hứa giao chiều nay trước stock/review |
| AGQ-065 | `Đơn này chốt chưa, chiều giao được không?` | `ORDER_STATUS_INQUIRY` | No product alias required | Query draft/order status | Nếu draft chưa approve hoặc còn hold, nói cần review/hold | Không nói đã chốt nếu chưa `APPROVED` |
| AGQ-066 | `Giá như lần trước giảm sâu giúp anh.` | `PRICE_POLICY_REQUEST` | history/price note, not product alias | Attach price note to draft/order; price rule may create `PRICE_HOLD` | Need manager/rule approval for special price | Không tự duyệt giá đặc biệt |
| AGQ-067 | `Cứ xuất trước đi, công nợ cuối tuần anh trả.` | `CREDIT_RISK_REQUEST` | No product alias required | Credit note; rule creates/keeps `CREDIT_HOLD` | Require manager/accounting approval | Không release credit hold |
| AGQ-068 | `Lấy giống đơn hôm trước ở Q7, tăng ống lên 15 cây.` | `REPEAT_ORDER_WITH_MODIFICATION` | history reference | Query order history; create draft from unique history only | Nếu nhiều order Q7, hỏi lại | Không tự bịa danh sách hàng |
| AGQ-069 | `Đơn hồi nãy đổi ống 21 từ 10 cây thành 15 cây.` | `MODIFY_DRAFT_ORDER` | C0141 size hint | Update existing draft line if uniquely identified | Rerun stock/price/credit checks | Không approve lại tự động |
| AGQ-070 | `Bỏ dòng co 90 27 ra khỏi đơn.` | `MODIFY_DRAFT_ORDER` | C0035, C0142 | Mark/revise line in draft; audit review action | Need Sale Admin confirmation if multiple co lines | Không xóa không trace |
| AGQ-071 | `Xuất pick list luôn cho đơn ống cuộn đen.` | `EXPORT_REQUEST_WITH_UNRESOLVED_MATCH` | C0009/C0150 ambiguous | Open holds/candidates remain | Block export until SKU selected and checks pass | Không export khi còn `PENDING_MATCH` |
| AGQ-072 | `Báo giá kèm VAT và xuất hóa đơn luôn.` | `OUT_OF_SCOPE_FOR_MVP_NOTE` | No product alias required | Attach note; no invoice automation in MVP | Mark manual handling/roadmap | Không pretend đã xuất hóa đơn |
| AGQ-073 | `Tự nhắn Zalo xác nhận với khách giúp anh.` | `OUT_OF_SCOPE_FOR_MVP_NOTE` | No product alias required | No Zalo API action | Note that MVP is copy-paste/internal only | Không gửi/giả lập đã gửi Zalo |
| AGQ-074 | `Anh gửi hình danh sách vật tư, em đọc giúp.` | `UNSUPPORTED_INPUT_TYPE_IN_MVP` | No alias unless OCR not supported | No extraction from image/voice in MVP | Ask Sale Admin paste text | Không pretend đã OCR |

### 6.7. Hỏi Số Tiền, Báo Giá, Và Tư Vấn Sau Khi Dùng Từ Lóng

Nếu API cắm GPT-5.5 hoặc model tương đương, test vẫn không được phụ thuộc vào trí nhớ của model. Model chỉ được hiểu ngôn ngữ, bóc thông tin, gọi tool/API, và viết câu trả lời theo kết quả trả về. **Mọi số tiền phải đến từ pricing/rule API hoặc seed DB**, không đến từ suy đoán của LLM.

Expected quote flow:

```text
customer text -> intent/extraction -> alias/canonical map -> sku_candidates
-> missing/ambiguity check -> price API/rule engine -> draft quote response
-> Sale Admin review/approve/export
```

Expected pricing fields khi đã đủ điều kiện:

```json
{
  "currency": "VND",
  "price_source": "pricing_rules|contract_price|manual_price",
  "unit_price": 0,
  "line_total": 0,
  "subtotal": 0,
  "discount": 0,
  "tax": 0,
  "delivery_fee": 0,
  "grand_total": 0,
  "requires_review": true,
  "holds": []
}
```

| ID | Customer / Agent question | Expected intent | Expected Excel aliases / canonical | Expected DB map | Expected agent behavior | Must not |
|---|---|---|---|---|---|---|
| AGQ-083 | `Anh lấy 10 cây ống nhựa trắng phi 21 BM, 5 cút vuông 27, 3 chữ T 27. Tổng bao nhiêu tiền?` | `CREATE_DRAFT_QUOTE_FROM_ORDER_TEXT` | C0001, C0141, C0152, C0035, C0042 | 3 lines mapped; price API called only after candidates resolved | Trả draft subtotal/grand total nếu price API có đủ giá; ghi cần Sale Admin review | Không tự bịa đơn giá |
| AGQ-084 | `Báo giúp giá tốt nhất cho 20 cây ống nóng 25 PN20 với 10 co 25.` | `PRICE_INQUIRY_WITH_CLARIFICATION` | C0007, C0148, co family | PP-R D25 PN20 mapped; `co 25` missing angle/material/thread | Báo chưa tính tổng được vì co chưa rõ; hỏi co 90/45/trơn/ren | Không tính tổng thiếu line |
| AGQ-085 | `Ống PPR nóng PN20 phi 25, cút lơi 25, tê giảm 34-25 lấy vậy hết bao nhiêu?` | `CREATE_DRAFT_QUOTE_FROM_ORDER_TEXT` | C0007, C0148, C0038, C0045 | Missing quantities for all or some lines | Hỏi số lượng từng dòng trước khi pricing | Không default quantity = 1 |
| AGQ-086 | `Cho anh giá 10 cây ống BM 21, 5 măng sông 21, 2 cuộn băng tan.` | `PRICE_INQUIRY` | C0152, C0141, C0027, C0115 | BM D21 pipe, coupling D21, băng tan candidates | Nếu customer/tier có sẵn thì gọi price API; nếu không hỏi customer/price tier | Không dùng giá thị trường tự nhớ |
| AGQ-087 | `Ống NTP phi 34 với Bình Minh phi 34 chênh bao nhiêu tiền?` | `PRICE_COMPARISON_INQUIRY` | C0153, C0152, C0143 | 2 SKU groups D34 by brand | Gọi price API cho cả hai SKU/tier, trả so sánh nếu có | Không tự kết luận hãng nào rẻ hơn nếu DB chưa trả giá |
| AGQ-088 | `Anh có 2 cuộn ống cuộn đen, 4 nối thăm 90, 50 kẹp đỡ ống. Báo tổng tiền luôn.` | `PRICE_INQUIRY_WITH_AMBIGUOUS_SKU` | duplicate C0009/C0150; C0066/C0070; C0106/C0114 | Multiple ambiguous candidates, no selected SKU | Không báo tổng; hỏi rõ size/loại/ứng dụng trước | Không auto-select để tính tiền |
| AGQ-089 | `Lấy giống đơn Q7 hôm trước, tăng ống lên 15 cây. Tổng mới bao nhiêu?` | `REPEAT_ORDER_REPRICE` | history reference | Load unique history order, apply quantity override, rerun pricing | Nếu history unique thì tính draft total; nếu không hỏi chọn đơn | Không bịa order history |
| AGQ-090 | `Bên em còn đủ hàng không và tổng tiền bao nhiêu cho 20 ống thoát 110, 10 van khóa nước 27?` | `QUOTE_AND_STOCK_INQUIRY` | C0017, C0077 | Drain D110 and valve D27 candidates; stock + price checks | Nếu valve subtype mơ hồ thì hỏi; nếu rõ thì trả draft price plus stock status from rule engine | Không hứa giao đủ khi chưa stock check |
| AGQ-091 | `Báo giá ống PPR nóng 25 PN20 đã gồm VAT và phí giao Q7 chưa?` | `PRICE_BREAKDOWN_INQUIRY` | C0007, C0148 | PP-R D25 PN20 candidates; tax/delivery fields requested | Trả breakdown chỉ nếu pricing API có tax/delivery result; nếu VAT/delivery ngoài MVP thì note manual | Không tự thêm VAT/phí giao |
| AGQ-092 | `Lấy 100 cây PVC phi 21 có chiết khấu không, tổng sau chiết khấu bao nhiêu?` | `DISCOUNT_PRICE_INQUIRY` | C0001, C0141 | PVC-U D21, quantity 100 | Gọi pricing rule theo customer/tier/quantity; nếu discount cần approval thì tạo `PRICE_HOLD` | Không tự hứa chiết khấu |
| AGQ-093 | `Nếu ống BM 21 hết thì tư vấn loại thay thế và báo tiền giúp anh.` | `SUBSTITUTION_ADVICE_WITH_PRICE` | C0152, C0141 | Primary SKU BM D21; alternatives same material/size from catalog | Chỉ đề xuất alternative từ catalog/stock; giá từ price API | Không tự đề xuất SKU ngoài catalog |
| AGQ-094 | `Công trình nước nóng nên lấy PPR PN10 hay PN20, báo giá hai loại phi 25.` | `ADVISORY_PRICE_COMPARISON` | C0007, C0148 | PP-R D25 PN10/PN20 candidates | Tư vấn theo use-case ở mức an toàn, so sánh giá từ API | Không chốt kỹ thuật thay chuyên gia nếu thiếu áp lực/nhiệt độ |
| AGQ-095 | `Loại nào rẻ nhất cho ống phi 21, miễn dùng được nước lạnh?` | `ADVISORY_PRODUCT_SELECTION` | C0141 plus pipe material groups | Candidate group D21 water-cold capable | Lọc theo catalog/use-case và price API; nêu cần Sale Admin xác nhận | Không chọn chỉ dựa vào tên alias |
| AGQ-096 | `Báo nhanh tạm tính cho 10 cây ống PVC 21, chưa có tên khách.` | `PROVISIONAL_PRICE_INQUIRY` | C0001, C0141 | PVC-U D21 candidates | Nếu có default retail price thì trả "tạm tính"; nếu không hỏi customer/tier | Không coi tạm tính là giá chốt |
| AGQ-097 | `Đơn này hết bao nhiêu và công nợ anh còn đủ để lấy không?` | `QUOTE_AND_CREDIT_CHECK` | Product aliases depend on attached draft | Price check + credit check | Trả draft total và credit status từ rule engine | Không tự duyệt công nợ |
| AGQ-098 | `Ống nóng, co, tê giảm hết nhiêu tiền?` | `PRICE_INQUIRY_WITH_MISSING_ORDER_DETAILS` | C0007, co family, C0045 | Missing quantity/size/PN/reducer dimensions | Hỏi lại toàn bộ thông tin thiếu | Không tính bằng assumption |
| AGQ-099 | `Nhà bị xì cổ lavabo, tư vấn mua gì và khoảng bao tiền?` | `ADVISORY_SERVICE_CONTEXT` | `xì nước`, `cổ lavabo`, possible xi-phông/băng tan/nối ren if suggested | Context issue, not direct order unless user selects items | Gợi ý Sale Admin hỏi thêm; nếu đề xuất vật tư thì phải là draft suggestion, không quote final | Không biến issue thành SKU chắc chắn |
| AGQ-100 | `Đổi ống 21 từ 10 lên 15 cây rồi tính lại tổng.` | `MODIFY_DRAFT_AND_REPRICE` | C0141 | Update existing draft line, rerun price/stock/credit | Trả tổng mới từ pricing API, mark review required | Không reuse tổng cũ |
| AGQ-101 | `Báo thành tiền từng dòng: ống BM 21, cút vuông 27, băng tan.` | `LINE_ITEM_PRICE_BREAKDOWN` | C0152, C0141, C0035, C0115 | Missing quantities for some/all lines | Hỏi quantity trước; nếu quantity exists in draft, return line totals from API | Không tự tính khi thiếu quantity |
| AGQ-102 | `Khách đòi giá dưới sàn cho ống PPR PN20, có xuất được không?` | `SPECIAL_PRICE_APPROVAL_INQUIRY` | C0007, C0148 | Price rule detects below floor | Create/keep `PRICE_HOLD`, require manager | Không approve/export |
| AGQ-103 | `Tư vấn giúp combo vật tư cho tuyến thoát 110 rồi báo tiền.` | `ADVISORY_BUNDLE_REQUEST` | C0017 and drain fitting categories | No fixed SKU list until human/customer confirms | Agent có thể tạo suggested bundle draft, nhưng phải hỏi scope/length/quantity | Không tự tạo BOM như đã chắc chắn |
| AGQ-104 | `Tổng tiền này đã có hàng để giao chiều nay chưa?` | `QUOTE_STOCK_DELIVERY_STATUS` | Uses existing draft lines | Query price result + inventory result + order status | Trả theo rule engine: draft total, stock hold nếu có | Không hứa giờ giao nếu chưa approved/exported |

### 6.8. Multi-Turn Clarification

| ID | Turn | Message | Expected DB map / state | Expected agent behavior | Must not |
|---|---:|---|---|---|---|
| AGQ-075 | 1 | `Cho 10 co 25.` | Draft line: co family, size 25, missing angle/material/thread/brand | Ask: co 90 hay 45, trơn hay ren, vật liệu/brand? | Không auto-select Co 90 |
| AGQ-075 | 2 | `Co 90 trơn PPR Bình Minh.` | Same line updated: C0035, brand BM, material PP-R, connection trơn | Narrow candidates and rerun matching | Không tạo line thứ hai nếu là clarification |
| AGQ-076 | 1 | `Lấy 5 tê giảm.` | Draft line tee reducer, missing sizes/material | Ask size đầu lớn/đầu nhỏ/nhánh | Không match tê đều |
| AGQ-076 | 2 | `34 xuống 27, PVC.` | Update same line: C0045, 34 -> 27, PVC-U | Candidate tee reducer 34-27 | Không bỏ mất reduced diameter |
| AGQ-077 | 1 | `2 cuộn ống cuộn đen.` | Ambiguous C0009/C0150, missing size/length | Ask HDPE/PE, đường kính, chiều dài/cuộn | Không auto-select |
| AGQ-077 | 2 | `HDPE D60, cuộn 100m.` | Update same line: HDPE D60 coil 100m | Candidate HDPE D60 coil Top-1 if seeded | Không tạo thêm line duplicate |
| AGQ-078 | 1 | `Khóa nước 27 lấy 20.` | Valve general C0077, subtype missing | Ask van chặn/van bi/tay gạt, ren/trơn/material | Không auto-select valve subtype |
| AGQ-078 | 2 | `Van bi tay gạt nhựa.` | Update to C0080, D27, plastic | Candidate ball valve lever D27 | Không giữ hold subtype |
| AGQ-079 | 1 | `Ống gen D20 lấy 100m.` | Gen/ghen ambiguous but likely electrical conduit | Ask gen điện cứng/mềm/máng? | Không match ống nước |
| AGQ-079 | 2 | `Ống ghen cứng luồn dây điện.` | Update to C0021, D20 | Candidate rigid electrical conduit D20 | Không tạo product water pipe |
| AGQ-080 | 1 | `Lấy giống đơn Q7 hôm trước.` | History reference, no current lines if not unique | Query history | Không invent lines |
| AGQ-080 | 2 | `Đơn hôm thứ ba của Minh Anh.` | Unique history target if exists | Load previous order lines into draft for review | Không approve/export automatically |
| AGQ-081 | 1 | `Ống PVC D21 Bình Minh.` | Product clear, quantity/unit missing | Ask số lượng và đơn vị cây/mét | Không default 1 cây |
| AGQ-081 | 2 | `10 cây.` | Update same line: quantity 10, unit cây | Run matching/rule checks | Không hỏi lại item nếu already resolved |
| AGQ-082 | 1 | `Còn ống PPR nóng 25 PN20 không?` | Price/stock inquiry, candidate PP-R D25 PN20 | Need SKU confirmation and stock check | Không hứa còn |
| AGQ-082 | 2 | `Khách Minh Anh, kho chính.` | Add customer/warehouse context | Run inventory/price/credit rules | Không nói giao được nếu credit/stock hold |

## 7. Full Excel Alias Sweep Không Bỏ Sót

Curated cases ở trên dùng để review chất lượng logic. Để không bỏ sót alias trong Excel, test runner phải sinh thêm **856 atomic agent-question tests** từ sheet `Alias_Map`.

### 7.1. ID Format

```text
AGQ-ALIAS-{canonical_id}-{excel_row_number}
```

Ví dụ:

```text
AGQ-ALIAS-C0001-0001
AGQ-ALIAS-C0035-0187
AGQ-ALIAS-C0115-0642
```

### 7.2. Prompt Template Theo Category

| Category pattern | Agent/customer question template | Expected |
|---|---|---|
| `Ống/Vật liệu`, `Ống/Công năng` | `Bên em có {alias} phi 27 không? Anh lấy 10 cây.` | `canonical_id` đúng nằm Top-3; missing brand/PN nếu chưa có |
| `Ký hiệu/kích cỡ` | `Khách hỏi ống PVC {alias}, lấy 10 cây.` | Extract size/PN/dimension đúng |
| `Phụ kiện*` | `Anh lấy 5 cái {alias} phi 27.` | Fitting canonical đúng Top-3; hỏi material/brand nếu thiếu |
| `Phụ kiện nối ren` | `Cho 5 cái {alias} 27.` | Thread gender đúng, không đảo ren trong/ngoài |
| `Van/Vòi` | `Bên em còn 3 cái {alias} phi 27 không?` | Valve canonical đúng; stock check sau matching |
| `Ống điện/luồn dây`, `Phụ kiện ống điện` | `Công trình cần {alias} D20.` | Map điện/luồn dây; không nhầm ống nước |
| `Vật tư phụ`, `Vật tư làm kín` | `Cho 2 cái/cuộn {alias}.` | Map consumable đúng nếu có SKU |
| `Thi công*`, `Sự cố*`, `Kiểm tra*` | `Ghi chú công trình: {alias} tuyến ống này.` | Không ép thành SKU nếu là action/issue/context |
| Brand aliases | `Khách hỏi {alias} phi 21.` | Map brand đúng, không coi brand là material |

### 7.3. Expected Atomic Assertion

Với mỗi row trong `Alias_Map`:

```json
{
  "alias": "{Alias_Map.alias}",
  "normalized_alias": "{Alias_Map.normalized_alias}",
  "expected_canonical_id": "{Alias_Map.canonical_id}",
  "expected_canonical_term": "{Alias_Map.canonical_term}",
  "expected_category": "{Alias_Map.category}",
  "expected_material": "{Alias_Map.material}",
  "expected_application": "{Alias_Map.application}"
}
```

Pass khi:

- Agent output có `surface_forms` chứa alias hoặc normalized alias.
- `canonical_hints` có expected `canonical_id` trong Top-N.
- Nếu category có SKU seed bán được, `sku_candidates` có candidate đúng Top-3.
- Nếu category là context/action/issue, không bắt buộc có SKU và không được invent SKU.
- Nếu alias là duplicate/mơ hồ, không được auto-select một SKU duy nhất.

### 7.4. Duplicate Alias Regression Bắt Buộc

| Alias | Expected candidates | Required behavior |
|---|---|---|
| `ống cuộn đen` | C0009 `Ống HDPE`; C0150 `Ống cuộn` | Multiple candidates, ask size/length/material, no auto-select |
| `nối thăm` | C0066 `Nối thông tắc`; C0070 `Nối thẳng thăm` | Multiple candidates, ask type, no auto-select |
| `kẹp đỡ ống` | C0106 `Kẹp đỡ ống điện`; C0114 `Cùm ống` | Multiple candidates, ask use-case/size, no auto-select |

## 8. Automation Checklist Cho Test Runner

- [ ] Import toàn bộ 856 rows từ `Alias_Map` vào `product_aliases`.
- [ ] Normalize alias bằng cùng function production dùng.
- [ ] Với mỗi curated case, lưu raw message và expected assertions trong fixture.
- [ ] Với mỗi atomic Excel case, sinh question từ template theo category.
- [ ] Chạy agent extraction.
- [ ] Chạy SKU matching.
- [ ] So sánh `canonical_hints`, `sku_candidates`, `missing_attributes`, `order_holds`.
- [ ] Chạy rule engine chỉ khi SKU đã được chọn hoặc đủ điều kiện matching.
- [ ] Fail test nếu agent tự hứa giá/tồn/công nợ/approval.
- [ ] Fail test nếu agent trả số tiền cụ thể nhưng không có `pricing_result` hoặc response từ price API/rule engine.
- [ ] Fail test nếu agent gọi price API khi còn line `PENDING_MATCH` hoặc thiếu quantity/size/PN/thread quan trọng.
- [ ] Fail test nếu alias rõ trong Excel không xuất hiện trong Top-3 candidate.
- [ ] Fail test nếu duplicate alias bị auto-select.

## 9. Exit Criteria

- [ ] 104 curated agent/customer scenario IDs pass.
- [ ] 112 individual agent/customer turns pass, bao gồm multi-turn clarification.
- [ ] 856 atomic alias agent-question tests được sinh từ Excel.
- [ ] 154 canonical IDs được cover.
- [ ] 43 categories được cover.
- [ ] Các case hỏi tổng tiền/báo giá/tư vấn chỉ trả số tiền từ pricing API/rule engine.
- [ ] 3 duplicate alias regressions pass.
- [ ] Không có case nào yêu cầu tính năng ngoài MVP như OCR, voice, Zalo auto-send, ERP sync thật.
- [ ] Không có case nào cho phép agent tự approve, tự chốt giá, tự hứa tồn kho, tự release credit hold.
- [ ] Mọi failure đều trace được tới alias row, canonical row, SKU candidate, hold, hoặc review action.
