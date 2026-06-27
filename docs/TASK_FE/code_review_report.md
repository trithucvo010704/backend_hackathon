# HƯỚNG DẪN: CODE REVIEW REPORT (BÁO CÁO KIỂM DUYỆT CODE - FRONTEND)

## 1. Mô tả
Tài liệu này dùng để ghi lại kết quả của quá trình kiểm duyệt mã nguồn cho các Task Frontend (Web). Mục tiêu là đảm bảo code tuân thủ các tiêu chuẩn về giao diện (UI), trải nghiệm (UX), hiệu năng rendering, và quy chuẩn Component-based.

## 2. Cách viết
1. **Thông tin định danh:** Ghi rõ Reviewer (người kiểm duyệt), Author (tác giả code) và liên kết đến PR/Task.
2. **Nhận xét chi tiết:** 
    - Chỉ rõ vị trí lỗi (File, Line number).
    - Phân loại vấn đề: UI/UX (lệch design), Logic, Component Reuse, State Management, Performance (Re-render)...
    - Giải thích tại sao đây là lỗi và gợi ý cách sửa đổi tối ưu.
3. **Kết luận:** Đưa ra quyết định cuối cùng (Approved, Request Changes, hoặc Comment).

## 3. Nguồn thông tin
- Pull Request / Source code thực tế.
- Đặc tả giao diện (`ui_ux_spec.md`) và Figma.
- Tài liệu `02_CODING_STANDARDS.md` (phần FE).
- Task Spec và yêu cầu nghiệp vụ của Story.

## 4. Cách thu thập
- Sử dụng tính năng Review trên GitHub/GitLab.
- Chạy thử giao diện ở môi trường local, kiểm tra trên các kích thước màn hình khác nhau (Responsive).
- Sử dụng React/Vue DevTools để kiểm tra State và Props.
- Kiểm tra kết quả Linter và Build.

## 5. Format gợi ý / Template áp dụng

```markdown
# [REVIEW] FE CODE REVIEW REPORT: [Tên Task/PR]

**Reviewer:** [Tên Tech Lead/Agent]
**Tác giả:** [Tên Developer]
**Ngày review:** YYYY-MM-DD
**Liên kết PR:** [Link]

## 1. TỔNG QUAN CHẤT LƯỢNG (OVERALL QUALITY)
- **Độ chính xác UI (Pixel Perfect):** [Đạt / Cần chỉnh sửa]
- **Tương thích thiết bị (Responsive):** [Tốt / Có lỗi]
- **Quản lý State & Component:** [Đạt / Cần tối ưu]

## 2. NHẬN XÉT CHI TIẾT (DETAILED COMMENTS)
| Vị trí (File:Line) | Loại | Nhận xét & Đề xuất |
| :--- | :--- | :--- |
| `src/components/Button.tsx:10` | UI | Màu sắc nút chưa đúng với mã màu trong Figma. |
| `src/pages/Home.tsx:45` | Performance | Gây re-render không cần thiết. Nên dùng `useMemo` hoặc `useCallback`. |
| `src/styles/global.css:20` | CSS | Sử dụng magic number cho padding. Nên dùng các biến token từ Design System. |

## 3. KẾT LUẬN
- [ ] **APPROVED:** Code tốt, giao diện khớp thiết kế.
- [ ] **COMMENT:** Có một vài lỗi UI nhỏ, có thể sửa sau.
- [ ] **REQUEST CHANGES:** Cần sửa lỗi logic hoặc lỗi UI nghiêm trọng trước khi merge.
```
