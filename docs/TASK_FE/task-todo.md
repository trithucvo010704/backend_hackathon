# HƯỚNG DẪN VIẾT TÀI LIỆU TASK TO-DO LIST (task-todo.md)

Tài liệu này là bản đặc tả chi tiết quá trình thực thi code Frontend (Low-level implementation), được viết bởi **Developer**. Nó cụ thể hóa các định hướng từ `task-spec.md` thành các bước thực hiện thực tế.

---

## 1. CHI TIẾT COMPONENT & FILE (Implementation Targets)

1. **Mô tả:** Xác định chính xác các tệp tin và Component sẽ được tạo mới hoặc chỉnh sửa.
2. **Cách viết:** Liệt kê đường dẫn file (Component, Service, Module, Style). Nêu rõ vai trò của từng file.
3. **Nguồn thông tin:** Cấu trúc source code hiện tại và định hướng từ `task-spec.md`.
4. **Cách thu thập:** Rà soát cây thư mục project để tìm vị trí đặt Component hoặc Service mới.
5. **Format gợi ý / Template áp dụng:**
   - [ ] `src/app/features/X/components/Y.component.ts`: Tạo mới component hiển thị Y.
   - [ ] `src/app/features/X/services/Z.service.ts`: Thêm method call API W.
   - [ ] `src/app/features/X/components/Y.component.scss`: Định nghĩa style cho component Y.

---

## 2. LOGIC XỬ LÝ & TRẠNG THÁI (Detailed UI Logic)

1. **Mô tả:** Mô tả chi tiết logic xử lý sự kiện, quản lý trạng thái local và các ràng buộc dữ liệu trên UI.
2. **Cách viết:** Trình bày các bước xử lý logic (ví dụ: click button -> validate form -> show loading -> call API). Mô tả cách cập nhật State.
3. **Nguồn thông tin:** `ui_ux_spec.md`, `user-story.md` và giải pháp kỹ thuật từ Tech Lead.
4. **Cách thu thập:** Phân tích các tương tác của người dùng trên Mockup và luồng dữ liệu tương ứng.
5. **Format gợi ý / Template áp dụng:**
   - **Bước 1:** Khởi tạo Form với các validator (Required, Email...).
   - **Bước 2:** Xử lý sự kiện `onSubmit`, hiển thị trạng thái `Loading`.
   - **Bước 3:** Gọi Service, xử lý kết quả trả về để cập nhật vào `Signal/Observable`.
   - **Bước 4:** Hiển thị thông báo thành công (Toast) và điều hướng về trang danh sách.

---

## 3. KIỂM THỬ GIAO DIỆN & UNIT TEST (FE Testing)

1. **Mô tả:** Xác định các kịch bản kiểm thử giao diện và Logic để đảm bảo tính đúng đắn của Component.
2. **Cách viết:** Liệt kê các test cases cho Component (Rendering, Event Handling) và Service (API Mapping).
3. **Nguồn thông tin:** Acceptance Criteria và Logic thực thi ở mục 2.
4. **Cách thu thập:** Suy luận các trạng thái giao diện (Success, Error, Empty, Loading).
5. **Format gợi ý / Template áp dụng:**
   | Case ID | Kịch bản kiểm thử | Hành động | Kết quả mong đợi |
   |---|---|---|---|
   | TC-01 | Hiển thị dữ liệu | Vào trang | Component render đúng list data từ API |
   | TC-02 | Validate Form | Để trống trường bắt buộc | Hiển thị thông báo lỗi màu đỏ |
   | TC-03 | Xử lý lỗi API | API trả về 500 | Hiển thị Toast thông báo lỗi hệ thống |

---

## 4. FE DEV CHECKLIST

1. **Mô tả:** Các bước kiểm tra cuối cùng về chất lượng giao diện, hiệu năng và Clean Code trước khi tạo Pull Request.
2. **Cách viết:** Checklist các tiêu chuẩn về UI/UX, Performance (Change Detection) và Coding Standards.
3. **Nguồn thông tin:** Tài liệu `09_DESIGN_SYSTEM.md` và tiêu chuẩn Codebase FE.
4. **Cách thu thập:** Đối chiếu code và giao diện thực tế với các quy tắc chung.
5. **Format gợi ý / Template áp dụng:**
   - [ ] Giao diện đã khớp với Mockup (Pixel Perfect)?
   - [ ] Đã handle trạng thái Loading/Empty cho mọi màn hình?
   - [ ] Đã unsubcribe các Observable để tránh Memory Leak?
   - [ ] Code đã chạy tốt trên các trình duyệt/thiết bị yêu cầu (Responsive)?
   - [ ] Các icon/hình ảnh đã được tối ưu dung lượng và gán nhãn `alt/aria-label`?
