# HƯỚNG DẪN: TECHNICAL DEBT NOTE (GHI CHÚ NỢ KỸ THUẬT - MOBILE)

## 1. Mô tả
Tài liệu này dùng để ghi lại các khoản "nợ kỹ thuật" phát sinh trong quá trình phát triển Mobile app. Ví dụ: bỏ qua tối ưu hóa hình ảnh để kịp demo, chưa xử lý triệt để chế độ Offline, hoặc sử dụng thư viện bên thứ ba tạm thời.

## 2. Cách viết
1. **Thông tin cơ bản:** Vị trí của đoạn code/module chứa nợ.
2. **Mô tả vấn đề:** Tại sao đây là nợ? (VD: Chưa tối ưu UI cho tablet, hardcode API endpoint, thiếu xử lý lỗi mất mạng).
3. **Lý do chấp nhận nợ:** Giải thích bối cảnh (VD: Cần build app gấp cho đội QC test, chờ backend hoàn thiện API mới).
4. **Hành động khắc phục:** Giải pháp tối ưu và ước tính nỗ lực.

## 3. Nguồn thông tin
- Source code thực tế.
- Comment từ Tech Lead trong Code Review.
- Kết quả test trên thiết bị thật (VD: App bị giật lag nhẹ ở màn hình danh sách).

## 4. Cách thu thập
- Tự rà soát performance của app (Frame rate, Memory usage).
- Tổng hợp từ các TODO trong code Mobile.
- Phân tích từ các báo cáo crash không thường xuyên.

## 5. Format gợi ý / Template áp dụng

```markdown
# [DEBT] MOBILE DEBT: [Tên Module/Vấn đề]

**Ngày ghi nhận:** YYYY-MM-DD
**Người ghi nhận:** [Tên Developer/Agent]
**Mức độ ưu tiên refactor:** [High/Medium/Low]

## 1. VỊ TRÍ (LOCATION)
- **File:** `App/Features/Home/ListContainer.swift`
- **Hàm/Class:** `loadImages()`

## 2. MÔ TẢ VẤN ĐỀ (ISSUE DESCRIPTION)
[Ví dụ: Đang tải ảnh trực tiếp từ URL mà không có cơ chế Caching. Điều này làm app tốn băng thông và load chậm khi mạng yếu.]

## 3. LÝ DO TẠM CHẤP NHẬN (RATIONALE)
[Ví dụ: Chưa kịp tích hợp thư viện SDWebImage/Glide, cần release bản alpha cho team BA kiểm tra luồng nghiệp vụ.]

## 4. GIẢI PHÁP & ƯỚC TÍNH (SOLUTION & ESTIMATION)
- **Giải pháp:** Tích hợp thư viện Image Caching và implement Lazy Loading.
- **Ước tính nỗ lực:** 0.5 Man-day.
- **Hạn chót đề xuất xử lý:** Trước khi đóng Sprint 2.

## 5. TÀI LIỆU LIÊN QUAN
- [Link PR]
- [Link Task Spec]
```
