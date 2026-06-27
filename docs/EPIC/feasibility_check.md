# HƯỚNG DẪN: FEASIBILITY CHECK (KIỂM TRA TÍNH KHẢ THI)

## 1. Mô tả
Tài liệu này dùng để phân tích và đánh giá xem một yêu cầu nghiệp vụ (Epic/User Story) có khả thi để triển khai về mặt kỹ thuật trong một repository cụ thể hay không. Việc đánh giá bao gồm khả năng đáp ứng của công nghệ hiện tại, tài nguyên hệ thống, và các rủi ro kỹ thuật tiềm ẩn.

## 2. Cách viết
1. **Thông tin chung:** Tên người đánh giá (thường là Tech Lead hoặc Senior Dev) và Epic/Story đang xem xét.
2. **Phân tích kỹ thuật:**
    - **Công nghệ:** Đánh giá stack hiện tại có hỗ trợ các tính năng mới không.
    - **Tác động hệ thống:** Dự đoán ảnh hưởng đến hiệu năng, bảo mật và các module hiện hữu.
    - **Rủi ro:** Liệt kê các rủi ro (ví dụ: thư viện bên thứ ba không ổn định, giới hạn của database...).
3. **Kết luận:** Đưa ra quyết định cuối cùng và các điều kiện kèm theo nếu có.

## 3. Nguồn thông tin
- Tài liệu nghiệp vụ ([Epic Brief/Specs](brief.md)).
- Tài liệu kỹ thuật của repository ([01_PROJECT_ARCHITECTURE.md](../repository-level/01_PROJECT_ARCHITECTURE.md), [04_TECH_STACK.md](../repository-level/04_TECH_STACK.md)).
- Documentation của các thư viện/framework liên quan.

## 4. Cách thu thập
- Nghiên cứu mã nguồn hiện tại của dự án.
- Thực hiện PoC (Proof of Concept) cho các tính năng phức tạp hoặc mới lạ.
- Tham khảo ý kiến từ các chuyên gia trong team.
- Tra cứu tài liệu kỹ thuật bên ngoài và các bài toán tương tự đã giải quyết.

## 5. Format gợi ý / Template áp dụng

```markdown
# [CHECK] FEASIBILITY CHECK: [Tên Epic/Tính năng]

**Người đánh giá:** [Tên Tech Lead/Agent]
**Ngày đánh giá:** YYYY-MM-DD

## 1. PHÂN TÍCH CHI TIẾT
- **Khả năng đáp ứng công nghệ:** [Có/Không] - Giải thích ngắn gọn.
- **Tác động đến kiến trúc hiện tại:** [Thấp/Trung bình/Cao] - Mô tả các module bị ảnh hưởng.
- **Tài nguyên cần thiết:** [Thêm hạ tầng, thư viện mới, hay dùng tài nguyên cũ].
- **Rủi ro kỹ thuật tiềm ẩn:**
    - Rủi ro 1: [Mô tả]
    - Rủi ro 2: [Mô tả]

## 2. KẾT LUẬN
- [ ] **FEASIBLE:** Có thể triển khai ngay.
- [ ] **FEASIBLE WITH CONDITIONS:** Có thể triển khai nếu giải quyết được [điều kiện].
- [ ] **NOT FEASIBLE:** Không thể triển khai do [lý do].

## 3. ĐỀ XUẤT HÀNH ĐỘNG
[Các bước tiếp theo: Thực hiện PoC, thay đổi stack, hay điều chỉnh yêu cầu nghiệp vụ]
```
