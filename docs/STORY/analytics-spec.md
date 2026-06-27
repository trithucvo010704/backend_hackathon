# HƯỚNG DẪN VIẾT TÀI LIỆU ANALYTICS SPECIFICATION (analytics-spec.md)

Tài liệu này định nghĩa các sự kiện (Events) và chỉ số cần thu thập để theo dõi hành vi người dùng và hiệu quả của tính năng.

Dưới đây là các thành phần cấu trúc bắt buộc:

---

## 1. DANH SÁCH SỰ KIỆN (Tracking Events)

1. **Mô tả:** Danh sách các hành động của người dùng cần được ghi lại.
2. **Cách viết:** Xác định tên sự kiện (Event Name) và các thuộc tính đi kèm (Properties).
3. **Nguồn thông tin:** Product Manager, Data Analyst.
4. **Cách thu thập:** Phân tích các điểm tương tác quan trọng trên User Flow.
5. **Format gợi ý / Template áp dụng:**
   | Tên sự kiện | Mô tả | Trigger (Khi nào bắn) | Thuộc tính (Properties) |
   |---|---|---|---|
   | `button_click` | Click nút mua hàng | Khi bấm nút [Mua ngay] | `product_id`, `price` |

---

## 2. PHÂN LUỒNG CHUYỂN ĐỔI (Funnel Tracking)

1. **Mô tả:** Theo dõi chuỗi các sự kiện để đo lường tỷ lệ chuyển đổi (Conversion Rate).
2. **Cách viết:** Xác định các bước trong phễu (Funnel). Ví dụ: Xem sản phẩm -> Thêm giỏ hàng -> Thanh toán.
3. **Nguồn thông tin:** Marketing Team, Product Owner.
4. **Cách thu thập:** Sắp xếp các sự kiện theo trình tự thời gian.
5. **Format gợi ý / Template áp dụng:**
   - **Funnel A:** [Event 1] > [Event 2] > [Event 3]

---

## 3. ĐỊNH DANH NGƯỜI DÙNG (User Identification)

1. **Mô tả:** Quy định cách thức gắn định danh (ID) cho người dùng để theo dõi xuyên suốt các phiên làm việc.
2. **Cách viết:** Xác định thời điểm gọi hàm `identify` và các thuộc tính người dùng cần lưu (Email, Plan, Role).
3. **Nguồn thông tin:** Tech Lead, Data Architect.
4. **Cách thu thập:** Theo dõi trạng thái đăng nhập (Auth status).
5. **Format gợi ý / Template áp dụng:**
   - **Identify on Login:** Gửi `user_id` và `user_role`.
