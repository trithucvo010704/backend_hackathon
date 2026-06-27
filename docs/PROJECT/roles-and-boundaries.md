# HƯỚNG DẪN VIẾT TÀI LIỆU roles-and-boundaries.md (SCOPE, ROLES & BOUNDARIES)

Tài liệu `roles-and-boundaries.md` là văn bản mang tính **thực thi (Operational)** cực kỳ quan trọng áp dụng cho mọi dự án phần mềm. Đây là tài liệu quy định "ranh giới" của hệ thống và con người, làm "hiến pháp" để Developer lập trình chức năng Phân quyền (RBAC), và để QA dùng làm căn cứ viết Test Case bảo mật/luồng rẽ nhánh.

Dưới đây là 5 thành phần cấu trúc tiêu chuẩn và hướng dẫn chi tiết cách viết, áp dụng được cho cả dự án phần mềm truyền thống lẫn dự án có tích hợp AI.

---

## 1. Phạm Vi Thực Thi Chi Tiết (Detailed Execution Scope)

1. **Mô tả:** Định nghĩa ranh giới kỹ thuật và ranh giới nghiệp vụ của hệ thống. Nêu rõ tính năng nào làm tới đâu, mức độ tự động hóa, và giới hạn tích hợp với các hệ thống bên ngoài (External Systems/3rd Parties).
2. **Cách viết:** Trả lời: Hành động này do hệ thống tự làm hay người dùng làm tay? Có đồng bộ 2 chiều với hệ thống khác không? Việc đẩy dữ liệu, tạo bảng, hay gửi SMS là trong hay ngoài phạm vi phiên bản này?
3. **Nguồn thông tin:** Hợp đồng/SOW, Tài liệu Yêu cầu nghiệp vụ (BRD), Tech Lead.
4. **Cách thu thập:** Rà soát từng chốt chạm trong sơ đồ luồng nghiệp vụ (Workflow). Thấy bất kỳ hành động nào liên quan đến hệ thống khác phải đặt câu hỏi ngay: "Đồng bộ thế nào? Chặn lỗi ra sao?".
5. **Format gợi ý / Template áp dụng:**
   - **In-Scope:** [Các giới hạn tính năng sẽ làm]
   - **Out-of-Scope:** [Tính năng KHÔNG làm, hoặc do hệ thống khác/con người tự chịu trách nhiệm]

---

## 2. Cơ Cấu Tác Nhân (Team & System Actors)

1. **Mô tả:** Định nghĩa tất cả các "Tác nhân" (Actors) sẽ tham gia tương tác với hệ thống. Tác nhân có thể là Con người (User/Admin), Hệ thống bên thứ 3 (Webhook/Cronjob), hoặc Trí tuệ nhân tạo (AI Agents).
2. **Cách viết:** Xác định có bao nhiêu nhóm người dùng? (Khách hàng, Nhân viên, Quản trị viên...). Có hệ thống tự động/AI nào chạy ngầm không? Trách nhiệm cốt lõi của từng tác nhân là gì? 
3. **Nguồn thông tin:** Sơ đồ User Journey, Kiến trúc hệ thống.
4. **Cách thu thập:** Liệt kê toàn bộ các role xuất hiện trong quy trình thực tế. Đừng bỏ quên các role kiểm duyệt (Checker/Reviewer) hoặc role vận hành (System Admin/DevOps).
5. **Format gợi ý / Template áp dụng:**
   | Vai trò / Tác nhân | Loại: Human/System/AI | Nhiệm vụ cốt lõi |
   |---|---|---|
   | [Tên Role] | [Loại] | [Nhiệm vụ] |

---

## 3. Ma Trận Phân Quyền (Permission Matrix - RBAC)

1. **Mô tả:** Đây là bảng dữ liệu "xương sống" cho team Backend code phân quyền và phân tách giao diện Frontend. Bảng này phải vét cạn toàn bộ các thao tác (Actions) có thể làm trên hệ thống.
2. **Cách viết:** Xác định mỗi Role được làm gì và không được làm gì? (Cần bóc tách rõ quyền View/Read, Create, Update, Delete). Ai có quyền chuyển trạng thái dữ liệu?
3. **Nguồn thông tin:** Luồng nghiệp vụ (Business Flow), Trải nghiệm người dùng (UX/UI Mocks).
4. **Cách thu thập:** Đừng dùng từ "CRUD" chung chung. Hãy đặt mình vào từng tính năng/màn hình, liệt kê mọi tương tác. Sau đó đánh dấu check (✅ / ❌) cho từng cột Role.
5. **Format gợi ý / Template áp dụng:**
   | Tính năng / Hành động | Role A (Admin) | Role B (User) | Role C (Guest) |
   |---|:---:|:---:|:---:|
   | Xem danh sách (View) | ✅ | ✅ | ❌ |
   | Tạo mới (Create) | ✅ | ❌ | ❌ |
   | Cập nhật (Update) | ✅ (Tất cả) | ✅ (Chỉ của mình) | ❌ |
   | Xóa (Delete) | ✅ | ❌ | ❌ |

---

## 4. Giới Hạn Hệ Thống & Tự Động Hóa (System Boundaries & Constraints)

1. **Mô tả:** Định nghĩa rõ những "Giới hạn đỏ" (Hard constraints) mà hệ thống tự động (Script, Cronjob, hoặc AI Agent) TUYỆT ĐỐI không được phép vượt qua.
2. **Cách viết:** Xác định hành động tự động nào nếu sai sẽ gây hậu quả nghiêm trọng? Hệ thống tự động có được phép tự ra quyết định cuối cùng không, hay bắt buộc phải có con người xác nhận (Human-in-the-loop)?
3. **Nguồn thông tin:** Đội ngũ Security, Business Owner, Tech Lead.
4. **Cách thu thập:** Đánh giá rủi ro (Risk Assessment). Đặt câu hỏi: "Nếu module tự động hóa/AI này bị lỗi hoặc bị hack, nó có thể phá hủy cái gì?".
5. **Format gợi ý / Template áp dụng:**
   - **CẤM:** [Hành động bị cấm]
   - **BẮT BUỘC:** [Hành động bắt buộc phải có con người xác nhận]

---

## 5. Quy Tắc Đặc Biệt & Ngoại Lệ (Special Rules & Edge Cases)

1. **Mô tả:** Giải thích các quy tắc nghiệp vụ (Business Rules) phức tạp về mặt quản trị mà không thể biểu diễn chỉ bằng bảng Ma trận phân quyền.
2. **Cách viết:** Xác định có trường hợp nào 1 user đóng 2 vai trò? Tính năng ủy quyền (Delegation) hoạt động thế nào? Quy định về số lượng tối thiểu/tối đa (Max/Min) của một nhóm quyền?
3. **Nguồn thông tin:** Quy trình quản trị thực tế của tổ chức/khách hàng.
4. **Cách thu thập:** Tổ chức họp Q&A để hỏi những câu "oái oăm" (Edge Cases).
5. **Format gợi ý / Template áp dụng:**
   | Tên quy tắc (Edge Case) | Mô tả chi tiết | Nơi xử lý (FE/BE) |
   |---|---|---|
   | [Tên quy tắc] | [Mô tả] | [Vị trí xử lý] |
