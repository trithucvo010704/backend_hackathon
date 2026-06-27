# 1. TÊN EPIC (Epic Title)
`Xây dựng + Giao diện Xử lý Đơn hàng (Order Fulfillment Workbench) + để Sale Admin quản lý toàn bộ quy trình từ nhập tin nhắn đến xuất chứng từ`

# 2. MÔ TẢ EPIC (Epic Statement)
Là một **Sale Admin**
Tôi muốn **có một màn hình duy nhất hướng dẫn tôi qua các bước: nhập tin nhắn → xem phân tích → xử lý exception → duyệt → xuất PDF**
Để tôi có thể **hoàn thành một đơn hàng mà không cần chuyển qua lại nhiều tab hay hệ thống, đồng thời ghi nhận lại đầy đủ lịch sử chỉnh sửa.**

# 3. MỤC TIÊU KINH DOANH (Business Goals & Metrics)
- **Vấn đề đang gặp phải (Pain-point):** Quy trình xử lý ngoại lệ tốn quá nhiều thời gian do Admin bị "lạc" giữa nhiều màn hình (tra giá, tra tồn, copy/paste Zalo).
- **Giá trị mang lại (Value):** Hợp nhất toàn bộ quy trình làm việc (Workflow). Admin chỉ việc xử lý đúng chỗ bị báo đỏ, hệ thống tự động lo các khâu còn lại.
- **Chỉ số đo lường (Metrics/KPIs):** 
  - Đơn không lỗi (Auto-approved): Xử lý < 10 giây (chỉ bấm duyệt).
  - Đơn có 1-2 exception: Xử lý < 1 phút.
  - Đơn có nhiều exception hoặc cần Override: Xử lý < 3 phút.
  - **KPI Trải nghiệm:** Tỷ lệ đơn hàng được hoàn thành trong tối đa 2 bước thao tác (không refresh, không mở tab khác) ≥ 90%.

# 4. PHẠM VI (Scope & Boundaries)
- ✅ **In-scope (Sẽ bao gồm):**
  - Layout chia cột: Tin nhắn Zalo (trái), Bảng dữ liệu xử lý (phải).
  - Tính năng **Inline Editing** trực tiếp trên bảng: Chỉnh sửa `SKU`, `Quantity`, và `Unit Price`.
  - Cơ chế **Auto-fix** thông minh cho các dòng có Confidence cao.
  - Form xác nhận **Override** bắt buộc nhập lý do (Ghi Audit Log chi tiết).
  - Xuất file PDF (Báo giá, Phiếu lấy hàng) bằng công nghệ Puppeteer (Node.js) hoặc Weasyprint (Python).
  - Hệ thống phím tắt (Keyboard Shortcuts) và Seed Data hỗ trợ Demo.
- ❌ **Out-of-scope (Không bao gồm / Để dành cho Phase sau):**
  - Tích hợp chat Zalo trả lời khách hàng trực tiếp trên UI.

# 5. TIÊU CHÍ NGHIỆM THU CẤP CAO (High-Level Acceptance Criteria)
- Giao diện **PHẢI** tự động tính lại tổng tiền và cập nhật trạng thái Hold/Warning (Credit, Stock) real-time ngay khi Admin sửa số lượng hoặc đơn giá.
- Hệ thống **PHẢI** phân loại Exception thành 2 loại:
  - `Loại 1 (Auto-fixable)`: Nếu Top-1 Confidence ≥ 0.7, hệ thống tự chọn SKU, đổi màu badge thành `AUTO-FIXED`.
  - `Loại 2 (Manual required)`: Nếu Confidence < 0.5 hoặc tie-score, highlight đỏ/cam bắt Admin tự chọn.
- Hệ thống **KHÔNG ĐƯỢC** cho phép Admin bấm nút `Override` nếu chưa nhập chuỗi lý do (reason) dài tối thiểu 5 ký tự.
- Hệ thống **PHẢI** lưu Audit Log chuẩn xác vào DB với các trường: `user_id, order_id, action_type (override, edit_sku, edit_qty, edit_price), old_value, new_value, reason`.

# 6. RÀNG BUỘC & GIẢ ĐỊNH (Assumptions & Constraints)
- **Ràng buộc Demo (Seed Data):** Bắt buộc phải có Dropdown "Chọn kịch bản demo" trên UI để load ngay 4 tình huống: (1) Đơn hoàn hảo, (2) Lỗi 1 dòng SKU, (3) Lỗi Credit Hold, (4) Lỗi hỗn hợp (SKU + Credit).
- **Ràng buộc UI (Keyboard Accessibility):** Phải hỗ trợ các phím tắt cứng: `↑ / ↓` (Di chuyển dòng), `Enter` (Mở dropdown chọn SKU), `Space` (Tick chọn), `Ctrl/Cmd + Enter` (Duyệt toàn bộ đơn), `Esc` (Hủy thao tác/Đóng dropdown).

# 7. SỰ PHỤ THUỘC & RỦI RO (Dependencies & Risks)
- **Phụ thuộc (Dependencies):** Phụ thuộc toàn bộ data JSON trả về từ Epic 1, 2, 3.
- **Rủi ro (Risks):** Render PDF phía Frontend bằng JS thường bị vỡ Layout. -> **Phương án xử lý:** Sử dụng route API ở Backend (`/api/export-pdf`) dựng sẵn HTML/CSS template và dùng Puppeteer headless chrome chụp xuất ra file PDF chuẩn kích thước A4.

# 8. DANH SÁCH USER STORY DỰ KIẾN (Child Stories / Breakdown)
- `[US-4.1] - Xử lý đơn hàng hoàn chỉnh từ nhập liệu đến xuất chứng từ`
  - **Story:** Là một `Sale Admin`, tôi muốn `nhập tin nhắn, xem toàn bộ kết quả phân tích (gồm exception auto-fix và manual), chỉnh sửa inline (giá, số lượng, SKU), duyệt đơn có lưu log lý do và xuất PDF ngay trên cùng một màn hình`, để `hoàn thành quy trình chốt đơn cực kỳ nhanh chóng mà không bị gián đoạn flow công việc`.
