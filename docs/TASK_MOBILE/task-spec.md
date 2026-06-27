# HƯỚNG DẪN VIẾT TÀI LIỆU TASK SPECIFICATION (task-spec.md)

Tài liệu này là bản định hướng kỹ thuật cao cấp (High-level technical design) được viết bởi **Tech Lead**. Nó tập trung vào giải pháp kiến trúc, lựa chọn công nghệ và luồng dữ liệu tổng quát cho một Task.

---

## 1. MÔ TẢ TASK (TASK DESCRIPTION)

1. **Mô tả:** Tóm tắt ngắn gọn về phạm vi công việc và mục tiêu kỹ thuật cần đạt được.
2. **Cách viết:** Trả lời: Task này giải quyết vấn đề gì? Kết quả đầu ra mong muốn là gì? Giới hạn của task đến đâu?
3. **Nguồn thông tin:** Phân rã từ User Story và tài liệu `ui_ux_spec.md` hoặc `api-spec.md`.
4. **Cách thu thập:** Đọc kỹ User Story và xác định các thành phần Backend/Frontend cần can thiệp.
5. **Format gợi ý / Template áp dụng:**
   - **Task ID:** `[id]`
   - **Story ID Tham chiếu:** `[Link to Story]`
   - **Mục tiêu:** [Mô tả ngắn gọn kết quả kỹ thuật sau khi xong task]

---

## 2. GIẢI PHÁP KỸ THUẬT & LUỒNG XỬ LÝ (Technical Solution & Flow)

1. **Mô tả:** Xác định kiến trúc, thư viện sử dụng và sơ đồ luồng dữ liệu tương tác giữa các thành phần.
2. **Cách viết:** Chỉ định các công nghệ/thư viện mới cần thêm. Vẽ luồng tương tác giữa các module (ví dụ: Controller -> Service -> Kafka -> Consumer).
3. **Nguồn thông tin:** `03-db-diagram.md`, `04-tech-stack.md` và các tiêu chuẩn kiến trúc dự án.
4. **Cách thu thập:** Phác thảo luồng xử lý dữ liệu và xác định các điểm giao tiếp với hệ thống bên ngoài hoặc module khác.
5. **Format gợi ý / Template áp dụng:**
   - **Công nghệ/Thư viện:** [Ví dụ: Sử dụng Spring Cloud Stream cho Kafka, Thêm thư viện MapStruct...]
   - **Tương tác API/Service:** [Ví dụ: Lắng nghe Topic `order-created`, Gọi API của module Identity để lấy thông tin user...]
   - **Technical Flow:**
     ```mermaid
     graph LR
       A[Input/Trigger] --> B[Processing Logic]
       B --> C[Data Persistence/Output]
     ```

---

## 3. CÁC BƯỚC ĐỊNH HƯỚNG (Architectural Steps)

1. **Mô tả:** Danh sách các bước quan trọng về mặt kiến trúc mà Developer cần tuân thủ.
2. **Cách viết:** Liệt kê các cột mốc quan trọng (milestones) trong quá trình phát triển mà không cần chỉ định rõ tên file hay dòng code.
3. **Nguồn thông tin:** Kinh nghiệm của Tech Lead và thiết kế hệ thống.
4. **Cách thu thập:** Chia nhỏ giải pháp ở mục 2 thành các khối công việc logic.
5. **Format gợi ý / Template áp dụng:**
   - [ ] Thiết lập cấu hình kết nối Kafka/Database mới.
   - [ ] Xây dựng cấu trúc dữ liệu (DTO/Entity) theo chuẩn API Spec.
   - [ ] Triển khai Business Logic cốt lõi (Core Logic).
   - [ ] Tích hợp các dịch vụ bên thứ ba (External Integration).
   - [ ] Đảm bảo bao phủ các trường hợp lỗi (Error Handling Strategy).
