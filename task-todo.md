# Task Todo: Fix Backend Build Errors

## Scope

- Restore Maven compile for the current `vn.ezisolutions.cloud.hackathon` backend source.
- Keep changes minimal and focused on broken references left after cleanup.
- Do not introduce new business rules.
- Do not rotate or remove committed secrets in this task; report them separately as a follow-up risk.

## Files to Update

- `src/main/java/vn/ezisolutions/cloud/eziops/SpringBootApplication.java`
  - Add a compatibility launcher for existing IDE run configurations that still point to the old main class.
- `src/main/java/vn/ezisolutions/cloud/hackathon/entities/UserEntity.java`
  - Remove stale relation to deleted `ProjectEntity`.
  - Keep role relation used by authentication.
- `src/main/java/vn/ezisolutions/cloud/hackathon/entities/RoleEntity.java`
  - Add missing role entity required by `UserEntity` and `AuthService`.
- `src/main/java/vn/ezisolutions/cloud/hackathon/repositories/jpa/UserRepository.java`
  - Add missing user repository required by `AuthService`.
- `src/main/java/vn/ezisolutions/cloud/hackathon/repositories/jpa/RoleRepository.java`
  - Add missing role repository required by `AuthService`.
- `src/main/java/vn/ezisolutions/cloud/hackathon/properties/IdSystemProperties.java`
  - Add missing ID System properties required by auth exchange.
- `src/main/java/vn/ezisolutions/cloud/hackathon/properties/BitbucketProperties.java`
  - Add missing properties required by `RestClientConfig`.
- `src/main/java/vn/ezisolutions/cloud/hackathon/listeners/RedisPublisher.java`
  - Remove stale runner-specific method referencing deleted DTO.
- `src/main/java/vn/ezisolutions/cloud/hackathon/services/auth/AppSecurityService.java`
  - Remove stale domain permission service referencing deleted project/review entities.
- `src/main/java/vn/ezisolutions/cloud/hackathon/services/rest/DbDiagramService.java`
  - Remove stale integration service referencing deleted dbdiagram DTOs.

## Implementation Checklist

- [x] Add missing auth-support classes only where still used.
- [x] Remove stale methods/services whose dependent domain classes were deleted.
- [x] Run Maven compile/test verification.
- [x] Confirm no unrelated files are reverted.
- [x] Verify old IDE main class delegates to the new application package.

## Follow-up Scope: Port Product-Service AI Config

- Source project: `C:\Users\PV\Downloads\crawler\crawler_jd\product-service`.
- Target project: `C:\Users\PV\Downloads\codex_hackathon\backend`.
- Port the reusable AI configuration slice only.
- Do not port JD crawler/job tools or product-service business APIs.
- Do not copy literal secrets from product-service local configuration.

## Product-Service AI Files to Port

- `pom.xml`
  - Add Spring AI dependency management.
  - Add Google GenAI, Google GenAI embedding, MCP client/server, and Chroma vector store starters.
- `src/main/resources/application*.properties`
  - Add Spring AI Gemini embedding and Chroma vector-store configuration using environment placeholders.
- `src/main/java/vn/ezisolutions/cloud/hackathon/documents/ai_core/*`
  - Add AI core JPA entities.
- `src/main/java/vn/ezisolutions/cloud/hackathon/repositories/ai_core/*`
  - Add AI core repositories.
- `src/main/java/vn/ezisolutions/cloud/hackathon/services/ai_core/*`
  - Add agent client creation and AI env selection.
- `src/main/java/vn/ezisolutions/cloud/hackathon/core/jpa/JsonConverters.java`
  - Add JSON converters used by AI entities.
- `src/main/java/vn/ezisolutions/cloud/hackathon/enums/AiProvider.java`
  - Add provider enum.
- `src/main/java/vn/ezisolutions/cloud/hackathon/properties/AiCoreProperties.java`
  - Add generic AI cooldown configuration.
- `src/main/java/vn/ezisolutions/cloud/hackathon/configs/JpaConfig.java`
  - Include non-`repositories.jpa` packages in repository scanning.

## AI Port Checklist

- [x] Port dependency and property configuration.
- [x] Port AI entities, repositories, services, and JSON converters.
- [x] Keep product-service secrets out of committed properties.
- [x] Run Maven verification.

## Follow-up Scope: PostgreSQL Migration And Base Source Audit

- Source baseline: `C:\Users\PV\Downloads\codex_hackathon\base_source\eziops.app-gw`.
- Target backend must move from MySQL driver/dialect/properties to PostgreSQL.
- Keep only reusable base-source ideas:
  - AI client/model/env pattern.
  - Chroma vector-store settings.
  - JDBC chat memory concept with PostgreSQL dialect.
- Do not restore removed EziOps domain modules, MCP agent tools, chat domain, or Bitbucket/dbdiagram DTO services unless Bob explicitly asks.
- Do not copy committed secrets from base source.

## PostgreSQL Checklist

- [x] Replace MySQL JDBC dependency with PostgreSQL driver.
- [x] Replace MySQL datasource driver/dialect/properties with PostgreSQL.
- [x] Remove MySQL-only `BINARY(16)` and `LONGTEXT` column definitions from current entities.
- [x] Port PostgreSQL-backed JDBC chat memory from base-source `AiConfig`.
- [x] Add manual PostgreSQL schema for current identity, AI core, and chat memory tables.
- [x] Re-run compile verification.
