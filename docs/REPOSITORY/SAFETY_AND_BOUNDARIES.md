# QUY TẮC AN TOÀN & GIỚI HẠN (SAFETY_AND_BOUNDARIES.md)

Tài liệu này thiết lập các rào cản kỹ thuật và pháp lý để bảo vệ tính ổn định của Repository.

---

## 1. GIỚI HẠN PHẠM VI SỬA ĐỔI (CODE MODIFICATION SCOPE)

1. **Mô tả:** Xác định các vùng code "nhạy cảm" mà AI không được tự ý sửa đổi.
2. **Cách viết:** Liệt kê rõ các đường dẫn thư mục bị cấm (No-touch zones).
3. **Nguồn thông tin:** Tech Lead, Security Officer.
4. **Cách thu thập:** Phân loại các module cốt lõi (Core modules) và module rủi ro cao.
5. **Format gợi ý / Template áp dụng:**
   - **Cấm sửa**: `.github/workflows/`, `scripts/deploy/`, `src/core/security/`.
   - **Được phép**: `src/modules/`, `src/dtos/`, `tests/`.

---

## 2. CHÍNH SÁCH CHẠY LỆNH (COMMAND EXECUTION POLICY)

1. **Mô tả:** Quy định về các lệnh Terminal/CLI mà Agent được phép thực thi.
2. **Cách viết:** Phân loại lệnh An toàn (Safe) và lệnh Nguy hiểm (Unsafe).
3. **Nguồn thông tin:** System administration policy.
4. **Cách thu thập:** Danh sách các lệnh có khả năng gây mất mát dữ liệu hoặc thay đổi cấu hình hệ thống.
5. **Format gợi ý / Template áp dụng:**
   - **Safe**: `ls`, `grep`, `cat`, `npm run test`.
   - **Unsafe**: `rm -rf`, `git push --force`, `drop table`.
