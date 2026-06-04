# Plan de Implementación — API de Gestión de Menú para Restaurante

> **Basado en:** ADR-001 al ADR-010  
> **Stack:** Java 17+, Spring Boot 3.x, PostgreSQL, Hexagonal Architecture  
> **Build:** Maven  
> **Estado:** Pendiente de inicio

---

## Fases del Plan

| Fase | Nombre | ADRs | Depende de |
|------|--------|------|-----------|
| 0 | Project Scaffolding | ADR-001, ADR-004, ADR-007 | — |
| 1 | Domain Layer | ADR-002 | Fase 0 |
| 2 | Persistence Infrastructure | ADR-003, ADR-005 | Fase 1 |
| 3 | Error Handling & API Layer | ADR-004, ADR-005, ADR-006 | Fase 2 |
| 4 | Application Layer (Use Cases) | ADR-002, ADR-004 | Fase 1, Fase 2 |
| 5 | Security & Authentication | ADR-008 | Fase 3, Fase 4 |
| 6 | Caching Strategy | ADR-009 | Fase 2 |
| 7 | Observability | ADR-010 | Fase 3 |
| 8 | Testing & QA | ADR-007 | Fase 1–7 (incremental) |
| 9 | Documentation & CI/CD | ADR-004 | Fase 3, Fase 8 |

---

## Fase 0: Project Scaffolding

### Contexto
Inicializar el proyecto Spring Boot con Maven, configurar dependencias, establecer la estructura de directorios de la arquitectura hexagonal, y configurar las herramientas de calidad (Checkstyle, SpotBugs, JaCoCo).

### Affected layers
- [x] Project setup / build config
- [ ] Database / Prisma schema
- [ ] Backend domain logic
- [ ] Backend application services
- [ ] Backend API / controllers
- [ ] Frontend features
- [ ] Tests
- [x] Documentation / ADRs

### Tasks
| # | Task | Layer | Complexity | Depends on |
|---|------|-------|------------|------------|
| 0.1 | Generate Spring Boot project with Maven | Build | Low | — |
| 0.2 | Configure pom.xml with all dependencies | Build | Medium | 0.1 |
| 0.3 | Create hexagonal directory structure | Build | Low | 0.1 |
| 0.4 | Configure application.yml (datasource, JPA, Flyway) | Config | Low | 0.1 |
| 0.5 | Configure static analysis tools (Checkstyle, SpotBugs) | Build | Medium | 0.1 |
| 0.6 | Configure JaCoCo with 80% coverage threshold | Build | Low | 0.1 |
| 0.7 | Create main application class and verify context loads | Build | Low | 0.2 |
| 0.8 | Add docker-compose.yml for local PostgreSQL | Infra | Low | 0.1 |

### Task details

#### Task 0.1: Generate Spring Boot project with Maven
**What:** Generate the base project using Spring Initializr or Maven archetype.  
**How:** Use Spring Boot 3.5.x (latest stable), Java 17+, Maven wrapper. Include spring-boot-starter-web, spring-boot-starter-data-jpa, spring-boot-starter-validation, flyway-core, flyway-database-postgresql.  
**Acceptance criteria:**
- [ ] `mvnw clean compile` passes
- [ ] `pom.xml` declares Spring Boot 3.5.x as parent
- [ ] Java 17 source/target configured
- [ ] Maven wrapper (mvnw) present
**Security considerations:** N/A

#### Task 0.2: Configure pom.xml with all dependencies
**What:** Add all required dependencies to pom.xml.  
**How:** Add these dependencies:
- **Core:** spring-boot-starter-web, spring-boot-starter-validation
- **Persistence:** spring-boot-starter-data-jpa, flyway-core, flyway-database-postgresql, postgresql
- **Documentation:** springdoc-openapi-starter-webmvc-ui (v2.8+)
- **Mapping:** mapstruct, mapstruct-processor (annotation processor)
- **Security:** spring-boot-starter-security, oauth2-resource-server, spring-security-oauth2-jose
- **Caching:** spring-boot-starter-cache, caffeine
- **Observability:** spring-boot-starter-actuator, micrometer-registry-prometheus, logstash-logback-encoder
- **Testing:** spring-boot-starter-test, testcontainers (postgresql, junit-jupiter), archunit-junit5, rest-assured
- **Tools:** lombok (optional), spring-boot-devtools (dev only)

**Acceptance criteria:**
- [ ] `mvnw dependency:tree` shows no conflicts
- [ ] MapStruct annotation processor configured under `maven-compiler-plugin`
- [ ] Testcontainers BOM included for version management
- [ ] JaCoCo plugin configured with 80% line coverage rule
**Security considerations:** Pin dependency versions to avoid CVEs; use Spring Boot BOM.

#### Task 0.3: Create hexagonal directory structure
**What:** Create the empty Java package structure matching the hexagonal architecture.  
**How:** Create directories under `src/main/java/com/restaurant/menu/`:
```
domain/
├── model/           # Category, MenuItem, Menu, ModifierGroup, Ingredient, Allergen
├── port/
│   ├── inbound/     # CreateCategoryUseCase, GetMenuItemsUseCase, etc.
│   └── outbound/    # CategoryRepositoryPort, MenuItemRepositoryPort, etc.
└── exception/       # DomainException, MenuItemNotFoundException, etc.

application/
├── service/         # CreateCategoryService, GetMenuItemsService, etc.
└── dto/             # CreateMenuItemCommand, MenuItemResult, etc.

infrastructure/
├── adapter/
│   ├── inbound/web/           # CategoryController, MenuItemController, etc.
│   │   └── dto/               # CreateCategoryRequest, MenuItemResponse, etc.
│   └── outbound/persistence/  # CategoryJpaEntity, MenuItemJpaRepository, etc.
│       └── mapper/            # CategoryPersistenceMapper, etc.
├── config/                    # SecurityConfig, CacheConfig, OpenApiConfig, etc.
├── exception/                 # GlobalExceptionHandler
└── filter/                    # CorrelationIdFilter, etc.

shared/                        # Utility classes, constants
```
Also create `src/test/java/com/restaurant/menu/` with matching structure.  
**Acceptance criteria:**
- [ ] All package directories exist
- [ ] Each directory has a `.gitkeep` placeholder (optional)
- [ ] Package names follow com.restaurant.menu.{layer}.{sub-layer}
**Security considerations:** N/A

#### Task 0.4: Configure application.yml
**What:** Create application.yml with datasource, JPA, Flyway, and SpringDoc config.  
**How:**
```yaml
spring:
  application:
    name: menu-api
  datasource:
    url: jdbc:postgresql://localhost:5432/menu_db
    username: menu_user
    password: menu_pass
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: validate  # Flyway manages schema
    show-sql: false
    open-in-view: false
    properties:
      hibernate:
        format_sql: true
  flyway:
    enabled: true
    locations: classpath:db/migration
    schemas: menu

springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
    operations-sorter: method
    tags-sorter: alpha

server:
  port: 8080

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
```
Also create `application-dev.yml` and `application-prod.yml` profiles.  
**Acceptance criteria:**
- [ ] Application starts with `spring.profiles.active=dev`
- [ ] Flyway migrations location configured
- [ ] ddl-auto set to 'validate' (not 'update')
**Security considerations:** Database credentials should be externalized via env vars in production.

#### Task 0.5: Configure static analysis tools
**What:** Add Checkstyle (Google style or custom), SpotBugs, and ErrorProne configuration.  
**How:** Add plugins to pom.xml, create `checkstyle.xml` config file.  
**Acceptance criteria:**
- [ ] `mvnw verify` runs Checkstyle checks
- [ ] SpotBugs runs during verify phase
- [ ] Code style violations fail the build (or warn in dev)
**Security considerations:** SpotBugs includes security detectors (FindSecBugs).

#### Task 0.6: Configure JaCoCo
**What:** Add JaCoCo with 80% line coverage rules and exclusions for generated code.  
**How:** Configure in pom.xml with exclusions for:
- `**/infrastructure/config/**`
- `**/infrastructure/adapter/inbound/web/dto/**`
- `**/infrastructure/adapter/outbound/persistence/**/*JpaEntity.*`
- `**/*MapperImpl.*` (MapStruct generated)
**Acceptance criteria:**
- [ ] `mvnw verify` generates coverage report
- [ ] Build fails if coverage below 80%
- [ ] HTML report in `target/site/jacoco/index.html`
**Security considerations:** N/A

#### Task 0.7: Create main application class
**What:** Create MenuApiApplication.java with Spring Boot annotation.  
**How:** Standard `@SpringBootApplication` class in `com.restaurant.menu`. Add a simple test `MenuApiApplicationTests.java` that verifies context loads.  
**Acceptance criteria:**
- [ ] `mvnw test` passes (context loads)
- [ ] Application starts on port 8080
**Security considerations:** N/A

