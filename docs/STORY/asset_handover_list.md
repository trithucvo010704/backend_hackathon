# HƯỚNG DẪN: ASSET HANDOVER LIST (DANH SÁCH BÀN GIAO TÀI NGUYÊN)

## 1. Mô tả
Tài liệu này dùng để liệt kê và quản lý các tài nguyên (assets) cần thiết cho việc phát triển một User Story, thường là các file thiết kế, hình ảnh, icon, hoặc tài liệu mẫu từ đội ngũ UI/UX bàn giao cho đội ngũ Development.

## 2. Cách viết
1. **Thông tin bàn giao:** Ghi tên người bàn giao (Designer) và người nhận (Dev Lead/Developer).
2. **Danh sách Assets:** 
    - Liệt kê chi tiết từng file.
    - Ghi rõ định dạng (SVG, PNG, JSON...).
    - Chỉ định vị trí lưu trữ hoặc đường dẫn (URL Figma, Google Drive, hoặc folder trong repo).
    - Ghi chú mục đích sử dụng nếu cần.

## 3. Nguồn thông tin
- File thiết kế (Figma, Adobe XD...).
- Yêu cầu từ User Story và UI/UX Spec.
- Kho tài nguyên dùng chung của dự án.

## 4. Cách thu thập
- Xuất (Export) các asset từ công cụ thiết kế.
- Kiểm tra lại kích thước, dung lượng và định dạng theo tiêu chuẩn dự án.
- Upload lên kho lưu trữ chung và lấy link/đường dẫn.
- Đối soát với danh sách yêu cầu trong UI/UX Spec để đảm bảo không thiếu sót.

## 5. Format gợi ý / Template áp dụng

```markdown
# [HANDOVER] ASSET HANDOVER LIST: [Tên User Story/Tính năng]

**Người bàn giao:** [Tên Designer/Agent]
**Người nhận:** [Tên Developer/Lead]
**Ngày bàn giao:** YYYY-MM-DD

## 1. DANH SÁCH TÀI NGUYÊN (ASSETS LIST)
| File Name | Định dạng | Vị trí / Link | Ghi chú |
| :--- | :--- | :--- | :--- |
| `icon-search.svg` | SVG | `/assets/icons/` | Dùng cho thanh search đầu trang. |
| `banner-home.webp`| WebP | [Link Drive/CDN] | Ảnh nền trang chủ, đã nén. |
| `lottie-loading.json`| JSON | `/assets/animations/`| Hiệu ứng loading khi fetch data. |

## 2. LƯU Ý KỸ THUẬT
- Độ phân giải: [VD: 2x, 3x cho mobile]
- Màu sắc: [VD: Tuân thủ Design System Palette]
- Font chữ: [VD: Sử dụng Inter font đã tích hợp]

## 3. XÁC NHẬN NHẬN BÀN GIAO
- [ ] Đã kiểm tra đầy đủ số lượng.
- [ ] Các định dạng đã đúng yêu cầu kỹ thuật.
```
