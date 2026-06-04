# ADR-005: Estrategia de Mapeo y DTOs entre Capas

## Status: Aceptado

## Contexto

En la arquitectura hexagonal existen tres capas con sus propios modelos de datos:
1. **Modelo de Dominio** — clases puras en `domain/model/`
2. **Entidad JPA** — clases anotadas con `@Entity` en `infrastructure/adapter/outbound/persistence/`
3. **DTOs de API** — clases de request/response en `infrastructure/adapter/inbound/web/dto/`

Se necesita una estrategia eficiente para mapear entre estos tres modelos sin acoplar las capas ni generar boilerplate manual.

**Restricciones:**
- Los mapeos deben ser type-safe
- Mínimo boilerplate posible
- Los mapeos deben ser testeables
- Debe manejar Value Objects del dominio (ej. `Price` ↔ `BigDecimal`)
- No debe haber dependencias de infraestructura en el dominio

## Opciones Consideradas

### Opción A: MapStruct (generación de código en tiempo de compilación)

Librería que genera código Java de mapeo en tiempo de compilación mediante procesadores de anotaciones.

- **Pros:**
  - Type-safe: errores de mapeo en compilación, no en runtime
  - Sin reflexión: código generado plano, sin overhead en runtime
  - Rendimiento óptimo (tan rápido como mapeo manual)
  - Soporte para mapeo de colecciones, objetos anidados y Value Objects
  - Inversión de dependencias: los mappers son interfaces, Spring inyecta la implementación
  - Testeable: las implementaciones generadas son clases concretas

- **Cons:**
  - Requiere configuración del procesador de anotaciones en pom.xml
  - Mensajes de error crípticos si los mapeos no son exactos
  - Curva de aprendizaje para mapeos complejos (uso de `@Mapping`, `expression`, `defaultExpression`)
  - Genera archivos adicionales en `/target` (pueden confundir en code review)

### Opción B: Mapeo Manual (métodos escritos a mano)

Cada adaptador contiene métodos de mapeo escritos manualmente.

- **Pros:**
  - Control total sobre el mapeo
  - Sin dependencias externas
  - Sin procesamiento de anotaciones
  - Fácil de debuggear

- **Cons:**
  - Alto boilerplate: N capas × M entidades = mucho código repetitivo
  - Propenso a errores humanos (olvidar mapear un campo nuevo)
  - Difícil de mantener cuando cambian los modelos
  - Baja productividad

### Opción C: ModelMapper (reflexión en runtime)

Librería que mapea objetos usando reflexión, con configuración mediante reglas.

- **Pros:**
  - Mínimo código: mapea automáticamente por convención de nombres
  - Rápido de implementar inicialmente

- **Cons:**
  - Errores de mapeo solo en runtime
  - Rendimiento inferior por uso de reflexión
  - Dificultad para mapear Value Objects personalizados
  - Caja negra: difícil de saber qué se mapeó exactamente
  - Problemas conocidos con tipos genéricos y colecciones anidadas
  - Violación del Principio de Menor Sorpresa

### Opción D: Records de Java + Mapeo con Constructor

Usar `record` de Java 17+ para DTOs y mapear mediante constructores y factory methods.

- **Pros:**
  - Inmutabilidad garantizada
  - Sintaxis concisa
  - Sin dependencias

- **Cons:**
  - Mapeo manual igual que Opción B para transformaciones complejas
  - Los records tienen limitaciones con herencia
  - No resuelve la transformación entre capas automáticamente

## Decisión

Se elige **Opción A: MapStruct** como estrategia de mapeo primaria, complementada con mapeo manual para casos excepcionales.

**Racional:**