#### Task 0.8: Add docker-compose.yml for local PostgreSQL
**What:** Create docker-compose.yml at project root for local development.  
**How:**
```yaml
version: '3.8'
services:
  postgres:
    image: postgres:16-alpine
    container_name: menu-db
    environment:
      POSTGRES_DB: menu_db
      POSTGRES_USER: menu_user
      POSTGRES_PASSWORD: menu_pass
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data

volumes:
  pgdata:
```
**Acceptance criteria:**
- [ ] `docker compose up -d` starts PostgreSQL 16
- [ ] Application connects successfully to local PostgreSQL
**Security considerations:** Default credentials only for local dev; use env vars for production.

---

## Fase 1: Domain Layer

### Contexto
Implementar el núcleo puro del dominio: entidades, value objects, puertos (interfaces) y excepciones de dominio. Esta capa NO debe tener ninguna dependencia de Spring, JPA ni ninguna infraestructura.

### Affected layers
- [ ] Database / Prisma schema
- [x] Backend domain logic
- [ ] Backend application services
- [ ] Backend API / controllers
- [ ] Frontend features
- [x] Tests (domain unit tests)
- [ ] Documentation / ADRs

### Tasks
| # | Task | Layer | Complexity | Depends on |
|---|------|-------|------------|------------|
| 1.1 | Implement Value Objects (Price, IDs, ValidityPeriod, etc.) | Domain | Low | 0.3 |
| 1.2 | Implement Category aggregate | Domain | Low | 1.1 |
| 1.3 | Implement Ingredient aggregate | Domain | Low | 1.1 |
| 1.4 | Implement MenuItem aggregate (core) | Domain | Medium | 1.1, 1.2, 1.3 |
| 1.5 | Implement ModifierGroup aggregate (with ModifierOption) | Domain | Medium | 1.1, 1.4 |
| 1.6 | Implement Menu aggregate (with MenuSection) | Domain | Medium | 1.2, 1.4 |
| 1.7 | Implement Allergen aggregate | Domain | Low | 1.1 |
| 1.8 | Implement NutritionalInfo value object | Domain | Low | 1.1 |
| 1.9 | Implement domain exceptions | Domain | Low | — |
| 1.10 | Define inbound ports (use case interfaces) | Domain | Medium | 1.2–1.9 |
| 1.11 | Define outbound ports (repository interfaces) | Domain | Medium | 1.2–1.9 |
| 1.12 | Write domain unit tests | Tests | Medium | 1.1–1.9 |

### Task details

#### Task 1.1: Implement Value Objects
**What:** Create immutable value objects as Java records.  
**How:** In `domain/model/`:
- `CategoryId(UUID value)` — record
- `MenuItemId(UUID value)` — record
- `MenuId(UUID value)` — record
- `ModifierGroupId(UUID value)` — record
- `IngredientId(UUID value)` — record
- `AllergenId(UUID value)` — record
- `Price(BigDecimal value)` — record with validation: value > 0, scale = 2. Throws `InvalidPriceException` if invalid.
- `ValidityPeriod(LocalDate start, LocalDate end)` — record with validation: end >= start. Throws `InvalidDateRangeException`.
- `PreparationTime(int minutes)` — record with validation: minutes > 0.
- `ImageUrl(String url)` — record with basic URL validation.
- `NutritionalInfo(Integer calories, BigDecimal proteinG, BigDecimal carbsG, BigDecimal fatG, BigDecimal fiberG, Integer sodiumMg)` — record.

**Acceptance criteria:**
- [ ] Price cannot be created with negative or zero value
- [ ] ValidityPeriod cannot have end before start
- [ ] PreparationTime cannot be negative
- [ ] All IDs are created with valid UUIDs
- [ ] NutritionalInfo accepts null values (optional fields)
- [ ] All value objects override equals/hashCode based on fields (records do this automatically)
**Security considerations:** N/A — pure domain, no I/O.

#### Task 1.2: Implement Category aggregate
**What:** Create Category domain entity.  
**How:** In `domain/model/Category.java`:
```java
public class Category {
    private final CategoryId id;
    private String name;
    private String description;
    private int displayOrder;
    private boolean active;

    // Constructor, getters, business methods
    // Business rules:
    // - name cannot be blank
    // - displayOrder must be >= 0
    // - activate() / deactivate() toggle the active flag
}
```
**Acceptance criteria:**
- [ ] Category created with required fields (id, name)
- [ ] Blank name throws IllegalArgumentException
- [ ] displayOrder must be >= 0
- [ ] activate() sets active=true
- [ ] deactivate() sets active=false
**Security considerations:** N/A

#### Task 1.3: Implement Ingredient aggregate
**What:** Create Ingredient domain entity.  
**How:** In `domain/model/Ingredient.java`. Fields: `IngredientId id`, `name`, `description`, `active`, `unit`.  
**Acceptance criteria:**
- [ ] Ingredient created with id and name
- [ ] Blank name rejected
- [ ] Unit is optional (e.g., "grams", "units")
**Security considerations:** N/A

#### Task 1.4: Implement MenuItem aggregate (core)
**What:** Create MenuItem domain entity — the core of the system.  
**How:** In `domain/model/MenuItem.java`:
```java
public class MenuItem {
    private final MenuItemId id;
    private String name;
    private String description;
    private Price price;
    private CategoryId categoryId;
    private PreparationTime preparationTime;
    private ImageUrl imageUrl;
    private boolean active;
    private List<Ingredient> ingredients;          // M:N with quantity
    private List<ModifierGroup> modifierGroups;    // M:N
    private List<Allergen> allergens;              // M:N
    private NutritionalInfo nutritionalInfo;       // 1:1

    // Business methods:
    // - calculateFinalPrice(): Price — base price + mandatory modifiers
    // - addIngredient(Ingredient, quantity)
    // - removeIngredient(IngredientId)
    // - assignModifierGroup(ModifierGroup)
    // - removeModifierGroup(ModifierGroupId)
    // - assignNutritionalInfo(NutritionalInfo)
    // - activate() / deactivate()
}
```
**Acceptance criteria:**
- [ ] MenuItem created with required fields
- [ ] `calculateFinalPrice()` returns base price + sum of mandatory modifier options
- [ ] Cannot add same ingredient twice (updates quantity instead)
- [ ] Cannot assign duplicate modifier group
- [ ] Name cannot be blank
- [ ] CategoryId must be provided
**Security considerations:** N/A

#### Task 1.5: Implement ModifierGroup aggregate
**What:** Create ModifierGroup entity containing ModifierOption children.  
**How:**
- `ModifierGroup(ModifierGroupId id, String name, String description, boolean required, int maxSelections, boolean active, List<ModifierOption> options)`
- `ModifierOption(ModifierOptionId id, String name, Price priceAdjustment, boolean active)` — can be a record or inner class.
- Business rules: maxSelections <= options.size(), required groups must have at least one option.

**Acceptance criteria:**
- [ ] ModifierGroup with required=true must have at least one option
- [ ] maxSelections > options.size() throws exception
- [ ] `calculatePriceImpact()` returns sum of all selected options' priceAdjustments
**Security considerations:** N/A

#### Task 1.6: Implement Menu aggregate
**What:** Create Menu entity with MenuSection children.  
**How:**
- `Menu(MenuId id, String name, String description, boolean active, ValidityPeriod validityPeriod, List<MenuSection> sections)`
- `MenuSection(MenuSectionId id, MenuId menuId, CategoryId categoryId, int displayOrder)` — links Menu → Category.
- Business rules: validity period must be valid, cannot add same category twice to a menu.

**Acceptance criteria:**
- [ ] Menu created with validity period validation
- [ ] Cannot add duplicate category to a menu
- [ ] `isActive()` checks active flag AND current date within validity period
**Security considerations:** N/A

#### Task 1.7: Implement Allergen aggregate
**What:** Create Allergen entity.  
**How:** `Allergen(AllergenId id, String name, String description, String icon)`. Simple entity, primarily used as a reference list.  
**Acceptance criteria:**
- [ ] Allergen created with id and name
- [ ] name cannot be blank
**Security considerations:** N/A

#### Task 1.8: Implement NutritionalInfo value object
**What:** Already created as part of Task 1.1. Confirm it's in the right place and complete.  
**Acceptance criteria:**
- [ ] NutritionalInfo is a record (immutable)
- [ ] Works with MenuItem's `assignNutritionalInfo()` method
**Security considerations:** N/A

