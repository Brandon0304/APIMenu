# Decisiones Arquitectónicas — API de Gestión de Menú para Restaurante

## Estado del Proyecto

- **Propósito:** API REST para gestionar el menú de un restaurante (categorías, platillos, ingredientes, modificadores, menús, alérgenos)
- **Stack:** Java 17+ / Spring Boot 3.x / PostgreSQL / Hexagonal Architecture
- **Estado:** Diseño arquitectónico inicial

## Índice de ADRs

| # | Título | Estado | Descripción |
|---|-------|--------|-------------|
| 001 | [Arquitectura Hexagonal (Ports & Adapters)](ADR-001-hexagonal-architecture.md) | ✅ Aceptado | Arquitectura general del sistema |
| 002 | [Modelo de Dominio y Contexto Delimitado](ADR-002-domain-model.md) | ✅ Aceptado | Entidades, agregados y value objects |
| 003 | [Estrategia de Base de Datos y Persistencia](ADR-003-database-persistence.md) | ✅ Aceptado | PostgreSQL, JPA + Flyway, esquema relacional |
| 004 | [Diseño de API REST y Documentación](ADR-004-api-design-documentation.md) | ✅ Aceptado | Endpoints, versionado, SpringDoc OpenAPI |
| 005 | [Estrategia de Mapeo y DTOs](ADR-005-mapping-dto-strategy.md) | ✅ Aceptado | MapStruct entre capas (DTO ↔ Comando ↔ Dominio ↔ JPA) |
| 006 | [Estrategia de Manejo de Errores](ADR-006-error-handling.md) | ✅ Aceptado | RFC 7807 Problem Details + códigos de dominio |
| 007 | [Estrategia de Pruebas](ADR-007-testing-strategy.md) | ✅ Aceptado | Pirámide de testing + ArchUnit + Testcontainers |
| 008 | [Estrategia de Seguridad y Autenticación](ADR-008-security-authentication.md) | 🔷 Propuesto | JWT con Spring Security 6, RBAC |
| 009 | [Estrategia de Cacheo](ADR-009-caching-strategy.md) | 🔷 Propuesto | Caffeine + Cache HTTP, futuro Redis |
| 010 | [Estrategia de Observabilidad](ADR-010-observability.md) | 🔷 Propuesto | Logs JSON, Micrometer/Prometheus, Actuator |

## Estado de los ADRs

- **✅ Aceptado** — Decisión finalizada y en vigor
- **🔷 Propuesto** — Decisión propuesta, pendiente de revisión o implementación
- **❌ Deprecado** — Decisión reemplazada por una ADR más reciente

## Stack Tecnológico Completo

| Componente | Tecnología | ADR |
|------------|-----------|-----|
| Lenguaje | Java 17+ (records, pattern matching) | ADR-001 |
| Framework | Spring Boot 3.x | ADR-001 |
| Arquitectura | Hexagonal (Ports & Adapters) | ADR-001 |
| Base de Datos | PostgreSQL 16+ | ADR-003 |
| ORM | Spring Data JPA + Hibernate | ADR-003 |
| Migraciones | Flyway | ADR-003 |
| Documentación | SpringDoc OpenAPI (Swagger UI) | ADR-004 |
| Mapeo | MapStruct | ADR-005 |
| Validación | Jakarta Bean Validation | ADR-004 |
| Testing | JUnit 5 + Mockito + Testcontainers + ArchUnit | ADR-007 |
| Seguridad | JWT + Spring Security 6 + BCrypt | ADR-008 |
| Caché | Caffeine (fase 1), Redis (fase 2) | ADR-009 |
| Observabilidad | Actuator + Micrometer + Logback JSON | ADR-010 |
| Build | Maven | ADR-001 |

## Principios Arquitectónicos

1. **Domain First:** El modelo de dominio es puro Java, sin dependencias de framework
2. **Separación de Responsabilidades:** Cada capa tiene una responsabilidad única
3. **Testabilidad:** Las reglas de negocio se prueban sin infraestructura
4. **Evolucionabilidad:** Cambiar tecnología no debe afectar el dominio
5. **Simplicidad Pragmática:** No sobreingeniería — aplicar el patrón donde aporta valor
6. **API Consistente:** Formato de respuesta, errores y paginación uniformes
7. **Seguridad por Diseño:** Autenticación y autorización desde el inicio

## Cómo Contribuir

Para proponer un cambio arquitectónico:
1. Crear una nueva ADR en `docs/adr/ADR-XXX-titulo.md`
2. Seguir el formato establecido (Contexto → Opciones → Decisión → Consecuencias)
3. Actualizar este README con la nueva entrada
4. Marcar como "Propuesto" hasta que sea revisada y aceptada
