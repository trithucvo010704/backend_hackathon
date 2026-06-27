# QUY CHUẨN REVIEW MÃ NGUỒN TẠI REPOSITORY (CODE_REVIEW_STANDARD.md)

Tài liệu này là "luật lệ" dành cho Agentic IDE khi thực hiện tự động kiểm duyệt mã nguồn (Auto-review) hoặc hỗ trợ Reviewer trong quá trình nộp PR.

Dưới đây là các thành phần cấu trúc bắt buộc:

---

## 1. TIÊU CHÍ AUTO-REVIEW (Auto-review Criteria)

1. **Mô tả:** Các quy tắc mà Agent phải tự kiểm tra trước khi báo cáo hoàn thành task.
2. **Cách viết:** Định nghĩa rõ các lỗi không được phép tồn tại.
3. **Nguồn thông tin:** Coding Standards, Clean Code principles.
4. **Cách thu thập:** Tổng hợp các lỗi hay gặp từ Linter.
5. **Format gợi ý / Template áp dụng:**
   - [ ] Check for unused imports and variables.
   - [ ] Ensure all public methods have Javadoc/Docstrings.
   - [ ] Verify that no sensitive data (keys, passwords) is hardcoded.

---

## 2. QUY TRÌNH PHẢN HỒI (Feedback Loop & PR Comments)

1. **Mô tả:** Hướng dẫn cách Agent hoặc Reviewer để lại nhận xét trên mã nguồn.
2. **Cách viết:** Định dạng comment mang tính xây dựng.
3. **Nguồn thông tin:** Quy trình làm việc (06-workflow).
4. **Cách thu thập:** Thống nhất văn phong review của team.
5. **Format gợi ý / Template áp dụng:**
   - **Format:** `[Type] [Description] [Suggestion]`
   - **Example:** `[Performance] This loop can be optimized using a Map. Suggestion: Use a HashMap for O(1) lookup.`

---

## 3. MẪU REVIEW RIÊNG CHO BACKEND & FRONTEND (Specific Review Templates)

1. **Mô tả:** Các checklist đặc thù cho từng layer để đảm bảo chất lượng kỹ thuật sâu.
2. **Cách viết:** Phân tách rõ các mục tiêu: BE tập trung vào Data/Performance, FE tập trung vào UI/State.
3. **Nguồn thông tin:** Tech Lead, Layer Architects.
4. **Cách thu thập:** Rà soát các vấn đề kỹ thuật đặc thù của stack (Node/Java vs React/Vue).
5. **Format gợi ý / Template áp dụng:**

### **Mẫu Review cho Backend (BE):**
- [ ] **Transaction Management:** Kiểm tra xem các hàm ghi DB đã có `@Transactional` chưa?
- [ ] **SQL Optimization:** Check lỗi N+1 query. Các câu truy vấn có dùng Index không?
- [ ] **Security:** Input validation đã được thực hiện ở tầng Controller/Service chưa?
- [ ] **Logging:** Có ghi log cho các trường hợp Error/Exception không?

### **Mẫu Review cho Frontend (FE):**
- [ ] **State Management:** Kiểm tra việc re-render không cần thiết. Có dùng `useMemo` / `useCallback` đúng chỗ không?
- [ ] **Responsive Design:** Giao diện đã hiển thị đúng trên các breakpoint chính chưa?
- [ ] **API Interaction:** Có xử lý trạng thái Loading và Error khi gọi API không?
- [ ] **Accessibility:** Các element quan trọng có đủ nhãn `aria-label` không?

---

## 4. DANH SÁCH NGOẠI LỆ (Review Exceptions)

1. **Mô tả:** Các vùng code hoặc loại file được phép bỏ qua.
2. **Cách viết:** Liệt kê các thư mục/file như: Code auto-gen, Mock data.
3. **Nguồn thông tin:** Tech Lead.
4. **Cách thu thập:** Rà soát các phần code không do con người trực tiếp viết.
5. **Format gợi ý / Template áp dụng:**
   - **Skip:** `**/generated/**`, `**/dist/**`, `**/*.pb.go`
