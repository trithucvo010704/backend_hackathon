# HƯỚNG DẪN: REVIEW REPORT (BÁO CÁO KIỂM DUYỆT)

## 1. Mô tả
Tài liệu này dùng để ghi nhận kết quả kiểm duyệt chất lượng của các tài liệu nghiệp vụ (Epic Brief, User Story, Specs) do BA hoặc AI Agent thực hiện. Mục đích là đảm bảo mọi đầu ra đều đạt tiêu chuẩn trước khi chuyển sang giai đoạn phát triển kỹ thuật.

## 2. Cách viết
1. **Thông tin chung:** Điền tên người review, ngày thực hiện và đánh giá điểm chất lượng tổng thể (từ 0-100).
2. **Trạng thái phê duyệt:** Đánh dấu vào ô tương ứng (PASS hoặc REVISING).
3. **Danh sách lỗi & câu hỏi:** 
    - Liệt kê từng vấn đề phát hiện được vào bảng.
    - Phân loại mức độ (High/Medium/Low).
    - Phân loại loại lỗi (Logic, Formatting, Missing Info, Security...).
    - Mô tả chi tiết lỗi và đặt câu hỏi làm rõ nếu cần.

## 3. Nguồn thông tin
- Tài liệu cần review (Epic Brief, User Story...).
- Guideline tiêu chuẩn của tài liệu đó (ví dụ: `brief.md` hoặc `user-story.md`).
- Business Rules đã được thống nhất từ trước.

## 4. Cách thu thập
- Đọc kỹ toàn bộ nội dung tài liệu mục tiêu.
- Đối soát với các tiêu chuẩn trong bộ Guideline.
- Thử nghiệm các kịch bản logic (edge cases) để tìm lỗ hổng.
- Phỏng vấn BA hoặc Stakeholders nếu có điểm chưa rõ.

## 5. Format gợi ý / Template áp dụng

```markdown
# [REPORT] REVIEW REPORT: [Tên Epic/Tính năng]

**Người Review:** [Tên người/Agent thực hiện]
**Ngày thực hiện:** YYYY-MM-DD
**Điểm chất lượng:** [0-100]/100

## 1. TRẠNG THÁI PHÊ DUYỆT
- [ ] **PASS:** Tài liệu đạt chuẩn, có thể chuyển sang bước tiếp theo.
- [ ] **REVISING:** Cần sửa đổi và review lại dựa trên danh sách lỗi bên dưới.

## 2. DANH SÁCH LỖI & CÂU HỎI
| ID | Mức độ | Loại lỗi | Chi tiết lỗi & Câu hỏi |
| :--- | :--- | :--- | :--- |
| 01 | [High/Low] | [Logic/UI...] | [Mô tả chi tiết tại đây] |

## 3. GHI CHÚ THÊM (NẾU CÓ)
[Nhập các lưu ý hoặc đề xuất cải tiến khác]
```
