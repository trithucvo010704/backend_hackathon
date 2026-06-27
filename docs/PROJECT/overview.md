# HƯỚNG DẪN VIẾT TÀI LIỆU overview.md (PROJECT CHARTER)

Tài liệu `overview.md` đóng vai trò là "Bản tuyên ngôn dự án" (Project Charter). Mục đích của file này là để bất kỳ ai (Dev, QA, BA, Khách hàng hoặc thành viên mới) chỉ cần đọc trong 5 phút là hiểu được **"Dự án này sinh ra để làm gì, giải quyết nỗi đau nào, và hình hài tổng quan của nó ra sao?"**.

Dưới đây là 9 thành phần cấu trúc bắt buộc và hướng dẫn chi tiết cách để viết từng thành phần.

---

## 1. Giới Thiệu Tổng Quan (Executive Summary)

1. **Mô tả:** Là phần tóm tắt dự án trong 2-3 câu ngắn gọn. Định nghĩa rõ sản phẩm là gì và "tôn chỉ" cốt lõi của nó.
2. **Cách viết:** Trả lời các câu hỏi: Sản phẩm này là gì (Web, App, hay Tool nội bộ)? Nó giải quyết bài toán "sống còn" nào? Tính năng/Đặc điểm "ăn tiền" nhất (Unique Selling Proposition) là gì?
3. **Nguồn thông tin:** Lấy từ bản Brief ban đầu của Khách hàng (Client), Chủ đầu tư (Sponsor), hoặc từ tầm nhìn của Giám đốc sản phẩm (BOD/C-level).
4. **Cách thu thập:** Phỏng vấn nhanh Sponsor: *"Nếu chỉ dùng 1 câu để bán sản phẩm này cho người khác, anh/chị sẽ nói gì?"*
5. **Format gợi ý / Template áp dụng:** 
   > "Dự án [Tên] là một hệ thống [Loại hình] giúp giải quyết vấn đề [Nỗi đau]. Mục tiêu cốt lõi là [Giá trị] với các tính năng đột phá như [Tính năng]."

---

## 2. Vấn Đề & Thách Thức (Pain Points)

1. **Mô tả:** Bảng liệt kê "những nỗi đau" hoặc hiện trạng tồi tệ khiến chúng ta phải bỏ tiền và nguồn lực ra làm dự án này.
2. **Cách viết:** Làm rõ hiện tại người dùng đang làm việc đó thủ công/khó khăn như thế nào? Bất cập lớn nhất là gì (Mất thời gian, mất tiền, hay dễ sai sót)? Hậu quả nếu KHÔNG có hệ thống này?
3. **Nguồn thông tin:** Người dùng cuối (End-users), Đội ngũ vận hành hiện tại (Operations Team).
4. **Cách thu thập:** Tổ chức phỏng vấn trực tiếp (User Interviews). Hoặc phân tích dữ liệu hệ thống cũ (Ví dụ: xuất báo cáo Jira để thấy tỷ lệ rework đang là 30%).
5. **Format gợi ý / Template áp dụng:**
   | Mã | Mô tả vấn đề | Hậu quả / Ảnh hưởng |
   |---|---|---|
   | P1 | [Vấn đề] | [Hậu quả] |

---

## 3. Mục Tiêu & Tiêu Chí Đo Lường (Objectives & KPIs)

1. **Mô tả:** Định nghĩa thế nào là "Thành công". Biến các Pain Points ở trên thành các chỉ số (KPI) cụ thể có thể đo lường được (Nguyên tắc SMART).
2. **Cách viết:** Trả lời: Làm sao để biết dự án thành công? Con số kỳ vọng là bao nhiêu? **Quan trọng nhất:** Dùng công cụ/hệ thống nào để tracking và đo lường con số đó?
3. **Nguồn thông tin:** Đảo ngược các vấn đề từ Pain Points. Tham vấn với Tech Lead hoặc Data Analyst về khả năng thu thập dữ liệu (Telemetry).
4. **Cách thu thập:** Thảo luận với team để chốt con số khả thi.
5. **Format gợi ý / Template áp dụng:**
   | Mục tiêu chiến lược | Con số kỳ vọng | Phương pháp / Công cụ đo lường |
   |---|---|---|
   | [Mục tiêu] | [Con số] | [Công cụ] |