#### Task 1.9: Implement domain exceptions
**What:** Create exception hierarchy in `domain/exception/`.  
**How:**
```java
public abstract class DomainException extends RuntimeException {
    private final String code;
    private final HttpStatus status;  // from org.springframework.http
    // constructor, getters
}

// One exception per error code:
public class MenuItemNotFoundException extends DomainException { ... }
public class CategoryNotFoundException extends DomainException { ... }
public class CategoryNameDuplicatedException extends DomainException { ... }
public class MenuItemNameDuplicatedException extends DomainException { ... }
public class InvalidPriceException extends DomainException { ... }
public class InvalidDateRangeException extends DomainException { ... }
public class MenuNotFoundException extends DomainException { ... }
public class ModifierGroupNotFoundException extends DomainException { ... }
public class IngredientNotFoundException extends DomainException { ... }
public class AllergenNotFoundException extends DomainException { ... }
public class MaxSelectionsExceededException extends DomainException { ... }
```
**Note:** DomainException references HttpStatus from Spring. This is a pragmatic compromise — the domain still imports from Spring for this one class. An alternative is to use integer status codes and map in the adapter. Decision: accept the minor coupling for simplicity.  
**Acceptance criteria:**
- [ ] Each exception has unique error code matching ADR-006 catalog
- [ ] DomainException carries status, code, and message
- [ ] All domain exceptions extend DomainException
**Security considerations:** Domain exceptions should NOT expose internal details. Messages should be user-friendly.

#### Task 1.10: Define inbound ports (use case interfaces)
**What:** Create interfaces in `domain/port/inbound/` that define what the application can do.  
**How:** One interface per use case or group of related operations:
```java
public interface CreateCategoryUseCase {
    CategoryResult execute(CreateCategoryCommand command);
}

public interface GetCategoriesUseCase {
    List<CategoryResult> execute();
}

public interface GetCategoryByIdUseCase {
    CategoryResult execute(CategoryId id);
}

public interface UpdateCategoryUseCase {
    CategoryResult execute(CategoryId id, UpdateCategoryCommand command);
}

public interface DeleteCategoryUseCase {
    void execute(CategoryId id);
}
```
Repeat for: MenuItem, Menu, ModifierGroup, Ingredient, Allergen. Use case granularity: one interface per operation (CQRS-light) OR grouped by resource. Decision: grouped by resource for simplicity (e.g., `CategoryUseCases` with all category operations) unless operations grow complex.  
**Acceptance criteria:**
- [ ] Each resource has corresponding use case interface(s)
- [ ] Methods accept typed command/query objects, not primitives
- [ ] Return typed result objects
- [ ] No framework annotations on interfaces
**Security considerations:** N/A

#### Task 1.11: Define outbound ports (repository interfaces)
**What:** Create interfaces in `domain/port/outbound/` for persistence.  
**How:**
```java
public interface CategoryRepositoryPort {
    Optional<Category> findById(CategoryId id);
    List<Category> findAll();
    Category save(Category category);
    void deleteById(CategoryId id);
    boolean existsByName(String name);
}

public interface MenuItemRepositoryPort {
    Optional<MenuItem> findById(MenuItemId id);
    List<MenuItem> findAll(Pageable pageable);  // Spring Data Pageable — acceptable coupling
    List<MenuItem> findByCategoryId(CategoryId categoryId);
    MenuItem save(MenuItem menuItem);
    void deleteById(MenuItemId id);
    boolean existsByNameAndCategory(String name, CategoryId categoryId);
}
```
Repeat for: Menu, ModifierGroup, Ingredient, Allergen.  
**Acceptance criteria:**
- [ ] Each aggregate has a corresponding repository port
- [ ] Methods use domain types (CategoryId, Price, etc.) not primitives
- [ ] Save returns the persisted entity
- [ ] Find methods return Optional for single results
**Security considerations:** N/A

#### Task 1.12: Write domain unit tests
**What:** Create JUnit 5 tests for all domain entities and value objects.  
**How:** For each domain class, create a corresponding test:
- `PriceTest` — validates positive value, scale
- `CategoryTest` — validates creation, name, displayOrder
- `MenuItemTest` — validates creation, price calculation with modifiers, ingredient management
- `MenuTest` — validates validity period, section management
- `ModifierGroupTest` — validates max selections, required option count
- `DomainExceptionTest` — validates exception hierarchy
- etc.

**Testing approach:** Pure JUnit 5 + AssertJ. No Spring, no mocking. Instantiate domain objects directly with test data.  
**Acceptance criteria:**
- [ ] 100% coverage of business rules in domain entities
- [ ] Tests run in < 2 seconds total
- [ ] No Spring context loaded
- [ ] AssertJ used for fluent assertions
- [ ] Edge cases tested (nulls, empty strings, boundary values)
**Security considerations:** N/A

---

## Fase 2: Persistence Infrastructure

### Contexto
Implementar la capa de persistencia: migraciones Flyway con el esquema PostgreSQL, entidades JPA (separadas del dominio), repositorios Spring Data, adapters de persistencia con MapStruct, y JPA Attribute Converters para Value Objects.

### Affected layers
- [x] Database / Prisma schema
- [x] Backend domain logic
- [ ] Backend application services
- [ ] Backend API / controllers
- [ ] Frontend features
- [x] Tests (persistence integration tests)
- [ ] Documentation / ADRs

### Tasks
| # | Task | Layer | Complexity | Depends on |
|---|------|-------|------------|------------|
| 2.1 | Create Flyway migration V1: categories table | DB | Low | 0.4 |
| 2.2 | Create Flyway migration V2: menu_items table | DB | Low | 2.1 |
| 2.3 | Create Flyway migration V3: ingredients + menu_item_ingredients | DB | Medium | 2.2 |
| 2.4 | Create Flyway migration V4: modifier_groups + modifier_options | DB | Medium | 2.2 |
| 2.5 | Create Flyway migration V5: menu_item_modifier_groups | DB | Low | 2.4 |
| 2.6 | Create Flyway migration V6: menus + menu_sections | DB | Medium | 2.1 |
| 2.7 | Create Flyway migration V7: allergens + menu_item_allergens | DB | Low | 2.2 |
| 2.8 | Create Flyway migration V8: nutritional_info | DB | Low | 2.2 |
| 2.9 | Implement JPA entity classes (all tables) | Backend | Medium | 2.1–2.8 |
| 2.10 | Implement Spring Data JPA repositories | Backend | Low | 2.9 |
| 2.11 | Implement JPA Attribute Converters for Value Objects | Backend | Medium | 2.9, 1.1 |
| 2.12 | Implement PersistenceAdapter classes with MapStruct mappers | Backend | High | 2.10, 2.11, 1.11 |
| 2.13 | Write persistence integration tests with Testcontainers | Tests | Medium | 2.12 |

### Task details

#### Task 2.1–2.8: Flyway Migrations
**What:** Create SQL migration files in `src/main/resources/db/migration/`.  
**How:** Files named `V1__create_categories.sql`, `V2__create_menu_items.sql`, etc. Each file contains `CREATE TABLE` statements following the schema from ADR-003. Include indexes for foreign keys and active columns.  
**Schema reference:** See ADR-003 for full column definitions.  

**Naming convention:** snake_case for all identifiers. PK = UUID. FK = `{entity}_id`. Timestamps: `created_at`, `updated_at` with `NOT NULL DEFAULT NOW()`.  

**Indexes to create:**
- `categories`: idx_categories_active (active)
- `menu_items`: idx_menu_items_category (category_id), idx_menu_items_active (active)
- `menu_item_ingredients`: PK composite index (auto)
- `menus`: idx_menus_active (active)
- `modifier_groups`: idx_modifier_groups_active (active)
- etc.

**Acceptance criteria:**
- [ ] `mvnw flyway:migrate` applies all migrations
- [ ] All tables created with correct columns, types, and constraints
- [ ] Foreign keys and unique constraints in place
- [ ] `mvnw flyway:info` shows all migrations as "Success"
- [ ] Migration is idempotent (can run multiple times on clean DB)
**Security considerations:** Use parameterized SQL in Flyway scripts (plain SQL, no dynamic values). Schema is isolated (schema: `menu`).

#### Task 2.9: Implement JPA entity classes
**What:** Create `@Entity` classes in `infrastructure/adapter/outbound/persistence/`.  
**How:** One JPA entity per database table:
- `CategoryJpaEntity` — `@Table(name = "categories", schema = "menu")`
- `MenuItemJpaEntity` — with `@ManyToOne` for category, `@ManyToMany` for ingredients and allergens
- `IngredientJpaEntity`
- `ModifierGroupJpaEntity` — with `@OneToMany` for modifier_options
- `ModifierOptionJpaEntity`
- `MenuJpaEntity` — with `@OneToMany` for menu_sections
- `MenuSectionJpaEntity`
- `AllergenJpaEntity`
- `NutritionalInfoJpaEntity` — with `@OneToOne` for menu_item
- Join tables: `MenuItemIngredientJpaEntity`, `MenuItemModifierGroupJpaEntity`, `MenuItemAllergenJpaEntity`

