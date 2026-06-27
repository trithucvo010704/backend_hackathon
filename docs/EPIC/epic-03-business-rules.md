# 1. TÊN EPIC (Epic Title)
`Xây dựng + Rule Engine Đánh giá rủi ro (Risk Assessment Engine) + để cảnh báo Giá, Tồn kho và Công nợ linh hoạt`

# 2. MÔ TẢ EPIC (Epic Statement)
Là một **Quản lý / Kế toán**
Tôi muốn **hệ thống tự động rà soát Tồn kho khả dụng, tính toán Giá theo chính sách và kiểm tra Dư nợ**
Để tôi có thể **ưu tiên xử lý các đơn hàng có rủi ro cao và đưa ra quyết định duyệt/hủy (Override) linh hoạt dựa trên dữ liệu minh bạch, thay vì "chặn cứng" một cách máy móc.**

# 3. MỤC TIÊU KINH DOANH (Business Goals & Metrics)
- **Vấn đề đang gặp phải (Pain-point):** Nhân viên Sales chốt nhầm giá, hoặc lên đơn cho khách vượt hạn mức. Tuy nhiên, nếu hệ thống chặn cứng tuyệt đối thì doanh nghiệp lại dễ mất các khách VIP hoặc lỡ các dự án khẩn cấp.
- **Giá trị mang lại (Value):** Đóng vai trò như một bộ lọc rủi ro (Risk Filter). Cung cấp đủ thông tin cảnh báo để người có thẩm quyền quyết định, triệt tiêu lỗi do vô ý nhưng vẫn giữ được sự linh hoạt trong đàm phán kinh doanh.
- **Chỉ số đo lường (Metrics/KPIs):** 
  - **Kỹ thuật:** 100% đơn hàng vi phạm được gắn đúng loại Flag (STOCK/CREDIT/PRICE).
  - **Vận hành:** Thời gian trung bình từ lúc Hold đến lúc đơn được duyệt hoặc hủy < 5 phút với 90% đơn.
  - **Business:** Tỷ lệ đơn bị Override (vượt qua Hold) nằm trong mức kiểm soát (< 20%), không gây nghẽn doanh thu. Số lượng đơn không thành công do thiếu tồn giảm > 50%.

# 4. PHẠM VI (Scope & Boundaries)
- ✅ **In-scope (Sẽ bao gồm):**
  - **Rule `STOCK_HOLD`:** Kiểm tra Tồn khả dụng theo mô hình Hard/Soft Reservation.
  - **Rule `CREDIT_HOLD`:** Đánh giá Dư nợ hiện tại + Đơn mới so với Hạn mức tín dụng.
  - **Rule `PRICE_WARNING`:** So sánh giá nhập với giá hệ thống (dựa trên bảng `price_policies`), cho phép dung sai (`price_tolerance_percent`).
  - Giao thức xử lý đồng thời (Concurrency lock) cho Inventory.
- ❌ **Out-of-scope (Không bao gồm / Để dành cho Phase sau):**
  - Quản lý chính sách khuyến mãi phức tạp (Mua 10 tặng 1, tặng voucher).
  - Đồng bộ công nợ real-time với hệ thống Kế toán ERP bên thứ ba.

# 5. TIÊU CHÍ NGHIỆM THU CẤP CAO (High-Level Acceptance Criteria)
- Hệ thống **PHẢI** tính Tồn khả dụng = Tồn vật lý gốc - Số lượng `HARD_RESERVATION` (các đơn đã được duyệt/xác nhận). Các đơn nháp (DRAFT/PENDING) chỉ là `SOFT_RESERVATION` và không chiếm tồn kho thực tế.
- Hệ thống **PHẢI** bỏ qua cảnh báo Giá nếu giá nhập nằm trong khoảng dung sai cho phép (VD: +/- 10% so với giá cơ sở đã chiết khấu).
- Hệ thống **PHẢI** tự động giải phóng tồn kho (release lock) nếu đơn hàng quá hạn `expiry_time` (VD: 2 giờ) mà không được duyệt.
- Hệ thống **KHÔNG ĐƯỢC** để xảy ra tình trạng "bán quá tồn" (Overselling) trong môi trường nhiều người dùng thao tác cùng lúc.

# 6. RÀNG BUỘC & GIẢ ĐỊNH (Assumptions & Constraints)
- **Giả định (Assumptions):** Giả định mỗi khách hàng có 1 hạn mức tín dụng và nhóm giá (customer_group) được giả lập sẵn.
- **Ràng buộc (Constraints):** Engine phải gom toàn bộ logic check vào 1 luồng duy nhất để giảm độ trễ (Latency). Bắt buộc phải có Unit Test mô phỏng tình huống Concurrent.

# 7. SỰ PHỤ THUỘC & RỦI RO (Dependencies & Risks)
- **Phụ thuộc (Dependencies):** Phụ thuộc vào đầu ra của Epic 2 (đã map đúng SKU và UoM).
- **Rủi ro (Risks):** Race condition khi nhiều người cùng chốt đơn dẫn đến âm kho. -> **Phương án xử lý:** Sử dụng `SELECT ... FOR UPDATE` ở mức Database để khóa row khi gọi hàm `reserve_inventory`, hoặc áp dụng Optimistic Locking với cột `version`.

# 8. DANH SÁCH USER STORY DỰ KIẾN (Child Stories / Breakdown)
- `[US-3.1] - Đánh giá rủi ro tổng hợp (Full Order Risk Assessment)`
  - **Story:** Là một `Hệ thống`, tôi muốn `kiểm tra đồng thời Tồn kho, Công nợ và Giá cả của toàn bộ đơn hàng trong một lần gọi API duy nhất`, để `trả về danh sách các flag cảnh báo và điểm số rủi ro tổng hợp (Overall Risk Score) cho Sale Admin`.
  - **Schema JSON Output mẫu:**
    ```json
    {
      "order_id": "ORD-001",
      "risk_flags": ["STOCK_HOLD", "CREDIT_HOLD"],
      "overall_risk_score": 0.85,
      "details": {
        "stock": {
          "sku_123": { "requested": 10, "available_hard": 5, "short": 5 }
        },
        "credit": {
          "current_balance": 5000000,
          "credit_limit": 10000000,
          "order_value": 8000000,
          "new_balance": 13000000,
          "exceed": 3000000
        },
        "price": {
          "sku_123": { "input_price": 15000, "system_price": 12000, "diff_percent": 25, "flag": "PRICE_HIGH" }
        }
      }
    }
    ```
