# HƯỚNG DẪN VIẾT TÀI LIỆU api-integration.md (QUY CHUẨN GIAO TIẾP API)

Tài liệu này là "Ngôn ngữ chung" giữa Frontend, Backend và các đối tác bên ngoài. Nó đảm bảo tính đồng nhất trong giao tiếp dữ liệu.

> [!IMPORTANT]
> Tài liệu `api-spec.md` tại cấp độ **User Story** là **Shared Contract** duy nhất. Backend làm Output, Frontend làm Input.

Dưới đây là 4 thành phần cấu trúc bắt buộc:

---

## 1. Cấu Trúc Phản Hồi Chuẩn (Base Response Format)

1. **Mô tả:** Định dạng JSON "đồng phục" cho 100% các API trả về.
2. **Cách viết:** Xác định cấu trúc chung gồm những key nào? Dữ liệu thật bọc ở đâu? Cách báo lỗi qua status code.
3. **Nguồn thông tin:** Tech Lead, Backend Architect.
4. **Cách thu thập:** Thống nhất format JSON dùng chung cho toàn bộ dự án.
5. **Format gợi ý / Template áp dụng:**
   - **Thành công:**
   ```json
   {
     "status": 1,
     "data": { "id": 1, "name": "Item" },
     "message": "Success"
   }
   ```
   - **Thất bại:**
    ```json
   {
     "status": 0,
     "data": null,
     "message": "Lỗi người dùng",
     "payload": { "error_code": 400 }
   }
   ```

---

## 2. Từ Điển Mã Lỗi (Error Code Dictionary)

1. **Mô tả:** Hệ thống mã lỗi (Error Code) giúp Frontend xử lý logic và đa ngôn ngữ dễ dàng.
2. **Cách viết:** Phân nhóm lỗi theo đầu số (1xxx, 2xxx). Gắn mỗi mã với một hành động cụ thể cho Frontend.
3. **Nguồn thông tin:** Tech Lead, Business Analyst.
4. **Cách thu thập:** Liệt kê các kịch bản lỗi nghiệp vụ (Business Exceptions) và lỗi hệ thống.
5. **Format gợi ý / Template áp dụng:**
   | Mã lỗi (Code) | HTTP Status | Mô tả nguyên nhân | Hành động của Frontend |
   |---|---|---|---|
   | [CODE_001] | [4xx/5xx] | [Lý do] | [Xử lý UI] |

---

## 3. Quy Tắc Thiết Kế Endpoint (RESTful Standards)

1. **Mô tả:** Luật đặt tên URL và sử dụng HTTP Methods.
2. **Cách viết:** Trả lời: Dùng Noun hay Verb? Quy tắc dùng method (GET/POST/PUT/DELETE) ra sao? Quy định về Versioning.
3. **Nguồn thông tin:** Backend Team, API Design Guide.
4. **Cách thu thập:** Áp dụng tiêu chuẩn RESTful API phổ biến.
5. **Format gợi ý / Template áp dụng:**
   - **URL Pattern:** `/api/v1/[resources]`
   - **Naming:** Sử dụng danh từ số nhiều, lowercase.
   - **Methods:** `GET` (Lấy), `POST` (Tạo), `PUT` (Cập nhật), `DELETE` (Xóa).

---

## 4. Quản Trị Xác Thực & Bảo Mật (Auth & Security)

1. **Mô tả:** Quy định cách hệ thống bảo mật các API (Token, API Key).
2. **Cách viết:** Xác định cách truyền Token (Header), thời gian sống (TTL), và cơ chế Refresh Token.
3. **Nguồn thông tin:** Security Architect, Tech Lead.
4. **Cách thu thập:** Thiết kế luồng xác thực (Authentication Flow).
5. **Format gợi ý / Template áp dụng:**
   - **Header:** `Authorization: Bearer [token]`
   - **Auth Flow:**
     ```mermaid
     sequenceDiagram
       Client->>Auth: Login Credentials
       Auth-->>Client: JWT + Refresh Token
       Client->>API: Header Authorization
     ```
