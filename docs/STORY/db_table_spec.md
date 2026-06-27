# HƯỚNG DẪN VIẾT TÀI LIỆU ĐẶC TẢ BẢNG CSDL (db_table_spec.md)

Tài liệu này hướng dẫn cách đặc tả chi tiết một bảng trong Cơ sở dữ liệu (Database Table Specification). Nó giúp cho Developer và DBA nắm rõ cấu trúc, mục đích lưu trữ và các mối quan hệ của bảng đó trong hệ thống.

---

## 1. MÔ TẢ TỔNG QUAN BẢNG (TABLE OVERVIEW)

1. **Mô tả:** Định nghĩa tên bảng và mục đích lưu trữ chính của bảng trong hệ thống nghiệp vụ.
2. **Cách viết:** Nêu rõ tên bảng (viết thường, số nhiều theo chuẩn convention nếu có) và một câu giải thích ngắn gọn dữ liệu mà bảng này sẽ chứa.
3. **Nguồn thông tin:** Yêu cầu nghiệp vụ từ User Story (`user-story.md`) và thiết kế Database tổng thể (`project-level/03-db-diagram.md`).
4. **Cách thu thập:** Phân tích từ đối tượng nghiệp vụ (Entity) trong User Story để xác định tên bảng và ngữ cảnh lưu trữ.
5. **Format gợi ý / Template áp dụng:**
   - **Bảng:** `[Tên bảng]`
   - **Mô tả:** [Câu giải thích ngắn gọn mục đích của bảng]
   > *Ví dụ:* 
   > - **Bảng:** `orders`
   > - **Mô tả:** Lưu trữ thông tin đơn hàng của khách.

---

## 2. CẤU TRÚC CỘT DỮ LIỆU (COLUMNS SPECIFICATION)

1. **Mô tả:** Liệt kê chi tiết các cột (trường dữ liệu) có trong bảng cùng với kiểu dữ liệu, ràng buộc bắt buộc và ý nghĩa của chúng.
2. **Cách viết:** Điền thông tin vào bảng đặc tả. Ghi rõ tên trường, kiểu dữ liệu (VD: UUID, VARCHAR...), có bắt buộc nhập hay không (Có/Không), và giải thích ý nghĩa của trường dữ liệu đó. Cần làm rõ các trường khóa chính (PK) và khóa ngoại (FK) ở cột mô tả nếu có.
3. **Nguồn thông tin:** Tài liệu Data Dictionary (`data-dictionary.md`) và yêu cầu lưu trữ dữ liệu từ phía frontend/backend.
4. **Cách thu thập:** Lấy danh sách các trường dữ liệu cần thiết từ Data Dictionary và ánh xạ chúng sang các kiểu dữ liệu vật lý (như UUID, VARCHAR, Decimal, Enum).
5. **Format gợi ý / Template áp dụng:**
   | STT | Tên trường | Kiểu dữ liệu | Bắt buộc? | Mô tả |
   |---|---|---|---|---|
   | 1 | `id` | UUID | Có | Khóa chính của bảng (PK) |
   | 2 | `user_id` | UUID | Có | ID của người dùng sở hữu đơn hàng (FK) |
   | 3 | `total_amount` | Decimal | Có | Tổng giá trị đơn hàng |
   | 4 | `status` | Enum | Có | Trạng thái đơn hàng (pending, paid, cancelled...) |

---

## 3. QUAN HỆ VÀ RÀNG BUỘC (RELATIONSHIPS & CONSTRAINTS)

1. **Mô tả:** Định nghĩa rõ ràng cách bảng này liên kết với các bảng khác thông qua Khóa ngoại (Foreign Key) và các quan hệ ngược (Reverse Relation), kèm theo các ràng buộc cần thiết khi join bảng.
2. **Cách viết:** Liệt kê từng mối quan hệ:
   - Sử dụng tag `[Foreign Key]` để chỉ ra cột nào của bảng hiện tại trỏ đến bảng nào.
   - Sử dụng tag `[Reverse Relation]` để chỉ ra các bảng khác trỏ về bảng hiện tại.
   - Giải thích ngắn gọn tác dụng hoặc lưu ý bắt buộc khi thao tác (VD: "Bắt buộc phải join...").
3. **Nguồn thông tin:** Biểu đồ ERD (Entity-Relationship Diagram), tài liệu `db_design.md` và `03-db-diagram.md`.
4. **Cách thu thập:** Phân tích luồng dữ liệu liên bảng, thảo luận với Technical Lead/DBA để xác định chiều của các mối quan hệ (1-1, 1-N, N-N).
5. **Format gợi ý / Template áp dụng:**
   - **Quan hệ (Relationships):**
     - `[Foreign Key]` [Tên cột FK] liên kết tới bảng `[Tên bảng gốc]` ([Cột gốc]). [Lưu ý/Giải thích].
     - `[Reverse Relation]` Bảng `[Tên bảng trỏ tới]` trỏ tới bảng này qua `[Tên cột FK]`.
   > *Ví dụ:*
   > - **Quan hệ (Relationships):**
   >   - `[Foreign Key]` `user_id` liên kết tới bảng `users` (`id`). Bắt buộc phải join với bảng này để lấy email khách hàng.
   >   - `[Reverse Relation]` Bảng `payments` trỏ tới bảng này qua `payment_order_id`.
