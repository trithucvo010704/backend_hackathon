# HƯỚNG DẪN VIẾT TÀI LIỆU BACKEND LOGIC FLOW & DATA FLOW (logic_flow.md)

Tài liệu này dùng để phác họa luồng xử lý bên trong một component, một hàm, hoặc một luồng dịch chuyển dữ liệu (Data Flow) cụ thể của Task. Nó là kim chỉ nam cho Dev Code logic.

---

## 1. SƠ ĐỒ LUỒNG XỬ LÝ (EVENT & DATA FLOW)

*   **Mô tả:** Bản vẽ trình tự các sự kiện (Events) tác động lên hệ thống và cách dữ liệu chảy qua các tầng (Layer) để trả về kết quả.
*   **Cách viết:**
    *   **Câu hỏi cần trả lời:** Khi có một Trigger (Sự kiện click, nhận Request), dữ liệu sẽ đi qua hàm nào trước? Điều kiện kiểm tra (Validate) là gì? Dữ liệu được biến đổi (Map) ra sao?
    *   **Lấy thông tin ở đâu:** Kiến trúc xử lý luồng của dự án (Ví dụ: Request -> Controller -> Service -> Repository).
    *   **Lấy như thế nào:** Bắt buộc sử dụng mã `mermaid` để vẽ. Sơ đồ phải tập trung vào "Control Flow" (Luồng điều khiển) và "State Management" (Sự thay đổi của State).
    *   **Format gợi ý / Template áp dụng:**
        **Tùy chọn 1: Flowchart (Cho logic rẽ nhánh thông thường)**
        ```mermaid
        graph TD
            A([Khởi phát sự kiện]) --> B{Kiểm tra tính hợp lệ}
            B -- Lỗi --> C[Xử lý lỗi / Trả về Error]
            B -- Hợp lệ --> D[Gọi Dependency / Gọi API]
            D --> E[Chuyển đổi dữ liệu / Map Model]
            E --> F[Cập nhật State / Lưu DB]
            F --> G([Render UI / Response])
        ```
        
        **Tùy chọn 2: Sequence Diagram (BẮT BUỘC cho các logic gọi API song song hoặc kết nối 3rd-party)**
        ```mermaid
        sequenceDiagram
            participant UI as Client UI
            participant BE as Backend API
            participant VNP as VNPay (3rd-party)
            UI->>BE: 1. Request tạo đơn hàng
            BE->>BE: 2. Validate dữ liệu
            par Lưu DB & Gọi đối tác
                BE->>BE: 3a. Insert DB (Status: Pending)
                BE->>VNP: 3b. Request lấy URL thanh toán
            end
            VNP-->>BE: 4. Trả về Payment URL
            BE-->>UI: 5. Response kèm URL để Client redirect
        ```

---

## 2. QUY TẮC NGHIỆP VỤ & XỬ LÝ LỖI (BUSINESS RULES & ERROR HANDLING)

*   **Mô tả:** Diễn giải bằng chữ cho sơ đồ phía trên, tập trung liệt kê các quy tắc cứng (Business constraints).
*   **Cách viết:**
    *   **Câu hỏi cần trả lời:** Có phép toán nào đặc biệt không? Có cần format lại Data trước khi lưu không? Nếu API lỗi (500) thì hàm này sẽ quăng ra Exception gì?
    *   **Lấy thông tin ở đâu:** Từ tài liệu Story `data-dictionary.md`.
    *   **Lấy như thế nào:** Liệt kê rõ ràng dưới dạng các gạch đầu dòng. Đặc biệt quan tâm đến luồng bắt lỗi tập trung (Global Exception Handling).
    *   **Format gợi ý / Template áp dụng:**
        - **Quy tắc 1:** [Điều kiện ràng buộc] (Ví dụ: Phải convert Timezone sang UTC trước khi gọi API).
        - **Error Handling:** Bắt lỗi và hiển thị thân thiện, không được throw raw error object ra UI.
