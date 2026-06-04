# ADR-007: Estrategia de Pruebas

## Status: Aceptado

## Contexto

Se necesita definir la estrategia de pruebas para la API de gestión de menú, cubriendo los diferentes niveles de testing: unitario, integración y funcional.

**Restricciones:**
- Las pruebas deben ser rápidas, deterministas y fáciles de ejecutar
- La arquitectura hexagonal fue elegida en parte por su testabilidad
- Se deben probar las reglas de negocio del dominio sin levantar Spring
- Se deben probar los adaptadores (REST, JPA) con contexto Spring
- Se debe mantener un estándar de calidad (cobertura mínima)
- Se debe poder ejecutar en CI/CD

## Opciones Consideradas

### Opción A: Pirámide de Testing Clásica (Unitario > Integración > E2E)

- **Unidad (70%):** Pruebas del dominio sin Spring (modelos, value objects, reglas de negocio)
- **Integración (20%):** Pruebas de adaptadores (REST controllers, persistence, mappers)
- **Funcional/E2E (10%):** Pruebas de API completas con contexto Spring Boot

- **Pros:**
  - Pirámide clásica y probada
  - Pruebas unitarias rápidas (milisegundos) que no requieren infraestructura
  - Las pruebas de integración verifican que los adaptadores funcionan correctamente
  - Las pruebas funcionales validan flujos completos
  - La arquitectura hexagonal facilita esta pirámide naturalmente

- **Cons:**
  - Las pruebas de integración requieren configuración de base de datos (Testcontainers)
  - Las pruebas E2E son lentas y frágiles
  - El mocking debe ser disciplinado para no sobre-acoplar las pruebas

### Opción B: Pruebas de Integración como Prioridad (Integración > Unidad)

Mayor énfasis en pruebas de integración con Testcontainers y menor cobertura de unidad.

- **Pros:**
  - Más confianza en que el sistema funciona realmente
  - Detecta problemas de integración temprano

- **Cons:**
  - Pruebas lentas (minutos en lugar de segundos)
  - Mayor tiempo de feedback en desarrollo
  - Las pruebas de integración son más frágiles ante cambios de esquema
  - Dependencia de infraestructura externa (PostgreSQL en Testcontainers)

### Opción C: Testing con ArchUnit (Testing Arquitectónico)

Se añaden pruebas arquitectónicas con ArchUnit para verificar que se cumplen las reglas de la arquitectura hexagonal.

- **Pros:**
  - Verifica automáticamente que no se violen las reglas arquitectónicas
  - Previene regresiones arquitectónicas (ej. dominio importando Spring)
  - Falla la build si se rompen las reglas

- **Cons:**
  - No reemplaza las pruebas funcionales
  - Añade otra dimensión de pruebas que mantener

## Decisión

Se elige **Opción A (Pirámide de Testing Clásica) complementada con Opción C (ArchUnit)**.

**Estructura de pruebas:**

```
src/test/java/com/restaurant/menu/
├── domain/                          # Pruebas unitarias (sin Spring)
│   ├── model/
│   │   ├── MenuItemTest.java        # Reglas de negocio del item
│   │   ├── PriceTest.java           # Value Object Price
│   │   └── MenuTest.java            # Reglas de negocio del menú
│   └── exception/
│       └── DomainExceptionTest.java
│
├── application/                     # Pruebas de aplicación
│   └── service/
│       ├── CreateMenuItemUseCaseTest.java  # Mock de puertos
│       └── GetMenuUseCaseTest.java
│
├── infrastructure/                  # Pruebas de integración
│   ├── adapter/
│   │   ├── inbound/
│   │   │   └── web/
│   │   │       ├── CategoryControllerTest.java    # WebMvcTest
│   │   │       └── MenuItemControllerTest.java    # WebMvcTest
│   │   └── outbound/
│   │       └── persistence/
│   │           ├── CategoryPersistenceAdapterTest.java
│   │           └── MenuItemPersistenceAdapterTest.java
│   └── config/
│       └── GlobalExceptionHandlerTest.java
│
├── architecture/                    # Pruebas arquitectónicas
│   └── HexagonalArchitectureTest.java  # ArchUnit
│
└── e2e/                             # Pruebas funcionales
    └── MenuFlowIntegrationTest.java  # SpringBootTest con Testcontainers
```

**Estrategia por capa:**

### 1. Dominio (Pruebas Unitarias — JUnit 5 + AssertJ)

Sin Spring, sin mocking. Pruebas directas de las clases de dominio.

```java
class PriceTest {
    @Test
    void shouldCreatePriceWhenValueIsPositive() {
        Price price = new Price(new BigDecimal("25.50"));
        assertThat(price.value()).isEqualByComparingTo("25.50");
    }

    @Test
    void shouldThrowExceptionWhenValueIsNegative() {
        assertThatThrownBy(() -> new Price(new BigDecimal("-10.00")))
            .isInstanceOf(InvalidPriceException.class);
    }
}

class MenuItemTest {
    @Test
    void shouldCalculateFinalPriceWithModifiers() {
        MenuItem item = new MenuItem(/* ... */);
        item.addModifierGroup(new MandatoryModifierGroup(/* ... */));
        Price finalPrice = item.calculateFinalPrice();
        assertThat(finalPrice.value()).isEqualByComparingTo("30.00");
    }
}
```

### 2. Aplicación (Pruebas con Mock de Puertos — JUnit 5 + Mockito)

Se mockean los puertos outbound para probar la orquestación de casos de uso.

