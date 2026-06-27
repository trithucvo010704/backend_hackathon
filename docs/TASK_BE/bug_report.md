# HƯỚNG DẪN: TASK BUG REPORT (BÁO CÁO LỖI CẤP ĐỘ TASK)

## 1. Mô tả
Tài liệu này dùng để báo cáo các lỗi kỹ thuật phát sinh trong quá trình phát triển code hoặc unit test của một Task cụ thể. Khác với Story Bug Report, tài liệu này tập trung vào các chi tiết kỹ thuật như lỗi cú pháp, lỗi logic code, crash hệ thống, hoặc sai lệch phản hồi API.

## 2. Cách viết
1. **Thông tin định danh:** Người báo lỗi, mức độ (Critical/High/Med/Low) và ID Task liên quan.
2. **Các bước tái hiện:** Mô tả chi tiết hành động kỹ thuật (ví dụ: chạy command, gọi API với param nào) để gây ra lỗi.
3. **Thực tế vs Mong muốn:**
    - **Thực tế:** Ghi lại mã lỗi (error code), stack trace hoặc nội dung response sai.
    - **Mong muốn:** Kết quả kỹ thuật đúng theo Spec.
4. **Môi trường:** Ghi rõ lỗi xảy ra ở môi trường nào (Local/Dev/Docker).

## 3. Nguồn thông tin
- File log của ứng dụng hoặc server.
- Output từ console/terminal.
- Kết quả chạy Unit Test / Integration Test.
- Tài liệu API Spec của task.

## 4. Cách thu thập
- Chạy lại code trong môi trường debug để xác định dòng code gây lỗi.
- Copy stack trace từ log file hoặc terminal.
- Sử dụng các công cụ như Postman/Insomnia để bắt response API.
- Chụp ảnh màn hình các thông báo lỗi trên UI (nếu có liên quan đến task FE).

## 5. Format gợi ý / Template áp dụng

```markdown
# [BUG] TASK LEVEL: [Tên lỗi kỹ thuật]

**ID:** BUG-TASK-XXXX
**Task liên quan:** [ID Task]
**Mức độ:** [Critical/High/Med/Low]

## 1. CÁC BƯỚC TÁI HIỆN (REPRODUCTION STEPS)
1. Cấu hình môi trường với biến `NODE_ENV=development`.
2. Chạy lệnh `npm run task:process-data`.
3. Truyền tham số đầu vào là `id=null`.

## 2. THỰC TẾ VS MONG MUỐN (ACTUAL VS EXPECTED)
- **Thực tế:** Hệ thống crash với lỗi `TypeError: Cannot read property 'map' of null` tại file `processor.ts:45`.
- **Mong muốn:** Hệ thống phải trả về mã lỗi 400 và thông báo "Invalid ID".

## 3. THÔNG TIN KỸ THUẬT (TECHNICAL INFO)
- **Stack Trace:** [Dán stack trace tại đây]
- **Environment:** Local / Docker
- **Branch/Commit:** `feature/abc` - Commit `8f2d3a`

## 4. ĐỀ XUẤT SỬA LỖI (SUGGESTED FIX)
[Nếu có, ví dụ: Thêm check null cho biến data trước khi gọi hàm map]
```
