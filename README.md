# Menu API — Restaurant Menu Management

REST API for managing restaurant menus, built with hexagonal architecture (DDD) in Java 21 + Spring Boot 3.4.

## Tech Stack

- **Java 21**, Spring Boot 3.4.5, Maven
- **PostgreSQL 16**, Flyway migrations
- **Spring Security 6** with JWT (RS256) + OAuth2 Resource Server
- **Spring Cache** with Caffeine (in-memory)
- **Micrometer** + Prometheus metrics
- **Logstash** structured JSON logging
- **MapStruct** for entity mappings
- **Testcontainers**, ArchUnit, JaCoCo (80% coverage)

## Architecture

Hexagonal (ports & adapters) with 3 layers:
- `domain/` — pure business logic (no framework deps)
- `application/` — use case orchestration
- `infrastructure/` — I/O adapters (REST, JPA, security, cache)

## Quick Start

```bash
docker compose up -d
./mvnw flyway:migrate
./mvnw spring-boot:run
```

Default admin credentials: `admin@restaurant.com` / `admin123`

## API

| Endpoint | Description |
|----------|-------------|
| `POST /api/v1/auth/login` | Login → JWT token |
| `GET /api/v1/categories` | List categories (public) |
| `POST /api/v1/categories` | Create category (ADMIN) |
| `GET/PUT/DELETE /api/v1/menu-items` | Menu item CRUD |
| `GET/PUT/DELETE /api/v1/menus` | Menu CRUD |
| `GET /api/v1/ingredients` | List ingredients (public) |
| `GET /api/v1/allergens` | List allergens (public) |

Swagger UI: http://localhost:8080/swagger-ui.html

## Testing

```bash
./mvnw test              # 105+ tests
./mvnw verify            # + Checkstyle + JaCoCo coverage
```

## Build

```bash
./mvnw clean package -DskipTests
java -jar target/menu-api-*.jar
```
