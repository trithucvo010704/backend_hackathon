# HƯỚNG DẪN VIẾT TÀI LIỆU TASK TO-DO LIST (task-todo.md)

Tài liệu này là bản đặc tả chi tiết quá trình thực thi code (Low-level implementation), được viết bởi **Developer**. Nó cụ thể hóa các định hướng từ `task-spec.md` thành các bước thực hiện thực tế.

---

## 1. CHI TIẾT FILE & CLASS (Implementation Targets)

1. **Mô tả:** Xác định chính xác các tệp tin và lớp sẽ được tạo mới hoặc chỉnh sửa trong quá trình thực hiện task.
2. **Cách viết:** Liệt kê đường dẫn file và tên class/interface tương ứng. Nêu rõ mục đích của từng thay đổi.
3. **Nguồn thông tin:** Cấu trúc source code hiện tại và định hướng từ `task-spec.md`.
4. **Cách thu thập:** Rà soát code hiện có để tìm vị trí cần chèn logic hoặc tạo module mới.
5. **Format gợi ý / Template áp dụng:**
   - [ ] `src/main/java/com/project/dto/XRequest.java`: Tạo mới DTO cho request.
   - [ ] `src/main/java/com/project/service/YService.java`: Thêm method xử lý logic Z.
   - [ ] `src/main/resources/mapper/ZMapper.xml`: Viết query SQL lấy dữ liệu.

---

## 2. LOGIC THỰC THI CHI TIẾT (Execution Logic)

1. **Mô tả:** Mô tả chi tiết thuật toán, các bước xử lý dữ liệu và các điều kiện kiểm tra bên trong code.
2. **Cách viết:** Trình bày tuần tự các bước logic (Algorithm steps). Sử dụng mã giả (pseudocode) nếu cần thiết để làm rõ các đoạn code phức tạp.
3. **Nguồn thông tin:** Nghiệp vụ trong User Story và giải pháp kỹ thuật từ Tech Lead.
4. **Cách thu thập:** Phân tích luồng dữ liệu đầu vào, các bước biến đổi và kết quả đầu ra.
5. **Format gợi ý / Template áp dụng:**
   - **Bước 1:** Validate dữ liệu đầu vào (Check null, check range...).
   - **Bước 2:** Gọi Service bên ngoài để kiểm tra trạng thái A.
   - **Bước 3:** Thực hiện tính toán B dựa trên công thức C.
   - **Bước 4:** Lưu kết quả vào Database và bắn message tới Kafka topic D.

---

## 3. KIỂM THỬ ĐƠN VỊ (Unit Testing)

1. **Mô tả:** Xác định các kịch bản kiểm thử cần thiết để đảm bảo tính đúng đắn của code và bao phủ các trường hợp ngoại lệ.
2. **Cách viết:** Liệt kê các test cases kèm theo dữ liệu đầu vào (Input) và kết quả mong đợi (Expected Output).
3. **Nguồn thông tin:** Acceptance Criteria của User Story và Logic thực thi ở mục 2.
4. **Cách thu thập:** Suy luận các trường hợp biên (edge cases), dữ liệu sai định dạng và luồng xử lý thành công.
5. **Format gợi ý / Template áp dụng:**
   | Case ID | Kịch bản kiểm thử | Dữ liệu đầu vào | Kết quả mong đợi |
   |---|---|---|---|
   | TC-01 | Xử lý thành công | Data hợp lệ | Trả về 200 OK & Lưu DB |
   | TC-02 | Lỗi validate dữ liệu | Trường A = null | Trả về 400 Bad Request |
   | TC-03 | Lỗi hệ thống bên ngoài | API đối tác timeout | Retry 3 lần & Log error |

---

## 4. GHI CHÚ TỰ KIỂM TRA (Dev Checklist)

1. **Mô tả:** Các bước kiểm tra cuối cùng về chất lượng code và an toàn hệ thống trước khi tạo Pull Request.
2. **Cách viết:** Checklist các tiêu chuẩn Clean Code, Security và Performance đã được thống nhất trong dự án.
3. **Nguồn thông tin:** Tài liệu `02_CODING_STANDARDS.md` và `08_SAFETY_AND_BOUNDARIES.md`.
4. **Cách thu thập:** Đối chiếu code đã viết với các quy tắc chung của dự án.
5. **Format gợi ý / Template áp dụng:**
   - [ ] Đã handle Exception cho tất cả các luồng chính?
   - [ ] Đã đóng các tài nguyên (Stream, Connection) sau khi sử dụng?
   - [ ] Không có thông tin nhạy cảm (Password, Token) được log ra console?
   - [ ] Code đã tuân thủ đúng định dạng (Formatting) và đặt tên (Naming) của dự án?
   - [ ] Đã chạy Unit Test và đạt tỷ lệ bao phủ (Coverage) tối thiểu?
