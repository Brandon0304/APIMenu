# ADR-003: Estrategia de Base de Datos y Persistencia

## Status: Aceptado

## Contexto

Se necesita definir el motor de base de datos, la estrategia de persistencia ORM, el manejo de migraciones y el diseño del esquema relacional para la API de gestión de menú.

**Restricciones:**
- Base de datos relacional especificada: PostgreSQL
- Debe cumplir con la arquitectura hexagonal (el dominio no debe conocer JPA)
- Las migraciones deben ser versionadas y reproducibles
- Debe soportar las relaciones del dominio (M:N, 1:N, 1:1)
- Debe considerar multi-tenancy futuro (varios restaurantes)

## Opciones Consideradas

### Opción A: Spring Data JPA con Hibernate (ORM completo)

ORM con mapeo objeto-relacional completo, lazy loading, caché de primer nivel y generación automática de esquemas.

**Separación estricta:** Entidades JPA separadas de los modelos de dominio. Los adaptadores de persistencia mapean entre ambos.

- **Pros:**
  - Madurez y ecosistema: documentación amplia, comunidad grande
  - Lazy loading optimiza consultas
  - Spring Data JPA reduce drásticamente el código de repositorio
  - Soporte nativo de PostgreSQL (tipos JSONB, arrays)
  - Integración con Spring Transaction Management
  - Soporte de @Embedded para Value Objects

- **Cons:**
  - Mayor complejidad de mapeo Dominio ↔ JPA Entity
  - Posible N+1 queries si no se gestiona bien
  - Las anotaciones JPA en las entidades de infraestructura son válidas pero requieren disciplina
  - Migraciones automáticas (ddl-auto) peligrosas en producción

### Opción B: Spring Data JDBC (enfoque más simple)

ORM más ligero que JPA, sin lazy loading, sin caché de primer nivel, sin sesión. Sigue el enfoque Aggregate de DDD.

- **Pros:**
  - Más simple y predecible que JPA
  - Sin lazy loading — las consultas son explícitas
  - Buen soporte para aggregates DDD
  - Menos magic que Hibernate

- **Cons:**
  - Menos maduro y con menos ejemplos que JPA
  - Manejo manual de relaciones M:N
  - No soporta lazy loading — puede ser ineficiente para relaciones profundas
  - Migrar desde JDBC a otra tecnología puede ser igual de complejo

### Opción C: jOOQ (Query DSL, SQL-first)

Librería que genera código a partir del esquema SQL y permite escribir consultas type-safe en Java.

- **Pros:**
  - Control total sobre SQL generado
  - Type-safe queries
  - No hay ORM magic — lo que ves es lo que ejecuta
  - Excelente para consultas complejas y reportes

- **Cons:**
  - No es ORM: requiere mapeo manual de resultados a objetos
  - Más boilerplate que JPA para CRUD simple
  - Curva de aprendizaje del DSL
  - No se integra tan naturalmente con Spring Data repositories

### Opción D: Flyway para migraciones (combinado con JPA)

Independientemente del ORM, se usa Flyway para migraciones versionadas en lugar de ddl-auto de Hibernate.

- **Pros (combinado con Opción A):**
  - Migraciones explícitas, versionadas y revisables
  - Rollback controlado
  - Integración con pipelines CI/CD
  - Compatible con cualquier ORM

## Decisión

Se elige **Opción A (Spring Data JPA con Hibernate) combinada con Opción D (Flyway para migraciones)**.

**Racional:**

1. **JPA con separación estricta:** Las entidades JPA (`@Entity`) son clases separadas en `infrastructure/adapter/outbound/persistence/`. No se mezclan con los modelos de dominio. Esto cumple con la arquitectura hexagonal.
2. **Flyway sobre ddl-auto:** Las migraciones se gestionan con Flyway, no con `spring.jpa.hibernate.ddl-auto=update`. Esto garantiza que los cambios sean versionados, revisables y aplicados de forma controlada.
3. **PostgreSQL específico:** Se aprovecharán tipos nativos de PostgreSQL donde tenga sentido (JSONB para metadata, arrays para IDs simples).
4. **Mapeadores con MapStruct:** Los adaptadores usarán MapStruct para mapear entre Entidades JPA ↔ Modelos de Dominio.

**Estrategia de esquema relacional:**

