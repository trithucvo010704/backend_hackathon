# HƯỚNG DẪN VIẾT TÀI LIỆU TIÊU CHUẨN CODE (CODING_STANDARDS.md)

Tài liệu này định nghĩa "Luật chơi chung" (Coding Conventions) của team để đảm bảo tính nhất quán của mã nguồn bất kể do ai (hoặc AI nào) viết ra.

---

## 1. QUY ƯỚC ĐẶT TÊN (NAMING CONVENTIONS)

1. **Mô tả:** Quy định cách đặt tên cho biến, hàm, class, file và thư mục.
2. **Cách viết:** Liệt kê các quy tắc cứng kèm ví dụ minh họa Đúng/Sai.
3. **Nguồn thông tin:** Chuẩn cộng đồng (PEP8, Google Style Guide) và thống nhất nội bộ.
4. **Cách thu thập:** Rà soát Linter config hoặc quy chuẩn đã có của team.
5. **Format gợi ý / Template áp dụng:**
   - **Class/Interface**: Dùng `PascalCase` (VD: `UserController`).
   - **Biến/Hàm**: Dùng `camelCase` (VD: `getUserList()`).
   - **Hằng số**: Dùng `UPPER_SNAKE_CASE` (VD: `MAX_RETRY_COUNT`).
   - **Folder/File**: Dùng `kebab-case` (VD: `auth-service/login.dto.ts`).

---

## 2. XỬ LÝ LỖI & DTO (ERROR HANDLING & DTO)

1. **Mô tả:** Chuẩn hóa cách ném lỗi và định dạng dữ liệu trả về cho Client.
2. **Cách viết:** Cung cấp mẫu JSON Response chuẩn và yêu cầu tái sử dụng các Custom Exception Classes.
3. **Nguồn thông tin:** Base classes và Global Exception Handler hiện có trong code.
4. **Cách thu thập:** Đọc code phần `ResponseWrapper` hoặc `ExceptionHandler`.
5. **Format gợi ý / Template áp dụng:**
   - **Standard Response**: 
     ```json
     { "code": 200, "message": "Success", "data": {...} }
     ```
   - **Error Handling**: Sử dụng `BusinessException` cho các lỗi nghiệp vụ và `SystemException` cho lỗi hệ thống.

---

## 3. VALIDATION (KIỂM TRA DỮ LIỆU)

1. **Mô tả:** Quy tắc kiểm tra tính hợp lệ của dữ liệu đầu vào.
2. **Cách viết:** Nêu rõ nguyên tắc "Fail-fast" (chặn lỗi ngay tại Controller).
3. **Nguồn thông tin:** Framework-specific validation library (Zod, Hibernate Validator, etc.).
4. **Cách thu thập:** Kiểm tra các Annotations hoặc Schemas đang dùng trong Controller.
5. **Format gợi ý / Template áp dụng:**
   - Mọi Request DTO phải có Schema Validation đi kèm.
   - Không được phép xử lý dữ liệu thô (raw data) trong tầng Service.
