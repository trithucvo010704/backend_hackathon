# HƯỚNG DẪN TÍCH HỢP NGOẠI VI (EXTERNAL_INTEGRATIONS.md)

Tài liệu này quy định cách thức ứng dụng giao tiếp với các hệ thống bên thứ ba (Third-party APIs).

---

## 1. QUY CHUẨN GỌI API BÊN NGOÀI (EXTERNAL API CALLS)

1. **Mô tả:** Các tiêu chuẩn khi gửi request ra ngoài hệ thống.
2. **Cách viết:** Quy định về Timeout, Retry policy và logging cho external calls.
3. **Nguồn thông tin:** Infrastructure/Common utilities của dự án.
4. **Cách thu thập:** Kiểm tra code phần `RestTemplate`, `Axios` config hoặc `FeignClient`.
5. **Format gợi ý / Template áp dụng:**
   - **Timeout**: Connect 2s, Read 5s.
   - **Retry**: Max 3 lần với Exponential Backoff.
   - **Circuit Breaker**: Phải sử dụng cho các service trọng yếu.

---

## 2. QUẢN LÝ THÔNG TIN BẢO MẬT (SECRET MANAGEMENT)

1. **Mô tả:** Cách thức lưu trữ và truy xuất các API Keys, Secrets.
2. **Cách viết:** Quy định tuyệt đối cấm hardcode key vào mã nguồn.
3. **Nguồn thông tin:** Environment variables hoặc Secret Vault (AWS Secrets Manager, HashiCorp Vault).
4. **Cách thu thập:** Đọc tệp tin `.env.example` hoặc cấu hình Secret manager.
5. **Format gợi ý / Template áp dụng:**
   - Dùng biến môi trường (ENV) cho Local/Dev.
   - Dùng Vault Service cho môi trường Production.
