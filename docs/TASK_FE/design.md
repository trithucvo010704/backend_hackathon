# HƯỚNG DẪN VIẾT TÀI LIỆU FRONTEND DESIGN DETAILS (design.md)

Tài liệu này xác định các thành phần UI (Components) và cách thức quản lý trạng thái (State Management) trong một Task cụ thể.

---

## 1. CẤU TRÚC UI & COMPONENTS

*   **Mô tả:** Bản thiết kế bóc tách giao diện thành các Component nhỏ để code.
*   **Cách viết:**
    *   **Câu hỏi cần trả lời:** Giao diện này cấu thành từ những Component nào? Có dùng chung Component nào đã có sẵn trong dự án không?
    *   **Lấy thông tin ở đâu:** Từ Mockups và Design System của dự án.
    *   **Lấy như thế nào:** Phân tích theo nguyên lý Atomic Design. Ưu tiên kế thừa và sử dụng triệt để thư viện UI (Tailwind, MUI, Bootstrap, Nebular...) đã được cấu hình trong `package.json`. HẠN CHẾ tối đa việc tự viết Custom CSS.
    *   **Format gợi ý / Template áp dụng:**
        - **Layout Component:** `[Container, Grid, Card]`
        - **Core Components:** `[BaseTable, BaseButton, SmartModal]`
        - **Style Framework:** Tuân thủ thư viện nội bộ.

---

## 2. QUẢN LÝ TRẠNG THÁI & TƯƠNG TÁC (STATE & FEEDBACK)

*   **Mô tả:** Cách thức Component lưu trữ dữ liệu tạm thời và phản hồi với thao tác của user.
*   **Cách viết:**
    *   **Câu hỏi cần trả lời:** Dữ liệu search/filter lưu ở đâu? Khi bấm nút "Lưu" thì màn hình hiện thông báo như thế nào?
    *   **Lấy thông tin ở đâu:** Các thao tác từ UI/UX Specs.
    *   **Lấy như thế nào:** Phân biệt rõ Local State (Component tự quản) và Global State (Redux, Vuex, NgRx, Context). Chỉ đẩy lên Global khi dữ liệu cần được chia sẻ qua lại giữa nhiều màn hình khác nhau. Bắt buộc áp dụng kỹ thuật Debounce/Throttle cho các tương tác gõ text (Search).
    *   **Format gợi ý / Template áp dụng:**
        - **State Storage:** Sử dụng Local State.
        - **Optimization:** Debounce input search (300ms).
        - **Feedback:** Hiển thị Toast thông báo sau mọi hành động Submit.

---

## 3. KIỂM THỬ GIAO DIỆN & TÀI LIỆU COMPONENT (COMPONENT TEST & DOCS)

*   **Mô tả:** Đảm bảo các Component giao diện hoạt động độc lập và được tài liệu hóa để có thể tái sử dụng.
*   **Cách viết:**
    *   **Câu hỏi cần trả lời:** Component này có phức tạp không? Cần test các tương tác (click, type) hay chỉ test giao diện tĩnh? Có cần đưa lên Storybook để người khác dễ dàng xem lại không?
    *   **Lấy thông tin ở đâu:** Thiết kế Mockup và trạng thái Component.
    *   **Lấy như thế nào:** Yêu cầu rõ công nghệ viết test (ví dụ: Cypress Component Test, Jest, React Testing Library). Bắt buộc viết Storybook nếu là Core Component (như Nút bấm, Table, Input).
    *   **Format gợi ý / Template áp dụng:**
        - **Component Docs:** Bắt buộc cập nhật file `[ComponentName].stories.tsx` trên Storybook để showcase UI.
        - **Component Test:** Bắt buộc viết Unit Test cho logic của hàm xử lý và Test UI (Render đúng).