**Important:** These are JPA entities — they have `@Entity`, `@Id`, `@Column`, etc. They are NOT domain objects. They live ONLY in the persistence adapter package.  
**Acceptance criteria:**
- [ ] All table mappings correct (names, schema)
- [ ] Relationships mapped with LAZY loading (except where EAGER is justified)
- [ ] `@CreatedDate` / `@UpdatedDate` via `@EntityListeners(AuditingEntityListener.class)` or manual timestamps
- [ ] UUIDs mapped with `@Type(UuidCharType.class)` or similar for PostgreSQL UUID column type
- [ ] No business logic in entity classes — pure data holders
**Security considerations:** N/A

#### Task 2.10: Implement Spring Data JPA repositories
**What:** Create `@Repository` interfaces extending `JpaRepository` in the persistence package.  
**How:**
```java
public interface CategoryJpaRepository extends JpaRepository<CategoryJpaEntity, UUID> {
    boolean existsByName(String name);
    List<CategoryJpaEntity> findByActiveTrueOrderByDisplayOrderAsc();
}

public interface MenuItemJpaRepository extends JpaRepository<MenuItemJpaEntity, UUID> {
    List<MenuItemJpaEntity> findByCategoryId(UUID categoryId);
    boolean existsByNameAndCategoryId(String name, UUID categoryId);
    @Query("SELECT m FROM MenuItemJpaEntity m JOIN FETCH m.category WHERE m.active = true")
    List<MenuItemJpaEntity> findAllActiveWithCategory();
}
```
**Acceptance criteria:**
- [ ] All standard CRUD operations work
- [ ] Custom queries defined for business needs
- [ ] `JOIN FETCH` used to avoid N+1 in list queries
- [ ] `@EntityGraph` as alternative to JOIN FETCH where appropriate
**Security considerations:** N/A — repositories don't handle authentication.

#### Task 2.11: Implement JPA Attribute Converters
**What:** Create `@Converter` classes for Value Object mapping between domain and JPA.  
**How:** In `infrastructure/adapter/outbound/persistence/`:
```java
@Converter(autoApply = true)
public class PriceConverter implements AttributeConverter<Price, BigDecimal> {
    @Override
    public BigDecimal convertToDatabaseColumn(Price price) {
        return price != null ? price.value() : null;
    }

    @Override
    public Price convertToEntityAttribute(BigDecimal value) {
        return value != null ? new Price(value) : null;
    }
}
// Repeat for: PreparationTime, ImageUrl, ValidityPeriod (maps to two columns — needs @Embedded instead)
```
**Note:** Value Objects that map to a single column use `@Converter`. `ValidityPeriod` (start+end dates) uses `@Embeddable` in the JPA entity.  
**Acceptance criteria:**
- [ ] Price ↔ BigDecimal conversion works
- [ ] PreparationTime ↔ Integer conversion works
- [ ] ImageUrl ↔ String conversion works
- [ ] ValidityPeriod mapped as @Embedded with start_date/end_date columns
- [ ] Null handling: converter returns null for null inputs
**Security considerations:** N/A

#### Task 2.12: Implement PersistenceAdapter classes with MapStruct
**What:** Create the adapter classes that implement the outbound ports (domain interfaces) using JPA repositories and MapStruct mappers.  
**How:** For each aggregate:
1. Create a MapStruct mapper interface (e.g., `CategoryPersistenceMapper`):
```java
@Mapper(componentModel = "spring", uses = {PriceConverter.class, UUIDMapper.class})
public interface CategoryPersistenceMapper {
    CategoryJpaEntity toJpaEntity(Category domain);
    Category toDomain(CategoryJpaEntity jpaEntity);
}
```

2. Create the adapter class:
```java
@Component
@RequiredArgsConstructor
public class CategoryPersistenceAdapter implements CategoryRepositoryPort {
    private final CategoryJpaRepository jpaRepository;
    private final CategoryPersistenceMapper mapper;

    @Override
    public Optional<Category> findById(CategoryId id) {
        return jpaRepository.findById(id.value())
            .map(mapper::toDomain);
    }

    @Override
    public Category save(Category category) {
        CategoryJpaEntity entity = mapper.toJpaEntity(category);
        CategoryJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    // ... other methods
}
```

Repeat for: MenuItemPersistenceAdapter, MenuPersistenceAdapter, ModifierGroupPersistenceAdapter, IngredientPersistenceAdapter, AllergenPersistenceAdapter.  
**Acceptance criteria:**
- [ ] Each outbound port has a corresponding adapter implementation
- [ ] MapStruct mappers correctly map all fields (verified by test)
- [ ] Adapter methods delegate to JPA repository and map results
- [ ] Transactional behavior correct (read-only for queries, read-write for mutations)
- [ ] `@Transactional(readOnly = true)` on query methods
- [ ] Batch saves use `saveAll()` for performance
**Security considerations:** Ensure no SQL injection — all queries go through Spring Data (parameterized).

#### Task 2.13: Write persistence integration tests
**What:** Create integration tests for persistence adapters using Testcontainers.  
**How:** Use `@DataJpaTest` + `@Testcontainers`:
```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class CategoryPersistenceAdapterTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("test-menu")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired private CategoryJpaRepository jpaRepository;
    private CategoryPersistenceMapper mapper = Mappers.getMapper(CategoryPersistenceMapper.class);
    private CategoryPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new CategoryPersistenceAdapter(jpaRepository, mapper);
    }

    @Test
    void shouldSaveAndFindCategory() {
        Category category = new Category(new CategoryId(UUID.randomUUID()), "Entradas", "Appetizers", 1, true);
        Category saved = adapter.save(category);
        assertThat(saved.id()).isNotNull();

        Optional<Category> found = adapter.findById(saved.id());
        assertThat(found).isPresent();
        assertThat(found.get().name()).isEqualTo("Entradas");
    }
}
```
**Acceptance criteria:**
- [ ] Testcontainers starts PostgreSQL automatically
- [ ] All CRUD operations tested for each adapter
- [ ] Edge cases: duplicate names, null fields, empty lists
- [ ] Verify mapper mappings by round-tripping through DB
- [ ] Tests clean up after themselves (transactional rollback)
- [ ] Flyway migrations run automatically on test DB
**Security considerations:** Test DB runs in disposable container — no security concern but must not use production credentials.

---

## Fase 3: Error Handling & API Layer

### Contexto
Implementar el manejador global de excepciones (RFC 7807 Problem Details), la capa de API REST (controladores, DTOs), y la documentación SpringDoc OpenAPI.

### Affected layers
- [ ] Database / Prisma schema
- [ ] Backend domain logic
- [ ] Backend application services
- [x] Backend API / controllers
- [ ] Frontend features
- [x] Tests (controller integration tests)
- [ ] Documentation / ADRs

### Tasks
| # | Task | Layer | Complexity | Depends on |
|---|------|-------|------------|------------|
| 3.1 | Implement GlobalExceptionHandler | Backend | Medium | 1.9, 0.2 |
| 3.2 | Implement API response wrapper (ResponseEnvelope) | Backend | Low | — |
| 3.3 | Implement API DTOs (request/response records) | Backend | Low | 1.1, 1.2–1.8 |
| 3.4 | Implement API mappers (MapStruct for DTO ↔ Command) | Backend | Medium | 3.3, 1.10–1.11 |
| 3.5 | Implement CategoryController | Backend | Medium | 3.2, 3.4 |
| 3.6 | Implement MenuItemController | Backend | Medium | 3.2, 3.4 |
| 3.7 | Implement MenuController | Backend | Medium | 3.2, 3.4 |
| 3.8 | Implement ModifierGroupController | Backend | Medium | 3.2, 3.4 |
| 3.9 | Implement IngredientController + AllergenController | Backend | Low | 3.2, 3.4 |
| 3.10 | Configure SpringDoc OpenAPI with security scheme | Config | Low | 0.2, 3.5–3.9 |
| 3.11 | Write controller integration tests (@WebMvcTest) | Tests | Medium | 3.5–3.9 |

### Task details

