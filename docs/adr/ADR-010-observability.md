# ADR-010: Estrategia de Observabilidad (Logs, Métricas, Trazas)

## Status: Aceptado

## Contexto

La API de gestión de menú necesita ser observable para poder operarse en producción. Esto incluye logging estructurado, métricas de negocio y rendimiento, y trazabilidad de requests.

**Restricciones:**
- Debe funcionar en entornos cloud (Kubernetes, Docker Compose)
- Los logs deben ser estructurados (JSON) para ser consumidos por herramientas de logging (Elasticsearch, Loki, CloudWatch)
- Las métricas deben exponerse para ser recolectadas por Prometheus
- La trazabilidad debe permitir seguir una request a través de los distintos componentes
- Mínima sobrecarga en rendimiento

## Opciones Consideradas

### Opción A: Spring Boot Actuator + Micrometer + Logback JSON + MDC

- **Actuator:** Endpoints de health, info, metrics, env
- **Micrometer:** Métricas expuestas en formato Prometheus (`/actuator/prometheus`)
- **Logback:** Configuración JSON (LogstashEncoder) + MDC para tracing
- **Métricas personalizadas:** Contadores de negocio (items creados, menús activos, etc.)

- **Pros:**
  - Stack nativo de Spring Boot, mínima configuración
  - Micrometer como fachada de métricas (vendor-neutral)
  - Integración directa con Prometheus y Grafana
  - MDC (Mapped Diagnostic Context) para correlation IDs
  - Logs JSON estructurados parseables por cualquier agregador

- **Cons:**
  - Sin trazabilidad distribuida completa (necesita Zipkin o Jaeger)
  - Logback JSON requiere dependencia adicional (logstash-logback-encoder)
  - Las métricas por defecto de Actuator pueden ser demasiado genéricas

### Opción B: Elastic APM o Datadog APM (SaaS)

Agente APM que se inyecta en la JVM y recolecta automáticamente trazas, métricas y logs.

- **Pros:**
  - Cero configuración en código
  - Trazabilidad distribuida completa con visualización
  - Detección automática de transacciones lentas
  - Correlación automática de logs y trazas

- **Cons:**
  - Dependencia de un servicio externo (costo)
  - Vendor lock-in
  - Overhead de rendimiento del agente
  - Complejidad operativa (instalación del agente, configuración de red)

### Opción C: Solo Logging Simple (Sin métricas ni trazas)

Logging tradicional en texto plano con SLF4J + Logback. Sin métricas ni tracing.

- **Pros:**
  - Mínima complejidad
  - Sin dependencias adicionales

- **Cons:**
  - Sin capacidad de monitoreo proactivo
  - Sin métricas para alertas
  - Sin trazabilidad para debugging de problemas
  - Logs en texto plano difíciles de parsear automáticamente

## Decisión

Se elige **Opción A: Spring Boot Actuator + Micrometer + Logback JSON + MDC** como stack de observabilidad.

**Implementación:**

### 1. Logging Estructurado (JSON)

```xml
<!-- logback-spring.xml -->
<configuration>
    <appender name="JSON" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="net.logstash.logback.encoder.LogstashEncoder">
            <includeMdc>true</includeMdc>
            <includeContext>false</includeContext>
            <customFields>{"application":"menu-api","environment":"${ENV:-local}"}</customFields>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="JSON"/>
    </root>
</configuration>
```

### 2. Correlation ID vía MDC Filter

```java
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter implements Filter {
    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        String correlationId = Optional.ofNullable(
            ((HttpServletRequest) request).getHeader(CORRELATION_ID_HEADER))
            .orElse(UUID.randomUUID().toString());

        MDC.put("correlationId", correlationId);
        ((HttpServletResponse) response).setHeader(CORRELATION_ID_HEADER, correlationId);

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
```

### 3. Métricas de Negocio

```java
@Component
public class MenuMetrics {
    private final MeterRegistry meterRegistry;

    public MenuMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void recordMenuItemCreated() {
        meterRegistry.counter("menu.items.created").increment();
    }

    public void recordMenuActivated() {
        meterRegistry.counter("menu.activated").increment();
    }

    public void recordCategoryDeleted() {
        meterRegistry.counter("categories.deleted").increment();
    }

    public void recordPriceCalculationTime(long millis) {
        meterRegistry.timer("menu.price.calculation.time")
            .record(Duration.ofMillis(millis));
    }
}
```

### 4. Health Checks Personalizados

```java
@Component
public class DatabaseHealthIndicator implements HealthIndicator {
    private final DataSource dataSource;

    @Override
    public Health health() {
        try (Connection conn = dataSource.getConnection()) {
            return conn.isValid(1000)
                ? Health.up().withDetail("database", "PostgreSQL").build()
                : Health.down().withDetail("database", "Connection invalid").build();
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }
}
```

### 5. Endpoints de Actuator

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus,env,loggers
  endpoint:
    health:
      show-details: when-authorized
      show-components: when-authorized
  metrics:
    tags:
      application: menu-api
      environment: ${ENV:local}
```

## Consecuencias

**Positivas:**
- Logs estructurados JSON parseables por cualquier agregador (ELK, Loki)
- Correlation ID en cada log para seguir requests
- Métricas de negocio y rendimiento expuestas para Prometheus/Grafana
- Health checks personalizados para monitoreo de dependencias
- Sin dependencias externas costosas (Elastic APM, Datadog)
- Stack probado y maduro (Spring Boot Actuator + Micrometer)

**Negativas:**
- Dependencia de logstash-logback-encoder para JSON
- Las métricas de negocio requieren instrumentación manual
- Sin trazabilidad distribuida automática (requiere OpenTelemetry para tracing completo)
- Los health checks personalizados deben mantenerse actualizados

**Mitigaciones:**
- La instrumentación de métricas se integra en los casos de uso (capa de aplicación)
- Se evaluará OpenTelemetry para tracing distribuido si la arquitectura crece
- Las métricas clave se documentan para facilitar la creación de dashboards de Grafana

## Referencias

- Spring Boot Actuator — https://docs.spring.io/spring-boot/docs/current/actuator.html
- Micrometer — https://micrometer.io/
- Prometheus — https://prometheus.io/
- Grafana — https://grafana.com/
- ELK Stack — https://www.elastic.co/what-is/elk-stack
- OpenTelemetry — https://opentelemetry.io/
