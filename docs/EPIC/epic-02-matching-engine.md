# 1. TÊN EPIC (Epic Title)
`Xây dựng + Hybrid Matching Engine & UoM Service + để ánh xạ danh pháp lóng và chuẩn hóa đơn vị đo lường`

# 2. MÔ TẢ EPIC (Epic Statement)
Là một **Hệ thống OrderFlow AI**
Tôi muốn **sử dụng kỹ thuật tìm kiếm Hybrid (Full-Text Search + PostgreSQL Trigram) kết hợp với Service Quy đổi đơn vị (UoM) độc lập**
Để tôi có thể **tìm ra chính xác mã SKU kỹ thuật và tính toán đúng số lượng lưu kho từ dữ liệu thô của khách hàng mà không bị sai sót do từ đồng nghĩa hay sai lệch kích thước.**

# 3. MỤC TIÊU KINH DOANH (Business Goals & Metrics)
- **Vấn đề đang gặp phải (Pain-point):** Sai 1 ly đi 1 dặm (nhầm "co 90" với "co 45"). Việc quy đổi đơn vị cứng nhắc ("1 ống = 4m") gây sai lệch tồn kho vì mỗi loại ống/hãng có chiều dài khác nhau.
- **Giá trị mang lại (Value):** Đảm bảo an toàn tuyệt đối. Tự động chốt đơn nếu độ chính xác cao. Giữ lại cho người duyệt nếu độ chính xác thấp (tránh giao sai hàng).
- **Chỉ số đo lường (Metrics/KPIs):** 
  - **Auto-confirm (Tự động chốt):** Top-1 accuracy ≥ 95% (Hệ thống tự chốt nếu Confidence ≥ 0.8).
  - **Needs Clarification (Cần xác nhận):** Top-5 recall = 100% (Hiển thị 5 ứng viên đúng nhất để Admin chọn nếu Confidence < 0.8).
  - **Đo lường Vận hành:** Thời gian trung bình Admin xử lý các case `NEEDS_CLARIFICATION` phải < 10 giây/đơn.

# 4. PHẠM VI (Scope & Boundaries)
- ✅ **In-scope (Sẽ bao gồm):**
  - Database: Bảng `Products`, `Aliases`, và bảng `unit_conversions` linh hoạt (`sku_id`, `from_unit`, `to_unit`, `conversion_factor`, `context`).
  - Search Engine: Thuật toán Hybrid kết hợp Full-Text Search (FTS bằng `tsvector`) và Trigram (`pg_trgm`).
  - Microservice: Tách riêng Service quy đổi đơn vị (UoM Service) chạy độc lập sau bước Matching.
- ❌ **Out-of-scope (Không bao gồm / Để dành cho Phase sau):**
  - Hệ thống tự học thêm Alias từ dữ liệu bên ngoài tự động.

# 5. TIÊU CHÍ NGHIỆM THU CẤP CAO (High-Level Acceptance Criteria)
- Hệ thống **PHẢI** chuyển trạng thái sang `NEEDS_CLARIFICATION` nếu điểm Confidence của Top 1 < 0.8.
- Hệ thống **PHẢI** sử dụng bảng map động để quy đổi đơn vị (VD: PVC D25 -> nhân 4, Thép D25 -> nhân 6). Nếu không tìm thấy mapping hợp lệ, **PHẢI** đánh cờ `unit_conflict: true` để Admin xác nhận.
- Hệ thống **KHÔNG ĐƯỢC** cho phép gộp chung logic tính UoM vào trong câu query Matching SKU. Hai bước này phải tách bạch.

# 6. RÀNG BUỘC & GIẢ ĐỊNH (Assumptions & Constraints)
- **Giả định (Assumptions):** Cột `search_vector` trong PostgreSQL đã được update sẵn dữ liệu gộp của Tên SKU và Alias.
- **Ràng buộc (Constraints):** Truy vấn tìm kiếm (FTS + Trigram) phải xử lý siêu tốc (< 100ms) cho dù database lên đến 50,000 SKU.

# 7. SỰ PHỤ THUỘC & RỦI RO (Dependencies & Risks)
- **Phụ thuộc (Dependencies):** Cần dữ liệu `normalized_search`, `quantity_raw`, `unit_raw` chuẩn đầu ra từ Epic 1 truyền vào.
- **Rủi ro (Risks):** Câu lệnh ILIKE hoặc % Trigram thuần túy sẽ bị chậm nếu search chuỗi dài. -> **Phương án xử lý:** Sử dụng Hybrid Index: Tạo cột `tsvector` kết hợp GIN Index, tính điểm tổng `(trgm_score + fts_score)` để lấy Top 5.

# 8. DANH SÁCH USER STORY DỰ KIẾN (Child Stories / Breakdown)
- `[US-2.1] - Hybrid Search Engine (FTS + Trigram)`
  - **Story:** Là một `Hệ thống`, tôi muốn `truy vấn Database kết hợp cả Full-Text Search và Trigram trên trường normalized_search`, để `tìm ra ứng viên SKU chuẩn xác nhất kể cả khi tên hàng dài hoặc gõ tắt, đảo chữ`.
- `[US-2.2] - Service Quy đổi đơn vị (UoM Service độc lập)`
  - **Story:** Là một `Hệ thống`, tôi muốn `gọi một Service UoM riêng biệt sau khi đã chốt được mã SKU`, để `đối chiếu "unit_raw" với bảng "unit_conversions" của chính SKU đó, tự động nhân hệ số (conversion_factor) ra số lượng tồn kho chuẩn`.
