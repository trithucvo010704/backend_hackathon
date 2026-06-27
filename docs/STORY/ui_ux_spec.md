# HƯỚNG DẪN VIẾT TÀI LIỆU UI/UX SPECIFICATION (ui_ux_spec.md)

Tài liệu này xác định các nguyên tắc thiết kế giao diện, trải nghiệm người dùng và các tương tác frontend.

Dưới đây là các thành phần cấu trúc bắt buộc:

---

## 1. THIẾT KẾ GIAO DIỆN & MOCKUPS (Layout & Mockups)

1. **Mô tả:** Định nghĩa bố cục, các khối chức năng và ý đồ thiết kế để làm đầu vào cho Designer hoặc đính kèm thiết kế thực tế (Mockups).
2. **Cách viết:** 
   - Giai đoạn chưa có thiết kế: Mô tả chi tiết các thành phần cần có, vị trí tương đối và logic hiển thị.
   - Giai đoạn đã có thiết kế: Gắn link Figma và chú thích các vùng chức năng chính.
3. **Nguồn thông tin:** Tài liệu `user-story.md`, Master Layout và Design System của dự án.
4. **Cách thu thập:** Phác thảo wireframe sơ bộ hoặc liệt kê danh sách các UI Component cần thiết từ thư viện chuẩn (như Nebular, Material).
5. **Format gợi ý / Template áp dụng:**
   - **Yêu cầu giao diện (Input for Design):** [Mô tả chi tiết các khối chức năng, ví dụ: Header có Breadcrumb, Body chia 2 cột...]
   - **Mockup Link (Figma):** [Dán link Figma tại đây sau khi Designer hoàn thiện]
   - **Responsive Policy:**
     - Desktop: [Bố cục trên màn hình lớn]
     - Mobile: [Bố cục trên màn hình nhỏ]


---

## 2. TRẢI NGHIỆM & TRẠNG THÁI (UX & States)

1. **Mô tả:** Cách hệ thống phản hồi với hành động người dùng và các trạng thái thành phần.
2. **Cách viết:** Mô tả các trạng thái: Normal, Loading, Empty, Error. Quy định cách báo lỗi (Toast/Modal).
3. **Nguồn thông tin:** Tiêu chuẩn UX của hệ thống, Acceptance Criteria.
4. **Cách thu thập:** Liệt kê các kịch bản tương tác và trạng thái UI tương ứng.
5. **Format gợi ý / Template áp dụng:**
   | Component | Trạng thái | Hành vi / Hiển thị |
   |---|---|---|
   | [Tên Component] | [Loading/Error] | [Mô tả chi tiết] |

---

## 3. KHẢ NĂNG TRUY CẬP (Accessibility - A11y)

1. **Mô tả:** Đảm bảo hệ thống có thể được sử dụng bởi mọi người (trình đọc màn hình, bàn phím).
2. **Cách viết:** Kiểm tra điều hướng phím Tab, độ tương phản màu sắc và nhãn aria-label.
3. **Nguồn thông tin:** WCAG Guidelines, UI/UX Specs.
4. **Cách thu thập:** Checklist các yêu cầu tối thiểu về A11y cho từng màn hình.
5. **Format gợi ý / Template áp dụng:**
   - [ ] Gán nhãn `aria-label` cho các icon buttons.
   - [ ] Hỗ trợ Tab Navigation 100%.
   - [ ] Độ tương phản text/background đạt chuẩn AA.
