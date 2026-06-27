# HƯỚNG DẪN VIẾT TÀI LIỆU DATABASE DESIGN (db_design.md)

Tài liệu này dùng để thiết kế kiến trúc lưu trữ dữ liệu cho User Story. Hướng dẫn việc tạo bảng mới, sửa bảng cũ, đánh Index và quy tắc viết Migration an toàn.

---

## 1. THIẾT KẾ SCHEMA (SCHEMA DESIGN)

*   **Mô tả:** Bản vẽ các bảng (Table/Collection) và quan hệ giữa chúng (Entity-Relationship).
*   **Cách viết:**
    *   **Câu hỏi cần trả lời:** Tính năng này cần lưu trữ những gì? Cần tạo bảng mới hay thêm cột vào bảng cũ? Quan hệ giữa các bảng là 1-1, 1-N, hay N-N?
    *   **Lấy thông tin ở đâu:** Từ tài liệu Data Dictionary và User Story.
    *   **Lấy như thế nào:** Chuẩn hóa dữ liệu (Normalization) để tránh dư thừa, nhưng cũng cần tính đến việc phi chuẩn hóa (Denormalization) nếu ưu tiên tốc độ đọc. Xác định rõ Khóa chính (Primary Key) và Khóa ngoại (Foreign Key).
    *   **Format gợi ý / Template áp dụng:**
        | Tên Bảng (Table) | Cột (Column) | Kiểu dữ liệu (Type) | Ràng buộc (Constraint / Key) | Ghi chú |
        |---|---|---|---|---|
        | `users` | `id` | `BIGINT` | `PK, AUTO_INCREMENT` | |
        | `users` | `email` | `VARCHAR(255)`| `UNIQUE, NOT NULL` | |
        | `orders`| `user_id` | `BIGINT` | `FK -> users(id)` | |

---

## 2. CHIẾN LƯỢC TỐI ƯU & MIGRATION (INDEXING & SAFETY)

*   **Mô tả:** Hướng dẫn tối ưu hóa truy vấn và các lưu ý sinh tử khi thay đổi cấu trúc DB trên môi trường Production.
*   **Cách viết:**
    *   **Câu hỏi cần trả lời:** Query nào sẽ được gọi nhiều nhất (Read-heavy hay Write-heavy)? Cần đánh Index ở cột nào? Nếu chạy script cập nhật DB (Migration), làm sao để hệ thống không bị chết (Downtime)?
    *   **Lấy thông tin ở đâu:** Từ việc phân tích các API Endpoints và dự báo lượng dữ liệu (Traffic/Volume).
    *   **Lấy như thế nào:** Đưa ra quyết định đánh Index (B-Tree, Hash, GIN...) cho các cột thường xuyên được `WHERE`, `JOIN`, `ORDER BY`. BẮT BUỘC có quy tắc an toàn cho Migration (Ví dụ: Không bao giờ được phép dùng `DROP TABLE` hoặc xóa data trực tiếp).
    *   **Format gợi ý / Template áp dụng:**
        - **Indexing Strategy:** Đánh index `idx_user_email` cho bảng `users` cột `email` vì API tìm kiếm email được gọi với tần suất cao.
        - **Migration Rules:** Không dùng `ALTER TABLE ... DROP COLUMN`. Hãy tạo cột mới `new_col`, copy data sang, sau đó đánh dấu `old_col` là deprecated trong code trước.
