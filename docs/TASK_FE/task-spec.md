# HƯỚNG DẪN VIẾT TÀI LIỆU FRONTEND TASK SPECIFICATION (task-spec.md)

Tài liệu này là bản định hướng kỹ thuật cao cấp (High-level technical design) cho Frontend, được viết bởi **Tech Lead**. Nó tập trung vào phân rã Component, chiến lược quản lý trạng thái và luồng tích hợp dữ liệu.

---

## 1. MÔ TẢ TASK (TASK DESCRIPTION)

1. **Mô tả:** Tóm tắt ngắn gọn về phạm vi công việc trên giao diện và mục tiêu trải nghiệm người dùng.
2. **Cách viết:** Trả lời: Màn hình/Component nào cần can thiệp? Task này thay đổi hành vi người dùng như thế nào? Giới hạn của task đến đâu?
3. **Nguồn thông tin:** User Story và tài liệu `ui_ux_spec.md`.
4. **Cách thu thập:** Đọc kỹ User Story và đối chiếu với thiết kế trong `ui_ux_spec.md`.
5. **Format gợi ý / Template áp dụng:**
   - **Task ID:** `[id]`
   - **Story ID Tham chiếu:** `[Link to Story]`
   - **Mục tiêu:** [Mô tả ngắn gọn kết quả giao diện/trải nghiệm sau khi xong task]

---

## 2. GIẢI PHÁP UI & LUỒNG TÍCH HỢP (UI Solution & Integration Flow)

1. **Mô tả:** Xác định cấu trúc Component, cách quản lý dữ liệu (State) và luồng gọi API.
2. **Cách viết:** Chỉ định các Shared Components cần tái sử dụng. Xác định chiến lược State (ví dụ: dùng Signal, Observable hay Global Store). Vẽ luồng tương tác từ UI đến Services.
3. **Nguồn thông tin:** `04-tech-stack.md`, Design System của dự án và `api-spec.md`.
4. **Cách thu thập:** Phân tích Mockup để bóc tách các Component và xác định các điểm cần fetch/push dữ liệu.
5. **Format gợi ý / Template áp dụng:**
   - **Component Hierarchy:** [Ví dụ: Page A -> Smart Component B -> Dummy Component C...]
   - **State Management:** [Ví dụ: Dùng BehaviorSubject trong Service để lưu cache, dùng Signal cho local state...]
   - **API Integration:** [Ví dụ: Gọi API `GET /orders`, sử dụng Interceptor để handle token...]
   - **Technical Flow:**
     ```mermaid
     graph TD
       User[Hành động User] --> Comp[Component]
       Comp --> Service[Service/Store]
       Service --> API[Backend API]
     ```

---

## 3. CÁC BƯỚC ĐỊNH HƯỚNG (Architectural Steps)

1. **Mô tả:** Danh sách các bước quan trọng về mặt cấu trúc và tích hợp mà Developer cần tuân thủ.
2. **Cách viết:** Liệt kê các cột mốc quan trọng trong quá trình phát triển Frontend mà không đi sâu vào chi tiết CSS hay logic code cụ thể.
3. **Nguồn thông tin:** Kinh nghiệm của Tech Lead và tiêu chuẩn Codebase FE.
4. **Cách thu thập:** Chia nhỏ giải pháp ở mục 2 thành các khối công việc logic trên UI.
5. **Format gợi ý / Template áp dụng:**
   - [ ] Cấu hình Routing và Lazy Loading (nếu có).
   - [ ] Khai báo Models/Interfaces dựa trên API Spec.
   - [ ] Xây dựng khung Component và tích hợp State Management.
   - [ ] Tích hợp API và xử lý luồng dữ liệu động.
   - [ ] Triển khai các kịch bản UX (Loading, Error states, Toast).
