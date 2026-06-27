# Backend Base Project Audit

## 1. Mục tiêu

Biến `backend` từ một ứng dụng EziOps hoàn chỉnh thành một Spring Boot base:

- chạy được ngay với số dependency tối thiểu;
- không chứa nghiệp vụ EziOps;
- có cấu trúc rõ ràng để thêm module mới;
- các hạ tầng Redis, Kafka, S3, AI, MCP chỉ được bật khi cần;
- cấu hình không chứa credential;
- có test nền và tài liệu đủ để bắt đầu dự án mới.

## 2. Hiện trạng đã kiểm tra

- Spring Boot Maven project, Java 23.
- 386 file trong `src/main`, gồm 382 Java source và 4 resource file.
- Không có `src/test`.
- Khoảng 13.700 dòng Java.
- Build hiện tại thành công bằng Maven.
- `backend` và `base_source/eziops.app-gw` gần như là hai bản giống nhau.
- Workspace hiện chưa có frontend hoặc `package.json`.

Phân bố chính:

| Package | Files | Vai trò hiện tại |
|---|---:|---|
| `services` | 78 | Application service, AI, MCP, Kafka và integration |
| `dto` | 93 | DTO của app, MCP, chat và external API |
| `core` | 33 | Response, exception, filter, cache, utility |
| `entities` | 26 | JPA entity của EziOps |
| `mappers` | 32 | MapStruct mapper |
| `controllers` | 24 | REST API app/admin/chat/auth |
| `configs` | 24 | App, security, Redis, Kafka, AI, MCP, S3 |
| `repositories` | 24 | JPA repositories |
| `tools` | 20 | MCP tools và agent-specific tools |

Database schema hiện có 28 bảng và phần lớn là domain EziOps: project, sprint,
epic, story, task, ticket, review, AI, chat, documents và agent.

## 3. Các vấn đề cần xử lý trước

### P0 - Security

- Nhiều credential trong `application-local.properties` và
  `application-prod.properties` đang là literal value.
- Các file này hiện được Git track.
- `sonar-project.properties` chứa token dạng plaintext.
- `RestClientConfig` log cả request/response body nên có nguy cơ ghi token hoặc
  dữ liệu nhạy cảm vào log.

Hành động bắt buộc:

1. Thu hồi và tạo lại các credential đã commit.
2. Xóa credential khỏi lịch sử Git nếu repository đã được chia sẻ.
3. Chỉ dùng environment variable hoặc secret manager.
4. Thêm file cấu hình mẫu không chứa secret.
5. Mask header và body nhạy cảm trong HTTP logging.

### P1 - Base đang phụ thuộc quá nhiều hạ tầng

Ứng dụng hiện kéo theo JPA, MySQL, Redis, Kafka, Security, Spring AI, MCP,
Chroma, Google GenAI, S3, Apache POI, Bitbucket và dbdiagram. Một dự án mới
không nên phải cấu hình toàn bộ các hệ thống này mới có thể khởi động.

### P1 - `core` chưa thực sự độc lập

- `AppTokenFilter` phụ thuộc trực tiếp `AuthService`.
- `AuthService` phụ thuộc Redis, JPA, Role/User và ID System riêng của EziOps.
- `McpToolFilter` và các MCP config phụ thuộc domain MCP.
- Security rule chứa endpoint và tên agent cụ thể.
- Redis key vẫn dùng prefix `eziops`.

### P1 - Thiếu quality gate

- Không có unit test, integration test hoặc context smoke test.
- Build có cảnh báo Lombok builder default.
- MapStruct có nhiều unmapped target properties.
- Có deprecated API trong `McpResourceHandler`.

### P2 - Abstraction và naming chưa nhất quán

- Có cả `BaseResponse`, `BaseErrorResponse` và `ApiResponse`.
- `BaseResponse` dùng `Object`, mất type safety.
- API dùng `status = 0/1` song song với HTTP status.
- `CustomException` là checked exception nhưng được dùng như application error.
- Nhiều class dùng trùng `@Data`, `@Getter`, `@Setter`.
- Entity lặp lại `id`, `createdAt`, `updatedAt`.
- Package tổ chức theo technical layer khiến một feature nằm rải ở controller,
  DTO, mapper, service, repository và entity.

## 4. Cấu trúc base project đề xuất

Base nên dùng modular package-by-feature, giữ phần shared thật nhỏ:

