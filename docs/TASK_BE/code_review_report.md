# HƯỚNG DẪN: CODE REVIEW REPORT (BÁO CÁO KIỂM DUYỆT CODE)

## 1. Mô tả
Tài liệu này dùng để ghi lại kết quả của quá trình kiểm duyệt mã nguồn cho một Task hoặc một Pull Request (PR). Mục tiêu là đảm bảo code tuân thủ các tiêu chuẩn về chất lượng, hiệu năng, bảo mật và khả năng bảo trì của dự án.

## 2. Cách viết
1. **Thông tin định danh:** Ghi rõ Reviewer (người kiểm duyệt), Author (tác giả code) và liên kết đến PR/Task.
2. **Nhận xét chi tiết:** 
    - Chỉ rõ vị trí lỗi (File, Line number).
    - Phân loại vấn đề: Coding Standard, Logic, Performance, Security, Redundant code...
    - Giải thích tại sao đây là lỗi và gợi ý cách sửa đổi tối ưu.
3. **Kết luận:** Đưa ra quyết định cuối cùng (Approved, Request Changes, hoặc Comment).

## 3. Nguồn thông tin
- Pull Request / Source code thực tế.
- Tài liệu `02_CODING_STANDARDS.md` của dự án.
- Task Spec và yêu cầu nghiệp vụ của Story.
- Các quy tắc bảo mật và hiệu năng.

## 4. Cách thu thập
- Sử dụng tính năng Review trên GitHub/GitLab để comment trực tiếp vào code.
- Chạy thử code ở môi trường local để kiểm tra hành vi thực tế.
- Sử dụng các công cụ Linter và Static Analysis để phát hiện lỗi tự động.
- Đối soát code với Task TODO để đảm bảo không làm thiếu yêu cầu.

## 5. Format gợi ý / Template áp dụng

```markdown
# [REVIEW] CODE REVIEW REPORT: [Tên Task/PR]

**Reviewer:** [Tên Tech Lead/Agent]
**Tác giả:** [Tên Developer]
**Ngày review:** YYYY-MM-DD
**Liên kết PR:** [Link]

## 1. TỔNG QUAN CHẤT LƯỢNG (OVERALL QUALITY)
- **Tuân thủ tiêu chuẩn:** [Đạt / Cần sửa đổi]
- **Tính đúng đắn logic:** [Đạt / Có lỗi]
- **Hiệu năng & Bảo mật:** [Tốt / Cần tối ưu]

## 2. NHẬN XÉT CHI TIẾT (DETAILED COMMENTS)
| Vị trí (File:Line) | Loại | Nhận xét & Đề xuất |
| :--- | :--- | :--- |
| `src/auth.ts:12` | Security | Không nên hardcode secret key. Hãy dùng biến môi trường. |
| `src/user.ts:45` | Performance | Vòng lặp lồng nhau gây chậm. Nên dùng Map để tối ưu O(1). |
| `src/utils.ts:110` | Naming | Tên hàm `doIt()` không rõ nghĩa. Đổi thành `formatCurrency()`. |

## 3. KẾT LUẬN
- [ ] **APPROVED:** Code tốt, có thể merge.
- [ ] **COMMENT:** Có một vài lưu ý nhỏ, không bắt buộc sửa ngay.
- [ ] **REQUEST CHANGES:** Bắt buộc phải sửa các lỗi High/Medium trước khi merge.
```