```java
class CreateMenuItemUseCaseTest {
    @Mock private MenuItemRepositoryPort repository;
    @Mock private CategoryRepositoryPort categoryRepository;
    @InjectMocks private CreateMenuItemUseCase useCase;

    @Test
    void shouldCreateMenuItemWhenDataIsValid() {
        CreateMenuItemCommand command = /* ... */;
        when(categoryRepository.findById(any())).thenReturn(Optional.of(category));
        when(repository.save(any())).thenReturn(expectedItem);

        MenuItemResult result = useCase.execute(command);

        assertThat(result.name()).isEqualTo("Pizza Margherita");
        verify(repository).save(any());
    }
}
```

### 3. Infraestructura (Pruebas de Integración — @WebMvcTest, @DataJpaTest)

**Controladores REST (@WebMvcTest):**

```java
@WebMvcTest(CategoryController.class)
class CategoryControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private GetCategoriesUseCase getCategoriesUseCase;

    @Test
    void shouldReturn200WhenListingCategories() throws Exception {
        when(getCategoriesUseCase.execute()).thenReturn(List.of(categoryResult));

        mockMvc.perform(get("/api/v1/categories")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isArray());
    }
}
```

**Persistencia (@DataJpaTest + Testcontainers):**

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class CategoryPersistenceAdapterTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired private CategoryJpaRepository jpaRepository;
    private CategoryPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new CategoryPersistenceAdapter(jpaRepository, mapper);
    }

    @Test
    void shouldSaveAndRetrieveCategory() {
        Category category = new Category(/* ... */);
        Category saved = adapter.save(category);
        assertThat(saved.id()).isNotNull();
    }
}
```

### 4. Pruebas Arquitectónicas (ArchUnit)

```java
class HexagonalArchitectureTest {
    @Test
    void domainLayerShouldNotDependOnInfrastructure() {
        JavaClasses classes = new ClassFileImporter()
            .importPackages("com.restaurant.menu");

        ArchRule rule = layeredArchitecture()
            .consideringAllDependencies()
            .layer("Domain").definedBy("..domain..")
            .layer("Application").definedBy("..application..")
            .layer("Infrastructure").definedBy("..infrastructure..")
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Infrastructure")
            .whereLayer("Infrastructure").mayNotBeAccessedByLayers("Domain", "Application");

        rule.check(classes);
    }

    @Test
    void domainShouldNotImportSpring() {
        JavaClasses classes = new ClassFileImporter()
            .importPackages("com.restaurant.menu.domain");

        ArchRule rule = noClasses()
            .should().dependOnClassesThat().resideInAnyPackage("org.springframework..")
            .because("Domain layer must be framework-free");

        rule.check(classes);
    }

    @Test
    void domainShouldNotImportJpa() {
        JavaClasses classes = new ClassFileImporter()
            .importPackages("com.restaurant.menu.domain");

        ArchRule rule = noClasses()
            .should().dependOnClassesThat().resideInAnyPackage("jakarta.persistence..")
            .because("Domain layer must not depend on JPA");

        rule.check(classes);
    }
}
```

### 5. Pruebas Funcionales (SpringBootTest + Testcontainers)

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class MenuFlowIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired private TestRestTemplate restTemplate;

    @Test
    void shouldCreateAndRetrieveMenuItem() {
        // Crear categoría
        // Crear item del menú
        // Verificar que se puede obtener
    }
}
```

**Herramientas:**
| Herramienta | Propósito |
|-------------|-----------|
| JUnit 5 | Framework de pruebas |
| AssertJ | Aserciones fluidas |
| Mockito | Mocking de puertos en pruebas de aplicación |
| Spring Boot Test | @WebMvcTest, @DataJpaTest, @SpringBootTest |
| Testcontainers | PostgreSQL en contenedor para pruebas de integración |
| ArchUnit | Pruebas arquitectónicas |
| JaCoCo | Cobertura de código (mínimo 80%) |
| REST Assured | Testing de APIs REST (opcional, alternativa a MockMvc) |

**Cobertura mínima:** 80% (líneas de código), con exclusión de:
- Clases de configuración pura
- DTOs/Records (cubiertos indirectamente)
- Código generado por MapStruct

## Consecuencias

**Positivas:**
- Pirámide balanceada: pruebas rápidas y lentas en proporción adecuada
- Las reglas de negocio se prueban en milisegundos sin infraestructura
- ArchUnit previene regresiones arquitectónicas automáticamente
- Testcontainers garantiza que las pruebas de integración usan PostgreSQL real
- Cobertura mínima del 80% asegura calidad

**Negativas:**
- Tiempo de configuración inicial de Testcontainers
- Las pruebas de integración son más lentas (~1-3 segundos por prueba)
- Mantener las pruebas arquitectónicas requiere actualización cuando cambia la estructura
- JaCoCo debe configurarse para excluir código generado

**Mitigaciones:**
- Testcontainers reusa la misma instancia de contenedor para todas las pruebas (singleton)
- Las pruebas unitarias del dominio se ejecutan primero (feedback rápido)
- ArchUnit se ejecuta en una fase separada del build
- Las pruebas lentas (integración, E2E) se pueden etiquetar para ejecución selectiva

## Referencias

- Martin Fowler, "TestPyramid" — https://martinfowler.com/bliki/TestPyramid.html
- ArchUnit User Guide — https://www.archunit.org/userguide/html/000_Index.html
- Testcontainers Documentation — https://testcontainers.com/guides/
- JaCoCo Maven Plugin — https://www.eclemma.org/jacoco/trunk/doc/maven.html
