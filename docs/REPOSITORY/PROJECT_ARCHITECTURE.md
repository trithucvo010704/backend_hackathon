# HƯỚNG DẪN VIẾT TÀI LIỆU KIẾN TRÚC DỰ ÁN (PROJECT_ARCHITECTURE.md)

Tài liệu này đóng vai trò như bản đồ quy hoạch tổng thể của dự án (Repository). Bất kỳ AI Agent hay Lập trình viên mới nào khi join vào dự án đều phải đọc file này đầu tiên để biết code nên đặt ở đâu và các thành phần giao tiếp với nhau như thế nào.

---

## 1. CÔNG NGHỆ CỐT LÕI (CORE TECHNOLOGIES)

1. **Mô tả:** Liệt kê các công cụ, ngôn ngữ và framework nền tảng cấu thành nên dự án này.
2. **Cách viết:** Liệt kê ngắn gọn dưới dạng gạch đầu dòng các thành phần chính yếu.
3. **Nguồn thông tin:** File cấu hình package (`package.json`, `pom.xml`, `go.mod`), hoặc hồ sơ thiết kế hệ thống.
4. **Cách thu thập:** Đọc trực tiếp các tệp tin cấu hình dependency của dự án hoặc truy vấn Tech Lead.
5. **Format gợi ý / Template áp dụng:**
   - **Ngôn ngữ**: [Ví dụ: Java 21 / TypeScript 5.0]
   - **Framework**: [Ví dụ: Spring Boot 3.2 / Next.js 14]
   - **Database**: [Ví dụ: PostgreSQL 15, MongoDB]
   - **Infrastructure**: [Ví dụ: Docker, Kubernetes, AWS]

---

## 2. PHÂN TẦNG KIẾN TRÚC (LAYERED ARCHITECTURE)

1. **Mô tả:** Quy định về mô hình kiến trúc của mã nguồn (Ví dụ: MVC, Clean Architecture, 3-Tier) và vai trò của từng tầng.
2. **Cách viết:** Giải thích rõ ràng nguyên tắc đóng gói và gọi hàm giữa các layer. Nhấn mạnh các nguyên tắc cấm (Dependency rules).
3. **Nguồn thông tin:** Quyết định thiết kế của System Architect hoặc hồ sơ kiến trúc cấp cao.
4. **Cách thu thập:** Phỏng vấn Architect hoặc phân tích cấu trúc module hiện tại trong code.
5. **Format gợi ý / Template áp dụng:**
   - **Kiến trúc áp dụng**: [Ví dụ: 3-Tier Architecture]
   - **1. Controller Layer**: Chỉ làm nhiệm vụ nhận Request và trả Response. Cấm viết logic nghiệp vụ ở đây.
   - **2. Service Layer**: Chứa toàn bộ Business Logic và điều phối dữ liệu.
   - **3. Repository/Data Layer**: Tương tác trực tiếp với Database.

---

## 3. CẤU TRÚC THƯ MỤC CHÍNH (DIRECTORY STRUCTURE)

1. **Mô tả:** Giải thích ý nghĩa của các thư mục gốc trong Repository để Agent biết vị trí đọc/ghi file.
2. **Cách viết:** Đi từ gốc (Root) xuống các nhánh chính (cấp 1 và cấp 2) mang ý nghĩa quan trọng.
3. **Nguồn thông tin:** Cấu trúc thư mục thực tế của dự án.
4. **Cách thu thập:** Sử dụng lệnh `ls -R` hoặc `tree` để quan sát cấu trúc vật lý.
5. **Format gợi ý / Template áp dụng:**
   - `src/configs/`: Chứa cấu hình hệ thống (Database, Security).
   - `src/modules/`: Chứa các module tính năng nghiệp vụ.
   - `src/core/`: Chứa các Exception và Utility classes dùng chung.
   - `docs/`: Chứa tài liệu hướng dẫn và sơ đồ.
