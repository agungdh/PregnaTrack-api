# AGENTS.md

Compact guide for OpenCode sessions in `PregnaTrack`. Skip what is obvious from `pom.xml` or filenames.

## Stack at a glance

- Spring Boot **4.0.7**, Java **25** (Maven toolchain is JDK 25; build fails on older JDKs).
- Spring Modulith **2.0.7** is on the classpath, MapStruct **1.6.3** + Lombok, springdoc-openapi 3.0.2, Postgres driver.
- Single-module Maven project. Base package: `id.my.agungdh.pregnatrack`. Layout is **feature-based** (e.g. `user/`, `config/`, `exception/`), not layered.
- Use the wrapper, not a system `mvn`: `./mvnw ...`. Wrapper jar is gitignored, only `maven-wrapper.properties` is committed.

## Common commands

- Build: `./mvnw compile`
- Run app: `./mvnw spring-boot:run` (requires Postgres + Valkey — see below).
- Run all tests: `./mvnw test` (the only test is `PregnaTrackApplicationTests.contextLoads`, a `@SpringBootTest` that needs the full stack).
- Run a single test class/method: `./mvnw -Dtest=PregnaTrackApplicationTests#contextLoads test`
- Format/lint: none configured — do not invent one.

## Local infrastructure (mandatory for run/test)

`docker-compose.yml` at the repo root defines the only supported dev stack. The Spring config in `src/main/resources/application.yaml` is hard-coded to these endpoints (no profiles).

- Postgres: `127.0.0.1:5432`, db `pregna_track`, user/password `admin`/`admin`.
- Valkey (Redis protocol): `127.0.0.1:6379`, no password.
- Mailpit SMTP: `127.0.0.1:1025` (web UI on `8025`).
- MinIO ("silo" container): `127.0.0.1:9000` API, `9001` console, `minioadmin`/`minioadmin`.
- Adminer: `127.0.0.1:8083`.

Bring it up before any run/test: `docker compose up -d postgres valkey` (the rest are optional until their feature is wired in).

## Architecture facts worth knowing

- **Auth is opaque-token-in-Redis, not JWT.** `AuthService.login` stores `auth:token:<uuid>` -> `userId` with a 24h TTL. `AuthFilter` reads `Authorization: Bearer ...`, resolves the user id, and sets the **Spring Security principal to the user's `Long` id** (no roles/authorities). The OpenAPI definition declares a `bearerFormat = "JWT"` scheme but the runtime is opaque — the scheme is metadata only.
- `BaseEntity` (`config/BaseEntity.java`) auto-fills `uuid` (v4), `createdAt/updatedAt` (epoch ms), and `createdBy/updatedBy` by reading the `Long` principal from `SecurityContextHolder`. If you change how the principal is set, audit fields break silently.
- Soft delete: every persistable entity must extend `BaseEntity` and add `@SQLRestriction("deleted_at IS NULL")` (see `User`). Use `BaseRepository.softDelete(entity)` — never `delete(entity)`.
- DTOs are Java `record`s. Mappers are MapStruct interfaces; the maven config sets `-Amapstruct.defaultComponentModel=spring`, so generated mappers are Spring beans automatically. Use `@Builder(disableBuilder = true)` on mappers when the source/target are records (see `UserMapper`).
- `spring.jpa.hibernate.ddl-auto=update` — there is **no Flyway/Liquibase**. Schema changes apply on boot.
- Virtual threads are on (`spring.threads.virtual.enabled=true`).
- `SecurityConfig` permits `/api/auth/login`, `/v3/api-docs/**`, `/swagger-ui/**`, `/swagger-ui.html`, `/api-docs/**`; everything else requires the bearer token.

## Endpoints currently exposed

- `POST /api/auth/login` → `{ "token": "..." }` (public).
- `POST /api/users` → `UserResponse` (requires bearer; for now only this endpoint proves the auth path end-to-end).
- Swagger UI: `/swagger-ui.html` (public).

## Style/quirks

- Lombok `@RequiredArgsConstructor` is the default for DI; do not add a manual constructor unless the class already has one (see `UserService` — the only manual constructor — left in deliberately).
- Use the existing `GlobalExceptionHandler` patterns; do not throw raw `RuntimeException` from services without checking it will be mapped to a sensible 5xx with a logged message.
- `HELP.md` is the stock Spring Initializr reference and is `.gitignore`d — do not edit or rely on it.
