# ADR-001: Arquitectura Hexagonal (Ports & Adapters) para la API de Gestión de Menú

## Status: Aceptado

## Contexto

Se necesita diseñar una API REST para la gestión del menú de un restaurante. El sistema debe ser mantenible a largo plazo, testeable, y permitir evolucionar componentes de infraestructura (base de datos, frameworks) sin afectar la lógica de negocio.

El equipo es pequeño (1-3 desarrolladores) y se busca un balance entre calidad arquitectónica y velocidad de desarrollo inicial. La aplicación gestiona reglas de negocio como precios, modificadores de platillos, alérgenos, y categorización de menú — no es un CRUD simple.

**Restricciones:**
- Backend en Java con Spring Boot
- Base de datos PostgreSQL
- Documentación con Swagger/OpenAPI
- Principios SOLID como guía

## Opciones Consideradas

### Opción A: Arquitectura Hexagonal (Ports & Adapters)

Arquitectura que organiza el código en torno al dominio de negocio, no a la tecnología. El núcleo del dominio es puro Java sin anotaciones de Spring ni JPA. Los puertos (interfaces) definen contratos, y los adaptadores implementan la tecnología concreta (REST, JPA).

- **Pros:**
  - Aislamiento total del dominio: la lógica de negocio se prueba sin Spring Context
  - Framework reemplazable: cambiar de JPA a jOOQ o de PostgreSQL a MongoDB no afecta el dominio
  - Principio de Inversión de Dependencias (SOLID D) aplicado rigurosamente
  - Separación clara de responsabilidades: cada adaptador hace solo su tarea
  - Soporte nativo para múltiples canales de entrada (REST + colas + CLI) sin duplicar lógica
  - Testeabilidad: pruebas unitarias del dominio en milisegundos sin infraestructura

- **Contras:**
  - Mayor ceremonia inicial: más interfaces, mappers, y clases de infraestructura
  - Curva de aprendizaje para desarrolladores no familiarizados con el patrón
  - Puede ser excesivo para operaciones puramente CRUD sin reglas de negocio
  - Proliferación de mapeadores entre capas (Dominio ↔ JPA ↔ DTO)

### Opción B: Arquitectura por Capas Tradicional (Controller → Service → Repository)

Estructura clásica de Spring Boot donde los controladores llaman a servicios que llaman a repositorios.

- **Pros:**
  - Familiar para la mayoría de desarrolladores Spring
  - Baja ceremonia inicial: menos archivos y capas
  - Rápido prototipado y entrega inicial
  - Amplia documentación y ejemplos disponibles

- **Cons:**
  - Las anotaciones de framework (@Entity, @Transactional) se filtran en todas las capas
  - La lógica de negocio se acopla a Spring y JPA
  - Pruebas unitarias requieren contexto Spring o mocking complejo
  - Cambiar de tecnología de persistencia requiere reescribir el dominio
  - Violación frecuente del Principio de Inversión de Dependencias
  - Las reglas de negocio se dispersan entre servicios y entidades JPA

### Opción C: Modular Monolith con DDD Táctico

Similar a la hexagonal pero organizada en módulos por bounded context, cada uno con su propio modelo y persistencia, dentro de un mismo deployable.

- **Pros:**
  - Alta cohesión por módulo
  - Límites explícitos entre contextos
  - Preparado para escalar a microservicios si es necesario

- **Cons:**
  - Para un equipo pequeño y un solo contexto de menú, introduce complejidad innecesaria
  - Duplicación de configuración entre módulos
  - Overhead de integración entre módulos

## Decisión

Se elige **Arquitectura Hexagonal (Ports & Adapters)**, pero con una aplicación pragmática — no dogmática.

**Racional:**

1. El dominio de menú de restaurante tiene reglas de negocio reales (cálculo de precios con modificadores, validación de disponibilidad, manejo de alérgenos) que se benefician del aislamiento.
2. La inversión en ceremonia inicial se recupera rápidamente en mantenibilidad y testabilidad.
3. Se aplicará hexagonal de forma pragmática: donde una operación es puramente CRUD (ej. CRUD de categorías), se puede simplificar el número de capas sin romper la arquitectura.
4. El stack tecnológico (Spring Boot 3.x, JPA, PostgreSQL) puede evolucionar; la arquitectura protege contra cambios futuros.

**Estructura de paquetes:**

```
com.restaurant.menu/
├── domain/                    # Núcleo puro — sin dependencias de framework
│   ├── model/                 # Entidades de dominio y Value Objects
│   ├── port/                  # Puertos (interfaces)
│   │   ├── inbound/           # Casos de uso (entrada)
│   │   └── outbound/          # Repositorios y servicios externos (salida)
│   └── exception/             # Excepciones de dominio
│
├── application/               # Casos de uso (orquestación)
│   ├── service/               # Implementación de puertos inbound
│   └── dto/                   # DTOs de aplicación (commmands, queries)
│
├── infrastructure/            # Adaptadores (implementaciones técnicas)
│   ├── adapter/
│   │   ├── inbound/
│   │   │   └── web/           # Controladores REST, DTOs de request/response
│   │   └── outbound/
│   │       └── persistence/   # JPA entities, repositorios Spring Data
│   ├── config/                # Configuración Spring (beans, swagger, seguridad)
│   └── exception/             # Manejadores globales de excepción (@RestControllerAdvice)
│
└── shared/                    # Código compartido (utils, constantes)
```

## Consecuencias

**Positivas:**
- El dominio es testeable sin Spring Context — pruebas unitarias rápidas y deterministas
- Cambiar de JPA a JDBC o de PostgreSQL a MySQL requiere solo cambiar un adaptador
- Los casos de uso son explícitos y navegables en el código
- Los desarrolladores pueden trabajar en paralelo: dominio vs infraestructura

**Negativas:**
- ~20-30% más archivos que una arquitectura por capas tradicional
- Los nuevos miembros del equipo necesitan entender Hexagonal antes de ser productivos
- Se debe vigilar el "over-engineering": no toda operación necesita 5 capas

**Mitigaciones:**
- Se usará MapStruct para eliminar boilerplate de mapeo entre capas
- Se documentará la arquitectura con ejemplos concretos para onboarding
- Las operaciones CRUD simples pueden usar un flujo simplificado dentro del mismo patrón

## Referencias

- Alistair Cockburn, "Hexagonal Architecture" (2005)
- Robert C. Martin, "Clean Architecture" (2017)
- Vaughn Vernon, "Implementing Domain-Driven Design" (2013)
- Spring Boot Reference Documentation, v3.x