---

## 4. Đối Tượng Thụ Hưởng (Stakeholders / Value Proposition)

1. **Mô tả:** Liệt kê các nhóm người (Persona) hoặc hệ thống bên thứ 3 sẽ sử dụng trực tiếp hoặc được hưởng lợi từ sản phẩm. 
2. **Cách viết:** Xác định có bao nhiêu nhóm User? (Đừng quên Admin, Quản lý, Kế toán...). Mỗi nhóm sẽ dùng hệ thống để làm gì và họ "được cái lợi gì" (Value) từ hệ thống?
3. **Nguồn thông tin:** Sơ đồ tổ chức của khách hàng, hoặc quá trình Mapping User Journey.
4. **Cách thu thập:** Đặt mình vào vị trí của từng người dùng và viết theo chuẩn User Story cơ bản.
5. **Format gợi ý / Template áp dụng:**
   | Đối tượng | Giá trị nhận được / Mục đích sử dụng |
   |---|---|
   | [Persona] | [Value Proposition] |

---

## 5. Phạm Vi Tổng Quan (High-level Scope)

1. **Mô tả:** Khẳng định ranh giới dự án. Nêu rõ những gì SẼ LÀM (In Scope) và cực kỳ quan trọng là **NHỮNG GÌ KHÔNG LÀM (Out of Scope)** để chống tình trạng phình to dự án (Scope Creep).
2. **Cách viết:** Xác định điểm dừng của dự án ở đâu? Tính năng/Luồng nghiệp vụ nào thường dễ bị lầm tưởng là có (nhưng thực tế là ta không làm/hoặc đẩy sang phase sau)? Có tích hợp với hệ thống bên thứ 3 nào không?
3. **Nguồn thông tin:** Hợp đồng dự án (Contract/SOW), Ngân sách, Thỏa thuận chốt với khách hàng.
4. **Cách thu thập:** Bám sát biên bản họp chốt yêu cầu. (Phần này có thể đặt hyperlink trỏ sang file `02-scope.md` chi tiết).
5. **Format gợi ý / Template áp dụng:**
   - **In-Scope:** [Tính năng A, B, C]
   - **Out-of-Scope:** [Tính năng X, Y, Z]

---

## 6. Giải Pháp Công Nghệ & Kiến Trúc (Tech Stack)

1. **Mô tả:** Bức tranh tổng quan về kỹ thuật. Không cần quá sâu như System Design, nhưng phải đủ để biết dự án chạy trên nền tảng gì.
2. **Cách viết:** Trả lời: Hệ thống bao gồm các Layer nào (FE, BE, Database, Queue, External Services)? Sử dụng ngôn ngữ/framework gì? Giao tiếp với nhau ra sao?
3. **Nguồn thông tin:** Tech Lead, System/Solutions Architect.
4. **Cách thu thập:** Yêu cầu Tech Lead vẽ một sơ đồ khối (Block Diagram) cơ bản. Tốt nhất là dùng mã `mermaid` để vẽ trực tiếp vào Markdown.
5. **Format gợi ý / Template áp dụng:**
   | Layer | Công nghệ | Lý do chọn / Ứng dụng |
   |---|---|---|
   | [FE/BE/DB] | [Tech Name] | [Reason] |

---

## 7. Lộ Trình Phát Triển (Roadmap / Milestones)

1. **Mô tả:** Chia dự án thành các Giai đoạn (Phases) lớn để bàn giao dần. Giúp team thấy được "đường dài" và Stakeholders có thể nghiệm thu từng phần (Agile).
2. **Cách viết:** Xác định dự án chia làm mấy Phase? Mục tiêu và tính năng cốt lõi của từng Phase là gì? Khi nào thì ra được MVP?
3. **Nguồn thông tin:** Từ quá trình User Story Mapping, Master Schedule của Project Manager.
4. **Cách thu thập:** Chia cắt các module theo thứ tự ưu tiên (Priority). Cái gì tạo ra giá trị lõi (Core Value) thì xếp vào Phase 1.
5. **Format gợi ý / Template áp dụng:**
   - **Phase 1 (MVP):** [Mô tả]
   - **Phase 2:** [Mô tả]

