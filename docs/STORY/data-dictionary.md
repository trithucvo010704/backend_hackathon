# HƯỚNG DẪN VIẾT TÀI LIỆU DATA DICTIONARY (data-dictionary.md)

Tài liệu này định nghĩa từ vựng nghiệp vụ (Ubiquitous Language) và các quy tắc/ràng buộc về dữ liệu (Business Rules). Nó đảm bảo cả team BA, Dev, QA và Khách hàng đều dùng chung một hệ ngôn ngữ khi nói về một trường thông tin.

---

## 1. THUẬT NGỮ NGHIỆP VỤ (UBIQUITOUS LANGUAGE)

*   **Mô tả:** Giải nghĩa các từ viết tắt, từ lóng hoặc các khái niệm nghiệp vụ đặc thù có trong User Story.
*   **Cách viết:**
    *   **Câu hỏi cần trả lời:** Có từ khóa nào mà người ngoài ngành đọc vào sẽ không hiểu không? Một khái niệm có bị gọi bằng nhiều tên khác nhau (VD: Khách hàng / Customer / Client) không?
    *   **Lấy thông tin ở đâu:** Hỏi trực tiếp chuyên gia nghiệp vụ (Domain Expert) hoặc Product Owner.
    *   **Lấy như thế nào:** Thu thập và chốt một từ vựng duy nhất. Bắt buộc mọi tài liệu, code, và giao tiếp phải dùng từ này.
    *   **Format gợi ý / Template áp dụng:**
        | Thuật ngữ | Tiếng Anh (trong Code) | Ý nghĩa / Giải thích |
        |---|---|---|
        | Chờ duyệt | `PENDING` | Trạng thái khi User nộp đơn nhưng Quản lý chưa xử lý. |

---

## 2. RÀNG BUỘC DỮ LIỆU & BUSINESS RULES (DATA FIELDS)

*   **Mô tả:** Chi tiết hóa từng trường dữ liệu (Field) sẽ xuất hiện trên UI hoặc lưu xuống DB kèm theo các quy tắc nghiệp vụ.
*   **Cách viết:**
    *   **Câu hỏi cần trả lời:** Trường dữ liệu này tên là gì? Kiểu dữ liệu gì? Có bắt buộc nhập không? Có giới hạn độ dài/định dạng không? Các quy tắc logic liên đới là gì?
    *   **Lấy thông tin ở đâu:** Từ mockups giao diện, quy định pháp luật (VD: CCCD phải đủ 12 số), hoặc quy tắc kinh doanh của công ty.
    *   **Lấy như thế nào:** Liệt kê tất cả các trường. Mapping trực tiếp các Business Rules phức tạp vào từng trường (VD: Ngày kết thúc phải lớn hơn ngày bắt đầu).
    *   **Format gợi ý / Template áp dụng:**
        | Tên trường (Label) | Code Name | Kiểu dữ liệu | Bắt buộc | Mặc định (Default) | Min/Max Length | Validation & Ràng buộc (Rules) |
        |---|---|---|---|---|---|---|
        | Email | `email` | String | Y | `null` | Max 255 | Đúng format email. Duy nhất trong hệ thống. |
        | Trạng thái | `status` | Enum | Y | `PENDING` | - | Chỉ nhận: PENDING, ACTIVE, INACTIVE. |
        | Ngày sinh | `dob` | Date | N | `null` | - | Không được ở tương lai. Phải >= 18 tuổi. |