```text
backend/
├─ docs/
│  ├─ BASE_PROJECT_AUDIT.md
│  ├─ ARCHITECTURE.md
│  ├─ GETTING_STARTED.md
│  └─ DECISIONS.md
├─ src/
│  ├─ main/
│  │  ├─ java/<base-package>/
│  │  │  ├─ Application.java
│  │  │  ├─ shared/
│  │  │  │  ├─ api/
│  │  │  │  │  ├─ ApiResponse.java
│  │  │  │  │  ├─ ApiError.java
│  │  │  │  │  └─ PageResponse.java
│  │  │  │  ├─ exception/
│  │  │  │  │  ├─ AppException.java
│  │  │  │  │  └─ GlobalExceptionHandler.java
│  │  │  │  ├─ security/
│  │  │  │  │  ├─ CurrentUser.java
│  │  │  │  │  ├─ TokenAuthenticator.java
│  │  │  │  │  └─ SecurityConfig.java
│  │  │  │  ├─ persistence/
│  │  │  │  │  └─ AuditableEntity.java
│  │  │  │  └─ config/
│  │  │  │     ├─ JacksonConfig.java
│  │  │  │     ├─ JpaConfig.java
│  │  │  │     └─ WebConfig.java
│  │  │  ├─ modules/
│  │  │  │  └─ identity/
│  │  │  │     ├─ api/
│  │  │  │     ├─ application/
│  │  │  │     ├─ domain/
│  │  │  │     └─ infrastructure/
│  │  │  └─ integrations/
│  │  │     ├─ cache/
│  │  │     └─ http/
│  │  └─ resources/
│  │     ├─ application.yml
│  │     ├─ application-local.yml
│  │     ├─ application-prod.yml
│  │     └─ db/migration/
│  └─ test/
│     └─ java/<base-package>/
│        ├─ ArchitectureTest.java
│        └─ ApplicationContextTest.java
├─ .env.example
├─ Dockerfile
├─ pom.xml
└─ README.md
```

`modules/identity` là module mẫu và có thể giữ User/Role nếu dự án mới thực sự
cần tài khoản nội bộ. Authentication bên ngoài phải đi qua interface
`TokenAuthenticator`; không để core biết ID System, Redis hoặc database cụ thể.

## 5. KEEP / REFACTOR / REMOVE

### KEEP - giữ ý tưởng hoặc nền tảng

| Thành phần | Quyết định |
|---|---|
| Maven Wrapper, Docker multi-stage | Giữ |
| Spring Web, Validation | Giữ |
| Spring Security | Giữ framework, không giữ rule EziOps |
| Spring Data JPA + MySQL | Giữ nếu base mục tiêu luôn dùng relational DB |
| Jackson Java Time config | Giữ |
| Global exception handling | Giữ |
| Pagination response/request | Giữ |
| CORS configuration | Giữ và đưa sang typed properties |
| MapStruct, Lombok | Giữ có kiểm soát |
| User/Role | Giữ như module `identity` tùy chọn |
| Redis helper | Giữ như integration tùy chọn |
| Generic HTTP client | Giữ abstraction và timeout/retry policy |

### REFACTOR - giữ mục đích nhưng viết lại ranh giới

| Thành phần hiện tại | Refactor thành |
|---|---|
| `SpringBootApplication` | `Application`, package trung lập |
| `BaseResponse`, `BaseErrorResponse`, `ApiResponse` | Một `ApiResponse<T>` và một `ApiError` |
| `BasePagination` | `PageResponse<T>` immutable |
| `CustomException` | `AppException extends RuntimeException` có error code |
| `RestResponseEntityExceptionHandler` | `GlobalExceptionHandler`, response và message nhất quán |
| `AuthorizedUser` | `CurrentUser`, authorities luôn trả collection rỗng thay vì `null` |
| `AppTokenFilter` | Filter phụ thuộc `TokenAuthenticator` interface |
| `AuthService` | Adapter của identity provider, tách khỏi core |
| `WebSecurityConfig` | Rule trung lập, route public lấy từ config |
| `UserEntity`, `RoleEntity` | Chuyển vào identity module, bỏ liên hệ Project |
| Timestamp lặp trong entity | `AuditableEntity` |
| `RedisClient`, `AbstractCacheService` | Optional cache adapter, không dùng prefix `eziops` |
| `RestClientConfig` | Generic client factory; không tạo bean Bitbucket/dbdiagram/ID System |
| `AsyncConfig` | Typed properties và graceful shutdown |
| `application*.properties` | YAML sạch, toàn bộ secret từ environment |
| `schema.sql` | Flyway migration tối thiểu cho identity |
| `pom.xml` | Dependency tối thiểu, test dependencies đúng scope, version đồng bộ BOM |
| Layer packages | Package theo feature/module |
| `docs` trong `.gitignore` | Bỏ ignore để tài liệu được version control |

