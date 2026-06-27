# Agent memory chung - OrderFlow backend E2E

Thời điểm ghi: 2026-06-27, sau khi user yêu cầu dừng lại và ghi rõ đã làm tới đâu, chưa làm gì.

## Mục tiêu user giao

User yêu cầu test E2E backend `D:\hackerthon\backend_hackathon` theo tài liệu:

- `D:\hackerthon\backend_hackathon\docs\OrderFlow AI MVP.pdf`
- Test full API bằng `curl`.
- Test luôn các test script trong:
  `D:\hackerthon\backend_hackathon\docs\PROJECT\test_scripts`
- User đã cung cấp OpenAI API key. Không được ghi lại key plaintext vào file/log/repo. Chỉ ghi nhận là key đã có.

## Đã làm đến đâu

### 1. Đã đọc và hiểu tổng quan repo

Repo backend là Java Spring Boot/Maven:

- File chính: `pom.xml`
- Main class: `vn.ezisolutions.cloud.hackathon.SpringBootApplication`
- Java trong `pom.xml`: `23`
- Spring Boot parent: `4.0.5`
- Dockerfile build bằng `maven:3.9.9-eclipse-temurin-23`, runtime `eclipse-temurin:23-jdk-noble`
- Local profile ở:
  `D:\hackerthon\backend_hackathon\src\main\resources\application-local.properties`
- Default config ở:
  `D:\hackerthon\backend_hackathon\src\main\resources\application.properties`

Module OrderFlow nằm chủ yếu trong:

`D:\hackerthon\backend_hackathon\src\main\java\vn\ezisolutions\cloud\hackathon\modules\orderflow`

### 2. Đã đọc PDF OrderFlow AI MVP

Đã trích nội dung PDF bằng `pdfplumber`. PDF có 38 trang.

Nội dung chính của PDF:

- MVP là Review Workbench nội bộ cho Sale Admin, không phải chatbot bán hàng.
- Luồng chính:
  1. Sale Admin login.
  2. Chọn customer/project/warehouse.
  3. Paste raw order text.
  4. Backend lưu `raw_order_texts`, tạo `draft_orders`.
  5. AI extraction tạo `draft_order_lines`.
  6. SKU matching tạo `sku_candidates`.
  7. Rule engine check price/inventory/credit.
  8. Tạo hold nếu cần.
  9. Sale Admin review, sửa line, chọn SKU, release hold.
  10. Approve order.
  11. Generate quote hoặc pick list HTML preview.
  12. Có processing/audit/review logs.

### 3. Đã lập danh sách API thực tế trong code

Đã dùng `rg` và đọc controller. Các endpoint thực tế đã thấy:

Auth:

- `POST /api/auth/login`
- `GET /api/auth/me`
- `POST /api/auth/logout`

Master data:

- `GET /api/customers`
- `GET /api/customers/{id}`
- `GET /api/customers/{id}/projects`
- `GET /api/products/skus`
- `GET /api/products/skus/{id}`
- `GET /api/products/aliases`
- `GET /api/warehouses`
- `GET /api/inventory/balances`
- `GET /api/price-lists`
- `GET /api/credit-profiles/{customerId}`

AI/debug:

- `POST /api/ai/extract-order`

Draft order:

- `POST /api/draft-orders/from-text`
- `GET /api/draft-orders`
- `GET /api/draft-orders/{id}`
- `POST /api/draft-orders/{id}/run-checks`
- `POST /api/draft-orders/{id}/approve`
- `POST /api/draft-orders/{id}/reject`
- `POST /api/draft-orders/{id}/rerun-extraction`
- `PATCH /api/draft-orders/{id}`

Draft order line:

- `PATCH /api/draft-order-lines/{lineId}`
- `POST /api/draft-order-lines/{lineId}/match-skus`
- `GET /api/draft-order-lines/{lineId}/sku-candidates`
- `POST /api/draft-order-lines/{lineId}/select-sku`
- `POST /api/draft-order-lines/{lineId}/reject`

