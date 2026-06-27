# HƯỚNG DẪN: STORY TEST REPORT (BÁO CÁO KIỂM THỬ STORY)

## 1. Mô tả
Tài liệu này dùng để tổng kết kết quả kiểm thử sau khi hoàn thành một User Story. Nó đánh giá toàn diện từ khía cạnh nghiệp vụ, trải nghiệm người dùng đến hiệu năng kỹ thuật, nhằm đưa ra quyết định cuối cùng về việc có cho phép Story này được coi là hoàn thành (Done) hay không.

## 2. Cách viết
1. **Thông tin thực hiện:** Ghi người thực hiện test (QC/Lead) và kết quả tổng quát (Đạt/Không đạt).
2. **Đánh giá nghiệp vụ:** Kiểm tra xem mọi kịch bản trong `test-scenarios.md` đã pass chưa và các AC đã được thỏa mãn đầy đủ chưa.
3. **Đánh giá UI/UX:** Đối soát với thiết kế và tiêu chuẩn trải nghiệm người dùng (tốc độ, tính dễ dùng).
4. **Kết luận Release:** Đánh giá rủi ro và đưa ra quyết định GO/NO GO cho việc release hoặc tích hợp vào Epic.

## 3. Nguồn thông tin
- Kịch bản kiểm thử (`test-scenarios.md`).
- Đặc tả giao diện (`ui_ux_spec.md`).
- Danh sách lỗi (`bug_report.md` của story).
- Kết quả chạy test tự động và test thủ công.

## 4. Cách thu thập
- Thu thập bằng chứng Pass/Fail từ quá trình thực thi test scenarios.
- Đo lường hiệu năng (thời gian tải trang, phản hồi API) bằng công cụ browser devtools.
- So sánh pixel-perfect giữa giao diện thực tế và bản thiết kế Figma.
- Tổng hợp danh sách các lỗi còn tồn đọng (nếu có) và đánh giá mức độ ảnh hưởng của chúng.

## 5. Format gợi ý / Template áp dụng

```markdown
# [REPORT] STORY TEST REPORT: [Tên User Story]

**Người thực hiện:** [Tên QC/Lead/Agent]
**Ngày báo cáo:** YYYY-MM-DD
**Kết quả Story:** [ĐẠT / KHÔNG ĐẠT]

## 1. TÌNH TRẠNG KỊCH BẢN KIỂM THỬ (TEST SCENARIOS STATUS)
| Tổng số | Thành công | Thất bại | Bỏ qua | Tỷ lệ Pass |
| :--- | :--- | :--- | :--- | :--- |
| 10 | 9 | 1 | 0 | 90% |

## 2. ĐÁNH GIÁ CHI TIẾT
- **Tính đúng đắn nghiệp vụ (Business Logic):** [OK/Not OK] - [Mô tả nếu có vấn đề]
- **Trải nghiệm người dùng (UX):** [Mượt mà / Cần cải thiện]
- **Giao diện (UI):** [Khớp thiết kế / Có lệch nhẹ]
- **Hiệu năng (Performance):** [Tải trang 1.5s - Đạt yêu cầu]

## 3. DANH SÁCH LỖI CÒN TỒN ĐỘNG (PENDING BUGS)
- [BUG-ID-001]: Lỗi hiển thị icon trên thiết bị di động (Low priority).
- [BUG-ID-005]: Thiếu hiệu ứng hover cho nút Submit (Minor).

## 4. KẾT LUẬN RELEASE
- [ ] **GO:** Sẵn sàng đóng Story và chuyển sang Epic Review.
- [ ] **NO GO:** Cần sửa các lỗi mức độ Medium trở lên trước khi tiếp tục.
```
