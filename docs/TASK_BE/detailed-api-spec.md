# HƯỚNG DẪN VIẾT ĐẶC TẢ CHI TIẾT API (detailed-api-spec.md)

Tài liệu này cung cấp hướng dẫn chuẩn để viết đặc tả chi tiết cho từng API, tập trung vào cấu trúc dữ liệu minh bạch và ví dụ thực tế. Đây là phiên bản nâng cấp của `api-spec.md`, tối ưu cho việc triển khai code chính xác.

---

## 1. Tên API & Mô tả tổng quan

1. **Mô tả:** Xác định danh tính của API và mục đích nghiệp vụ mà nó giải quyết.
2. **Cách viết:** 
   - Tên API: Ngắn gọn, nêu bật hành động và thực thể (VD: Tạo tài khoản người dùng).
   - Mô tả: Giải thích API này dùng để làm gì, kết quả mong đợi sau khi thực hiện thành công là gì (VD: tạo mã định danh, gửi mail xác nhận...).
   - **Visuals (Optional):** Sử dụng `mermaid` để vẽ sequence diagram hoặc flowchart nếu logic xử lý phía Backend phức tạp.
3. **Nguồn thông tin:** User Story, Business Process Flow.
4. **Cách thu thập:** Phân tích các bước trong User Journey để xác định điểm chạm với dữ liệu.
5. **Format gợi ý / Template áp dụng:**
   ```markdown
   **Tên API:** [Tên chức năng API]

   **1. Mô tả tổng quan:**
   [Mô tả chi tiết mục đích và luồng xử lý chính của API].
   ```

---

## 2. Thông tin kết nối

1. **Mô tả:** Các thông số kỹ thuật cơ bản để gọi API.
2. **Cách viết:** 
   - Method: Sử dụng đúng HTTP Method (POST, GET, PUT, PATCH, DELETE).
   - Endpoint: Đường dẫn URL chính xác, tuân thủ RESTful (kebab-case).
3. **Nguồn thông tin:** `project-level/07-api-integration.md`.
4. **Cách thu thập:** Đối soát với danh mục Endpoint đã được phê duyệt trong kiến trúc tổng thể.
5. **Format gợi ý / Template áp dụng:**
   ```markdown
   **2. Thông tin kết nối:**

   * **Method (Phương thức):** `[HTTP_METHOD]`
   * **Endpoint (Đường dẫn):** `[URL_PATH]`
   ```

---

## 3. Yêu cầu gửi đi (Request)

1. **Mô tả:** Đặc tả toàn bộ dữ liệu đầu vào mà Frontend cần gửi lên Backend.
2. **Cách viết:** 
   - Headers: Các header bắt buộc (VD: Content-Type, Authorization).
   - Bảng Request Body: Liệt kê chi tiết từng trường (Tên trường, Kiểu dữ liệu, Bắt buộc, Mô tả).
   - **QUY ĐỊNH BẮT BUỘC:** Đối với các trường có kiểu dữ liệu là **Object** hoặc **Array**, **KHÔNG ĐƯỢC PHÉP** chỉ mô tả chung chung (VD: `data | Object | Dữ liệu`). Bạn phải liệt kê chi tiết các thuộc tính con bên trong bằng cách sử dụng dấu chấm `.` (VD: `data.user_id`, `data.items[0].name`).
   - JSON Example: Một gói dữ liệu mẫu hoàn chỉnh.
3. **Nguồn thông tin:** `data-dictionary.md`, `db_design.md`.
4. **Cách thu thập:** Rà soát các trường thông tin cần thiết trên UI/UX Design để đảm bảo không thiếu dữ liệu.
5. **Format gợi ý / Template áp dụng:**
   ```markdown
   **3. Yêu cầu gửi đi (Request):**

   * **Headers:**
   * `Content-Type`: application/json
   * `Authorization`: Bearer [token] (nếu có)

   * **Cấu trúc Dữ liệu gửi đi (Request Body):**

   | Tên trường (Field) | Kiểu dữ liệu (Type) | Bắt buộc (Required)? | Mô tả chi tiết (Description) |
   | --- | --- | --- | --- |
   | `parent_field` | Object | **Có** | Mô tả tổng quan về đối tượng. |
   | `parent_field.child_key` | String | **Có** | Mô tả chi tiết thuộc tính bên trong object. **(Bắt buộc liệt kê chi tiết như thế này)** |

   * **Ví dụ gói dữ liệu gửi đi (JSON):**
   ```json
   {
     "parent_field": {
        "child_key": "value"
     }
   }
   ```
   ```

---

## 4. Kết quả trả về (Response)

1. **Mô tả:** Đặc tả cấu trúc dữ liệu mà Backend trả về sau khi xử lý thành công.
2. **Cách viết:** 
   - HTTP Status: Mã trạng thái thành công (VD: 200 OK, 201 Created).
   - Bảng Response Body: Liệt kê các trường trả về.
   - **QUY ĐỊNH BẮT BUỘC:** Tương tự Request, các trường **Object/Array** phải được "phẳng hóa" (flatten) trong bảng mô tả bằng dấu chấm `.` để đặc tả từng thuộc tính con. Tuyệt đối không để trống hoặc mô tả sơ sài ở cấp độ Object cha.
   - JSON Example: Ví dụ thực tế về gói tin phản hồi thành công.
3. **Nguồn thông tin:** `07-api-integration.md` (Base Response), `db_design.md`.
4. **Cách thu thập:** Xác định các thông tin tối thiểu cần thiết để Frontend hiển thị hoặc tiếp tục luồng xử lý.
5. **Format gợi ý / Template áp dụng:**
   ```markdown
   **4. Kết quả trả về (Response):**

   * **Cấu trúc Dữ liệu trả về khi thành công (HTTP Status 200/201):**

   | Tên trường (Field) | Kiểu dữ liệu (Type) | Mô tả chi tiết (Description) |
   | --- | --- | --- |
   | `status` | String | Trạng thái API ("success"). |
   | `data` | Object | Đối tượng chứa dữ liệu phản hồi. |
   | `data.user_id` | String | Mã định danh người dùng. **(Bắt buộc liệt kê chi tiết các trường con)** |
   | `data.items` | Array[Object] | Danh sách các phần tử. |
   | `data.items[].id` | Number | ID của từng phần tử trong mảng. |

   * **Ví dụ gói dữ liệu nhận về (JSON):**
   ```json
   {
     "status": "success",
     "data": { 
        "user_id": "USR-123",
        "items": [
            {"id": 1}
        ]
     }
   }
   ```
   ```

---

## 5. Các mã lỗi thường gặp (Error Codes)

1. **Mô tả:** Liệt kê các kịch bản lỗi có thể xảy ra và cách phản hồi.
2. **Cách viết:** Liệt kê mã HTTP đi kèm với giải thích nguyên nhân cụ thể.
3. **Nguồn thông tin:** `project-level/07-api-integration.md`.
4. **Cách thu thập:** Phân tích các ràng buộc nghiệp vụ (VD: trùng email, thiếu quyền...) và lỗi hệ thống.
5. **Format gợi ý / Template áp dụng:**
   ```markdown
   **5. Các mã lỗi thường gặp (Error Codes):**

   * `400 Bad Request`: [Lý do lỗi dữ liệu].
   * `401 Unauthorized`: [Lỗi xác thực].
   * `409 Conflict`: [Lỗi xung đột dữ liệu/nghiệp vụ].
   * `500 Internal Server Error`: [Lỗi hệ thống].
   ```