```
Tablas del esquema "menu":

categories
├── id (UUID, PK)
├── name (VARCHAR 100, NOT NULL, UNIQUE)
├── description (TEXT)
├── display_order (INTEGER, NOT NULL, DEFAULT 0)
├── active (BOOLEAN, DEFAULT true)
├── created_at (TIMESTAMP, NOT NULL)
└── updated_at (TIMESTAMP, NOT NULL)

menu_items
├── id (UUID, PK)
├── category_id (UUID, FK → categories.id)
├── name (VARCHAR 200, NOT NULL)
├── description (TEXT)
├── price (DECIMAL 10,2, NOT NULL)
├── image_url (VARCHAR 500)
├── preparation_time_minutes (INTEGER)
├── active (BOOLEAN, DEFAULT true)
├── created_at (TIMESTAMP, NOT NULL)
└── updated_at (TIMESTAMP, NOT NULL)
UNIQUE (category_id, name)

menu_item_ingredients
├── menu_item_id (UUID, FK → menu_items.id)
├── ingredient_id (UUID, FK → ingredients.id)
├── quantity (DECIMAL 10,2, NOT NULL)
├── unit (VARCHAR 50)
└── PRIMARY KEY (menu_item_id, ingredient_id)

ingredients
├── id (UUID, PK)
├── name (VARCHAR 200, NOT NULL, UNIQUE)
├── description (TEXT)
├── active (BOOLEAN, DEFAULT true)
├── created_at (TIMESTAMP, NOT NULL)
└── updated_at (TIMESTAMP, NOT NULL)

menus
├── id (UUID, PK)
├── name (VARCHAR 200, NOT NULL)
├── description (TEXT)
├── active (BOOLEAN, DEFAULT true)
├── start_date (DATE)
├── end_date (DATE)
├── created_at (TIMESTAMP, NOT NULL)
└── updated_at (TIMESTAMP, NOT NULL)

menu_sections
├── id (UUID, PK)
├── menu_id (UUID, FK → menus.id)
├── category_id (UUID, FK → categories.id)
├── display_order (INTEGER, NOT NULL, DEFAULT 0)
└── UNIQUE (menu_id, category_id)

modifier_groups
├── id (UUID, PK)
├── name (VARCHAR 200, NOT NULL)
├── description (TEXT)
├── required (BOOLEAN, DEFAULT false)
├── max_selections (INTEGER)
├── active (BOOLEAN, DEFAULT true)
├── created_at (TIMESTAMP, NOT NULL)
└── updated_at (TIMESTAMP, NOT NULL)

modifier_options
├── id (UUID, PK)
├── modifier_group_id (UUID, FK → modifier_groups.id)
├── name (VARCHAR 200, NOT NULL)
├── price_adjustment (DECIMAL 10,2, DEFAULT 0)
├── active (BOOLEAN, DEFAULT true)
├── created_at (TIMESTAMP, NOT NULL)
└── updated_at (TIMESTAMP, NOT NULL)

menu_item_modifier_groups
├── menu_item_id (UUID, FK → menu_items.id)
├── modifier_group_id (UUID, FK → modifier_groups.id)
└── PRIMARY KEY (menu_item_id, modifier_group_id)

allergens
├── id (UUID, PK)
├── name (VARCHAR 100, NOT NULL, UNIQUE)
├── description (TEXT)
├── icon (VARCHAR 50)
└── active (BOOLEAN, DEFAULT true)

menu_item_allergens
├── menu_item_id (UUID, FK → menu_items.id)
├── allergen_id (UUID, FK → allergens.id)
└── PRIMARY KEY (menu_item_id, allergen_id)

nutritional_info
├── id (UUID, PK)
├── menu_item_id (UUID, UNIQUE, FK → menu_items.id)
├── calories (INTEGER)
├── protein_grams (DECIMAL 7,2)
├── carbs_grams (DECIMAL 7,2)
├── fat_grams (DECIMAL 7,2)
├── fiber_grams (DECIMAL 7,2)
├── sodium_mg (INTEGER)
├── created_at (TIMESTAMP, NOT NULL)
└── updated_at (TIMESTAMP, NOT NULL)
```

**Convenciones de naming:**
- Tablas en snake_case, plural (coincide con la entidad principal)
- Columnas en snake_case
- PK: `id` de tipo UUID
- FK: `{entidad}_id` en snake_case
- Timestamps: `created_at`, `updated_at`
- Soft-delete no se implementa por ahora; se usa columna `active` booleana

## Consecuencias

**Positivas:**
- El dominio permanece puro — sin anotaciones JPA
- Las migraciones son explícitas, versionadas y revisables en code review
- PostgreSQL ofrece escalabilidad y tipos avanzados para futuras necesidades (JSONB, PostGIS)
- Flyway se integra naturalmente con Spring Boot y CI/CD
- UUIDs como PK evitan enumeración secuencial y facilitan migraciones distribuidas

**Negativas:**
- Duplicación de modelos: entidad JPA + modelo de dominio + DTOs
- MapStruct necesario para mapear entre capas, añadiendo dependencia
- Los adaptadores JPA deben mapear manualmente Value Objects (ej. `Price` → `BigDecimal`)
- Las consultas complejas pueden requerir JPQL o Specifications

**Mitigaciones:**
- MapStruct elimina el boilerplate de mapeo manual
- JPA Attribute Converters para Value Objects simples
- Specifications + QueryDSL para consultas dinámicas complejas

## Referencias

- Flyway Documentation — https://documentation.red-gate.com/flyway
- Spring Data JPA Reference — https://docs.spring.io/spring-data/jpa/docs/current/reference/html/
- PostgreSQL Documentation — https://www.postgresql.org/docs/
- Vlad Mihalcea, "High-Performance Java Persistence" (2020)
