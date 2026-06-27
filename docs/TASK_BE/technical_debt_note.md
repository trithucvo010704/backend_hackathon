# HƯỚNG DẪN: TECHNICAL DEBT NOTE (GHI CHÚ NỢ KỸ THUẬT)

## 1. Mô tả
Tài liệu này dùng để ghi lại các khoản "nợ kỹ thuật" (technical debt) phát sinh trong quá trình phát triển nhanh để kịp deadline hoặc do giới hạn hiện tại của hệ thống. Việc ghi chép này giúp team không quên các điểm cần refactor hoặc tối ưu trong tương lai, đảm bảo sức khỏe dài hạn của dự án.

## 2. Cách viết
1. **Thông tin cơ bản:** Vị trí của đoạn code/module chứa nợ kỹ thuật.
2. **Mô tả vấn đề:** Tại sao đoạn code này được coi là "nợ"? (VD: Dùng library cũ, giải thuật chưa tối ưu, thiếu unit test, hardcode...).
3. **Lý do chấp nhận nợ:** Giải thích bối cảnh tại thời điểm viết code (VD: Cần demo gấp cho khách hàng, chờ module khác hoàn thiện).
4. **Hành động khắc phục:** Mô tả cách sửa đổi mong muốn và ước tính nỗ lực cần thiết.

## 3. Nguồn thông tin
- Source code thực tế.
- Các nhận xét trong Code Review (`code_review_report.md`).
- Kết quả kiểm tra hiệu năng hoặc bảo mật.

## 4. Cách thu thập
- Tự rà soát lại code sau khi hoàn thành task.
- Tổng hợp từ các comment "TODO" hoặc "FIXME" trong code.
- Phân tích từ các cảnh báo của công cụ Static Analysis (SonarQube, v.v.).
- Thảo luận trong các buổi Retro hoặc họp kỹ thuật.

## 5. Format gợi ý / Template áp dụng

```markdown
# [DEBT] TECHNICAL DEBT NOTE: [Tên Module/Vấn đề]

**Ngày ghi nhận:** YYYY-MM-DD
**Người ghi nhận:** [Tên Developer/Agent]
**Mức độ ưu tiên refactor:** [High/Medium/Low]

## 1. VỊ TRÍ (LOCATION)
- **File:** `src/services/payment-processor.ts`
- **Hàm/Class:** `processTransaction()`

## 2. MÔ TẢ VẤN ĐỀ (ISSUE DESCRIPTION)
[Ví dụ: Đang dùng lồng nhau nhiều `if-else` để check điều kiện thanh toán. Code hiện tại khó đọc, khó mở rộng thêm các cổng thanh toán mới.]

## 3. LÝ DO TẠM CHẤP NHẬN (RATIONALE)
[Ví dụ: Cần release tính năng thanh toán bằng ví điện tử trong 2 ngày tới, chưa đủ thời gian để áp dụng Strategy Pattern.]

## 4. GIẢI PHÁP & ƯỚC TÍNH (SOLUTION & ESTIMATION)
- **Giải pháp:** Áp dụng Strategy Pattern cho từng loại thanh toán.
- **Ước tính nỗ lực:** 1 ngày công (1 Man-day).
- **Hạn chót đề xuất xử lý:** Trước Sprint tiếp theo.

## 5. TÀI LIỆU LIÊN QUAN
- [Link PR chứa nợ]
- [Link Task Spec]
```
