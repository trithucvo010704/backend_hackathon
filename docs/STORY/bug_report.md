# HƯỚNG DẪN: STORY BUG REPORT (BÁO CÁO LỖI CẤP ĐỘ STORY)

## 1. Mô tả
Tài liệu này dùng để báo cáo các lỗi về logic nghiệp vụ, sai lệch so với yêu cầu (Spec) hoặc lỗi tích hợp phát hiện được trong giai đoạn kiểm thử nghiệm thu (Acceptance Testing) của một User Story. Nó tập trung vào góc nhìn của người dùng và giá trị nghiệp vụ.

## 2. Cách viết
1. **Thông tin cơ bản:** Tiêu đề lỗi ngắn gọn, mức độ nghiêm trọng (Critical/High/Medium/Low).
2. **Mô tả sai lệch:** Nêu rõ lỗi xảy ra ở đâu, Story nào và hiện tượng cụ thể là gì.
3. **Kịch bản gây lỗi:** Trích dẫn ID kịch bản từ `test-scenarios.md` và liệt kê các bước tái hiện (steps to reproduce).
4. **Kết quả vs Mong muốn:** So sánh trực tiếp những gì hệ thống đang làm so với những gì Spec yêu cầu.
5. **Ảnh hưởng:** Phân tích tác động của lỗi đến dữ liệu và trải nghiệm người dùng.

## 3. Nguồn thông tin
- Tài liệu User Story và AC (`user-story.md`).
- Kịch bản kiểm thử (`test-scenarios.md`).
- Hệ thống thực tế đang chạy (Staging/UAT).
- Nhật ký hệ thống (Logs/Database records).

## 4. Cách thu thập
- Thực hiện kiểm thử dựa trên các kịch bản đã định nghĩa.
- Chụp ảnh màn hình (Screenshot) hoặc quay video minh họa lỗi.
- Kiểm tra dữ liệu trong DB hoặc phản hồi từ API để xác định điểm sai.
- Đối chiếu với tài liệu nghiệp vụ gốc để xác nhận đây là lỗi (bug) chứ không phải là thay đổi yêu cầu (change request).

## 5. Format gợi ý / Template áp dụng

```markdown
# [BUG] STORY LEVEL: [Tên lỗi ngắn gọn]

**ID:** BUG-STORY-XXXX
**Mức độ:** [Critical / High / Medium / Low]
**Trạng thái:** New

## 1. MÔ TẢ LỖI (DESCRIPTION)
- **Tên Story:** [Tên User Story liên quan]
- **Hiện tượng:** [Mô tả ngắn gọn lỗi, ví dụ: Nút Thanh toán không hoạt động khi áp dụng voucher]

## 2. KỊCH BẢN TÁI HIỆN (REPRODUCTION STEPS)
- **Căn cứ:** [ID Scenario trong test-scenarios.md]
- **Các bước:**
    1. Truy cập vào giỏ hàng.
    2. Chọn 1 sản phẩm có giá trị > 100k.
    3. Nhập mã voucher `DISCOUNT10`.
    4. Click nút "Thanh toán".

## 3. KẾT QUẢ THỰC TẾ VS MONG MUỐN
- **Thực tế:** [Hệ thống báo lỗi 500 hoặc không phản hồi]
- **Mong muốn:** [Hệ thống phải chuyển hướng sang trang thanh toán và trừ tiền đúng 10%]

## 4. BẰNG CHỨNG (EVIDENCES)
- [Link ảnh màn hình / Video]
- [Log API / Error Message]

## 5. ẢNH HƯỞNG (IMPACT)
- [ ] Gây sai lệch dữ liệu tài chính.
- [ ] Chặn đứng luồng nghiệp vụ chính.
- [ ] Lỗi hiển thị UI nhẹ.
```