### REMOVE - nghiệp vụ hoặc integration riêng EziOps

Xóa khỏi base:

- Project, member, repository.
- Epic, story, task, sprint, ticket và sequence generator.
- Review request, review comment, QA batch log và pull request.
- API spec, DB table spec, guideline doc, concept note và project document.
- Chat conversation/message.
- Agent brain, AI environment/model và toàn bộ `services/ai`.
- Toàn bộ MCP DTO, mapper, service, resource, tool và 10 agent config.
- Bitbucket integration và DTO.
- dbdiagram integration.
- Chroma/vector store và Google GenAI.
- S3 config nếu base mới chưa xác nhận bắt buộc upload.
- Kafka config/listener/producer vì consumer hiện đang bị comment; đưa lại sau như
  một optional integration khi có use case.
- `RunnerApiFilter` nếu không còn runner API.
- `MongoDbUtils`, `McpToolContextUtils`, `McpResponse`, `KafkaSystemEvent`.
- Constant EziOps: Kafka topic, Redis channel/key, role/project-specific rules.
- Apache POI và các dependency không còn được sử dụng.
- Build script chứa registry/image name của EziOps.
- Sonar URL/project/token riêng của công ty khỏi base public/generic.

## 6. Dependency tối thiểu đề xuất

Baseline:

- `spring-boot-starter-web`
- `spring-boot-starter-validation`
- `spring-boot-starter-security`
- `spring-boot-starter-data-jpa`
- MySQL driver
- MapStruct
- Lombok
- `spring-boot-starter-test`
- `spring-security-test`

Optional theo profile/module:

- Redis
- Kafka
- S3
- retry
- AI/MCP/vector store

Không đặt version thủ công cho Spring Boot starter nếu BOM đã quản lý. Đặc biệt
không trộn Spring Boot 4 parent với version `3.4.2` trên starter AOP.

## 7. Lộ trình thực hiện

### Phase 0 - Bảo vệ secret và đóng băng baseline

1. Rotate credential đã commit.
2. Chuyển secret sang environment variable.
3. Ghi nhận build baseline và tạo smoke test.
4. Tạo nhánh hoặc tag backup trước cleanup.

### Phase 1 - Tạo shared kernel sạch

1. Chuẩn hóa response, error và pagination.
2. Tách security contract khỏi `AuthService`.
3. Chuẩn hóa config/properties.
4. Thêm auditable entity và migration.

### Phase 2 - Giữ identity như vertical slice mẫu

1. Chuyển User/Role/Auth vào `modules/identity`.
2. Xóa dependency từ User sang Project.
3. Dùng authentication adapter có thể thay thế.
4. Thêm unit/integration test cho auth và role.

### Phase 3 - Gỡ domain EziOps theo dependency order

1. Gỡ controller và MCP tools.
2. Gỡ service và mapper.
3. Gỡ repository, entity, DTO và enum.
4. Gỡ integration và dependency không còn dùng.
5. Thu gọn database migration.

### Phase 4 - Làm hạ tầng thành optional

1. Redis/cache theo conditional property.
2. HTTP client factory không log dữ liệu nhạy cảm.
3. Chỉ thêm Kafka, S3 hoặc AI khi dự án mới cần.

### Phase 5 - Quality gate và tài liệu

1. Context test, unit test và repository integration test.
2. Build không warning nghiêm trọng.
3. Kiểm tra dependency và secret scan.
4. Hoàn thiện README, architecture và getting started.

## 8. Tiêu chí hoàn thành

- `mvn clean verify` thành công.
- Application khởi động mà không cần Redis, Kafka, S3, AI hoặc MCP.
- Không còn package/class mang tên EziOps hoặc agent cũ.
- Không có secret literal trong repository.
- Có ít nhất context smoke test, response/error test và identity test.
- Flyway tạo được database mới từ đầu.
- Docker image không chứa credential và chạy bằng non-root user.
- `docs/` được Git track và đủ hướng dẫn bắt đầu dự án mới.