1. **Type safety en compilación:** Los errores de mapeo se detectan al compilar, no en producción. Esto es crítico cuando se agregan nuevos campos a las entidades.
2. **Rendimiento:** Sin reflexión ni overhead en runtime. Implementaciones planas en Java.
3. **Manejo de Value Objects:** MapStruct permite converters personalizados para mapear `Price` ↔ `BigDecimal`, `MenuItemId` ↔ `UUID`, etc.
4. **Inversión de dependencias:** Spring inyecta las implementaciones generadas, manteniendo el patrón de puertos y adaptadores.

**Estrategia de mappers:**

```
Capa de API (Controller)              Capa de Aplicación              Capa de Persistencia
────────────────────────              ──────────────────              ─────────────────────
CreateMenuItemRequest  ──[mapper]──→  CreateMenuItemCommand  ──[mapper]──→  MenuItemJpaEntity
                                              │
                                              ↓
                                       MenuItem (domain)
                                              │
                                              ↓
MenuItemResponse       ←──[mapper]──  MenuItemResult       ←──[mapper]──  MenuItemJpaEntity
```

**Definición de mappers:**

```java
// Mapper de API (DTO ↔ Command/Result)
@Mapper(componentModel = "spring")
interface MenuItemApiMapper {
    CreateMenuItemCommand toCommand(CreateMenuItemRequest request);
    MenuItemResponse toResponse(MenuItemResult result);
}

// Mapper de Persistencia (Domain ↔ JPA Entity)
@Mapper(componentModel = "spring", uses = {PriceMapper.class, UUIDMapper.class})
interface MenuItemPersistenceMapper {
    MenuItemJpaEntity toJpaEntity(MenuItem domain);
    MenuItem toDomain(MenuItemJpaEntity jpaEntity);
}
```

**DTOs (Request/Response) como records de Java:**

Los DTOs de API se implementarán como `record` de Java 17+ para inmutabilidad y concisión.

```java
public record CreateMenuItemRequest(
    String name,
    String description,
    BigDecimal price,
    UUID categoryId,
    Integer preparationTimeMinutes,
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
    Instant createdAt,
    Instant updatedAt
) {}
```

**Commands y Results (capa de aplicación) como records:**

```java
public record CreateMenuItemCommand(
    String name,
    String description,
    Price price,
    CategoryId categoryId,
    Integer preparationTimeMinutes,
    ImageUrl imageUrl
) {}

public record MenuItemResult(
    MenuItemId id,
    String name,
    String description,
    Price price,
    CategoryId categoryId,
    Boolean active,
    Instant createdAt,
    Instant updatedAt
) {}
```

**Mapeo de Value Objects con MapStruct:**

```java
@Component
public class PriceMapper {
    public BigDecimal toBigDecimal(Price price) {
        return price != null ? price.value() : null;
    }

    public Price toPrice(BigDecimal value) {
        return value != null ? new Price(value) : null;
    }
}
```

## Consecuencias

**Positivas:**
- Mapeo type-safe comprobado en compilación
- Sin overhead de reflexión en tiempo de ejecución
- Separación limpia entre DTOs, Commands, Modelo de Dominio y Entidades JPA
- Los cambios en un modelo (ej. nuevo campo en dominio) generan error de compilación si no se actualiza el mapper
- Los records de Java proporcionan inmutabilidad y bajo boilerplate

**Negativas:**
- Dependencia adicional: `mapstruct` y `mapstruct-processor` en pom.xml
- Compilación más lenta por el procesador de anotaciones
- Se debe configurar correctamente ruta de generated sources en el IDE
- Para mapeos muy complejos, MapStruct puede requerir `@Mapping` con expresiones que son difíciles de leer

**Mitigaciones:**
- Los mappers se cubren con pruebas de integración que verifican mapeos completos
- Se documentan los converters de Value Objects para que sean reutilizables
- IDE plugins (MapStruct Support) mejoran la experiencia de desarrollo

## Referencias

- MapStruct Reference Guide — https://mapstruct.org/documentation/reference-guide/
- MapStruct Spring Integration — https://mapstruct.org/documentation/spring-integration/
- Java 17 Records — JEP 395 (https://openjdk.org/jeps/395)