---

## 8. Rủi Ro & Biện Pháp Giảm Thiểu (Risks & Mitigations)

1. **Mô tả:** Nhận diện trước các "bãi mìn" có thể làm hỏng dự án và đưa ra khiên bảo vệ. Đây là phần cực kỳ quan trọng đối với các dự án phức tạp hoặc có tính tích hợp cao.
2. **Cách viết:** Trả lời: Điều tồi tệ nhất có thể xảy ra là gì? Nếu nó xảy ra thì ta làm gì để chặn nó (phòng ngừa) hoặc cứu vãn tình hình (xử lý)?
3. **Nguồn thông tin:** Tổ chức buổi họp Brainstorming với toàn team (BA, Dev, QA, DevOps).
4. **Cách thu thập:** Khuyến khích mọi người nghĩ về các tình huống xấu nhất (What if). Phân loại rủi ro theo Mức độ nghiêm trọng.
5. **Format gợi ý / Template áp dụng:**
   | Rủi ro | Hậu quả | Biện pháp phòng ngừa | Biện pháp xử lý |
   |---|---|---|---|
   | [Risk] | [Impact] | [Mitigation] | [Contingency] |

---

## 9. Thuật Ngữ & Quy Định Chung (Glossary & Global Rules)

1. **Mô tả:** Định nghĩa các thuật ngữ nghiệp vụ cốt lõi (Glossary) và các ràng buộc nghiệp vụ mang tính toàn cục (Global Rules) của dự án. Việc này giúp toàn bộ các thành viên dự án (BA, Dev, QA, Client) hiểu và sử dụng chung một ngôn ngữ nghiệp vụ thống nhất, đồng thời nắm rõ những "lằn ranh đỏ" về mặt nghiệp vụ không được phép vi phạm khi thiết kế và vận hành hệ thống.
2. **Cách viết:**
   - **Thuật ngữ (Glossary):** Xác định và liệt kê các khái niệm nghiệp vụ đặc thù, các trạng thái nghiệp vụ phức tạp. Mỗi thuật ngữ cần giải thích rõ ràng và ánh xạ sang tên tiếng Anh (Code Name) dùng trong code.
   - **Ràng buộc nghiệp vụ cốt lõi (Global Business Constraints):** Nêu rõ các quy tắc nghiệp vụ mang tính bắt buộc, phân quyền toàn cục hoặc giới hạn hệ thống (ví dụ: ngân sách API, phân quyền chuyển đổi trạng thái tự động).
3. **Nguồn thông tin:** Chuyên gia nghiệp vụ (Domain Experts), Chủ sở hữu sản phẩm (Product Owners), hoặc tài liệu cam kết hạ tầng kỹ thuật/ngân sách.
4. **Cách thu thập:** Tổ chức phỏng vấn và thảo luận trực tiếp với Domain Expert/PO để chốt thống nhất các khái niệm. Đánh giá rủi ro hệ thống để đưa ra các ràng buộc an toàn cốt lõi.
5. **Format gợi ý / Template áp dụng:**
   
   ### 9.1 Thuật ngữ cốt lõi (Glossary)
   | Thuật ngữ | Tiếng Anh (trong Code) | Ý nghĩa / Giải thích |
   |---|---|---|
   | Lead / Khách hàng tiềm năng | `Lead` | Là thông tin định danh (Tên, SĐT, Nhu cầu) thu thập được từ các kênh tiếp thị/phễu bán hàng. |
   | Nurturing Level 3 | `NurturingLevel3` | Trạng thái AI đã tương tác qua lại ít nhất 3 vòng hội thoại và khách hàng đồng ý để lại số điện thoại hoặc đồng ý hẹn gặp. |

   ### 9.2 Ràng buộc nghiệp vụ cốt lõi (Global Business Constraints)
   *   **Ngân sách API:** Mỗi tài khoản môi giới chỉ được tiêu hao tối đa X token/tháng.
   *   **Quy tắc phân quyền:** Chỉ hệ thống (System/AI) mới có quyền đổi trạng thái từ "Lead Mới" sang "Nurturing", môi giới không được thao tác bằng tay.
