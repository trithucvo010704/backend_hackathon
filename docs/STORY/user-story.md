# HƯỚNG DẪN VIẾT TÀI LIỆU USER STORY (user-story.md)

Tài liệu này là chi tiết của một tính năng từ góc nhìn của người dùng. Một User Story đủ tốt là một Story sẵn sàng để được phát triển (Ready for Dev).

Dưới đây là các thành phần cấu trúc bắt buộc:

---

## 1. MÔ TẢ USER STORY (Story Description)

1. **Mô tả:** Trình bày yêu cầu theo cú pháp chuẩn của Agile để làm rõ Đối tượng, Hành động và Giá trị.
2. **Cách viết:** Trả lời: Ai thực hiện? Làm gì? Tại sao làm? Story đáng giá bao nhiêu điểm (Point)?
3. **Nguồn thông tin:** Lấy từ phần Scope của file \`brief.md\` và hiểu biết về Persona người dùng.
4. **Cách thu thập:** Phân tích nhu cầu thực tế. Áp dụng Vertical Slicing để đảm bảo Story có thể release độc lập.
5. **Format gợi ý / Template áp dụng:**
   - **Story:** Là một \`[Role/Persona]\`, tôi muốn \`[Action]\`, để \`[Value/Reason]\`.
   - **Story Points:** \`[1, 2, 3, 5, 8]\`

---

## 2. ACCEPTANCE CRITERIA (Tiêu Chí Nghiệm Thu)

1. **Mô tả:** Định nghĩa rõ ràng các điều kiện/kịch bản để xác nhận rằng tính năng này đã "Hoàn thành" và hoạt động đúng.
2. **Cách viết:** Mô tả các kịch bản Happy path, Unhappy path và Edge cases bằng ngôn ngữ BDD.
3. **Nguồn thông tin:** Logic quy trình từ \`brief.md\` và thảo luận cùng QA/Tester.
4. **Cách thu thập:** Liệt kê các kịch bản có thể xảy ra và mô tả chúng bằng cấu trúc Given-When-Then.
5. **Format gợi ý / Template áp dụng:**
   - **Scenario 1:** [Tên kịch bản]
     - **GIVEN** [Bối cảnh]
     - **WHEN** [Hành động]
     - **THEN** [Kết quả]

---

## 3. OUT OF SCOPE (Ngoài Phạm Vi)

1. **Mô tả:** Xác định rõ ràng những gì **không** thuộc phạm vi của User Story này để tránh "Scope Creep".
2. **Cách viết:** Liệt kê các tính năng dễ gây nhầm lẫn nhưng đã được chốt là không làm trong Story này.
3. **Nguồn thông tin:** Quá trình Grooming/Planning meeting.
4. **Cách thu thập:** Rà soát các ranh giới nghiệp vụ đã thống nhất với Stakeholders.
5. **Format gợi ý / Template áp dụng:**
   - [x] KHÔNG bao gồm tính năng [Tính năng A].
   - [x] KHÔNG hỗ trợ [Trường hợp B].