#### Task 3.1: Implement GlobalExceptionHandler
**What:** Create `@RestControllerAdvice` class in `infrastructure/exception/`.  
**How:** Follow ADR-006 specification:
```java
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // Domain exceptions
    @ExceptionHandler(DomainException.class)
    public ProblemDetail handleDomainException(DomainException ex, WebRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(ex.getStatus(), ex.getMessage());
        problem.setTitle(ex.getCode());
        problem.setProperty("code", ex.getCode());
        problem.setProperty("timestamp", Instant.now());
        problem.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
        return problem;
    }

    // Validation errors
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNPROCESSABLE_ENTITY,
            "The request contains invalid fields");
        problem.setTitle("VALIDATION_ERROR");
        problem.setProperty("code", "VALIDATION_ERROR");
        problem.setProperty("timestamp", Instant.now());

        List<Map<String, Object>> errors = ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> Map.of(
                "field", fe.getField(),
                "message", fe.getDefaultMessage(),
                "rejectedValue", fe.getRejectedValue()))
            .toList();
        problem.setProperty("errors", errors);
        return ResponseEntity.unprocessableEntity().body(problem);
    }

    // Constraint violations (for @PathVariable, @RequestParam)
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNPROCESSABLE_ENTITY, "Constraint violation");
        problem.setTitle("VALIDATION_ERROR");
        problem.setProperty("code", "VALIDATION_ERROR");
        problem.setProperty("timestamp", Instant.now());
        // Extract violations...
        return problem;
    }

    // HTTP message not readable (malformed JSON)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadable() {
        return ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, "Malformed request body");
    }

    // Generic fallback (500) — no stack trace exposed
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        problem.setTitle("INTERNAL_ERROR");
        problem.setProperty("code", "INTERNAL_ERROR");
        problem.setProperty("timestamp", Instant.now());
        // Log the full exception internally
        log.error("Unexpected error", ex);
        return problem;
    }
}
```
**Acceptance criteria:**
- [ ] Domain exceptions return appropriate HTTP status and code
- [ ] Validation errors return 422 with field-level details
- [ ] Malformed JSON returns 400
- [ ] 500 errors don't expose stack trace in response body
- [ ] All responses follow RFC 7807 Problem Detail format
- [ ] Unknown paths return 404 (Spring Boot default)
**Security considerations:** Stack traces NEVER exposed in response body. Log full exception internally with correlation ID.

#### Task 3.2: Implement API response wrapper
**What:** Create a ResponseEnvelope class that wraps all API responses.  
**How:** Follow ADR-004 format:
```java
public record ResponseEnvelope<T>(
    T data,
    Map<String, Object> metadata
) {
    public static <T> ResponseEnvelope<T> of(T data) {
        return new ResponseEnvelope<>(data, Map.of(
            "timestamp", Instant.now().toString(),
            "version", "1.0"
        ));
    }

    // Paginated version
    public record Paginated<T>(
        List<T> data,
        PaginationMetadata pagination
    ) {}

    public record PaginationMetadata(
        int page, int size, long totalElements,
        int totalPages, boolean sorted
    ) {}
}
```
Optional: implement via `ResponseBodyAdvice` interface to automatically wrap all controller responses. Decision: manual wrapping in controllers for explicitness, or ResponseBodyAdvice for DRY. Recommended: use ResponseBodyAdvice to avoid forgetting to wrap.  
**Acceptance criteria:**
- [ ] All 200-level responses wrapped in `{ data, metadata }`
- [ ] Paginated responses include `{ data, pagination }`
- [ ] Empty responses (204) NOT wrapped
**Security considerations:** N/A

#### Task 3.3: Implement API DTOs
**What:** Create request and response DTOs as Java records in `infrastructure/adapter/inbound/web/dto/`.  
**How:** One record per request/response per endpoint:
```java
// ==== CATEGORY ====
public record CreateCategoryRequest(
    @NotBlank String name,
    String description,
    @Min(0) int displayOrder,
    boolean active
) {}

public record UpdateCategoryRequest(
    @NotBlank String name,
    String description,
    @Min(0) int displayOrder
) {}

public record UpdateCategoryStatusRequest(
    boolean active
) {}

public record CategoryResponse(
    UUID id,
    String name,
    String description,
    int displayOrder,
    boolean active,
    Instant createdAt,
    Instant updatedAt
) {}

// ==== MENU ITEM ====
public record CreateMenuItemRequest(
    @NotBlank String name,
    String description,
    @NotNull @Positive BigDecimal price,
    @NotNull UUID categoryId,
    @Min(1) Integer preparationTimeMinutes,
    String imageUrl
) {}

public record MenuItemResponse(
    UUID id,
    String name,
    String description,
    BigDecimal price,
    UUID categoryId,
    String categoryName,
    boolean active,
    int preparationTimeMinutes,
    String imageUrl,
    Instant createdAt,
    Instant updatedAt
) {}

// ... repeat for Menu, ModifierGroup, Ingredient, Allergen, NutritionalInfo
```
**Acceptance criteria:**
- [ ] All request DTOs have Jakarta Bean Validation annotations (@NotBlank, @NotNull, @Positive, @Size, etc.)
- [ ] Response DTOs include all fields consumers need
- [ ] DTOs are records (immutable)
- [ ] snake_case matches JSON field naming (Java records use camelCase by default; configure Jackson to use snake_case globally or use @JsonProperty)
**Security considerations:** Request DTOs must validate input — never trust client data.

#### Task 3.4: Implement API mappers (MapStruct)
**What:** Create MapStruct mappers to convert between API DTOs and application commands/results.  
**How:** In `infrastructure/adapter/inbound/web/`:
```java
@Mapper(componentModel = "spring")
public interface CategoryApiMapper {
    CreateCategoryCommand toCommand(CreateCategoryRequest request);
    UpdateCategoryCommand toCommand(UpdateCategoryRequest request);
    CategoryResponse toResponse(CategoryResult result);

    List<CategoryResponse> toResponseList(List<CategoryResult> results);
}
```
Also create a shared config for Jackson (snake_case):
```java
@Configuration
public class JacksonConfig {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}
```
**Acceptance criteria:**
- [ ] All API mappers compile with MapStruct
- [ ] snake_case JSON serialization working globally
- [ ] Date/time serialized as ISO 8601 strings
- [ ] Null fields omitted from JSON or included as null (decide: include with `@JsonInclude(NON_NULL)` for responses)
**Security considerations:** N/A

#### Task 3.5–3.9: Implement Controllers
**What:** Create REST controllers for each resource.  
**How:** For each resource, create a `@RestController` class:

```java
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Manage food categories")
public class CategoryController {

    private final GetCategoriesUseCase getCategoriesUseCase;
    private final GetCategoryByIdUseCase getCategoryByIdUseCase;
    private final CreateCategoryUseCase createCategoryUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;
    private final CategoryApiMapper mapper;

    @GetMapping
    @Operation(summary = "List all categories", description = "Returns paginated list of categories")
    public ResponseEntity<ResponseEnvelope<List<CategoryResponse>>> getAll(
            @PageableDefault(size = 20, sort = "displayOrder") Pageable pageable) {
        // Implementation delegates to use case
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseEnvelope<CategoryResponse>> getById(@PathVariable UUID id) {
        // ...
    }

    @PostMapping
    public ResponseEntity<ResponseEnvelope<CategoryResponse>> create(
            @RequestBody @Valid CreateCategoryRequest request) {
        // ...
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseEnvelope<CategoryResponse>> update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateCategoryRequest request) {
        // ...
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ResponseEnvelope<CategoryResponse>> updateStatus(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateCategoryStatusRequest request) {
        // ...
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteCategoryUseCase.execute(new CategoryId(id));
        return ResponseEntity.noContent().build();
    }
}
```

Repeat for: MenuItemController, MenuController, ModifierGroupController, IngredientController, AllergenController. MenuItemController handles sub-resources:
- `GET /api/v1/menu-items/{id}/ingredients`
- `POST /api/v1/menu-items/{id}/ingredients`
- `DELETE /api/v1/menu-items/{id}/ingredients/{ingredientId}`
- `GET /api/v1/menu-items/{id}/modifier-groups`
- `POST /api/v1/menu-items/{id}/modifier-groups`
- `DELETE /api/v1/menu-items/{id}/modifier-groups/{groupId}`
- `GET /api/v1/menu-items/{id}/allergens`
- `GET /api/v1/menu-items/{id}/nutritional-info`
- `PUT /api/v1/menu-items/{id}/nutritional-info`

**Note:** Controllers should be thin — they only:
1. Validate input (via @Valid)
2. Map request DTO to command
3. Call use case
4. Map result to response DTO
5. Return ResponseEntity with appropriate status

