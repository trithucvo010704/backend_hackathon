# HƯỚNG DẪN BẢO MẬT & API GATEWAY (SECURITY_AND_GATEWAY.md)

Tài liệu này quy định các tiêu chuẩn về an toàn thông tin và cách thức giao tiếp thông qua Gateway.

---

## 1. XÁC THỰC & PHÂN QUYỀN (AUTHN & AUTHZ)

1. **Mô tả:** Quy trình đăng nhập và kiểm tra quyền hạn của người dùng.
2. **Cách viết:** Giải thích cơ chế Token (JWT), phân quyền (RBAC/ABAC) và phạm vi kiểm tra.
3. **Nguồn thông tin:** Security Config của dự án (Spring Security, Passport, etc.).
4. **Cách thu thập:** Đọc code phần `SecurityConfig` hoặc `AuthMiddleware`.
5. **Format gợi ý / Template áp dụng:**
   - **Cơ chế**: JWT (Stateless).
   - **RBAC**: Phân quyền dựa trên Roles lưu trong Token.
   - **Encryption**: Mật khẩu phải được Hash bằng BCrypt.

---

## 2. CẤU HÌNH API GATEWAY (GATEWAY CONFIG)

1. **Mô tả:** Quy tắc điều hướng và lọc request tại cổng vào hệ thống.
2. **Cách viết:** Liệt kê các quy tắc về Rate Limiting, CORS, và Routing.
3. **Nguồn thông tin:** Cấu hình Gateway (NGINX, Kong, Spring Cloud Gateway).
4. **Cách thu thập:** Đọc tệp tin cấu hình Gateway hoặc Infra config.
5. **Format gợi ý / Template áp dụng:**
   - **Rate Limit**: 100 req/min per IP.
   - **CORS**: Chỉ cho phép các domain trong whitelist.
   - **Headers**: Bắt buộc có `X-Request-ID` cho mục đích tracing.
