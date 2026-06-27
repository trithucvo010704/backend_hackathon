# HƯỚNG DẪN: CODE REVIEW REPORT (BÁO CÁO KIỂM DUYỆT CODE - MOBILE)

## 1. Mô tả
Tài liệu này dùng để ghi lại kết quả của quá trình kiểm duyệt mã nguồn cho các Task Mobile (iOS/Android/Cross-platform). Mục tiêu là đảm bảo code tuân thủ các tiêu chuẩn về hiệu năng thiết bị di động, quản lý bộ nhớ, trải nghiệm cảm ứng (Touch UX), và quy chuẩn của từng nền tảng.

## 2. Cách viết
1. **Thông tin định danh:** Ghi rõ Reviewer (người kiểm duyệt), Author (tác giả code) và liên kết đến PR/Task.
2. **Nhận xét chi tiết:** 
    - Chỉ rõ vị trí lỗi (File, Line number).
    - Phân loại vấn đề: Mobile UX, Performance (Battery/CPU), Memory Leak, Platform Convention (HIG/Material), Logic...
    - Giải thích tại sao đây là lỗi và gợi ý cách sửa đổi tối ưu.
3. **Kết luận:** Đưa ra quyết định cuối cùng (Approved, Request Changes, hoặc Comment).

## 3. Nguồn thông tin
- Pull Request / Source code thực tế.
- Đặc tả giao diện (`ui_ux_spec.md`) và Figma.
- Apple Human Interface Guidelines (HIG) hoặc Android Material Design.
- Task Spec và yêu cầu nghiệp vụ của Story.

## 4. Cách thu thập
- Sử dụng tính năng Review trên GitHub/GitLab.
- Chạy thử ứng dụng trên Emulator/Simulator và thiết bị thật (Real device).
- Sử dụng Xcode Instruments hoặc Android Profiler để kiểm tra hiệu năng.
- Kiểm tra xử lý các trạng thái mất mạng (Offline mode), xoay màn hình, và quyền truy cập (Permissions).

## 5. Format gợi ý / Template áp dụng

```markdown
# [REVIEW] MOBILE CODE REVIEW REPORT: [Tên Task/PR]

**Reviewer:** [Tên Tech Lead/Agent]
**Tác giả:** [Tên Developer]
**Ngày review:** YYYY-MM-DD
**Liên kết PR:** [Link]

## 1. TỔNG QUAN CHẤT LƯỢNG (OVERALL QUALITY)
- **Hiệu năng & Pin:** [Tốt / Cần tối ưu]
- **Trải nghiệm cảm ứng (UX):** [Mượt mà / Cần chỉnh sửa]
- **Tuân thủ Platform:** [Đạt / Cần điều chỉnh]

## 2. NHẬN XÉT CHI TIẾT (DETAILED COMMENTS)
| Vị trí (File:Line) | Loại | Nhận xét & Đề xuất |
| :--- | :--- | :--- |
| `App/Views/Login.swift:30` | UX | Vùng chạm của nút Login quá nhỏ, khó bấm. Nên tăng size lên tối thiểu 44pt. |
| `src/utils/storage.js:15` | Memory | Không giải phóng listener khi component unmount, gây rò rỉ bộ nhớ. |
| `android/AndroidManifest.xml` | Security | Thừa quyền truy cập vị trí không cần thiết cho task này. |

## 3. KẾT LUẬN
- [ ] **APPROVED:** Code tốt, chạy mượt trên cả iOS và Android.
- [ ] **COMMENT:** Có một vài lưu ý về hiệu năng nhẹ.
- [ ] **REQUEST CHANGES:** Cần sửa lỗi rò rỉ bộ nhớ hoặc lỗi UX nghiêm trọng.
```