**Acceptance criteria:**
- [ ] All endpoints from ADR-004 are implemented
- [ ] Input validation works (bad requests return 422 with details)
- [ ] Responses wrapped in ResponseEnvelope
- [ ] Pagination works on list endpoints
- [ ] HTTP status codes follow conventions (201 for POST, 204 for DELETE, etc.)
- [ ] @Operation and @ApiResponse annotations present for Swagger documentation
**Security considerations:** Input validation is the first line of defense. All endpoints validate input. Sub-resource endpoints validate parent existence before returning child data.

#### Task 3.10: Configure SpringDoc OpenAPI
**What:** Configure OpenAPI info, security scheme, and global parameters.  
**How:** Create `OpenApiConfig.java`:
```java
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Menu API — Restaurant Management")
                .description("REST API for managing restaurant menu items, categories, modifiers, and allergens")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Development Team")
                    .email("dev@restaurant.com")))
            .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"))
            .components(new Components()
                .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT token obtained from /api/v1/auth/login")));
    }
}
```
**Acceptance criteria:**
- [ ] Swagger UI available at `/swagger-ui.html`
- [ ] OpenAPI spec at `/api-docs`
- [ ] All endpoints documented with tags, operations, and response schemas
- [ ] Security scheme visible in Swagger UI
- [ ] Request/Response schemas show correct fields and types
**Security considerations:** Document which endpoints are public vs protected.

#### Task 3.11: Write controller integration tests
**What:** Create @WebMvcTest tests for each controller.  
**How:**
```java
@WebMvcTest(CategoryController.class)
class CategoryControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private GetCategoriesUseCase getCategoriesUseCase;
    @MockBean private GetCategoryByIdUseCase getCategoryByIdUseCase;
    @MockBean private CreateCategoryUseCase createCategoryUseCase;
    @MockBean private UpdateCategoryUseCase updateCategoryUseCase;
    @MockBean private DeleteCategoryUseCase deleteCategoryUseCase;
    @Autowired private ObjectMapper objectMapper;

    @Test
    void shouldReturn200WhenListingCategories() throws Exception {
        when(getCategoriesUseCase.execute()).thenReturn(List.of(sampleCategoryResult()));

        mockMvc.perform(get("/api/v1/categories")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.metadata.timestamp").isString());
    }

    @Test
    void shouldReturn201WhenCreatingCategory() throws Exception {
        CreateCategoryRequest request = new CreateCategoryRequest("Entradas", "Appetizers", 1, true);
        when(createCategoryUseCase.execute(any())).thenReturn(sampleCategoryResult());

        mockMvc.perform(post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.name").value("Entradas"));
    }

    @Test
    void shouldReturn422WhenCreatingWithInvalidData() throws Exception {
        CreateCategoryRequest request = new CreateCategoryRequest("", null, -1, true);

        mockMvc.perform(post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void shouldReturn404WhenCategoryNotFound() throws Exception {
        when(getCategoryByIdUseCase.execute(any()))
            .thenThrow(new CategoryNotFoundException(new CategoryId(UUID.randomUUID())));

        mockMvc.perform(get("/api/v1/categories/{id}", UUID.randomUUID()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("CATEGORY_NOT_FOUND"));
    }
}
```
**Acceptance criteria:**
- [ ] All endpoints tested for 200/201 success cases
- [ ] Validation error cases return 422
- [ ] Not found cases return 404 with correct error code
- [ ] Response envelope format verified in tests
- [ ] Pagination parameters tested
**Security considerations:** Tests verify security constraints (if endpoint is protected, 401 without token).

---

## Fase 4: Application Layer (Use Cases)

### Contexto
Implementar los casos de uso (application services) que orquestan la lógica de negocio. Estos servicios implementan los inbound ports definidos en el dominio, utilizan los outbound ports para persistencia, y traducen entre comandos de aplicación y el modelo de dominio.

### Affected layers
- [ ] Database / Prisma schema
- [x] Backend domain logic
- [x] Backend application services
- [ ] Backend API / controllers
- [ ] Frontend features
- [x] Tests (use case unit tests with mocked ports)
- [ ] Documentation / ADRs

### Tasks
| # | Task | Layer | Complexity | Depends on |
|---|------|-------|------------|------------|
| 4.1 | Implement application commands/queries | Backend | Low | 1.1, 1.2–1.8 |
| 4.2 | Implement application results/DTOs | Backend | Low | 1.1, 1.2–1.8 |
| 4.3 | Implement Category use cases | Backend | Medium | 4.1, 4.2, 1.10, 1.11 |
| 4.4 | Implement MenuItem use cases | Backend | High | 4.1, 4.2, 1.10, 1.11 |
| 4.5 | Implement Menu use cases | Backend | Medium | 4.1, 4.2, 1.10, 1.11 |
| 4.6 | Implement ModifierGroup use cases | Backend | Medium | 4.1, 4.2, 1.10, 1.11 |
| 4.7 | Implement Ingredient + Allergen use cases | Backend | Low | 4.1, 4.2, 1.10, 1.11 |
| 4.8 | Write use case unit tests with mocked ports | Tests | Medium | 4.3–4.7 |

### Task details

#### Task 4.1: Implement application commands/queries
**What:** Create command and query records in `application/dto/`.  
**How:**
```java
// Commands (mutations)
public record CreateCategoryCommand(String name, String description, int displayOrder, boolean active) {}
public record UpdateCategoryCommand(String name, String description, int displayOrder) {}
public record UpdateCategoryStatusCommand(boolean active) {}

public record CreateMenuItemCommand(
    String name, String description, Price price, CategoryId categoryId,
    Integer preparationTimeMinutes, ImageUrl imageUrl
) {}
// ... etc for other resources

// Queries (read operations)
public record GetCategoryByIdQuery(CategoryId id) {}
public record GetMenuItemsByCategoryQuery(CategoryId categoryId) {}
```
**Acceptance criteria:**
- [ ] Commands use domain types (Price, CategoryId, etc.) not primitives
- [ ] Commands are immutable records
- [ ] One command class per operation type
**Security considerations:** N/A

#### Task 4.2: Implement application results/DTOs
**What:** Create result records returned by use cases.  
**How:**
```java
public record CategoryResult(
    CategoryId id, String name, String description,
    int displayOrder, boolean active, Instant createdAt, Instant updatedAt
) {}

public record MenuItemResult(
    MenuItemId id, String name, String description, Price price,
    CategoryId categoryId, Boolean active, PreparationTime preparationTime,
    ImageUrl imageUrl, Instant createdAt, Instant updatedAt
) {}
// ... etc
```
**Acceptance criteria:**
- [ ] Results use domain types
- [ ] Results are immutable records
- [ ] Results include all fields needed by API layer
**Security considerations:** N/A

#### Task 4.3–4.7: Implement use case services
**What:** Create service classes that implement inbound port interfaces.  
**How:** Each use case follows this pattern:
```java
@Service
@RequiredArgsConstructor
@Transactional
public class CreateCategoryService implements CreateCategoryUseCase {
    private final CategoryRepositoryPort categoryRepository;
    private final CategoryDomainMapper mapper;  // maps Command ↔ Domain

    @Override
    public CategoryResult execute(CreateCategoryCommand command) {
        // 1. Validate business rules
        if (categoryRepository.existsByName(command.name())) {
            throw new CategoryNameDuplicatedException(command.name());
        }

        // 2. Create domain entity
        Category category = Category.create(
            new CategoryId(UUID.randomUUID()),
            command.name(),
            command.description(),
            command.displayOrder(),
            command.active()
        );

        // 3. Persist
        Category saved = categoryRepository.save(category);

        // 4. Map to result and return
        return mapper.toResult(saved);
    }
}
```

For MenuItem use cases, the logic is richer:
```java
@Service
@RequiredArgsConstructor
@Transactional
public class CreateMenuItemService implements CreateMenuItemUseCase {
    private final MenuItemRepositoryPort menuItemRepository;
    private final CategoryRepositoryPort categoryRepository;
    private final IngredientRepositoryPort ingredientRepository;
    private final MenuItemDomainMapper mapper;

    @Override
    public MenuItemResult execute(CreateMenuItemCommand command) {
        // Verify category exists
        Category category = categoryRepository.findById(command.categoryId())
            .orElseThrow(() -> new CategoryNotFoundException(command.categoryId()));

        // Validate name uniqueness within category
        if (menuItemRepository.existsByNameAndCategory(command.name(), command.categoryId())) {
            throw new MenuItemNameDuplicatedException(command.name(), command.categoryId());
        }

        // Create domain entity
        MenuItem menuItem = MenuItem.create(
            new MenuItemId(UUID.randomUUID()),
            command.name(),
            command.description(),
            command.price(),
            command.categoryId(),
            command.preparationTime(),
            command.imageUrl()
        );

        // Persist
        MenuItem saved = menuItemRepository.save(menuItem);
        return mapper.toResult(saved);
    }
}
```

