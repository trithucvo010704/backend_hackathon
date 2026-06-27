# HƯỚNG DẪN VIẾT TÀI LIỆU BACKEND ACCEPTANCE CRITERIA (acceptance_criteria.md)

Tài liệu này đóng vai trò là "Kịch bản Test" (Test Case) cấp độ Task, dùng để làm căn cứ cho QA hoặc các công cụ Automated Testing (Cypress, Playwright, Jest) xác nhận Task đã hoàn thành.

---

## 1. KIỂM CHỨNG DATA & API PAYLOAD (DATA VERIFICATION)

*   **Mô tả:** Các tiêu chí đánh giá về mặt dữ liệu trả về của API.
*   **Cách viết:**
    *   **Câu hỏi cần trả lời:** Dữ liệu có trả về đúng format JSON quy định không? Các trường Nullable có được handle không?
    *   **Lấy thông tin ở đâu:** File `api_spec.md` của Task.
    *   **Lấy như thế nào:** Checklist các điều kiện bắt buộc về Schema.
    *   **Format gợi ý / Template áp dụng:**
        - [ ] Dữ liệu trả về đúng định dạng JSON contract.
        - [ ] Phân trang (Pagination) hoạt động chính xác.
        - [ ] Status Code trả về đúng chuẩn RESTful.

---

## 2. KIỂM CHỨNG NGHIỆP VỤ (BDD VERIFICATION)

*   **Mô tả:** Các kịch bản test luồng logic cốt lõi của task.
*   **Cách viết:**
    *   **Câu hỏi cần trả lời:** Khi user làm thao tác A trong bối cảnh B thì hệ thống phải ra kết quả C. Xử lý Edge cases (trường hợp dị biệt) như thế nào?
    *   **Lấy thông tin ở đâu:** File `user-story.md` phần Acceptance Criteria.
    *   **Lấy như thế nào:** Phân rã từ kịch bản BDD của Story thành các test case nhỏ hơn, tập trung vào Task hiện tại. Đặc biệt nhấn mạnh xử lý lỗi (Error handling) thân thiện, không crash app.
    *   **Format gợi ý / Template áp dụng:**
        - [ ] GIVEN `[Bối cảnh]` WHEN `[Hành động]` THEN `[Kết quả]`.
        - [ ] Handle Edge case: `[Mô tả case và cách xử lý]`.

---

## 3. HIỆU NĂNG & QUY CHUẨN CODE (QUALITY & SECURITY)

*   **Mô tả:** Các tiêu chuẩn kỹ thuật ẩn dưới bề mặt (Non-functional requirements) mà Dev phải tuân thủ.
*   **Cách viết:**
    *   **Câu hỏi cần trả lời:** Code có bị rò rỉ bộ nhớ (Memory Leak) không? Có lỗi ẩn trong console không? API gọi có bị chậm không? Có rò rỉ dữ liệu nhạy cảm không?
    *   **Lấy thông tin ở đâu:** Tiêu chuẩn Code Quality của toàn dự án.
    *   **Lấy như thế nào:** Đưa ra checklist các lỗi kỹ thuật thường gặp nhất để rà soát trước khi merge code.
    *   **Format gợi ý / Template áp dụng:**
        - [ ] Không có lỗi runtime hoặc console error (Frontend/Backend).
        - [ ] Không rò rỉ bộ nhớ (Quản lý lifecycle hợp lý).
        - [ ] Không lưu cleartext dữ liệu nhạy cảm ở Client.
