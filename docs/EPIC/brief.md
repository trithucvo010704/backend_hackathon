# HƯỚNG DẪN VIẾT TÀI LIỆU EPIC BRIEF (epic-[id]/brief.md)

Tài liệu này định hình tổng quan về Business Logic, quy trình và phạm vi của Epic trước khi chia nhỏ thành các User Story. Đây là "kim chỉ nam" để toàn bộ đội ngũ (Dev, Tester, Stakeholders) có cùng một bức tranh lớn về mục tiêu và giá trị của tính năng.

---

## 1. TÊN EPIC (Epic Title)

**Mô tả:** Quy tắc đặt tên cho Epic để đảm bảo tính chuyên nghiệp, duy nhất và giúp mọi người hiểu ngay nội dung chính của Epic đó.

**Cách viết:** Sử dụng công thức chuẩn: `[Hành động] + [Đối tượng] + [Mục đích]`.

**Nguồn thông tin:** Danh sách Epic trong Project Management Tool (Jira, ClickUp...) hoặc từ biên bản họp định hướng sản phẩm.

**Cách thu thập:** Trích xuất mã định danh được cấp và xác định 3 thành phần: Hành động chính là gì? Đối tượng tác động là ai/cái gì? Mục đích cuối cùng hướng tới là gì?

**Format gợi ý / Template áp dụng:**

> `[Hành động] + [Đối tượng] + [Mục đích]`
> *Ví dụ: Tích hợp hệ thống thanh toán đa kênh để tăng tỷ lệ chuyển đổi*

---

## 2. MÔ TẢ EPIC (Epic Statement)

**Mô tả:** Một câu tóm tắt ngắn gọn và súc tích theo định dạng User Story lớn giúp mọi tác nhân đọc vào đều hiểu ngay giá trị cốt lõi.

**Cách viết:** Sử dụng cấu trúc Persona - Action - Value. Hướng dẫn cho Junior: Phải viết sao cho bất kỳ ai (Dev, Tester, Khách hàng) đọc vào cũng hiểu ngay bức tranh lớn mà không cần giải thích thêm.

**Nguồn thông tin:** Yêu cầu từ Stakeholders hoặc tài liệu `01-overview.md`.

**Cách thu thập:** Phân tích yêu cầu thô và xác định: Ai là người dùng chính? Họ muốn làm gì? Tại sao họ muốn làm điều đó?

**Format gợi ý / Template áp dụng:**

> Là một **[Chân dung người dùng / Persona]**
>
> Tôi muốn **[Hành động / Tính năng lớn]**
>
> Để tôi có thể **[Giá trị / Mục tiêu kinh doanh đạt được]**

---

## 3. MỤC TIÊU KINH DOANH (Business Goals & Metrics)

**Mô tả:** Xác định lý do kinh doanh đằng sau Epic và cách chúng ta biết được việc triển khai Epic này đã thành công hay chưa.

**Cách viết:** Trình bày 3 khía cạnh: Nỗi đau (Pain-point), Giá trị giải quyết (Value) và Chỉ số đo lường (Metrics).

**Nguồn thông tin:** Product Owner (PO), Business Analyst Lead hoặc Stakeholders.

**Cách thu thập:** Đặt câu hỏi cho PO: Tại sao khách hàng cần Epic này bây giờ? Nếu không có nó thì sao? Làm sao để đo lường bằng con số cụ thể?

**Format gợi ý / Template áp dụng:**

- **Vấn đề đang gặp phải (Pain-point):** [Tại sao khách hàng cần làm EPIC này?]
- **Giá trị mang lại (Value):** [EPIC này giải quyết bài toán gì?]
- **Chỉ số đo lường (Metrics/KPIs):** [Ví dụ: Giảm 20% thời gian chốt đơn, Tăng 15% người dùng đăng ký mới...]

---

## 4. PHẠM VI (Scope & Boundaries)

**Mô tả:** Xác định biên giới rõ ràng của Epic để ngăn chặn tình trạng phình phạm vi (Scope Creep) trong quá trình phát triển.

**Cách viết:** Liệt kê các tính năng nằm trong phạm vi phát triển hiện tại (In-scope) và các phần cố tình loại bỏ hoặc để lại sau (Out-of-scope).

**Nguồn thông tin:** Thỏa thuận phạm vi dự án, nguồn lực nhân sự và thời gian cho phép.

**Cách thu thập:** Đối soát yêu cầu thực tế với Roadmap sản phẩm. BA cần khẳng định rõ những gì KHÔNG làm để quản lý kỳ vọng của Stakeholders.

