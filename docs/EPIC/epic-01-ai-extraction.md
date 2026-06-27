# 1. TÊN EPIC (Epic Title)
`Xây dựng + Module AI Data Intake + để trích xuất ngữ nghĩa và thực thể từ tin nhắn đặt hàng thô`

# 2. MÔ TẢ EPIC (Epic Statement)
Là một **Hệ thống OrderFlow AI**
Tôi muốn **sử dụng LLM để nhận diện và bóc tách cấu trúc (Structured Output) từ tin nhắn Zalo thô**
Để tôi có thể **chuyển đổi văn bản tự do thành danh sách các vật tư, tên công trình và khách hàng mà không cần gõ lại thủ công.**

# 3. MỤC TIÊU KINH DOANH (Business Goals & Metrics)
- **Vấn đề đang gặp phải (Pain-point):** Dữ liệu đầu vào từ Zalo hoàn toàn phi cấu trúc, gây nghẽn cổ chai trong khâu tiếp nhận.
- **Giá trị mang lại (Value):** Tự động hóa công đoạn đọc hiểu ban đầu, tạo ra mảng JSON sạch chứa dữ liệu nguyên bản và dữ liệu đã được chuẩn hóa để truyền xuống các hệ thống engine phía sau.
- **Chỉ số đo lường (Metrics/KPIs):** 
  - **KPI Kỹ thuật:** 100% tin nhắn trả về đúng cấu trúc JSON Schema (dùng Pydantic/Zod để validate).
  - **KPI Nghiệp vụ:** Độ chính xác trích xuất từng thực thể (tên hàng, số lượng) đạt >90% trên test set 100 tin nhắn đa dạng.
  - **KPI Tốc độ:** Thời gian xử lý trung bình < 3 giây cho mỗi tin nhắn.

# 4. PHẠM VI (Scope & Boundaries)
- ✅ **In-scope (Sẽ bao gồm):**
  - Nhận text/copy từ UI.
  - Trích xuất thông tin chung: Tên KH, Công trình.
  - Bóc tách thực thể vật tư: Lấy chuỗi gốc tuyệt đối (`raw_segment`) và dịch thuật ngữ lóng sang thuật ngữ chuẩn (`normalized_search`).
  - Xử lý lỗi (Fallback) và Retry khi gọi LLM.
- ❌ **Out-of-scope (Không bao gồm / Để dành cho Phase sau):**
  - AI đọc ảnh (Vision OCR) và Voice.
  - LLM tự động đoán/gán mã SKU chuẩn cuối cùng (Việc này do DB ở Epic 2 lo).

# 5. TIÊU CHÍ NGHIỆM THU CẤP CAO (High-Level Acceptance Criteria)
- Hệ thống **PHẢI** xuất ra định dạng JSON chuẩn sử dụng OpenAI Structured Outputs.
- Hệ thống **PHẢI** xuất ra JSON chứa `raw_segment` nguyên bản và `normalized_search` (alias chuẩn hóa từ lóng). 
- **NGHIÊM CẤM** tuyệt đối việc LLM tự gán mã SKU hay suy diễn tên hàng ngoài việc chuẩn hóa alias. Việc đối sánh SKU cuối cùng sẽ do Engine tìm kiếm (Epic 2) xử lý bằng chỉ mục DB.
- Trường `raw_segment` **PHẢI** là bản sao SUBSTRING nguyên văn từ tin nhắn, KHÔNG ĐƯỢC thêm bớt ký tự nào.
- Hệ thống **PHẢI** tự động đẩy trạng thái sang "Unresolved" để duyệt tay nếu JSON sai cấu trúc hoặc API lỗi sau 2 lần Retry.

# 6. RÀNG BUỘC & GIẢ ĐỊNH (Assumptions & Constraints)
- **Giả định (Assumptions):** Sử dụng OpenAI API model `gpt-4o-mini` để đảm bảo tối ưu token và thời gian phản hồi nhanh (< 3s).
- **Ràng buộc (Constraints):** Bắt buộc phải sử dụng parameter `response_format={"type": "json_schema"}` khi gọi OpenAI API.

# 7. SỰ PHỤ THUỘC & RỦI RO (Dependencies & Risks)
- **Phụ thuộc (Dependencies):** Cần Epic 4 (Workbench) hoàn thiện để có chỗ nhập Text.
- **Rủi ro (Risks):** OpenAI API timeout (rate limit, network) -> **Phương án xử lý:** Retry tối đa 2 lần với exponential backoff. Nếu vẫn lỗi, đẩy vào queue review thủ công và ghi log.

# 8. DANH SÁCH USER STORY DỰ KIẾN (Child Stories / Breakdown)
- `[US-1.1] - Trích xuất toàn bộ dữ liệu đơn hàng từ tin nhắn thô`
  - **Story:** Là một `Hệ thống`, tôi muốn `phân tích toàn bộ tin nhắn bằng 1 API call duy nhất sử dụng Structured Output`, để `lấy ra Header (khách, công trình) và Line Items (vật tư) kèm chuỗi gốc (raw_segment) và chuỗi chuẩn hóa (normalized_search) nhằm tối ưu tốc độ và độ chính xác`.
  - **Schema JSON Output mong đợi:**
    ```json
    {
      "customer_name_raw": "Anh Tuấn", 
      "project_raw": "nhà ông Hùng",
      "items": [
        {
          "raw_segment": "co 25",          // TEXT GỐC 100% (dùng để kiểm chứng)
          "normalized_search": "cút 25",   // Chuẩn hóa alias để DB trigram tìm
          "quantity_raw": "10",            // Số lượng trích xuất được
          "unit_raw": "cái"
        }
      ]
    }
    ```
