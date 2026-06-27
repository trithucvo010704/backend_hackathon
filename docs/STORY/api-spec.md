# HƯỚNG DẪN VIẾT TÀI LIỆU API SPECIFICATION (api-spec.md)

Tài liệu này định nghĩa giao ước kết nối (Data Contract) giữa Backend và Frontend. Nó nằm ở cấp độ User Story.

Dưới đây là các thành phần cấu trúc bắt buộc:

---

## 1. DANH SÁCH ENDPOINTS (Summary)

1. **Mô tả:** Bảng tổng hợp các API cần thiết để thực hiện Story.
2. **Cách viết:** Xác định số lượng API, chức năng và HTTP Method phù hợp (RESTful standard).
3. **Nguồn thông tin:** Phân tích các thao tác trên User Flow và Acceptance Criteria.
4. **Cách thu thập:** Tuân thủ tiêu chuẩn RESTful (danh từ số nhiều, kebab-case).
5. **Format gợi ý / Template áp dụng:**
   | Endpoint | Method | Mô tả chức năng | Quyền hạn (Auth/Role) |
   |---|---|---|---|
   | `/api/v1/resources` | GET | Lấy danh sách | User |

---

## 2. CHI TIẾT API (API Details)

1. **Mô tả:** Định nghĩa chi tiết Input (Request) và Output (Response) cho từng Endpoint. Mỗi API phải được đặc tả đầy đủ bao gồm: thông tin kết nối, cấu trúc dữ liệu gửi đi (Request), cấu trúc dữ liệu trả về (Response), và các mã lỗi thường gặp.
2. **Cách viết:**
    - **Thông tin kết nối:** Ghi rõ HTTP Method và Endpoint URL (tuân thủ RESTful, kebab-case).
    - **Headers:** Liệt kê các header bắt buộc (VD: `Content-Type`, `Authorization`).
    - **Request Body:** Sử dụng **bảng Markdown** để liệt kê chi tiết từng trường dữ liệu (Tên trường, Kiểu dữ liệu, Bắt buộc, Mô tả).
    - **Response Body:** Sử dụng **bảng Markdown** tương tự để liệt kê chi tiết các trường trả về.
    - **QUY ĐỊNH BẮT BUỘC:** Đối với các trường có kiểu dữ liệu là **Object** hoặc **Array**, **KHÔNG ĐƯỢC PHÉP** chỉ mô tả chung chung (VD: `data | Object | Dữ liệu`). Phải liệt kê chi tiết các thuộc tính con bên trong bằng cách sử dụng dấu chấm `.` (VD: `data.user_id`, `data.items[].name`).
    - **JSON Example:** BẮT BUỘC kèm khối code `json` cho cả Request và Response làm ví dụ minh họa thực tế.
    - **Error Codes:** Liệt kê các mã lỗi HTTP kèm giải thích nguyên nhân cụ thể.
3. **Nguồn thông tin:** Data Dictionary, DB Design, `project-level/07-api-integration.md`.
4. **Cách thu thập:** Định nghĩa rõ kiểu dữ liệu và tính bắt buộc. Rà soát các trường thông tin trên UI/UX Design. Đối soát với danh mục Endpoint đã phê duyệt.
5. **Format gợi ý / Template áp dụng:**
   `````markdown
   ### [Tên API]

   **Mô tả tổng quan:**
   [Mô tả chi tiết mục đích và luồng xử lý chính của API].

   **Thông tin kết nối:**

   * **Method (Phương thức):** `[HTTP_METHOD]`
   * **Endpoint (Đường dẫn):** `[URL_PATH]`

   **Yêu cầu gửi đi (Request):**

   * **Headers:**
     * `Content-Type`: application/json
     * `Authorization`: Bearer [token] (nếu có)

   * **Cấu trúc Dữ liệu gửi đi (Request Body):**

   | Tên trường (Field) | Kiểu dữ liệu (Type) | Bắt buộc (Required)? | Mô tả chi tiết (Description) |
   | --- | --- | --- | --- |
   | `parent_field` | Object | **Có** | Mô tả tổng quan về đối tượng. |
   | `parent_field.child_key` | String | **Có** | Mô tả chi tiết thuộc tính con. **(Bắt buộc liệt kê chi tiết)** |
   | `items` | Array[Object] | **Có** | Danh sách phần tử. |
   | `items[].id` | Number | **Có** | ID từng phần tử. **(Bắt buộc liệt kê chi tiết)** |

   * **Ví dụ gói dữ liệu gửi đi (JSON):**
   ```json
   {
     "parent_field": {
        "child_key": "value"
     },
     "items": [
        {"id": 1}
     ]
   }
   ```

   **Kết quả trả về (Response):**

    * **Cấu trúc Dữ liệu trả về khi thành công (HTTP Status 200/201):**

   | Tên trường (Field) | Kiểu dữ liệu (Type) | Mô tả chi tiết (Description) |
      | --- | --- | --- |
   | `status` | String | Trạng thái API ("success"). |
   | `data` | Object | Đối tượng chứa dữ liệu phản hồi. |
   | `data.user_id` | String | Mã định danh người dùng. **(Bắt buộc liệt kê chi tiết các trường con)** |
   | `data.items` | Array[Object] | Danh sách các phần tử. |
   | `data.items[].id` | Number | ID từng phần tử trong mảng. |

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

   **Các mã lỗi thường gặp (Error Codes):**

    * `400 Bad Request`: [Lý do lỗi dữ liệu đầu vào].
    * `401 Unauthorized`: [Lỗi xác thực / thiếu token].
    * `403 Forbidden`: [Không đủ quyền truy cập].
    * `409 Conflict`: [Lỗi xung đột dữ liệu/nghiệp vụ].
    * `500 Internal Server Error`: [Lỗi hệ thống].
   ````