Hold:

- `GET /api/draft-orders/{id}/holds`
- `POST /api/order-holds/{holdId}/release`
- `POST /api/order-holds/{holdId}/reject`

Document:

- `POST /api/draft-orders/{id}/documents/quote`
- `POST /api/draft-orders/{id}/documents/pick-list`
- `GET /api/draft-orders/{id}/documents`
- `GET /api/draft-order-documents/{id}`

Events/audit:

- `GET /api/draft-orders/{id}/processing-events`
- `GET /api/draft-orders/{id}/audit-events`
- `GET /api/draft-orders/{id}/review-actions`

### 4. Đã đọc script và nhận định về test scripts

Folder:

`D:\hackerthon\backend_hackathon\docs\PROJECT\test_scripts`

Có 7 file markdown:

- `test_script_index.md`
- `e2e_demo_test_script.md`
- `ai_extraction_test_script.md`
- `sku_matching_alias_test_script.md`
- `rule_engine_test_script.md`
- `agent_customer_question_test_script.md`
- `mvp_user_journey_agent_test_script.md`

Nhận định quan trọng:

- Đây là các tài liệu/script test dạng markdown, không phải executable test runner `.ps1`, `.sh`, `.js`, `.py`.
- Muốn “chạy script” tự động thì phải chuyển nội dung trong markdown thành curl/manual SQL assertions hoặc viết runner riêng.
- `test_script_index.md` có thứ tự khuyến nghị:
  1. AI extraction
  2. SKU matching alias
  3. Rule engine
  4. Agent customer question
  5. E2E demo
  6. MVP user journey agent

### 5. Đã đọc script có sẵn trong repo

Trong `D:\hackerthon\backend_hackathon\scripts` có:

- `orderflow-run-local.ps1`
- `orderflow-smoke.ps1`

`orderflow-run-local.ps1` dùng để start backend local với profile `local`.

`orderflow-smoke.ps1` làm smoke flow:

- Login bằng `sale.admin@orderflow.local` / `password`
- Gọi `/api/auth/me`
- Gọi master data: customers, warehouses, product SKUs
- Tuỳ option có thể gọi `/api/ai/extract-order`
- Tuỳ option có thể tạo draft order từ text

### 6. Đã kiểm tra migration/schema

Phát hiện quan trọng:

- `application.properties` và `application-local.properties` bật Flyway:
  `spring.flyway.enabled=true`
- Flyway location cấu hình:
  `classpath:db/migration`
- Nhưng trong source hiện tại chỉ thấy:
  `D:\hackerthon\backend_hackathon\src\main\resources\db\manual\postgres-core.sql`
- Không thấy folder:
  `D:\hackerthon\backend_hackathon\src\main\resources\db\migration`
- `target/classes` cũng chỉ có `db/manual/postgres-core.sql`, không có migration.
- Hibernate đang cấu hình:
  `spring.jpa.hibernate.ddl-auto=validate`

Hệ quả dự kiến:

- Nếu start backend với DB trống, app có khả năng fail vì Flyway không có migration tạo bảng OrderFlow và Hibernate validate schema.
- `postgres-core.sql` chỉ tạo các bảng core/ai cũ, không tạo đầy đủ bảng OrderFlow như `organizations`, `app_users`, `customers`, `draft_orders`, v.v.

### 7. Đã kiểm tra Docker

Máy có Docker CLI ở:

`D:\Docker\Docker\Docker\resources\bin\docker.exe`

Nhưng `docker` không nằm trong PATH.

Khi gọi Docker CLI bằng đường dẫn tuyệt đối:

- Docker client version: `29.0.1`
- Context: `desktop-linux`
- Lỗi kết nối daemon:
  `failed to connect to the docker API at npipe:////./pipe/dockerDesktopLinuxEngine`

Đã thử mở Docker Desktop:

`D:\Docker\Docker\Docker\Docker Desktop.exe`

Nhưng Docker Desktop thoát sớm. Log mới nhất:

`C:\Users\phong\AppData\Local\Docker\log\host\Docker Desktop.exe.log`

Báo lỗi:

`getting backend binary path: cannot find registry key "SOFTWARE\\Docker Inc.\\Docker Desktop"`

Kết luận Docker tại thời điểm đó:

- Docker CLI có sẵn nhưng Docker Desktop daemon chưa chạy.
- Bản Docker Desktop hiện tại có vẻ bị thiếu registry key/cài đặt không hoàn chỉnh.
- Chưa start được Postgres container.

Lưu ý: User abort đúng lúc đang thử `Start-Service com.docker.service` và `wsl -d docker-desktop -- echo ok`. Lệnh bị abort, có thể chưa kịp thực hiện hết. Cần kiểm tra lại service trước khi tiếp tục.

### 8. Đã kiểm tra Java/Maven

Tình trạng Java:

- `JAVA_HOME` hiện tại trỏ tới:
  `C:\Program Files\Java\jdk-21.0.10`
- Nhưng folder này không tồn tại trong `C:\Program Files\Java`; chỉ thấy:
  `C:\Program Files\Java\jdk-17`
- `java -version` qua PATH trả về Java 17.
- Tìm thấy JRE 21 trong SmartGit:
  `D:\IDE do not delete this folder\SmartGit\SmartGit\jre\bin\java.exe`
  nhưng đây là JRE, không phải JDK đầy đủ để build.

Đã thử chạy:

`.\\mvnw.cmd test`

Kết quả fail ngay do:

`The JAVA_HOME environment variable is not defined correctly`

Chưa chạy được unit test/build.

Cần để ý:

- `pom.xml` khai báo Java 23.
- Nếu không có JDK 23, Maven build có thể tiếp tục fail.
- Nếu muốn build đúng với Dockerfile, Docker daemon phải chạy được để dùng image Temurin 23.

### 9. Đã đọc một phần service workflow

Đã đọc:

- `DraftOrderWorkflowService`
- `SkuMatchingService`
- `OrderFlowRuleCheckService`
- Auth/security configs

Nhận định chính:

- `createFromText` không chỉ tạo draft, mà chạy ngay AI extraction, SKU matching, rule checks, rồi trả detail.
- `runChecks` tạo price/inventory/credit checks.
- `approve` không cho approve nếu còn hold OPEN hoặc line chưa có selected SKU.
- `generateDocument` hiện tại tạo HTML document nhưng chưa thấy check chặn tạo pick-list khi order còn hold/open trong phần code đã đọc. Cần test/verify tiếp.
- `/api/order-holds/{holdId}/reject` hiện đang gọi lại `release(...)`, tức reject hold thực chất release hold. Đây có thể là bug/behavior cần báo cáo nếu test tiếp.
- `/api/draft-orders/{id}/rerun-extraction` trả `BaseResponse.fail("Rerun extraction is planned...")`, tức endpoint có nhưng chưa implement.
- `PATCH /api/draft-orders/{id}` hiện chỉ trả detail, chưa sửa header thực sự.

## Chưa làm được

Chưa làm các việc sau:

1. Chưa chạy được Docker Postgres.
2. Chưa tạo được database `hackathon`.
3. Chưa chạy migration/seed vì source hiện đang thiếu `db/migration`.
4. Chưa start được backend local.
5. Chưa gọi API bằng `curl`.
6. Chưa login được `/api/auth/login`.
7. Chưa test E2E theo PDF.
8. Chưa test full API.
9. Chưa chạy `scripts/orderflow-smoke.ps1`.
10. Chưa chuyển các markdown test script trong `docs/PROJECT/test_scripts` thành executable runner.
11. Chưa dùng OpenAI API key để gọi API thật.
12. Chưa sửa code/backend.
13. Chưa tạo migration/seed mới.

