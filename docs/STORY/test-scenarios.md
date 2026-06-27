# HƯỚNG DẪN: TEST SCENARIOS (KỊCH BẢN KIỂM THỬ)

## 1. Mô tả
Tài liệu này định nghĩa các kịch bản kiểm thử chi tiết cho một User Story, được viết dưới dạng ngôn ngữ tự nhiên (Gherkin) để cả BA, Developer và QC đều có thể hiểu và thực hiện. Các kịch bản này bao gồm cả trường hợp thành công (Happy cases) và các trường hợp lỗi/ngoại lệ (Edge cases).

## 2. Cách viết
1. **Xác định kịch bản:** Đặt tên rõ ràng cho từng kịch bản (ví dụ: SCENARIO 1: [Hành động] [Kết quả]).
2. **Viết theo cấu trúc Gherkin:**
    - **Given:** Thiết lập tiền đề (trạng thái hệ thống, dữ liệu đầu vào).
    - **When:** Hành động của người dùng hoặc sự kiện kích hoạt.
    - **And:** Các hành động bổ sung (nếu có).
    - **Then:** Kết quả mong đợi của hệ thống.
3. **Phân loại:** Đánh dấu kịch bản là Chính (Major) hay Phụ (Minor).

## 3. Nguồn thông tin
- Tiêu chí nghiệm thu (Acceptance Criteria) trong `user-story.md`.
- Sơ đồ luồng (`user-flow.md`).
- Đặc tả API và giao diện liên quan.

## 4. Cách thu thập
- Phân tích AC để chuyển đổi thành các bước kiểm thử cụ thể.
- Động não (brainstorming) về các trường hợp người dùng nhập sai dữ liệu, mất kết nối mạng, hoặc các thao tác bất thường.
- Tham khảo các kịch bản kiểm thử mẫu của dự án cho các tính năng tương tự (ví dụ: login, CRUD).
- Thảo luận với Developer để hiểu các giới hạn kỹ thuật cần test.

## 5. Format gợi ý / Template áp dụng

```markdown
# [SPEC] TEST SCENARIOS: [Tên User Story]

**Mục tiêu:** Kiểm thử toàn diện các luồng nghiệp vụ của Story [ID].

## SCENARIO 1: [Tên kịch bản thành công]
- **Mức độ:** Major (Happy path)
- **Given:** [Tiền điều kiện, ví dụ: Người dùng đang ở trang thanh toán]
- **When:** [Hành động, ví dụ: Người dùng nhập mã giảm giá hợp lệ và nhấn Áp dụng]
- **Then:** [Kết quả, ví dụ: Tổng tiền được cập nhật giảm đi 10%]

## SCENARIO 2: [Tên kịch bản lỗi/edge case]
- **Mức độ:** Minor
- **Given:** [Người dùng đã nhập mã giảm giá hết hạn]
- **When:** [Nhấn nút Áp dụng]
- **Then:** [Hệ thống hiển thị thông báo "Mã đã hết hạn" màu đỏ]

## SCENARIO 3: [Tên kịch bản bảo mật/dữ liệu]
- **Given:** [Người dùng cố tình nhập mã độc SQL vào ô tìm kiếm]
- **When:** [Nhấn nút Search]
- **Then:** [Hệ thống lọc bỏ ký tự đặc biệt và không gây lỗi crash]
```
