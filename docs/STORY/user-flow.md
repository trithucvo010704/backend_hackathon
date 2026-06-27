# HƯỚNG DẪN VIẾT TÀI LIỆU USER FLOW (user-flow.md)

Tài liệu này dùng để phác họa đường đi của người dùng xuyên suốt tính năng. Nó giúp Dev Frontend hiểu được Navigation logic.

Dưới đây là các thành phần cấu trúc bắt buộc:

---

## 1. SƠ ĐỒ LUỒNG NGƯỜI DÙNG (Flow Chart)

1. **Mô tả:** Bản vẽ sơ đồ biểu diễn trình tự các màn hình và các điểm quyết định (Decision points).
2. **Cách viết:** Xác định điểm bắt đầu, các nút bấm chuyển màn hình và các nhánh rẽ điều kiện (đúng/sai). BẮT BUỘC vẽ các nhánh ngoại lệ.
3. **Nguồn thông tin:** Từ UI/UX Specs và Acceptance Criteria.
4. **Cách thu thập:** Sử dụng mã `mermaid` để vẽ. Mỗi node đại diện cho UI/Màn hình BẮT BUỘC phải được gắn link trỏ tới thiết kế Figma tương ứng.
5. **Format gợi ý / Template áp dụng:**
   ```mermaid
   graph TD
     A([Bắt đầu]) --> B["<a href='URL_FIGMA'>Màn hình A</a>"]
     B --> C{Điều kiện?}
     C -- Sai --> D["Hiển thị lỗi"]
     C -- Đúng --> E["Màn hình B"]
   ```

---

## 2. CHÚ THÍCH & PHÂN QUYỀN (Notes & Roles)

1. **Mô tả:** Diễn giải bằng chữ cho sơ đồ phía trên, đặc biệt nhấn mạnh vào các điều kiện rẽ nhánh và quyền hạn.
2. **Cách viết:** Trả lời: Role nào mới được phép đi qua luồng này? Có bước nào cần sự can thiệp của người thứ hai không?
3. **Nguồn thông tin:** Cơ chế phân quyền (RBAC) của dự án.
4. **Cách thu thập:** Giải thích rõ ràng nếu có sự kiện Async (bất đồng bộ).
5. **Format gợi ý / Template áp dụng:**
   - **Actor:** [User / Admin / System]
   - **Quyền hạn:** [Role cần có để đi qua luồng này]
   - **Ghi chú luồng bất đồng bộ:** [Mô tả nếu có sự kiện đẩy vào Queue]
