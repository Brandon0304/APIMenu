# ADR-009: Estrategia de Cacheo

## Status: Propuesto

## Contexto

La API de gestión de menú servirá datos que no cambian con frecuencia (categorías, platillos, menús) a potencialmente muchos consumidores (clientes web, kioscos, apps móviles). Es necesario definir una estrategia de cacheo para optimizar la latencia y reducir la carga en la base de datos.

**Restricciones:**
- El menú se consulta mucho más de lo que se modifica (read-heavy)
- Los cambios en el menú deben reflejarse con baja latencia (no pueden quedar datos obsoletos por horas)
- Multi-tenancy futuro: cada restaurante puede tener su propio menú
- La solución debe ser simple de operar inicialmente (equipo pequeño)

## Opciones Consideradas

### Opción A: Cacheo en el Adaptador de Persistencia con Spring Cache (Caffeine)

Cache en memoria local (Caffeine) gestionado por Spring Cache Abstraction. Se cachean los resultados de consultas a la base de datos a nivel de adaptador de persistencia.

```java
@Component
public class CachedCategoryRepositoryAdapter implements CategoryRepositoryPort {
    private final CategoryJpaRepository jpaRepository;
    private final CategoryMapper mapper;

    @Override
    @Cacheable(value = "categories", key = "#id")
    public Optional<Category> findById(CategoryId id) {
        return jpaRepository.findById(id.value())
            .map(mapper::toDomain);
    }

    @Override
    @CacheEvict(value = "categories", allEntries = true)
    public Category save(Category category) {
        // invalidar caché al escribir
    }
}
```

- **Pros:**
  - Simple de implementar con Spring Cache
  - Caffeine es rápido y maduro
  - Evita consultas repetitivas a la base de datos
  - Sin dependencias externas (Redis, etc.)

- **Cons:**
  - Caché local: cada instancia tiene su propia copia (inconsistencia entre instancias)
  - La invalidación de caché es compleja con relaciones entre entidades
  - Memoria limitada al heap de la JVM
  - No escala horizontalmente sin replicación o caché distribuida

### Opción B: Redis como Caché Distribuido

Redis externo como almacenamiento de caché compartido entre todas las instancias.

- **Pros:**
  - Caché compartida: todas las instancias ven los mismos datos
  - Redis es rápido (en memoria) y probado en producción
  - Soporta estructuras de datos avanzadas (sets, sorted sets para ranking)
  - TTL por clave, expiración automática
  - Puede servir también para sesiones, rate limiting, colas

- **Cons:**
  - Dependencia externa: un servicio más que operar
  - Mayor latencia que caché local (round trip de red)
  - Complejidad operativa (configuración, monitoreo, backup)
  - Costo de infraestructura adicional

### Opción C: Cache HTTP con Cabeceras (ETag + Cache-Control)

Cache a nivel HTTP mediante cabeceras, permitiendo que los clientes y proxies intermedios (CDN) cacheen las respuestas.

```java
@GetMapping("/{id}")
public ResponseEntity<MenuItemResponse> getById(@PathVariable UUID id) {
    MenuItemResult result = getMenuItemUseCase.execute(new MenuItemId(id));
    return ResponseEntity.ok()
        .cacheControl(CacheControl.maxAge(30, TimeUnit.SECONDS))
        .eTag(Integer.toHexString(result.hashCode()))
        .body(MenuItemResponse.from(result));
}
```

- **Pros:**
  - Sin dependencias de infraestructura adicional
  - Los clientes y CDNs pueden cachear respuestas
  - Reduce ancho de banda y carga en el servidor
  - ETag permite validación eficiente (304 Not Modified)

- **Cons:**
  - No reduce consultas a base de datos (el servidor aún procesa la request)
  - No todas las operaciones son cacheables (POST, PUT, DELETE)
  - Los clientes deben implementar correctamente las cabeceras

## Decisión

Se elige un **enfoque en dos fases:**

**Fase 1 (Inicial): Opción A (Caffeine + Spring Cache) a nivel de aplicación, combinado con Opción C (Cache HTTP).**
- Caché local Caffeine con TTL corto (30-60 segundos) para consultas de lectura
- Cache-Control y ETag en respuestas GET para cache HTTP
- Invalidación manual de caché en operaciones de escritura
- Sin dependencias externas para mantener simplicidad operativa

**Fase 2 (Escalado): Opción B (Redis) cuando sea necesario.**
- Migrar de Caffeine a Redis cuando haya múltiples instancias
- Redis Cache Manager para caché distribuida
- TTL configurables por tipo de recurso

**TTL propuestos por recurso (Fase 1):**

| Recurso | TTL Caffeine | TTL Cache-Control |
|---------|-------------|-------------------|
| Categorías | 60 seg | 60 seg |
| Items del Menú | 30 seg | 30 seg |
| Menús | 60 seg | 60 seg |
| Ingredientes | 120 seg | 120 seg |
| Alérgenos | 300 seg | 300 seg |
| Modificadores | 60 seg | 60 seg |

## Consecuencias

**Positivas:**
- Reducción significativa de consultas a base de datos en operaciones de lectura
- Mejora en la latencia de respuesta para el usuario final
- Sin dependencias externas en la fase inicial
- Cache-Control + ETag permiten ahorro de ancho de banda
- La invalidación por escritura mantiene los datos frescos

**Negativas:**
- TTL cortos significan que aún habrá consultas a base de datos
- La caché local Caffeine no replica entre instancias (problema al escalar)
- La invalidación manual requiere disciplina del desarrollador
- Las relaciones entre entidades complican la invalidación (ej. actualizar MenuItem requiere invalidar Menu)

**Mitigaciones:**
- Se usarán TTLs conservadores para minimizar datos obsoletos
- Se implementará invalidación en cascada para operaciones de escritura
- Se monitoreará la tasa de aciertos de caché (hit ratio) para ajustar TTLs
- Se documentará la estrategia de invalidación para cada recurso

## Referencias

- Spring Cache Abstraction — https://docs.spring.io/spring-framework/reference/integration/cache.html
- Caffeine Cache — https://github.com/ben-manes/caffeine
- HTTP Caching (MDN) — https://developer.mozilla.org/en-US/docs/Web/HTTP/Caching
- Redis Cache — https://redis.io/docs/manual/client-caching/
