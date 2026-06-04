# ADR-004: Diseño de la API REST y Estrategia de Documentación

## Status: Aceptado

## Contexto

Se debe definir el estilo de la API REST (RESTful, recursos, naming), la estrategia de versionado, los formatos de respuesta, y la herramienta de documentación interactiva para la gestión del menú del restaurante.

**Restricciones:**
- Debe ser una API REST
- Documentada con Swagger (OpenAPI)
- Debe seguir principios RESTful
- Los endpoints deben ser intuitivos para consumidores (frontend, mobile, third-party)
- Debe soportar paginación, filtrado y ordenamiento

## Opciones Consideradas

### Opción A: REST API con versionado por URL (/api/v1/) + SpringDoc OpenAPI

API RESTful tradicional con recursos en plural, versionado en el path, y SpringDoc OpenAPI (swagger-ui) para documentación interactiva.

**Endpoints propuestos:**

```
# Categorías
GET    /api/v1/categories?page=0&size=10&sort=name,asc
GET    /api/v1/categories/{id}
POST   /api/v1/categories
PUT    /api/v1/categories/{id}
PATCH  /api/v1/categories/{id}/status
DELETE /api/v1/categories/{id}

# Items del Menú
GET    /api/v1/menu-items?categoryId=&active=&search=
GET    /api/v1/menu-items/{id}
POST   /api/v1/menu-items
PUT    /api/v1/menu-items/{id}
PATCH  /api/v1/menu-items/{id}/status
DELETE /api/v1/menu-items/{id}

GET    /api/v1/menu-items/{id}/ingredients
POST   /api/v1/menu-items/{id}/ingredients
DELETE /api/v1/menu-items/{id}/ingredients/{ingredientId}

GET    /api/v1/menu-items/{id}/modifier-groups
POST   /api/v1/menu-items/{id}/modifier-groups
DELETE /api/v1/menu-items/{id}/modifier-groups/{groupId}

GET    /api/v1/menu-items/{id}/allergens
GET    /api/v1/menu-items/{id}/nutritional-info
PUT    /api/v1/menu-items/{id}/nutritional-info

# Ingredientes
GET    /api/v1/ingredients
GET    /api/v1/ingredients/{id}
POST   /api/v1/ingredients
PUT    /api/v1/ingredients/{id}
DELETE /api/v1/ingredients/{id}

# Grupos de Modificadores
GET    /api/v1/modifier-groups?includeOptions=true
GET    /api/v1/modifier-groups/{id}
POST   /api/v1/modifier-groups
PUT    /api/v1/modifier-groups/{id}
DELETE /api/v1/modifier-groups/{id}

GET    /api/v1/modifier-groups/{id}/options
POST   /api/v1/modifier-groups/{id}/options
PUT    /api/v1/modifier-groups/{id}/options/{optionId}
DELETE /api/v1/modifier-groups/{id}/options/{optionId}

# Menús
GET    /api/v1/menus?active=true
GET    /api/v1/menus/{id}
POST   /api/v1/menus
PUT    /api/v1/menus/{id}
DELETE /api/v1/menus/{id}

GET    /api/v1/menus/{id}/sections
POST   /api/v1/menus/{id}/sections
PUT    /api/v1/menus/{id}/sections/{sectionId}
DELETE /api/v1/menus/{id}/sections/{sectionId}

# Alérgenos
GET    /api/v1/allergens
GET    /api/v1/allergens/{id}
```

- **Pros:**
  - RESTful puro: recursos, nouns, HTTP verbs semánticos
  - Versionado en URL: explícito, fácil de implementar y consumir
  - SpringDoc OpenAPI genera documentación automática desde anotaciones
  - Paginación con Spring Data Pageable: estándar y predecible
  - Naming consistente (plural, kebab-case)
  - Sub-recursos anidados reflejan las relaciones del dominio

- **Cons:**
  - Versionado en URL puede ser menos flexible que content negotiation
  - Los endpoints anidados pueden generar N+1 consultas si no se optimizan
  - PUT vs PATCH puede causar confusión (PUT reemplaza todo, PATCH es parcial)

### Opción B: API REST con versionado por Header (Accept header)

