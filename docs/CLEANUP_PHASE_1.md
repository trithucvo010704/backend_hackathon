# Cleanup Phase 1: AI, MCP and Chat

## Removed

- Spring AI configuration and dependencies.
- Google GenAI, embedding, Chroma vector store and JDBC chat memory.
- MCP server/client configuration, MCP resource handler and MCP security filter.
- Ten named MCP agent configurations and all MCP tools.
- AI model, AI environment and agent brain APIs.
- Chat controller, services, DTOs, entities, repositories and mappers.
- MCP DTOs, services and mappers for project-related domains.
- Vector metadata and vector synchronization calls.
- Dead Kafka consumers/services that depended on Chat.

The Java source count changed from 382 files to 216 files.

## Kept

- Existing package/folder architecture.
- Authentication, User and Role.
- Common response, pagination, exception handling and utilities.
- Spring Security for `/app/**`.
- JPA, MySQL and current database entities outside AI/Chat.
- Redis and cache helpers.
- Kafka producer/configuration.
- Project, sprint, epic, story and task domains.
- Review, guideline, API spec, DB table spec and project document domains.
- Bitbucket, dbdiagram and S3 integrations.
- All original `application*.properties` and `logback.xml` configuration files.

## Verification

- No Spring AI or MCP classes remain in Java source.
- No Spring AI or MCP dependency remains in Maven dependency tree. Legacy
  `spring.ai.*` keys remain unchanged in the original configuration files as
  requested, but they are inactive because the related dependencies are gone.
- `mvn clean test -DskipTests`: success.
- `mvn package -DskipTests`: success.
- Runnable JAR: `target/app-gw-1.0.0.jar`.

The application was not started automatically with the existing local profile
because it points to a network database and uses `ddl-auto=update`. Starting it
could modify real data. Use an isolated/local database before runtime testing.
