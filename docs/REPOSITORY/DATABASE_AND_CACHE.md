# HƯỚNG DẪN VIẾT TÀI LIỆU DATABASE & CACHE (DATABASE_AND_CACHE.md)

Tài liệu này xác định các quy tắc khi làm việc với tầng lưu trữ dữ liệu và bộ nhớ đệm của dự án.

---

## 1. KẾT NỐI & TƯƠNG TÁC DATABASE (DB CONNECTION & ORM)

1. **Mô tả:** Quy định về cách ứng dụng kết nối và truy vấn cơ sở dữ liệu.
2. **Cách viết:** Liệt kê thư viện đang dùng và nêu rõ các quy định cấm (ví dụ: không dùng Raw Query).
3. **Nguồn thông tin:** File cấu hình kết nối DB và ORM của dự án.
4. **Cách thu thập:** Đọc tệp tin `application.yaml`, `prisma.schema` hoặc cấu hình tương đương.
5. **Format gợi ý / Template áp dụng:**
   - **ORM/Công cụ**: [Ví dụ: Spring Data JPA / Prisma]
   - **Quy tắc mapping**: Tên table dạng số nhiều, cột dạng `snake_case`.
   - **Audit columns**: Bắt buộc có `created_at`, `updated_at`, `created_by`.

---

## 2. CHIẾN LƯỢC CACHING (CACHING STRATEGY)

1. **Mô tả:** Hướng dẫn khi nào cần dùng Cache và cách quản lý vòng đời của Cache.
2. **Cách viết:** Định nghĩa rõ Namespace/Prefix cho Redis Key và các chính sách Eviction/TTL.
3. **Nguồn thông tin:** Chính sách tối ưu hiệu năng của dự án.
4. **Cách thu thập:** Kiểm tra cấu hình Redis/Cache manager trong code.
5. **Format gợi ý / Template áp dụng:**
   - **Cache Engine**: [Ví dụ: Redis]
   - **Key Pattern**: `[Project]:[Module]:[ID]` (VD: `ERP:User:123`).
   - **TTL**: Mặc định 3600s cho dữ liệu ít thay đổi.
