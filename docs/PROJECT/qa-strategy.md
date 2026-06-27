# HƯỚNG DẪN CHIẾN LƯỢC QUẢN TRỊ CHẤT LƯỢNG (qa-strategy.md)

Tài liệu này quy định cách thức kiểm soát chất lượng phần mềm, từ Unit Test đến End-to-End Test, đảm bảo sản phẩm release không có lỗi nghiêm trọng.

Dưới đây là các thành phần cấu trúc bắt buộc:

---

## 1. CẤU TRÚC KIỂM THỬ (Testing Pyramid)

1. **Mô tả:** Định nghĩa tỷ lệ và các loại kiểm thử sẽ áp dụng trong dự án.
2. **Cách viết:** Xác định các loại test: Unit Test (tại BE/FE), Integration Test (API), và UI Automation/Manual Test.
3. **Nguồn thông tin:** Tech Lead, QA Lead.
4. **Cách thu thập:** Dựa trên mức độ phức tạp và ngân sách dự án để chọn tỷ lệ test phù hợp.
5. **Format gợi ý / Template áp dụng:**
   - **Unit Test:** Chiếm 70% (Cover logic nghiệp vụ).
   - **Integration Test:** Chiếm 20% (Kiểm tra khớp nối API).
   - **E2E/UI Test:** Chiếm 10% (Kiểm tra luồng người dùng chính).

---

## 2. QUY TRÌNH BÁO CÁO LỖI (Bug Lifecycle & Reporting)

1. **Mô tả:** Quy định cách phát hiện, báo cáo và xử lý lỗi (Bugs) trên hệ thống quản lý task.
2. **Cách viết:** Định nghĩa các mức độ nghiêm trọng (Blocker, Critical, Major, Minor) và luồng trạng thái của Bug.
3. **Nguồn thông tin:** Quy trình làm việc của team (06-workflow).
4. **Cách thu thập:** Thống nhất mẫu báo cáo bug để Dev dễ tái hiện lỗi.
5. **Format gợi ý / Template áp dụng:**
   **Mẫu Bug Report:**
   - **Tiêu đề:** [Loại lỗi] [Mô tả ngắn]
   - **Môi trường:** Staging / Production / OS / Browser.
   - **Các bước tái hiện:** 1, 2, 3...
   - **Kết quả mong đợi:** ...
   - **Kết quả thực tế:** ...

---

## 3. CHẤP NHẬN NGHIỆM THU (User Acceptance Testing - UAT)

1. **Mô tả:** Quy trình bàn giao sản phẩm cho khách hàng hoặc Business Owner để xác nhận hoàn thành.
2. **Cách viết:** Trả lời: Ai là người duyệt cuối cùng? Biên bản nghiệm thu dựa trên tài liệu nào?
3. **Nguồn thông tin:** Business Analyst (BA), Client.
4. **Cách thu thập:** Đối chiếu Acceptance Criteria trong từng User Story.
5. **Format gợi ý / Template áp dụng:**
   - **Tiêu chí pass UAT:** 100% Must-have stories pass manual test + No Critical bugs.

---

## 4. KIỂM THỬ TỰ ĐỘNG (Automation Testing Standards)

1. **Mô tả:** Quy chuẩn cho việc viết code test tự động (Playwright, Cypress, Jest).
2. **Cách viết:** Xác định thư viện sử dụng, cấu trúc file test và cách đặt tên test case.
3. **Nguồn thông tin:** Tech Lead, Automation QA.
4. **Cách thu thập:** Tham khảo các best practices về Page Object Model (POM).
5. **Format gợi ý / Template áp dụng:**
   - **Framework:** Playwright (với TypeScript).
   - **Naming:** `[FeatureName].spec.ts`
   - **CI Integration:** Auto-run trên GitHub Actions khi mở PR.
