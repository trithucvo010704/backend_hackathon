# HƯỚNG DẪN VIẾT BẢN PHÁC THẢO Ý TƯỞNG GIAO DIỆN (CONCEPT NOTE)

Bản phác thảo Ý tưởng giao diện (Concept Note) là cầu nối quan trọng giữa các yêu cầu nghiệp vụ trừu tượng của Business Analyst (Lina) và thiết kế trực quan thực tế của UI/UX Designer (Robin). Tài liệu này hướng dẫn cách đặc tả một Concept Note chuẩn mực để designer hiểu ngay ngữ cảnh, chủ động phân tách các màn hình/trạng thái và thiết kế các hiệu ứng động một cách mượt mà nhất.

---

## 1. BỐI CẢNH & HỆ THỐNG THIẾT KẾ (CONTEXT & DESIGN SYSTEM)

1. **Mô tả:** Xác định phân hệ (repository) thực thi và hệ thống thiết kế (Design System) tương ứng của dự án. Điều này giúp BA làm rõ tính năng có thực sự cần giao diện hiển thị hay không (tránh vẽ concept cho các repository thuần backend như `api-gw`), đồng thời chuẩn hóa các thuật ngữ, màu sắc và component trước khi phác thảo.
2. **Cách viết:**
   - BA cần tra cứu danh sách repository của dự án (`project-level/05-repositories-registry.md`) để xác định repository đích sẽ hiển thị giao diện này (ví dụ: `web-client`, `mobile-app`).
   - **Quy tắc quan trọng:** Nếu tính năng chỉ xử lý ngầm (như định tuyến API tại `api-gw`, đồng bộ dữ liệu tại `integration-service`), dừng lại và **KHÔNG viết Concept Note**.
   - Tra cứu tài liệu Design System chuẩn áp dụng cho repository đó (`repository-level/09-design-system.md` hoặc `repository-level/stitch.md`).
   - Đính kèm tên Repository và đường link dẫn tới Design System hoặc Figma UI Kit tương ứng vào đầu tài liệu Concept Note.
3. **Nguồn thông tin:**
   - Danh mục repository của dự án (`project-level/05-repositories-registry.md`).
   - Tiêu chuẩn thiết kế hệ thống (`repository-level/09-design-system.md` hoặc `repository-level/stitch.md`).
   - Figma UI Kit hoặc Design Tokens của dự án.
4. **Cách thu thập:**
   - Rà soát yêu cầu kỹ thuật hoặc thảo luận nhanh với Technical Lead/Architect để xác định chính xác repository chịu trách nhiệm hiển thị giao diện.
   - Truy cập vào thư mục `repository-level` để lấy tài liệu đặc tả Design System tương ứng với repo đó.
5. **Format gợi ý / Template áp dụng:**
   ```markdown
   # [CONCEPT] CONCEPT NOTE: [TÊN MÀN HÌNH / TRẠNG THÁI]

   **Người soạn:** [Tên BA]
   **Người nhận:** [Tên Designer]
   **Ngày:** [YYYY-MM-DD]

   ---

   ## 1. BỐI CẢNH & HỆ THỐNG THIẾT KẾ (CONTEXT & DESIGN SYSTEM)

   * **Repository thực hiện:** [Tên repository phát triển giao diện, ví dụ: web-client hoặc mobile-app. Lưu ý: Nếu là repo backend như api-gw, không viết Concept Note này.]
   * **Design System áp dụng:** [Tên và link dẫn tới tài liệu Design System hoặc Figma UI Kit, ví dụ: PAI Sleek Dark Mode System (repository-level/09-design-system.md)]
   ```

---

## 2. Ý TƯỞNG CỐT LÕI (CORE IDEA)

1. **Mô tả:** Định nghĩa tầm nhìn nghệ thuật, phong cách chung và mục tiêu trải nghiệm người dùng (UX) của toàn bộ màn hình hoặc tính năng. Điều này giúp designer định hướng thẩm mỹ tổng thể (ví dụ: tối giản, Sleek Dark Mode, Glassmorphism) ngay từ đầu.
2. **Cách viết:**
   - Nêu rõ mục tiêu chính của màn hình/tính năng: Phục vụ đối tượng nào? Hiển thị trong bao lâu? Có vai trò gì trong hành trình của người dùng?
   - Mô tả phong cách (style) chung: Màu sắc chủ đạo, hiệu ứng nền (Soft Gradient, Acrylic Blur), font chữ định hướng.
   - Giữ ngôn ngữ súc tích, chuyên nghiệp và có tư duy UX.
