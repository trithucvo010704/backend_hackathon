# Acceptance Criteria: Fix Backend Build Errors

- `mvnw.cmd test` completes successfully.
- Java source compiles without missing-symbol errors.
- Authentication compile dependencies are present:
  - `RoleEntity`
  - `UserRepository`
  - `RoleRepository`
  - `IdSystemProperties`
- Stale references to deleted cleanup artifacts are removed:
  - project/review permission entities from `AppSecurityService`
  - dbdiagram DTOs from `DbDiagramService`
  - runner DTO from `RedisPublisher`
- No new business rule is invented.
- Existing package namespace remains `vn.ezisolutions.cloud.hackathon`.
- Existing IDE run configuration using `vn.ezisolutions.cloud.eziops.SpringBootApplication` can still find a main class.

## AI Config Port Acceptance Criteria

- Spring AI dependencies are present in `pom.xml`.
- Spring AI BOM is managed in dependency management.
- Gemini embedding and Chroma vector-store properties exist in base/local/prod properties.
- Product-service literal AI keys are not copied into this repo.
- AI core entities, repositories, and services compile under `vn.ezisolutions.cloud.hackathon`.
- JD crawler/job MCP tools are not ported.
- `mvnw.cmd test` succeeds.

## PostgreSQL Acceptance Criteria

- `pom.xml` uses PostgreSQL JDBC instead of MySQL connector.
- `application*.properties` use PostgreSQL driver/dialect/property names.
- No current source file contains MySQL-only `BINARY(16)` or `LONGTEXT` column definitions.
- AI chat memory uses PostgreSQL dialect, not MySQL dialect.
- A manual PostgreSQL schema exists for the currently mapped identity and AI tables.
- Build remains green after the PostgreSQL switch.
