# Kịch Bản Trình Bày (Pitch Deck Script) - OrderFlow AI

**Thời lượng:** 5 Phút
**Mục tiêu:** Thuyết phục ban giám khảo về tính khả thi, giá trị kinh doanh và chiến lược tiếp cận thị trường của OrderFlow AI thông qua nghệ thuật "nhả mồi".

---

## Slide 1: Đặt Vấn Đề & Nghịch Lý Ngành Vật Tư (Phút 1)
**Visual:** Hình ảnh tin nhắn Zalo lộn xộn của thợ thầu ("cho 20 ống 25 nóng", "tê giảm giống hôm qua") đặt cạnh màn hình ERP nhập liệu cứng nhắc.
**Script:**
"Mỗi ngày, các nhà phân phối ống nước xử lý hàng chục đến hàng trăm đơn hàng qua Zalo. Nhưng có một nghịch lý: khách hàng (thợ thầu) thì nhắn tin tự do, dùng tiếng lóng như 'co 27', 'ren trong 21', 'ống nóng 25', còn nhân viên Sale Admin thì phải ‘dịch’ thủ công thành mã hàng chuẩn xác, tra giá, tra tồn kho, và kiểm tra công nợ. Báo cáo nghiên cứu nội bộ của chúng tôi cho thấy khoảng cách này không phải cảm giác: catalogue chính hãng rất phân nhánh, còn ngôn ngữ mua bán ngoài thị trường lại bị nén thành vài từ ngắn.[R1] Chúng tôi đã tìm thấy một nút thắt cổ chai mà **không một chatbot AI thông thường nào trên thị trường có thể giải quyết được** – và hôm nay chúng tôi sẽ cho các bạn thấy tại sao."
*(Mồi #1: "không một chatbot AI thông thường nào giải quyết được" -> Gây tò mò: Tại sao ChatGPT hay các bot khác không làm được?)*
**Evidence note:** Báo cáo `Evidence That Slang Based Plumbing Supply Orders Create Real Operational Friction.pdf` chỉ ra các cụm như "co 27", "ren trong 21", "van 27" thường thiếu vật liệu, hệ kích thước, PN/độ dày, kiểu ren, geometry thẳng/giảm và các thuộc tính vận hành khác.

---

## Slide 2: Khách Hàng Mục Tiêu & Nỗi Đau (Phút 2)
**Visual:** Chân dung khách hàng (Proto-persona) - Chủ cửa hàng vật tư điện nước quy mô vừa (3-20 nhân sự), có biểu tượng "Pain Points" xoay quanh giá, tồn, nợ.
**Script:**
"Khách hàng đầu tiên của chúng tôi là một cửa hàng vật tư điện nước điển hình. Thú thật, **chúng tôi chưa phỏng vấn trực tiếp họ**. Nhưng chúng tôi có bằng chứng đủ mạnh để xem đây là một giả thuyết khách hàng đáng theo đuổi. Nỗi đau này được đúc kết từ ba lớp bằng chứng: catalogue chính hãng của Tiền Phong, Bình Minh, Dekko cho thấy SKU ngành ống/phụ kiện cực kỳ phân nhánh; các listing marketplace và post công khai dùng ngôn ngữ rút gọn như 'co 27, tê 27, lơi 27, nối ren trong 27'; và các thảo luận vận hành cho thấy ngay cả người trong nghề cũng có lúc vướng ở tên gọi, hệ size và fitting gần giống nhau.[R1][R2][R3]"
*(Mồi #2: "chưa phỏng vấn trực tiếp họ" -> Giám khảo soi: Sao dám chắc là nhu cầu thật nếu chưa gặp khách hàng?)*
**Evidence note:** Nói thẳng đây là "customer hypothesis", nhưng đã có bằng chứng desk research: official catalog complexity + public shorthand ordering + operational friction threads.

---

## Slide 3: Giải Pháp - OrderFlow AI Workbench (Phút 3)
**Visual:** Sơ đồ luồng xử lý: Text thô -> AI Extraction -> Rule Engine (Giá/Tồn/Nợ) -> Draft Order. Demo ngắn giao diện UI (Unified Workbench).
**Script:**
"Giải pháp của chúng tôi là OrderFlow AI Workbench. Trong MVP này, Sale Admin chỉ cần dán nội dung tin nhắn vào hệ thống. Ngay lập tức, AI trích xuất thực thể, tra cứu **catalogue mô phỏng gồm 50 SKU có chủ đích**, kiểm tra giá, tồn kho và công nợ qua Rule Engine. Nhưng – và đây là nguyên tắc cốt lõi của chúng tôi – **AI không bao giờ tự quyết định hay tự duyệt đơn**. Luôn luôn có một con người (Sale Admin) đóng vai trò Reviewer để duyệt cuối cùng."
*(Mồi #3: Dùng "catalogue mô phỏng" & "AI không bao giờ tự quyết định" -> Giám khảo soi: Tại sao không dùng data thật? Có phải năng lực AI của nhóm yếu nên mới bắt con người làm không?)*

---

## Slide 4: Giá Trị Kinh Doanh & Lộ Trình (Phút 4)
**Visual:** Biểu đồ thời gian xử lý đơn hàng (Trạng thái hiện tại: 10-15 phút vs Tương lai: < 1 phút). Lộ trình chạy Pilot Shadow Mode.
**Script:**
"Về giá trị mang lại, chúng tôi không vẽ ra những viễn cảnh phi thực tế. Chúng tôi đặt giả thuyết **giảm 50–70% thời gian xử lý cho các đơn hàng rõ ràng**. Cơ sở của giả thuyết này là chúng tôi đang loại bỏ các bước thủ công lặp lại: đọc tin nhắn, dịch tiếng lóng, tra catalogue, kiểm tồn, kiểm giá và kiểm công nợ. Còn về rủi ro sai đơn, benchmark vận hành kho cho thấy order accuracy là KPI sống còn: chỉ một tỷ lệ lỗi nhỏ cũng tạo ra backorder, trả hàng, giao lại và mất hài lòng khách hàng.[R4] Và MVP Hackathon này chính là công cụ để đo lường giả thuyết đó. Ngay sau Hackathon, chúng tôi có kế hoạch chạy Pilot ở chế độ Shadow Mode (chạy ngầm, không ảnh hưởng vận hành) để thu thập dữ liệu thật và chứng minh ROI. Xin lưu ý, trong giai đoạn này, **hệ thống hoàn toàn không tích hợp Zalo hay ERP**."
*(Mồi #4: "Giảm 50-70%" & "không tích hợp Zalo/ERP" -> Giám khảo soi: Con số này ở đâu ra? Không tích hợp Zalo thì giải quyết bài toán kiểu gì, khách vẫn phải copy-paste à?)*
**Evidence note:** Không nói 50-70% là kết quả đã kiểm chứng. Nói đây là hypothesis cần đo bằng log `Time to Safe Draft`, exception rate, hold rate và tỷ lệ sửa SKU trong pilot.

---

## Slide 5: Tổng Kết & Kêu Gọi (Phút 5)
**Visual:** Tóm tắt 3 điểm nhấn: Tốc Độ (AI Extraction) - An Toàn (Rule Engine) - Kiểm Soát (Audit Trail). Thông điệp chốt sale.
**Script:**
"Tóm lại, chúng tôi không mang đến đây một sản phẩm hoàn chỉnh với hàng tá tính năng dư thừa. Chúng tôi mang đến một bằng chứng khả thi (Proof of Concept) sắc bén, chứng minh rằng nút thắt nhập đơn ngành vật tư có thể được giải quyết triệt để bằng cách kết hợp sự linh hoạt của AI và sự chặt chẽ của Business Rules. Cảm ơn Ban giám khảo, và bây giờ, **chúng tôi đã sẵn sàng cho những câu hỏi khó nhất của các bạn**."
*(Mồi #5: "Sẵn sàng cho những câu hỏi khó nhất" -> Khích tướng nhẹ để kích thích BGK đặt ngay các câu hỏi đã gài mồi ở trên.)*

---

## Tham Chiếu Bằng Chứng

**[R1] Báo cáo nghiên cứu nội bộ:** `local/Evidence That Slang Based Plumbing Supply Orders Create Real Operational Friction.pdf`. Kết luận chính: bằng chứng đủ mạnh để xây hệ thống dịch đơn vật tư dùng tiếng lóng/viết tắt/thiếu thuộc tính thành lựa chọn SKU có thể xác nhận; vấn đề được chứng minh cộng dồn từ catalogue chính hãng, market shorthand và friction vận hành.

**[R2] Catalogue và tài liệu hãng:** Nhựa Tiền Phong công bố danh mục lớn trên các nhóm HDPE/PPR/PVC; catalogue PVC-U phân biệt nhiều loại coupling, adaptor ren trong/ren ngoài, co 45/90, tê, tê giảm, zắc co. Bình Minh tách phụ tùng PVC-U theo hệ inch/mét, socket, ren nhựa, ren đồng, co, tê, van và mã size như 21D/27D. Đây là bằng chứng rằng một cụm ngắn như "co 27" hoặc "nối 27" không đủ để xác định một SKU duy nhất.

**[R3] Bằng chứng thị trường công khai:** Báo cáo ghi nhận các listing/post công khai dùng shorthand như "co 27, tê 27, lơi 27, nối 27, nối ren trong 27, nối ren ngoài 27", và ví dụ e-commerce mà một nhóm "nối răng trong 34" tách thành nhiều biến thể như 34/21, 21/34, 34/27, 27/34. Đây là bằng chứng khách thật tìm và gọi hàng theo ngôn ngữ nén, không theo danh pháp catalogue.

**[R4] Benchmark vận hành:** ASCM xem order accuracy là KPI kho quan trọng và best-in-class warehouse thường nhắm 99.5-99.9% accuracy; nghiên cứu warehouse cũng chỉ ra lỗi sai item/sai quantity tạo ra backorder, trả hàng, rework và bất mãn khách hàng. Báo cáo dùng benchmark này để lập luận: slang nằm ở upstream của order accuracy, nên cải thiện khâu nhập đơn có giá trị vận hành trực tiếp.