## Các blocker hiện tại

### Blocker 1: Docker Desktop daemon không chạy

Docker CLI có nhưng daemon không kết nối được. Log Docker Desktop báo thiếu registry key.

Cần user hoặc người tiếp theo xử lý một trong các cách:

- Mở/sửa Docker Desktop trên Windows để daemon chạy được.
- Cài lại Docker Desktop đúng cách.
- Hoặc dùng Postgres local khác thay Docker.
- Hoặc dùng WSL/Postgres nếu đã có distro Linux có PostgreSQL.

### Blocker 2: Java/JDK sai

`JAVA_HOME` đang sai và Java PATH đang là 17, trong khi project khai báo Java 23.

Cần:

- Cài JDK 23, hoặc
- Sửa `JAVA_HOME` trỏ đúng JDK 23, hoặc
- Nếu project có thể hạ Java version thì phải có quyết định riêng, không tự ý sửa.

### Blocker 3: Thiếu migration và seed

Backend bật Flyway + Hibernate validate nhưng source không có `db/migration`.

Cần:

- Tạo migration từ `docs/db.dbml` và entity thực tế, hoặc
- Tìm branch/file migration bị thiếu, hoặc
- Tạm thời tạo schema/seed local tối thiểu để test MVP.

Không nên nói là API fail khi chưa start được backend. Hiện tại mới chỉ có blocker môi trường/schema trước khi test runtime.

## Plan tiếp theo nếu tiếp tục

1. Kiểm tra lại sau abort:
   - `Get-Service com.docker.service`
   - `wsl -l -v`
   - Docker daemon đã lên chưa:
     `& "D:\Docker\Docker\Docker\resources\bin\docker.exe" version`

2. Nếu Docker đã chạy:
   - Run Postgres container:
     `docker run --name orderflow-postgres -e POSTGRES_DB=hackathon -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres:16`

3. Xử lý Java:
   - Tìm/cài JDK 23.
   - Set shell env:
     `$env:JAVA_HOME="path-to-jdk-23"`
     `$env:PATH="$env:JAVA_HOME\bin;$env:PATH"`
   - Chạy lại:
     `.\\mvnw.cmd test`

4. Xử lý schema:
   - Kiểm tra có migration ẩn/branch khác không.
   - Nếu không có, tạo migration SQL từ entity/DBML cho các bảng OrderFlow.
   - Tạo seed tối thiểu:
     - 1 organization
     - user `sale.admin@orderflow.local` password `password`
     - user manager nếu cần
     - customer MINH_ANH, AN_PHAT, THANH_DAT
     - project, warehouse
     - SKU, alias, price, inventory, credit profiles

5. Start backend local:
   - Set env `SPRING_PROFILES_ACTIVE=local`
   - Set `POSTGRES_URL=jdbc:postgresql://localhost:5432/hackathon`
   - Set `POSTGRES_USERNAME=postgres`
   - Set `POSTGRES_PASSWORD=postgres`
   - Set `OPENAI_API_KEY` từ user-provided key, không ghi vào file.
   - Chạy `scripts/orderflow-run-local.ps1`

6. Test curl/manual:
   - Login
   - Auth me
   - Master data
   - AI extract debug
   - Create draft from text
   - Detail
   - Line candidate/select
   - Run checks
   - Holds release/reject
   - Approve/reject
   - Generate quote/pick-list
   - Documents
   - Processing/audit/review events

7. Test theo docs:
   - E2E-01 happy path
   - E2E-03 ambiguous SKU
   - E2E-04 stock hold
   - E2E-05 credit hold
   - Sau đó đối chiếu các markdown test scripts.

## Ghi chú bảo mật

OpenAI API key đã được user cung cấp trong chat. Không ghi lại plaintext vào file này. Nếu tiếp tục test, chỉ set vào env của shell runtime, không commit vào repo, không đưa vào `.properties`, không in ra log.

