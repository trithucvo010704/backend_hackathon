# HƯỚNG DẪN VIẾT TÀI LIỆU repositories-registry.md (DANH SÁCH MÃ NGUỒN & CẤU TRÚC)

Tài liệu này là tấm bản đồ định vị mã nguồn. Nó giúp các lập trình viên biết chính xác: **"Nên clone code ở đâu? Code xong bỏ vào thư mục nào?"**.

Dưới đây là 2 thành phần cấu trúc bắt buộc:

---

## 1. Danh Bạ Repositories (Repositories Registry)

1. **Mô tả:** Liệt kê toàn bộ các kho chứa mã nguồn (Repo) thuộc dự án. Đây là cái nhìn tổng quan nhất.
2. **Cách viết:** Trả lời: Dự án có bao nhiêu cục source code? Nằm ở đâu? Mỗi repo dùng công nghệ gì và sinh ra để làm nhiệm vụ gì?
3. **Nguồn thông tin:** Tech Lead, DevOps, Danh sách Repos trên nền tảng Git, [tech-stack.md](guideline://PROJECT/tech-stack.md).
4. **Cách thu thập:** Lấy danh sách từ GitHub/GitLab của dự án.
5. **Format gợi ý / Template áp dụng:**
   | Tên Repo | URL | Ngôn ngữ/Framework | Mục đích (Purpose) |
   |---|---|---|---|
   | [Repo A] | [URL] | [Tech] | [Nhiệm vụ] |

---

## 2. Tiêu Chí Phân Luồng Task (Task Routing / Usage Guidelines)

1. **Mô tả:** Hướng dẫn rõ ràng về việc **Khi nào thì dùng repo này?**. Tránh tình trạng code nhầm repo hoặc sai trách nhiệm.
2. **Cách viết:** Xác định nếu nhận một Task loại A, thì phải checkout code ở Repo nào? Ranh giới trách nhiệm (Boundary) của repo này tới đâu?
3. **Nguồn thông tin:** Tech Lead, System Architect, [roles-and-boundaries.md](guideline://PROJECT/roles-and-boundaries.md).
4. **Cách thu thập:** Phân loại các module nghiệp vụ và ánh xạ sang Repo tương ứng, đối chiếu với phân chia vai trò trong [roles-and-boundaries.md](guideline://PROJECT/roles-and-boundaries.md) và thông tin kiến trúc trong [overview.md](guideline://PROJECT/overview.md).
5. **Format gợi ý / Template áp dụng:**
   | Loại Task | Repo cần checkout | Ranh giới trách nhiệm (Scope & Boundary) | Ví dụ cụ thể |
   |---|---|---|---|
   | **Frontend Tasks** | `web-app` | Xây dựng giao diện người dùng, logic hiển thị, API integration phía client. | Phát triển màn hình Dashboard, thiết kế UI Form. |
   | **Backend Logic Tasks** | `api-service` | Xử lý logic nghiệp vụ, kết nối cơ sở dữ liệu, API Endpoints, background tasks. | Viết API đăng ký tài khoản, xử lý thanh toán. |
   | **Infrastructure/IaC Tasks** | `infra-as-code` | Cấu hình hạ tầng, máy chủ, CI/CD pipeline, Docker, Kubernetes. | Tạo file cấu hình Terraform, sửa deploy script. |
