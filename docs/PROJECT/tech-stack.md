# HƯỚNG DẪN VIẾT TÀI LIỆU tech-stack.md (TECH STACK & ARCHITECTURE)

Tài liệu `tech-stack.md` đóng vai trò là "Bản đồ công nghệ" của toàn bộ dự án. Nó giúp các thành viên mới (Dev, QA, DevOps) biết chính xác dự án đang sử dụng hệ sinh thái nào.

Dưới đây là 4 thành phần cấu trúc bắt buộc và hướng dẫn chi tiết cách viết.

---

## 1. Sơ Đồ Kiến Trúc Tổng Thể (High-level Architecture)

1. **Mô tả:** Là một bức tranh toàn cảnh bằng hình ảnh (hoặc text-based diagram) thể hiện cách các thành phần trong hệ thống (Frontend, Backend, Database, 3rd Parties) kết nối và nói chuyện với nhau.
2. **Cách viết:** Trả lời: User request đi từ đâu vào hệ thống? Phân tải (Load Balancer) thế nào? Hệ thống được chia làm mấy khối (Monolithic hay Microservices)?
3. **Nguồn thông tin:** System Architect (SA), Tech Lead, DevOps.
4. **Cách thu thập:** Yêu cầu Tech Lead vẽ sơ đồ khối cơ bản. Bắt buộc sử dụng mã \mermaid\ (flowchart).
5. **Format gợi ý / Template áp dụng:**
   \\\mermaid
   graph TD
     User --> FE[Frontend]
     FE --> BE[Backend API]
     BE --> DB[(Database)]
     BE --> 3rd[3rd Party Services]
   \\\

---

## 2. Chi Tiết Stack Công Nghệ (Technology Details)

1. **Mô tả:** Danh sách cụ thể các ngôn ngữ, framework, database, và tools cốt lõi được chốt sử dụng cho dự án.
2. **Cách viết:** Trả lời các câu hỏi về Frontend (React/Vue), Backend (NodeJS/Go), Database (Postgres/MySQL) kèm theo phiên bản cụ thể.
3. **Nguồn thông tin:** Quyết định chọn công nghệ của Tech Lead / CTO.
4. **Cách thu thập:** Lập danh sách và yêu cầu Tech Lead điền chính xác thông tin phiên bản (Version).
5. **Format gợi ý / Template áp dụng:**
   | Layer | Công nghệ & Version | Ứng dụng (Dùng để làm gì) |
   |---|---|---|
   | [Frontend] | [React v18] | [Giao diện người dùng] |
   | [Backend] | [NodeJS v20] | [API Gateway & Business Logic] |
   | [Database] | [PostgreSQL v15] | [Lưu trữ dữ liệu chính] |

---

## 3. Quản Trị Mã Nguồn & CI/CD (Version Control & Deployment)

1. **Mô tả:** Quy định về nơi lưu trữ mã nguồn, cách thức quản lý phiên bản và môi trường triển khai.
2. **Cách viết:** Trả lời: Mã nguồn để ở đâu? Dùng tool gì để CI/CD? Deploy lên hạ tầng nào (AWS/Azure)?
3. **Nguồn thông tin:** DevOps, Tech Lead.
4. **Cách thu thập:** Phỏng vấn team DevOps về luồng triển khai tự động.
5. **Format gợi ý / Template áp dụng:**
   - **VCS:** [GitHub / Bitbucket] (GitFlow)
   - **CI/CD Pipeline:** [GitHub Actions / Jenkins]
   - **Infra:** [AWS / On-premise]

---

## 4. Các Dịch Vụ Tích Hợp (3rd Party Services / External Integrations)

1. **Mô tả:** Liệt kê các dịch vụ bên thứ 3 (SMS, Email, Payment, Storage) để lập trình viên biết đường xin API Key/Secret.
2. **Cách viết:** Xác định các dịch vụ: Gửi SMS/Email? Thanh toán online? Lưu trữ file?
3. **Nguồn thông tin:** Yêu cầu nghiệp vụ từ khách hàng hoặc BA.
4. **Cách thu thập:** Rà soát tài liệu SOW và thiết kế hệ thống.
5. **Format gợi ý / Template áp dụng:**
   | Loại dịch vụ | Nhà cung cấp (Provider) | Mục đích sử dụng |
   |---|---|---|
   | [Payment] | [Stripe / VNPay] | [Thanh toán hóa đơn] |
   | [Storage] | [AWS S3 / Cloudinary] | [Lưu trữ ảnh và video] |

   > [!CAUTION]
   > TUYỆT ĐỐI KHÔNG lưu API Key/Secret ở trong file này.
