# ADR-006: Estrategia de Manejo de Errores y Formato de Respuesta

## Status: Aceptado

## Contexto

Toda API REST debe comunicar errores de forma clara, consistente y útil para los consumidores. Es necesario definir un formato estandarizado de respuesta de error, la estrategia de manejo global de excepciones, y los tipos de error que la API puede devolver.

**Restricciones:**
- Formato de error consistente en toda la API
- Debe diferenciar entre errores del cliente (4xx) y del servidor (5xx)
- Debe incluir detalles suficientes para debugging sin exponer información sensible
- Debe integrarse con la validación de Jakarta Bean Validation
- Debe cumplir con el estándar HTTP Problem Details (RFC 7807)

## Opciones Consideradas

### Opción A: RFC 7807 Problem Details (ProblemDetail de Spring Boot 3.x)

Spring Boot 3.x incluye soporte nativo para RFC 7807 mediante la clase `ProblemDetail` y el interface `ErrorResponse`. Se usa `@RestControllerAdvice` con `ResponseEntityExceptionHandler`.

**Estructura de respuesta de error:**

```json
{
  "type": "https://api.restaurant.com/errors/menu-item-not-found",
  "title": "Menu Item Not Found",
  "status": 404,
  "detail": "No menu item found with id: 550e8400-e29b-41d4-a716-446655440000",
  "instance": "/api/v1/menu-items/550e8400-e29b-41d4-a716-446655440000",
  "timestamp": "2026-06-04T12:00:00Z",
  "errors": []
}
```

**Para errores de validación:**

```json
{
  "type": "https://api.restaurant.com/errors/validation-error",
  "title": "Validation Error",
  "status": 422,
  "detail": "The request contains invalid fields",
  "instance": "/api/v1/menu-items",
  "timestamp": "2026-06-04T12:00:00Z",
  "errors": [
    {
      "field": "name",
      "message": "Name must not be blank",
      "rejectedValue": ""
    },
    {
      "field": "price",
      "message": "Price must be greater than 0",
      "rejectedValue": -10
    }
  ]
}
```

- **Pros:**
  - Estándar de internet (RFC 7807) — ampliamente adoptado
  - Soporte nativo en Spring Boot 3.x (`ProblemDetail`, `ErrorResponse`)
  - Fácil integración con el ecosistema Spring
  - Diferenciación clara entre error de validación, recurso no encontrado, conflicto, etc.
  - Extensible con propiedades personalizadas (`timestamp`, `errors`)
  - Integración directa con `@ExceptionHandler` y `@RestControllerAdvice`

- **Cons:**
  - El formato puede ser verboso para errores simples
  - No todos los clientes HTTP conocen RFC 7807
  - La personalización de ProblemDetail requiere configuración

### Opción B: Formato Propietario Simple

Respuesta de error con estructura mínima: `{ "error": "...", "message": "...", "status": 400 }`.

- **Pros:**
  - Simple, mínimo, fácil de parsear
  - Sin dependencia de estándares externos

- **Cons:**
  - No hay estandarización — cada API tiene su formato
  - Los consumidores deben aprender el formato específico
  - Sin soporte nativo en Spring Boot
  - Menos informativo para errores de validación complejos

### Opción C: JSON:API Error Format

Formato definido por la especificación JSON:API para errores.

```json
{
  "errors": [
    {
      "id": "12345",
      "status": "422",
      "code": "validation-error",
      "title": "Validation Error",
      "detail": "Name must not be blank",
      "source": {
        "pointer": "/data/attributes/name"
      }
    }
  ]
}
```

- **Pros:**
  - Formato completo y rico
  - Múltiples errores en una sola respuesta
  - Soporte para meta-información

- **Cons:**
  - Específico para APIs que siguen JSON:API (no es el caso)
  - Mayor verbosidad que RFC 7807
  - Sin soporte nativo en Spring Boot

### Opción D: Error Categorizado por Dominio

Se definen códigos de error específicos del dominio del restaurante (ej. `MENU_ITEM_NOT_FOUND`, `CATEGORY_NAME_DUPLICATED`, `PRICE_OUT_OF_RANGE`).

- **Pros:**
  - Los consumidores pueden manejar errores programáticamente por código
  - Mapeo directo a excepciones de dominio
  - Facilita internacionalización de mensajes

- **Cons:**
  - Requiere mantener un catálogo de códigos de error
  - Puede crecer sin control
  - Se puede combinar con Opción A para lo mejor de ambos

## Decisión

Se elige **Opción A (RFC 7807 Problem Details) combinada con la estrategia de códigos de error de la Opción D**.

Se implementará:

1. **Clase base `ProblemDetail`** de Spring Boot 3.x para todas las respuestas de error
2. **Manejador global** con `@RestControllerAdvice` que captura:
   - `MethodArgumentNotValidException` → 422 con errores de campo
   - `ConstraintViolationException` → 422
   - `NotFoundException` (dominio) → 404
   - `DuplicateResourceException` (dominio) → 409 Conflict
   - `BusinessRuleException` (dominio) → 422 Unprocessable Entity
   - `HttpMessageNotReadableException` → 400 Bad Request
   - `MissingServletRequestParameterException` → 400
   - `AccessDeniedException` → 403 Forbidden
   - `AuthenticationException` → 401 Unauthorized
   - `Exception` genérica → 500 Internal Server Error (sin exponer stack trace)
3. **Códigos de error del dominio** en una propiedad `code` dentro de ProblemDetail
4. **Formato extendido** con campos adicionales (timestamp, errors[] para validación)

**Catálogo inicial de códigos de error del dominio:**

| Código | HTTP Status | Descripción |
|--------|-------------|-------------|
| `CATEGORY_NOT_FOUND` | 404 | Categoría no encontrada |
| `CATEGORY_NAME_DUPLICATED` | 409 | Nombre de categoría duplicado |
| `MENU_ITEM_NOT_FOUND` | 404 | Item del menú no encontrado |
| `MENU_ITEM_NAME_DUPLICATED` | 409 | Nombre de item duplicado en la categoría |
| `INVALID_PRICE` | 422 | Precio inválido (negativo o cero) |
| `MENU_NOT_FOUND` | 404 | Menú no encontrado |
| `INVALID_DATE_RANGE` | 422 | Rango de fechas inválido |
| `MODIFIER_GROUP_NOT_FOUND` | 404 | Grupo de modificadores no encontrado |
| `INGREDIENT_NOT_FOUND` | 404 | Ingrediente no encontrado |
| `ALLERGEN_NOT_FOUND` | 404 | Alérgeno no encontrado |
| `MAX_SELECTIONS_EXCEEDED` | 422 | Excede el máximo de selecciones del modificador |
| `INTERNAL_ERROR` | 500 | Error interno del servidor |

**Excepciones de dominio:**

```java
// En domain/exception/
public abstract class DomainException extends RuntimeException {
    private final String code;
    private final HttpStatus status;

    // constructor, getters...
}

public class MenuItemNotFoundException extends DomainException {
    public MenuItemNotFoundException(MenuItemId id) {
        super("MENU_ITEM_NOT_FOUND",
              "No menu item found with id: " + id.value(),
              HttpStatus.NOT_FOUND);
    }
}
```

**Implementación del manejador global:**

```java
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ProblemDetail handleDomainException(DomainException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            ex.getStatus(), ex.getMessage());
        problem.setTitle(ex.getCode());
        problem.setProperty("code", ex.getCode());
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status,
            WebRequest request) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNPROCESSABLE_ENTITY,
            "The request contains invalid fields");
        problem.setTitle("Validation Error");
        problem.setProperty("code", "VALIDATION_ERROR");
        problem.setProperty("timestamp", Instant.now());

        List<FieldError> fieldErrors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(fe -> new FieldError(
                fe.getField(),
                fe.getDefaultMessage(),
                fe.getRejectedValue()))
            .toList();

        problem.setProperty("errors", fieldErrors);
        return ResponseEntity.unprocessableEntity().body(problem);
    }
}
```

## Consecuencias

**Positivas:**
- Formato de error estándar (RFC 7807) ampliamente reconocido
- Códigos de error específicos del dominio permiten manejo programático
- Error de validación detallado con nombres de campo
- Sin exposición de stack traces ni información sensible
- Integración natural con Spring Boot 3.x
- Los consumidores tienen una fuente única de verdad para errores

**Negativas:**
- Las respuestas de error pueden ser verbosas
- Mantener el catálogo de códigos de error requiere disciplina
- Las excepciones de dominio añaden archivos adicionales al proyecto

**Mitigaciones:**
- El catálogo de errores se documenta en el Swagger/OpenAPI
- Los códigos de error se organizan por recurso (CATEGORY_*, MENU_ITEM_*, etc.)
- Se crea una clase base `DomainException` que reduce el boilerplate por excepción

## Referencias

- RFC 7807: Problem Details for HTTP APIs — https://datatracker.ietf.org/doc/html/rfc7807
- Spring Boot Error Handling — https://docs.spring.io/spring-boot/docs/current/reference/html/web.html#web.servlet.spring-mvc.error-handling
- Microsoft REST API Guidelines — Error Handling
