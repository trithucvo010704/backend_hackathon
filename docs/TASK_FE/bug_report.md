# HƯỚNG DẪN: TASK BUG REPORT (BÁO CÁO LỖI CẤP ĐỘ TASK - FRONTEND)

## 1. Mô tả
Tài liệu này dùng để báo cáo các lỗi kỹ thuật phát sinh trong quá trình phát triển Frontend (Web). Tập trung vào các lỗi như crash giao diện, lỗi render component, sai lệch dữ liệu hiển thị từ API, hoặc lỗi logic xử lý State trên trình duyệt.

## 2. Cách viết
1. **Thông tin định danh:** Người báo lỗi, mức độ (Critical/High/Med/Low) và ID Task liên quan.
2. **Các bước tái hiện:** Mô tả chi tiết hành động trên trình duyệt (ví dụ: truy cập URL, click vào button X).
3. **Thực tế vs Mong muốn:**
    - **Thực tế:** Ghi lại lỗi hiển thị, console error, hoặc kết quả rendering sai.
    - **Mong muốn:** Giao diện hiển thị đúng và hoạt động mượt mà theo UI Spec.
4. **Môi trường:** Trình duyệt (Chrome/Safari), Viewport size (Desktop/Mobile), và môi trường (Local/Dev).

## 3. Nguồn thông tin
- Console log của trình duyệt (F12).
- Network tab (để kiểm tra response từ API).
- React/Vue/Angular DevTools.
- Ảnh chụp màn hình hoặc quay màn hình (GIF).

## 4. Cách thu thập
- Copy nội dung lỗi từ Console/Network tab.
- Sử dụng công cụ "Inspect element" để kiểm tra cấu trúc DOM bị lỗi.
- Chụp ảnh vùng giao diện bị lệch thiết kế (Pixel level).

## 5. Format gợi ý / Template áp dụng

```markdown
# [BUG] FE TASK: [Tên lỗi giao diện/logic]

**ID:** BUG-FE-XXXX
**Task liên quan:** [ID Task]
**Mức độ:** [Critical/High/Med/Low]
**Trình duyệt:** Chrome 102 / Safari 15.4

## 1. CÁC BƯỚC TÁI HIỆN (REPRODUCTION STEPS)
1. Chạy lệnh `npm run dev` và truy cập `localhost:3000/login`.
2. Nhập email đúng định dạng nhưng để trống mật khẩu.
3. Nhấn nút "Submit".

## 2. THỰC TẾ VS MONG MUỐN (ACTUAL VS EXPECTED)
- **Thực tế:** Nút "Submit" không phản hồi, và console báo lỗi `Uncaught TypeError: Cannot read properties of undefined (reading 'token')`.
- **Mong muốn:** Hệ thống hiển thị thông báo lỗi "Mật khẩu không được để trống" ngay dưới input.

## 3. THÔNG TIN KỸ THUẬT (TECHNICAL INFO)
- **Console Error:** `[Error] Uncaught TypeError: ... at Login.tsx:45`
- **Environment:** Local (MacOS)
- **Branch/Commit:** `feature/fe-login` - Commit `d4e5f6`

## 4. ĐỀ XUẤT SỬA LỖI (SUGGESTED FIX)
[Kiểm tra lại hàm handleLogin, đảm bảo đã kiểm tra tính hợp lệ của trường password trước khi gọi API]
```
