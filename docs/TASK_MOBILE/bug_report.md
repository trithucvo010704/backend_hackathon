# HƯỚNG DẪN: TASK BUG REPORT (BÁO CÁO LỖI CẤP ĐỘ TASK - MOBILE)

## 1. Mô tả
Tài liệu này dùng để báo cáo các lỗi kỹ thuật phát sinh trong quá trình phát triển code cho Task Mobile. Tập trung vào các chi tiết kỹ thuật như crash ứng dụng, lỗi xử lý UI trên thiết bị, sai lệch dữ liệu từ API hoặc lỗi logic cục bộ trên mobile.

## 2. Cách viết
1. **Thông tin định danh:** Người báo lỗi, mức độ (Critical/High/Med/Low) và ID Task liên quan.
2. **Các bước tái hiện:** Mô tả chi tiết hành động trên ứng dụng (ví dụ: bấm nút A, chuyển sang màn hình B).
3. **Thực tế vs Mong muốn:**
    - **Thực tế:** Mô tả hành vi sai (VD: App văng, UI bị tràn màn hình, data không load).
    - **Mong muốn:** Hành vi đúng theo thiết kế và spec.
4. **Môi trường:** Thiết bị thật (Real device), OS version, hoặc Simulator/Emulator.

## 3. Nguồn thông tin
- Logcat (Android) hoặc Console log (Xcode/iOS).
- Output từ terminal khi chạy app.
- Crashlytics / Firebase logs.
- Ảnh chụp/Video quay màn hình lỗi.

## 4. Cách thu thập
- Kết nối thiết bị với máy tính và đọc log qua IDE (Xcode/Android Studio).
- Sử dụng các công cụ như Flipper hoặc Reactotron để bắt tương tác mạng/state.
- Quay màn hình để mô tả rõ các bước gây lỗi UI/UX.

## 5. Format gợi ý / Template áp dụng

```markdown
# [BUG] MOBILE TASK: [Tên lỗi kỹ thuật/Crash]

**ID:** BUG-MOBILE-XXXX
**Task liên quan:** [ID Task]
**Mức độ:** [Critical/High/Med/Low]
**Thiết bị:** iPhone 13 (iOS 15.0) / Pixel 6 (Android 12)

## 1. CÁC BƯỚC TÁI HIỆN (REPRODUCTION STEPS)
1. Mở ứng dụng và vào màn hình "Profile".
2. Bấm vào nút "Edit Avatar".
3. Chọn ảnh từ thư viện nhưng bấm "Cancel" ngay lập tức.

## 2. THỰC TẾ VS MONG MUỐN (ACTUAL VS EXPECTED)
- **Thực tế:** Ứng dụng bị đơ (frozen) và sau đó văng ra màn hình home (Crash).
- **Mong muốn:** Ứng dụng đóng thư viện ảnh và quay lại màn hình Profile bình thường.

## 3. THÔNG TIN KỸ THUẬT (TECHNICAL INFO)
- **Crash Log:** `Exception: NullPointerException at AvatarPicker.kt:112`
- **Branch/Commit:** `feature/mobile-profile` - Commit `a1b2c3`

## 4. ĐỀ XUẤT SỬA LỖI (SUGGESTED FIX)
[Kiểm tra null-safety khi người dùng hủy chọn ảnh trong AvatarPicker]
```