El versionado se maneja mediante el header `Accept: application/vnd.restaurant.v1+json`.

- **Pros:**
  - URL más limpia sin versionado visible
  - Mayor granularidad en el versionado (por recurso)

- **Cons:**
  - Menos visible e intuitivo para consumidores
  - Dificulta el testeo desde navegador
  - Complejidad adicional en el enrutamiento Spring
  - Menos común en APIs públicas

### Opción C: GraphQL

API GraphQL donde el cliente especifica exactamente los campos que necesita.

- **Pros:**
  - Los clientes obtienen exactamente lo que piden (sin over-fetching)
  - Una sola entrada para múltiples recursos
  - Ideal para frontends que necesitan datos relacionados

- **Cons:**
  - Complejidad de implementación con Spring Boot
  - Caché HTTP más difícil (POST por defecto)
  - Sobrecarga de queries N+1 en el servidor
  - Menos ecosistema y herramientas que REST
  - No es REST — se aleja del requerimiento

## Decisión

Se elige **Opción A: REST API con versionado por URL (/api/v1/) + SpringDoc OpenAPI**.

**Racional:**

1. **Versionado por URL:** Es la opción más simple, explícita y común en APIs REST. Un cambio breaking en la API se refleja claramente como `/api/v2/...`.
2. **Recursos en plural, anidados:** `/menu-items/{id}/ingredients` refleja las relaciones del modelo de dominio de forma natural.
3. **SpringDoc OpenAPI:** Genera documentación automática desde anotaciones, minimizando el esfuerzo de mantener la documentación sincronizada con el código.
4. **Paginación estándar:** Se usará el formato de Spring Data (page, size, sort) con respuesta envolvente.

**Formato de respuesta estándar:**

```json
{
  "data": { ... },
  "metadata": {
    "timestamp": "2026-06-04T12:00:00Z",
    "version": "1.0"
  }
}
```

**Formato de respuesta paginada:**

```json
{
  "data": [ ... ],
  "pagination": {
    "page": 0,
    "size": 10,
    "totalElements": 100,
    "totalPages": 10,
    "sorted": true
  }
}
```

**Convenciones:**
- **HTTP Verbs:** GET (listar/obtener), POST (crear), PUT (reemplazar), PATCH (actualización parcial), DELETE (eliminar)
- **Códigos de estado:** 200 (OK), 201 (Created), 204 (No Content), 400 (Bad Request), 404 (Not Found), 409 (Conflict), 422 (Unprocessable Entity), 500 (Internal Server Error)
- **Naming:** snake_case para campos JSON (consistente con la base de datos)
- **Fechas:** ISO 8601 (yyyy-MM-dd'T'HH:mm:ss'Z')
- **IDs:** UUID en formato string

**Configuración de SpringDoc OpenAPI:**

```yaml
springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
    operations-sorter: method
    tags-sorter: alpha
  packages-to-scan: com.restaurant.menu.infrastructure.adapter.inbound.web
```

## Consecuencias

**Positivas:**
- Documentación siempre sincronizada con el código (anotaciones OpenAPI)
- Consumidores (frontend, mobile) tienen una referencia interactiva siempre disponible
- Versionado explícito permite evolucionar la API sin romper clientes existentes
- Paginación consistente en todos los endpoints de listado

**Negativas:**
- Mantener el versionado en URL requiere mantener controladores viejos o estrategia de traducción
- Las anotaciones OpenAPI añaden ruido visual a los controladores (mitigable con interfaces separadas)
- La paginación con page/size de Spring Data puede ser confusa (page empieza en 0)

**Mitigaciones:**
- Se creará una interfaz separada para la especificación OpenAPI y el controlador la implementará
- Se documentará que el paginado comienza en página 0
- Se considerará API version headless (solo v1 por ahora) para evitar mantenimiento prematuro

## Referencias

- REST API Design Rulebook — Mark Masse (2011)
- SpringDoc OpenAPI Documentation — https://springdoc.org/
- Microsoft REST API Guidelines — https://github.com/microsoft/api-guidelines
- JSON:API Specification — https://jsonapi.org/ (inspiración para formato de respuesta)