Create services for:
- **Category**: create, getById, getAll, update, delete, updateStatus
- **MenuItem**: create, getById, getAll (paginated, filterable), update, delete, updateStatus, manageIngredients, manageModifierGroups, manageAllergens, manageNutritionalInfo
- **Menu**: create, getById, getAll, update, delete, manageSections
- **ModifierGroup**: create, getById, getAll, update, delete, manageOptions
- **Ingredient**: create, getById, getAll, update, delete
- **Allergen**: getById, getAll (typically managed via MenuItem)

**Acceptance criteria:**
- [ ] All inbound ports have concrete service implementations
- [ ] Services validate business rules before persisting
- [ ] Services use @Transactional for data consistency
- [ ] Services map between commands, domain entities, and results
- [ ] Services properly throw domain exceptions for business rule violations
- [ ] MenuItem service handles sub-resource management (ingredients, modifiers, allergens)
**Security considerations:** Services should be aware of authorization boundaries — e.g., only ADMIN can delete. This is enforced at the controller/security layer, but services should not expose dangerous operations without context.

#### Task 4.8: Write use case tests with mocked ports
**What:** Create unit tests for each use case that mock outbound ports.  
**How:**
```java
@ExtendWith(MockitoExtension.class)
class CreateCategoryServiceTest {
    @Mock private CategoryRepositoryPort categoryRepository;
    @InjectMocks private CreateCategoryService service;

    @Test
    void shouldCreateCategoryWhenNameIsUnique() {
        // Arrange
        CreateCategoryCommand command = new CreateCategoryCommand("Entradas", "Appetizers", 1, true);
        when(categoryRepository.existsByName("Entradas")).thenReturn(false);
        when(categoryRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        CategoryResult result = service.execute(command);

        // Assert
        assertThat(result.name()).isEqualTo("Entradas");
        assertThat(result.active()).isTrue();
        verify(categoryRepository).save(any());
    }

    @Test
    void shouldThrowExceptionWhenNameDuplicated() {
        CreateCategoryCommand command = new CreateCategoryCommand("Entradas", null, 1, true);
        when(categoryRepository.existsByName("Entradas")).thenReturn(true);

        assertThatThrownBy(() -> service.execute(command))
            .isInstanceOf(CategoryNameDuplicatedException.class);

        verify(categoryRepository, never()).save(any());
    }
}
```
**Acceptance criteria:**
- [ ] Each use case has a test class
- [ ] Success path tested (command → domain → save → result)
- [ ] All business rule violations tested (duplicate name, not found, etc.)
- [ ] Mockito verifies correct repository method calls (save, findById, existsByName, etc.)
- [ ] Tests run without Spring context (pure JUnit 5 + Mockito)
- [ ] Clear test names: shouldXxxWhenYyy format
**Security considerations:** N/A — ports are mocked, no real data involved.

---

## Fase 5: Security & Authentication (ADR-008)

### Contexto
Implementar autenticación JWT y autorización RBAC. Fase 1: API Key simple para administradores. Fase 2: JWT completo con login, roles (ADMIN, VIEWER, KITCHEN), y protección de endpoints.

### Affected layers
- [x] Database / Prisma schema (users table)
- [ ] Backend domain logic
- [x] Backend application services
- [x] Backend API / controllers
- [ ] Frontend features
- [x] Tests (security tests)
- [x] Documentation / ADRs

### Tasks
| # | Task | Layer | Complexity | Depends on |
|---|------|-------|------------|------------|
| 5.1 | Create Flyway migration V9: users table | DB | Low | 2.1–2.8 |
| 5.2 | Create user JPA entity and repository | Backend | Low | 5.1 |
| 5.3 | Implement BCryptPasswordEncoder bean | Backend | Low | 0.2 |
| 5.4 | Implement AuthController (register + login) | Backend | Medium | 5.2, 5.3 |
| 5.5 | Implement JwtService (token generation + validation) | Backend | Medium | 5.3 |
| 5.6 | Implement JwtAuthFilter (OncePerRequestFilter) | Backend | Medium | 5.5 |
| 5.7 | Configure SecurityFilterChain | Backend | Medium | 5.6 |
| 5.8 | Add @PreAuthorize to controllers | Backend | Low | 5.7, 3.5–3.9 |
| 5.9 | Write security integration tests | Tests | High | 5.7, 5.8 |

### Task details

#### Task 5.1: Flyway migration V9 for users table
```sql
CREATE TABLE menu.users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(200) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'VIEWER',
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_users_email ON menu.users (email);
CREATE INDEX idx_users_role ON menu.users (role);
```
**Acceptance criteria:**
- [ ] Table created with correct schema
- [ ] Email has unique constraint
- [ ] Role restricted to valid values (via CHECK constraint or enum)
**Security considerations:** Passwords stored as BCrypt hashes only — never plaintext.

#### Task 5.2: User JPA entity
Create UserJpaEntity, UserJpaRepository, and User domain entity (or reuse domain entity).  
**Note:** The User is a system entity for authentication, not a restaurant domain entity. It can live in a separate security package.

#### Task 5.3–5.7: Implement security
See ADR-008 for full implementation details. Key components:
- **SecurityConfig:** SecurityFilterChain with permitAll for GET endpoints, authenticated for mutations
- **JwtService:** Uses RS256 key pair for signing/validation. Expiration: 15 min for access token, 7 days for refresh token.
- **JwtAuthFilter:** Extracts JWT from Authorization header, validates, sets SecurityContext
- **AuthController:** POST /api/v1/auth/register, POST /api/v1/auth/login, POST /api/v1/auth/refresh

**Acceptance criteria:**
- [ ] POST /api/v1/auth/register creates user with BCrypt password
- [ ] POST /api/v1/auth/login returns JWT access token
- [ ] Protected endpoints return 401 without valid JWT
- [ ] GET /api/v1/categories and GET /api/v1/menu-items are public (no auth)
- [ ] POST/PUT/DELETE endpoints require ADMIN role
- [ ] @PreAuthorize("hasRole('ADMIN')") works on mutation endpoints
- [ ] JWT includes role claim
**Security considerations:**
- Passwords hashed with BCrypt (strength >= 10)
- JWT signed with RS256 (asymmetric key pair)
- Private key stored securely (not in repo)
- Rate limiting on login endpoint to prevent brute force
- No sensitive data in JWT payload

#### Task 5.9: Security tests
Test the full auth flow and endpoint protection.  
**Acceptance criteria:**
- [ ] Register + Login flow tested
- [ ] JWT token validation tested (expired, malformed, wrong signature)
- [ ] Public endpoints accessible without token
- [ ] Protected endpoints reject unauthenticated requests
- [ ] Role-based access tested (ADMIN can mutate, VIEWER cannot)

---

## Fase 6: Caching Strategy (ADR-009)

### Contexto
Implementar caché local (Caffeine) para reducir carga en base de datos y mejorar latencia. Configurar caché HTTP con ETag y Cache-Control.

### Affected layers
- [ ] Database / Prisma schema
- [ ] Backend domain logic
- [x] Backend application services
- [x] Backend API / controllers
- [ ] Frontend features
- [x] Tests
- [ ] Documentation / ADRs

### Tasks
| # | Task | Layer | Complexity | Depends on |
|---|------|-------|------------|------------|
| 6.1 | Configure Spring Cache with Caffeine | Backend | Low | 0.2 |
| 6.2 | Add @Cacheable to persistence adapters | Backend | Medium | 6.1, 2.12 |
| 6.3 | Add HTTP Cache-Control headers to GET endpoints | Backend | Low | 3.5–3.9 |
| 6.4 | Add ETag support to GET endpoints | Backend | Low | 3.5–3.9 |
| 6.5 | Write cache invalidation tests | Tests | Medium | 6.2 |

### Task details

#### Task 6.1: Configure Caffeine
```java
@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .maximumSize(1000)
            .recordStats());
        return cacheManager;
    }
}
```
With per-cache TTLs via `@Cacheable(value = "categories", cacheManager = "...")`.

#### Task 6.2: Cache persistence adapters
Add `@Cacheable` to find methods and `@CacheEvict` to save/delete methods in persistence adapters.

#### Task 6.3–6.4: HTTP caching
Add Cache-Control header and ETag computation to GET responses.

---

## Fase 7: Observability (ADR-010)

### Contexto
Implementar logging estructurado JSON, métricas con Micrometer, health checks personalizados, y MDC para correlation IDs.

