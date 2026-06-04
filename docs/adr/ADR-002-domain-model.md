# ADR-002: Modelo de Dominio y Contexto Delimitado para la Gestión de Menú

## Status: Aceptado

## Contexto

Se debe modelar el dominio de la gestión de menú de un restaurante. Es necesario identificar las entidades, value objects, agregados y relaciones que permitan representar fielmente el negocio: categorías de comida, platillos, ingredientes, modificadores, menús estacionales e información nutricional.

**Restricciones:**
- El modelo debe ser puro (sin anotaciones JPA ni Spring) para cumplir con la arquitectura hexagonal
- Debe soportar menús dinámicos (cambios por temporada, horario, disponibilidad)
- Debe manejar modificadores de platillos (tamaños, ingredientes extra, opciones)
- Debe contemplar alérgenos e información nutricional
- Debe permitir la internacionalización futura (nombre y descripción en múltiples idiomas)

## Opciones Consideradas

### Opción A: Modelo de Dominio Enriquecido con DDD Táctico

Modelo con Aggregates, Value Objects, Domain Events y reglas de negocio encapsuladas en las entidades.

**Agregados propuestos:**

- **Agregado `Category`**: Categorías de comida
  - `CategoryId`, `name`, `description`, `displayOrder`, `active`
  - Value Object: `CategoryId`

- **Agregado `MenuItem`**: Platillos individuales
  - `MenuItemId`, `name`, `description`, `price`, `imageUrl`, `preparationTime`, `active`, `categoryId`
  - Value Objects: `MenuItemId`, `Price`, `PreparationTime`
  - Reglas: precio debe ser positivo, nombre no vacío

- **Agregado `Menu`**: Colecciones de platillos agrupados
  - `MenuId`, `name`, `description`, `active`, `validPeriod` (fecha inicio/fin)
  - Value Objects: `MenuId`, `ValidityPeriod`
  - Contiene `MenuSection` como entidad hija: vincula Menu con Category con orden

- **Agregado `ModifierGroup`**: Grupos de opciones (Tamaño, Extra, Tipo de pan)
  - `ModifierGroupId`, `name`, `description`, `required`, `maxSelections`, `active`
  - Contiene `ModifierOption`: nombre, ajuste de precio, activo

- **Agregado `Ingredient`**: Ingredientes con relación M:N a MenuItem
  - `IngredientId`, `name`, `description`, `active`, `unit`

- **Agregado `Allergen`**: Alérgenos
  - `AllergenId`, `name`, `description`, `icon`

- **Entidad `NutritionalInfo`**: Información nutricional por MenuItem
  - `calories`, `protein`, `carbs`, `fat`, `fiber`, `sodium`

- **Pros:**
  - Modelo rico que encapsula reglas de negocio en el lugar correcto
  - Ubicuidad del lenguaje entre desarrolladores y stakeholders del restaurante
  - Fácil de extender con nuevos comportamientos
  - Value Objects eliminan bugs de tipos primitivos (ej. Price en vez de Double)

- **Cons:**
  - Mayor complejidad inicial para modelar correctamente
  - Requiere entender DDD táctico (Aggregates, Value Objects, Repositories)
  - Puede resultar en sobreingeniería si no hay reglas de negocio complejas

### Opción B: Modelo Anémico (DTOs con getters/setters, sin lógica)

Entidades simples con solo atributos, getters y setters. Toda la lógica en servicios.

- **Pros:**
  - Simple de entender e implementar
  - Rápido de prototipar
  - Familiar para todos los desarrolladores

- **Cons:**
  - Las reglas de negocio quedan dispersas en servicios
  - No hay garantía de invariantes (ej. precio negativo posible)
  - Viola el encapsulamiento (Principio de Ocultación de Información)
  - Dificulta la evolución del modelo
  - No hay "lenguaje ubicuo" — el código no refleja el negocio

### Opción C: Modelo Híbrido

Entidades con lógica básica de validación y cálculos simples, pero sin eventos de dominio ni agregados complejos. La orquestación queda en servicios de aplicación.

- **Pros:**
  - Balance entre expresividad del modelo y simplicidad
  - Menos overhead que DDD completo
  - Más mantenible que modelo anémico

- **Cons:**
  - Frontera difusa entre qué va en la entidad y qué en el servicio
  - Puede degenerar en modelo anémico con el tiempo si no se disciplina

## Decisión

Se elige **Opción A: Modelo de Dominio Enriquecido con DDD Táctico**, pero aplicado pragmáticamente:

- **Aggregates claros**: `Category`, `MenuItem`, `Menu`, `ModifierGroup`, `Ingredient`, `Allergen`
- **Value Objects**: `Price`, `MenuItemId`, `MenuId`, `CategoryId`, `ValidityPeriod`, `NutritionalInfo`
- **Relaciones clave:**
  - `MenuItem` pertenece a una `Category`
  - `MenuItem` tiene muchos `Ingredient` (M:N) con cantidad
  - `MenuItem` tiene muchos `ModifierGroup` (M:N)
  - `Menu` contiene `MenuSection` que vinculan `Category` con orden
  - `MenuItem` tiene muchos `Allergen` (M:N)
  - `MenuItem` tiene una `NutritionalInfo` (1:1)
- **Reglas de negocio en el dominio:**
  - `Price` valida valor positivo
  - `MenuItem` valida nombre único dentro de su categoría
  - `ModifierGroup` valida que `maxSelections` no exceda el número de opciones
  - `Menu` valida que `validPeriod` tenga fecha fin posterior a fecha inicio
  - `MenuItem` calcula precio final considerando modificadores obligatorios

**Diagrama conceptual del dominio:**

```
Category (1) ────────── (N) MenuItem (1) ── (N) ModifierGroup
                              │                         │
                              │                         │
                         (N) Ingredient          (N) ModifierOption
                              │
                              │
                         (N) Allergen
                              │
                              │
                          (1) NutritionalInfo

Menu (1) ── (N) MenuSection ── (1) Category
```

## Consecuencias

**Positivas:**
- Las reglas de negocio están encapsuladas y son testeables de forma aislada
- El lenguaje del dominio (categoría, platillo, modificador, menú ejecutivo) está en el código
- Value Objects previenen errores de tipo (no se puede asignar un string donde va Price)
- Los agregados definen límites de consistencia transaccional

**Negativas:**
- Mayor cantidad de clases en el módulo domain/
- Los mapeadores entre dominio y JPA son más complejos (Value Objects requieren converters)
- Curva de aprendizaje para entender el modelo de dominio

**Mitigaciones:**
- Se documentará el modelo con diagramas en esta ADR
- Se crearán ejemplos de uso para cada agregado
- Los Value Objects se implementarán como `record` (Java 17+) para reducir boilerplate

## Referencias

- Eric Evans, "Domain-Driven Design: Tackling Complexity in the Heart of Software" (2003)
- Vaughn Vernon, "Implementing Domain-Driven Design" (2013)
- Martin Fowler, "AnemicDomainModel" (2003) — https://martinfowler.com/bliki/AnemicDomainModel.html