3. **Nguồn thông tin:** Epic Brief (`epic-level/brief.md`), User Story (`user-story.md`), cuộc họp brainstorming hoặc định hướng sản phẩm từ Stakeholders.
4. **Cách thu thập:** Phân tích nhu cầu từ Epic Brief và trao đổi trực tiếp với Product Owner để hiểu rõ phân khúc khách hàng mục tiêu và cảm giác trải nghiệm mong muốn.
5. **Format gợi ý / Template áp dụng:**
   ```markdown
   ## 2. Ý TƯỞNG CỐT LÕI (CORE IDEA)

   [Mô tả ngắn gọn về vai trò và trải nghiệm mong muốn. Ví dụ: Thiết kế giao diện trạng thái chờ (Loading/Transition State) hiển thị trong thời gian cực ngắn (dưới 1-2 giây) khi ứng dụng di động tiếp nhận phản hồi từ ID-system, thực hiện trích xuất dữ liệu, lưu trữ cục bộ và dọn dẹp URL. Trải nghiệm cần mang lại cảm giác mượt mượt, phản hồi tức thì và chuyên nghiệp, tránh để màn hình bị trắng hoặc đơ làm Broker hoang mang.]

   * **Style chung:** [Phong cách thiết kế chủ đạo. Ví dụ: Đồng bộ phong cách Sleek Dark Mode của ứng dụng PAI. Nền sử dụng hiệu ứng chuyển sắc mờ (Soft Gradient) tạo cảm giác không gian sâu và cao cấp.]
   ```

---

## 3. DANH SÁCH GIAO DIỆN & TRẠNG THÁI CẦN THIẾT KẾ (SCREEN LIST & UI STATES)

1. **Mô tả:** Liệt kê và đặc tả chi tiết toàn bộ các trạng thái giao diện và màn hình cần bàn giao. Việc này giúp designer chủ động "tách màn" thiết kế đầy đủ, tránh bỏ sót các trạng thái quan trọng như Loading hay Error.
2. **Cách viết:**
   - **Tách rõ danh sách màn hình (Screen List):** Nói rõ kỳ vọng designer cần vẽ bao nhiêu giao diện hoặc trạng thái cụ thể.
   - **Đặc tả từng trạng thái UI (UI States):** Không trộn lẫn hành vi hệ thống với giao diện. Hãy nêu rõ:
     - *Mô tả ngữ cảnh:* Khi nào xuất hiện trạng thái này? (Ideal Case hay Edge Case).
     - *Thành phần UI (Elements):* Các phần tử xuất hiện trên layout.
     - *Chốt hạ phần Chữ (Copywriting):* BẮT BUỘC ghi rõ text hiển thị chính xác (không dùng từ "hoặc", "...", hoặc để trống) để designer căn chỉnh khoảng cách chữ (kerning/leading) tối ưu.
     - *Chuyển động (Motion & Animation):* Chỉ rõ cách các thành phần chuyển động (ví dụ: loader xoay liên tục tốc độ thế nào, khi xong thì mờ dần Fade-out hay trượt Slide-in) để designer chuẩn bị prototype hoặc Lottie file.