### Affected layers
- [ ] Database / Prisma schema
- [ ] Backend domain logic
- [ ] Backend application services
- [x] Backend API / controllers
- [ ] Frontend features
- [x] Tests
- [x] Documentation / ADRs

### Tasks
| # | Task | Layer | Complexity | Depends on |
|---|------|-------|------------|------------|
| 7.1 | Configure Logback JSON encoder | Backend | Low | 0.2 |
| 7.2 | Implement CorrelationIdFilter | Backend | Low | 0.3 |
| 7.3 | Add MDC to GlobalExceptionHandler | Backend | Low | 7.2, 3.1 |
| 7.4 | Configure Actuator endpoints | Backend | Low | 0.2 |
| 7.5 | Implement custom business metrics | Backend | Medium | 4.3–4.7 |
| 7.6 | Implement custom HealthIndicators | Backend | Low | 7.4 |
| 7.7 | Verify Prometheus metrics endpoint | Tests | Low | 7.4 |

---

## Fase 8: Testing & QA (ADR-007)

### Contexto
Completar la pirámide de testing: pruebas arquitectónicas con ArchUnit, pruebas unitarias restantes, pruebas de integración con Testcontainers, y prueba funcional E2E.

### Affected layers
- [ ] Database / Prisma schema
- [ ] Backend domain logic
- [ ] Backend application services
- [ ] Backend API / controllers
- [ ] Frontend features
- [x] Tests
- [ ] Documentation / ADRs

### Tasks
| # | Task | Layer | Complexity | Depends on |
|---|------|-------|------------|------------|
| 8.1 | Implement ArchUnit architecture tests | Tests | Medium | 0.3 |
| 8.2 | Complete domain unit tests coverage | Tests | Medium | 1.12 |
| 8.3 | Complete use case tests coverage | Tests | Medium | 4.8 |
| 8.4 | Complete controller tests coverage | Tests | Medium | 3.11 |
| 8.5 | Write E2E test (menu flow) with Testcontainers | Tests | High | 4.3–4.7, 5.7 |
| 8.6 | Run full test suite and verify 80% coverage | Tests | Medium | 8.1–8.5 |

### Task details

#### Task 8.1: ArchUnit architecture tests
```java
@AnalyzeClasses(packages = "com.restaurant.menu")
class HexagonalArchitectureTest {
    @ArchTest
    static final ArchRule domain_should_not_depend_on_spring =
        noClasses().that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAPackage("org.springframework..")
            .because("Domain layer must be framework-free");

    @ArchTest
    static final ArchRule domain_should_not_depend_on_jpa =
        noClasses().that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAPackage("jakarta.persistence..")
            .because("Domain layer must be JPA-free");

    @ArchTest
    static final ArchRule hexagonal_layers = layeredArchitecture()
        .consideringAllDependencies()
        .layer("Domain").definedBy("..domain..")
        .layer("Application").definedBy("..application..")
        .layer("Infrastructure").definedBy("..infrastructure..")
        .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Infrastructure")
        .whereLayer("Infrastructure").mayNotBeAccessedByLayers("Domain", "Application");
}
```

#### Task 8.2–8.4: Complete test coverage
Fill gaps from earlier tasks. The goal is 80%+ line coverage.

#### Task 8.5: E2E flow test
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class MenuFlowIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired private TestRestTemplate restTemplate;

    @Test
    void shouldCompleteMenuManagementFlow() {
        // 1. Create category → 201
        // 2. Create menu item → 201
        // 3. Get menu item by ID → 200
        // 4. List menu items → 200 paginated
        // 5. Update menu item → 200
        // 6. Create modifier group → 201
        // 7. Assign modifier to menu item → 200
        // 8. Get menu item with modifiers → 200
        // 9. Delete menu item → 204
        // 10. Get deleted menu item → 404
    }
}
```

---

## Fase 9: Documentation & CI/CD

### Contexto
Finalizar la documentación del proyecto, refinar la especificación OpenAPI, y configurar integración continua.

### Affected layers
- [ ] Database / Prisma schema
- [ ] Backend domain logic
- [ ] Backend application services
- [ ] Backend API / controllers
- [ ] Frontend features
- [ ] Tests
- [x] Documentation / ADRs

### Tasks
| # | Task | Layer | Complexity | Depends on |
|---|------|-------|------------|------------|
| 9.1 | Write project README.md | Docs | Low | Fase 0–8 |
| 9.2 | OpenAPI spec refinement (add response schemas, error examples) | Docs | Low | 3.5–3.10 |
| 9.3 | Write CONTRIBUTING.md with setup instructions | Docs | Low | 0.4, 0.8 |
| 9.4 | Configure GitHub Actions CI | DevOps | Medium | 0.5, 0.6 |
| 9.5 | Create DEPLOYMENT.md with production considerations | Docs | Low | — |
| 9.6 | Final architecture review and ADR validation | Docs | Medium | Fase 1–8 |

---

## Resumen de Esfuerzo y Complejidad

| Fase | Nombre | # Tareas | Esfuerzo Estimado |
|------|--------|----------|-------------------|
| 0 | Project Scaffolding | 8 | 1–2 días |
| 1 | Domain Layer | 12 | 3–4 días |
| 2 | Persistence Infrastructure | 13 | 4–5 días |
| 3 | Error Handling & API Layer | 11 | 4–5 días |
| 4 | Application Layer | 8 | 3–4 días |
| 5 | Security & Authentication | 9 | 3–4 días |
| 6 | Caching Strategy | 5 | 1–2 días |
| 7 | Observability | 7 | 1–2 días |
| 8 | Testing & QA | 6 | 2–3 días |
| 9 | Documentation & CI/CD | 6 | 1–2 días |
| **Total** | | **85** | **23–33 días (~5–7 semanas)** |

> **Nota:** Las fases 1–4 son las más críticas y deben implementarse en orden. Las fases 5–7 pueden realizarse en paralelo después de la fase 4. La fase 8 (testing) es incremental y se ejecuta durante todo el proyecto.

---

## Riesgos y Bloqueadores

| Riesgo | Probabilidad | Impacto | Mitigación |
|--------|-------------|---------|-----------|
| Complejidad de mapeo MapStruct con Value Objects | Media | Medio | Crear converters reutilizables; documentar en ADR-005 |
| N+1 queries con JPA | Media | Alto | Usar JOIN FETCH y @EntityGraph; probar con AssertJ |
| Performance de Testcontainers en CI | Media | Bajo | Reutilizar contenedor; usar `@Testcontainers(singleton = true)` |
| Configuración de JWT RS256 | Baja | Medio | Usar Keytool para generar key pair; documentar en ADR-008 |
| Migraciones Flyway conflictivas | Baja | Alto | Siempre crear nuevas migraciones; nunca modificar existentes |
| Curva de aprendizaje de Hexagonal | Media | Medio | Pair programming inicial; documentación clara en ADRs |
| Dependencias con CVEs conocidas | Media | Alto | Usar Spring Boot BOM; Dependabot; `mvnw versions:display-dependency-updates` |

---

## Dependencias entre Tareas (Diagrama)

```
Fase 0 (Scaffolding)
    │
    ▼
Fase 1 (Domain Layer) ──────────────────────┐
    │                                       │
    ▼                                       │
Fase 2 (Persistence) ────┐                 │
    │                    │                 │
    ▼                    │                 │
Fase 3 (API Layer) ──┐  │                 │
    │                 │  │                 │
    ▼                 ▼  ▼                 ▼
Fase 4 (Application) ──► Fase 5 (Security) │
    │                    Fase 6 (Caching)   │
    │                    Fase 7 (Observ.)   │
    ▼                                       ▼
Fase 8 (Testing & QA) ◄─────────────────────┘
    │
    ▼
Fase 9 (Documentation & CI/CD)
```

---

## Criterios de Aceptación Globales

- [ ] Todas las migraciones Flyway se aplican correctamente en ambiente local
- [ ] La API responde en `http://localhost:8080/api/v1/...`
- [ ] Swagger UI accesible en `http://localhost:8080/swagger-ui.html`
- [ ] Todos los endpoints CRUD para los 6 recursos funcionan
- [ ] El manejador de errores devuelve RFC 7807 Problem Details
- [ ] Autenticación JWT funcional (register, login, protected endpoints)
- [ ] Caché Caffeine reduce consultas a DB (verificable por logs)
- [ ] Logs en formato JSON con correlation ID
- [ ] Métricas expuestas en `/actuator/prometheus`
- [ ] Cobertura de pruebas ≥ 80%
- [ ] ArchUnit no reporta violaciones arquitectónicas
- [ ] Pruebas E2E de flujo completo pasan
- [ ] CI Pipeline ejecuta tests automáticamente
- [ ] README.md documenta setup y arquitectura
