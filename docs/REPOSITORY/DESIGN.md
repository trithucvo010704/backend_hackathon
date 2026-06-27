# HỆ THỐNG GIAO DIỆN & DESIGN SYSTEM (DESIGN.md)

Tài liệu này quy định các tiêu chuẩn về giao diện người dùng (UI) và trải nghiệm (UX).

---

## 1. THƯ VIỆN COMPONENT & TOKEN (UI TOKENS)

1. **Mô tả:** Danh mục các màu sắc, font chữ và thành phần giao diện dùng chung.
2. **Cách viết:** Liệt kê các UI Library (Tailwind, Ant Design, MUI) và các biến Design Tokens.
3. **Nguồn thông tin:** File thiết kế Figma và mã nguồn CSS/Theme của dự án.
4. **Cách thu thập:** Đọc file `theme.ts` hoặc `tailwind.config.js`.
5. **Format gợi ý / Template áp dụng:**
   - **Primary Color**: `#1890ff`.
   - **Font**: Inter, Sans-serif.
   - **Spacing Unit**: 4px.

---

## 2. QUY TẮC PHÁT TRIỂN UI (UI DEVELOPMENT RULES)

1. **Mô tả:** Các tiêu chuẩn khi tạo mới hoặc sửa đổi thành phần giao diện.
2. **Cách viết:** Quy định về tính Responsive, Accessibility và tính tái sử dụng component (Atom design).
3. **Nguồn thông tin:** UX Guidelines của dự án.
4. **Cách thu thập:** Rà soát các UI pattern đang có trong thư mục `src/components/common`.
5. **Format gợi ý / Template áp dụng:**
   - [ ] BẮT BUỘC sử dụng các Shared Components thay vì tạo thẻ HTML thô.
   - [ ] BẮT BUỘC kiểm tra hiển thị trên Mobile (375px) trước khi hoàn thành.


