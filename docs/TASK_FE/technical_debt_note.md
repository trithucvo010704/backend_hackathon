# HƯỚNG DẪN: TECHNICAL DEBT NOTE (GHI CHÚ NỢ KỸ THUẬT - FRONTEND)

## 1. Mô tả
Tài liệu này dùng để ghi lại các khoản "nợ kỹ thuật" phát sinh trong quá trình phát triển Frontend. Ví dụ: viết logic xử lý phức tạp trực tiếp trong component thay vì tách ra custom hook, sử dụng hardcode CSS thay vì dùng theme variable, hoặc chưa tối ưu hóa ảnh và icon.

## 2. Cách viết
1. **Thông tin cơ bản:** Vị trí của đoạn code/component chứa nợ.
2. **Mô tả vấn đề:** Tại sao đây là nợ? (VD: Dùng thư viện ngoài quá nặng, logic xử lý State quá cồng kềnh, thiếu unit test cho component quan trọng).
3. **Lý do chấp nhận nợ:** Giải thích bối cảnh (VD: Cần code nhanh để kịp buổi demo Sprint Review, đang chờ Design System cập nhật token mới).
4. **Hành động khắc phục:** Giải pháp refactor và ước tính nỗ lực.

## 3. Nguồn thông tin
- Source code thực tế.
- Các nhận xét trong `code_review_report.md`.
- Kết quả từ các công cụ như Lighthouse (Performance/Accessibility).

## 4. Cách thu thập
- Rà soát lại các Component sau khi đóng task.
- Tổng hợp từ các comment `// TODO` hoặc `// FIXME`.
- Kiểm tra các cảnh báo (Warnings) trong console hoặc build log.

## 5. Format gợi ý / Template áp dụng

```markdown
# [DEBT] FE DEBT: [Tên Component/Vấn đề]

**Ngày ghi nhận:** YYYY-MM-DD
**Người ghi nhận:** [Tên Developer/Agent]
**Mức độ ưu tiên refactor:** [High/Medium/Low]

## 1. VỊ TRÍ (LOCATION)
- **File:** `src/components/Table/DataTable.tsx`
- **Component:** `DataTable`

## 2. MÔ TẢ VẤN ĐỀ (ISSUE DESCRIPTION)
[Ví dụ: Logic lọc và sắp xếp dữ liệu đang được viết trực tiếp trong component render. Khi dữ liệu lớn (>1000 dòng), giao diện bị lag do re-render liên tục.]

## 3. LÝ DO TẠM CHẤP NHẬN (RATIONALE)
[Ví dụ: Cần tính năng lọc để QC test luồng nghiệp vụ gấp, chưa đủ thời gian để tách logic ra Web Worker hoặc tối ưu hóa bằng useMemo.]

## 4. GIẢI PHÁP & ƯỚC TÍNH (SOLUTION & ESTIMATION)
- **Giải pháp:** Tách logic xử lý data ra custom hook và sử dụng thư viện ảo hóa (Virtual List) để hiển thị.
- **Ước tính nỗ lực:** 0.5 Man-day.
- **Hạn chót đề xuất xử lý:** Sprint tiếp theo.

## 5. TÀI LIỆU LIÊN QUAN
- [Link PR]
- [Link UI Spec]
```