**Format gợi ý / Template áp dụng:**

- ✅ **In-scope (Sẽ bao gồm):**
  - [Tính năng A]
  - [Tính năng B]
- ❌ **Out-of-scope (Không bao gồm / Để dành cho Phase sau):**
  - [Tính năng C - Ghi rõ lý do không làm ở Phase này]
  - [Tính năng D]

---

## 5. TIÊU CHÍ NGHIỆM THU CẤP CAO (High-Level Acceptance Criteria)

**Mô tả:** Các điều kiện tiên quyết và bắt buộc mà hệ thống phải đáp ứng để khách hàng chấp nhận nghiệm thu Epic này.

**Cách viết:** Viết các câu khẳng định về nghiệp vụ (PHẢI/KHÔNG ĐƯỢC). Hướng dẫn cho Junior: Không viết quá sâu vào UI/UX ở đây. Chỉ nêu các điều kiện để khách hàng "Say YES".

**Nguồn thông tin:** Yêu cầu nghiệp vụ từ khách hàng và các tiêu chuẩn hệ thống hiện có.

**Cách thu thập:** Phỏng vấn stakeholders về các kịch bản thành công quan trọng nhất.

**Format gợi ý / Template áp dụng:**

- Hệ thống **PHẢI** cho phép...
- Hệ thống **PHẢI** đảm bảo (Performance/Security)...
- Hệ thống **KHÔNG ĐƯỢC**...

---

## 6. RÀNG BUỘC & GIẢ ĐỊNH (Assumptions & Constraints)

**Mô tả:** Làm rõ các yếu tố kỹ thuật, thời gian hoặc nghiệp vụ bị giới hạn và những điều BA đang tin là đúng để xây dựng giải pháp.

**Cách viết:** Chia rõ thành hai mục: Giả định (những điểm cần xác nhận lại) và Ràng buộc (giới hạn bắt buộc).

**Nguồn thông tin:** Technical Lead, cơ sở hạ tầng của khách hàng, quy định pháp lý.

**Cách thu thập:** Thảo luận với team kỹ thuật về khả năng thực thi và rà soát hạ tầng hiện hữu của khách hàng.

**Format gợi ý / Template áp dụng:**

- **Giả định (Assumptions):** [Ví dụ: Giả định khách hàng đã có sẵn Server lưu trữ ảnh].
- **Ràng buộc (Constraints):** [Ví dụ: Ứng dụng phải chạy mượt trên các máy Android đời cũ].

---

## 7. SỰ PHỤ THUỘC & RỦI RO (Dependencies & Risks)

**Mô tả:** Nhận diện các yếu tố bên ngoài có thể làm chậm tiến độ và các rủi ro có thể xảy ra trong quá trình triển khai Epic.

**Cách viết:** Liệt kê các liên kết với Epic khác hoặc bên thứ 3 và đề xuất giải pháp cho các rủi ro.

**Nguồn thông tin:** Project Plan, API docs của bên thứ 3, sơ đồ hệ thống.

**Cách thu thập:** Thực hiện rà soát chéo với các team/Epic khác để tìm điểm giao thoa.

**Format gợi ý / Template áp dụng:**

- **Phụ thuộc (Dependencies):**
  - Cần EPIC [XYZ] hoàn thành trước.
  - Cần tài liệu API từ đối tác thứ 3 [Tên đối tác].
- **Rủi ro (Risks):** [Liệt kê rủi ro] -> [Đề xuất phương án giải quyết].

---

## 8. DANH SÁCH USER STORY DỰ KIẾN (Child Stories / Breakdown)

**Mô tả:** Trình bày yêu cầu theo cú pháp chuẩn của Agile để làm rõ Đối tượng, Hành động và Giá trị khi phân rã Epic lớn thành các User Story nhỏ hơn.

**Cách viết:** Trả lời: Ai thực hiện? Làm gì? Tại sao làm?

**Nguồn thông tin:** Lấy từ phần Scope của file `brief.md` và hiểu biết về Persona người dùng.

**Cách thu thập:** Phân tích nhu cầu thực tế. Áp dụng Vertical Slicing để đảm bảo Story có thể release độc lập.

**Format gợi ý / Template áp dụng:**

- `[US-01] - [Tên Story]`
  - **Story:** Là một `[Role/Persona]`, tôi muốn `[Action]`, để `[Value/Reason]`.
- `[US-02] - [Tên Story]`
  - **Story:** Là một `[Role/Persona]`, tôi muốn `[Action]`, để `[Value/Reason]`.
