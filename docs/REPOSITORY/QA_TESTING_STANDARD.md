# QUY CHUẨN KIỂM THỬ TẠI REPOSITORY (QA_TESTING_STANDARD.md)

Tài liệu này quy định cách thức Agent thực hiện kiểm thử tự động và đảm bảo chất lượng ngay tại môi trường phát triển (Local/Repository).

Dưới đây là các thành phần cấu trúc bắt buộc:

---

## 1. QUY TRÌNH CHẠY TEST LOCAL (Local Test Execution)

1. **Mô tả:** Các lệnh và trình tự cần thực hiện để verify code trước khi push.
2. **Cách viết:** Liệt kê chính xác các câu lệnh terminal và môi trường cần thiết.
3. **Nguồn thông tin:** `README.md` của repository.
4. **Cách thu thập:** Thử nghiệm chạy lệnh trực tiếp trên terminal.
5. **Format gợi ý / Template áp dụng:**
   - **Command:** `npm run test:unit`
   - **Requirement:** 100% tests must pass.

---

## 2. QUY CHUẨN VIẾT TEST CASE (Test Case Standards)

1. **Mô tả:** Hướng dẫn cách viết code test mới cho các tính năng vừa phát triển.
2. **Cách viết:** Quy định về cấu trúc file test và cách dùng Mocking.
3. **Nguồn thông tin:** QA Strategy (08-qa).
4. **Cách thu thập:** Tham khảo các framework test đang dùng.
5. **Format gợi ý / Template áp dụng:**
   - **File Pattern:** `[name].test.ts`
   - **Structure:** AAA (Arrange - Act - Assert).

---

## 3. MẪU KIỂM THỬ RIÊNG CHO BACKEND & FRONTEND (Specific Testing Templates)

1. **Mô tả:** Các phương pháp và kịch bản test đặc thù cho từng layer.
2. **Cách viết:** Trình bày các kỹ thuật test phổ biến nhất cho BE và FE.
3. **Nguồn thông tin:** QA Lead, Automation Engineers.
4. **Cách thu thập:** Tổng hợp các best practices về testing (Pyramid, Integration).
5. **Format gợi ý / Template áp dụng:**

### **Mẫu Kiểm thử cho Backend (BE):**
- **Unit Test:** Tập trung vào Business Logic trong Service. Mocking tầng Repository.
- **Integration Test:** Kiểm tra luồng từ Controller -> Service -> Database (H2/Docker DB).
- **Format gợi ý:**
  ```java
  @Test
  void should_calculate_total_price_correctly() {
    // Arrange: Mock product price
    // Act: Call service
    // Assert: Verify total
  }
  ```

### **Mẫu Kiểm thử cho Frontend (FE):**
- **Component Test:** Kiểm tra việc render UI và tương tác (click, input).
- **Snapshot Test:** Đảm bảo UI không bị thay đổi ngoài ý muốn.
- **Format gợi ý:**
  ```javascript
  test('should display error message on invalid email', () => {
    // Arrange: Render Login component
    // Act: Type wrong email and submit
    // Assert: Check if error toast is visible
  }
  ```

---

## 4. KIỂM TRA ĐỘ PHỦ (Coverage Verification)

1. **Mô tả:** Quy định về mức độ bao phủ tối thiểu của code test.
2. **Cách viết:** Tỷ lệ Line Coverage/Branch Coverage tối thiểu.
3. **Nguồn thông tin:** Tech Lead, CI/CD Pipeline.
4. **Cách thu thập:** Chạy lệnh sinh báo cáo coverage.
5. **Format gợi ý / Template áp dụng:**
   - **Minimum Coverage:** 80%.
   - **Report Command:** `npm run test:coverage`