3. **Nguồn thông tin:** Luồng nghiệp vụ từ User Story (`user-story.md`), Sơ đồ hành trình người dùng (`user-flow.md`), và tài liệu đặc tả API/hệ thống.
4. **Cách thu thập:** Phân tích kỹ các kịch bản thành công và thất bại trong luồng xử lý kỹ thuật. Trao đổi với Developer để xác định các trạng thái lỗi phát sinh từ hệ thống cần phản hồi lên giao diện.
5. **Format gợi ý / Template áp dụng:**
   ```markdown
   ## 3. DANH SÁCH GIAO DIỆN & TRẠNG THÁI CẦN THIẾT KẾ (SCREEN LIST)

   *Robin vui lòng thiết kế/tách thành **[Số lượng] trạng thái giao diện** dựa trên luồng xử lý dưới đây:*

   ### Trạng thái 1: [Tên Trạng thái tiêu chuẩn, ví dụ: Loading State] (Ideal Case)

   * **Mô tả:** [Giao diện xuất hiện trong điều kiện bình thường, ví dụ: Hiển thị khi hệ thống đang xử lý dọn dẹp URL và khởi tạo dữ liệu thành công.]
   * **Thành phần UI (Elements):**
     * *Khu vực trung tâm:* [Ví dụ: Một vòng tròn xoay tải dữ liệu (Spinning Loader) cách điệu tinh tế.]
     * *Text bên dưới Loader:* `"[Text chính xác, ví dụ: Đang kết nối bảo mật...]"` (Màu [Màu sắc theo design system, ví dụ: White/Gray]).
   * **Hiệu ứng chuyển động (Motion/Transition):**
     * [Mô tả chi tiết chuyển động, ví dụ: Loader xoay liên tục không bị giật khựng với tốc độ vừa phải.]
     * [Mô tả transition chuyển tiếp, ví dụ: Khi luồng xử lý hoàn tất -> Giao diện này sẽ mờ dần (Fade-out) và màn hình Dashboard chính trượt nhẹ vào từ bên phải (Slide-in).]

   ### Trạng thái 2: [Tên Trạng thái lỗi/ngoại lệ, ví dụ: Error State] (Edge Case)

   * **Mô tả:** [Giao diện xuất hiện khi gặp sự cố, ví dụ: Hiển thị khi xảy ra lỗi nhận diện mã khóa (URL callback bị thiếu tham số hoặc sai định dạng).]
   * **Thành phần UI (Elements):**
     * *Khu vực trung tâm:* [Ví dụ: Loader dừng xoay và ẩn đi, thay thế bằng biểu tượng Warning màu đỏ cách điệu.]
     * *Text báo lỗi:* `"[Text chính xác, ví dụ: Xác thực không thành công. Đang quay lại trang đăng nhập...]"` (Màu [Ví dụ: Đỏ nhạt/Danger Red]).
   * **Hành vi hệ thống & Motion:**
     * [Quy trình tự động, ví dụ: Tự động chuyển hướng Broker về giao diện Login sau 2 giây.]
     * [Yêu cầu transition, ví dụ: Robin lưu ý thiết kế transition quay lại trang Login một cách mượt mà và tự nhiên.]
   ```

---

## 4. TÀI LIỆU THAM KHẢO & CẢM HỨNG (INSPIRATIONS)

1. **Mô tả:** Cung cấp các nguồn cảm hứng thiết kế thực tế từ những sản phẩm hàng đầu để designer có cơ sở hình dung, chắt lọc các hiệu ứng tương tác cao cấp và phong cách bố cục.
2. **Cách viết:**
   - Liệt kê các ứng dụng cao cấp có trải nghiệm transition/micro-interaction tương tự (ví dụ: Linear, Slack, Stripe).
   - Chỉ rõ điểm thiết kế đắt giá cần học hỏi từ các mẫu đó để designer định vị mức độ kỳ vọng.
   - Cung cấp các đường liên kết trực quan (Dribbble, Behance, Figma prototype tham khảo nếu có).
3. **Nguồn thông tin:** Quá trình nghiên cứu thị trường, trải nghiệm sản phẩm cá nhân, các thư viện UI/UX trực tuyến uy tín.
4. **Cách thu thập:** Tìm kiếm và chọn lọc các kịch bản tương tác tốt nhất trên Dribbble/Behance, quay phim màn hình ứng dụng thực tế hoặc chụp ảnh màn hình lưu giữ làm tư liệu.
5. **Format gợi ý / Template áp dụng:**
   ```markdown
   ## 4. TÀI LIỆU THAM KHẢO & CẢM HỨNG (INSPIRATIONS)

   * [Mô tả điểm hay ho của nguồn tham khảo, ví dụ: Hiệu ứng chuyển màn hình (page transition loaders) cực kỳ tinh tế của các ứng dụng SaaS di động cao cấp như Linear hoặc Slack, nơi quá trình đăng nhập diễn ra chỉ trong chớp mắt nhưng rất tự nhiên và mượt mà.]
   * [Link ảnh/Video/Prototype tham khảo]
   ```
