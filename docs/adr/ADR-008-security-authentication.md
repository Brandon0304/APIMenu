# ADR-008: Estrategia de Seguridad y Autenticación

## Status: Aceptado

## Contexto

La API de gestión de menú necesita definir cómo manejará la autenticación y autorización de los consumidores. Aunque el alcance inicial se centra en la gestión del menú (operaciones CRUD sobre categorías, platillos, menús, etc.), es necesario sentar las bases de seguridad para evitar exponer datos sensibles y permitir que solo usuarios autorizados modifiquen el catálogo.

**Restricciones:**
- La API puede ser consumida por aplicaciones frontend (web, mobile) y potencialmente por integraciones third-party
- Algunas operaciones (crear, actualizar, eliminar) deben ser restringidas a administradores
- Las operaciones de consulta (listar, obtener) pueden ser públicas o con autenticación mínima
- Se debe considerar la protección de datos personales según la legislación colombiana (Ley 1581 de 2012)
- La solución debe ser stateless para facilitar el escalado horizontal

## Opciones Consideradas

### Opción A: JWT (JSON Web Tokens) con Spring Security 6

Autenticación stateless basada en tokens JWT firmados. Spring Security 6 maneja la cadena de filtros, y un filtro personalizado valida el JWT en cada request.

**Roles propuestos:**
- `ROLE_ADMIN`: Acceso completo a todas las operaciones (CRUD de menú, categorías, etc.)
- `ROLE_VIEWER`: Acceso de solo lectura a los endpoints de consulta
- `ROLE_KITCHEN`: Acceso a lectura de items del menú y gestión de disponibilidad

- **Pros:**
  - Stateless: no requiere sesión en servidor, escalable horizontalmente
  - Estándar ampliamente adoptado (JWT, OAuth2)
  - Spring Security 6 tiene soporte nativo para JWT y Resource Server
  - El token puede contener claims (roles, permisos, restauranteId)
  - Fácil de consumir desde frontends web y mobile

- **Cons:**
  - Los tokens JWT no pueden revocarse fácilmente (requieren blacklist)
  - Mayor complejidad de configuración inicial
  - El tamaño del token puede ser grande si se incluyen muchos claims
  - Requiere manejo seguro de la clave de firma (HS256 o RS256)

### Opción B: Sesiones HTTP con Spring Session

Autenticación basada en sesiones tradicionales, almacenadas en servidor o en Redis.

- **Pros:**
  - Simple de implementar
  - Facilidad para revocar sesiones
  - No requiere lógica de refresh token

- **Cons:**
  - Stateful: dificulta el escalado horizontal (requiere sticky sessions o Redis centralizado)
  - No es ideal para APIs REST consumidas por múltiples clientes
  - Mayor carga en el servidor para mantener sesiones

### Opción C: API Key para integraciones

Cada consumidor tiene una API key que se envía en el header. Sin roles ni autenticación de usuarios.

- **Pros:**
  - Extremadamente simple de implementar
  - Buena para integraciones machine-to-machine

- **Cons:**
  - Sin soporte para múltiples usuarios/roles
  - Sin capacidad de auditoría por usuario
  - No escala para aplicaciones con múltiples usuarios finales

## Decisión

Se elige **Opción A: JWT con Spring Security 6**, con un enfoque gradual:

**Fase 1 (MVP):** Autenticación por API Key simple para administradores. Sin roles complejos.

**Fase 2 (Evolución):** Sistema completo de autenticación con:
- Login con email y contraseña
- JWT firmado con RS256 (par de llaves asimétricas)
- Roles: ADMIN, VIEWER, KITCHEN
- Endpoints públicos: `GET /api/v1/categories`, `GET /api/v1/menu-items`
- Endpoints protegidos (ADMIN): `POST/PUT/DELETE` sobre cualquier recurso
- Refresh tokens para renovación segura

**Arquitectura de seguridad:**

```
┌──────────────┐     JWT (Bearer)     ┌─────────────────────┐
│   Cliente    │ ──────────────────→  │  SecurityFilterChain │
│  (Frontend)  │                     │  ┌───────────────┐  │
└──────────────┘                     │  │ JwtAuthFilter │  │
                                     │  └───────┬───────┘  │
                                     │          ▼           │
                                     │  ┌───────────────┐  │
                                     │  │  Authorization │  │
                                     │  │  (RBAC)       │  │
                                     │  └───────┬───────┘  │
                                     └──────────┼──────────┘
                                                ▼
                                     ┌─────────────────────┐
                                     │  Controller Layer    │
                                     │  (@PreAuthorize)    │
                                     └─────────────────────┘
```

**Configuración de seguridad:**

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session ->
                session.sessionCreationPolicy(STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(GET, "/api/v1/categories").permitAll()
                .requestMatchers(GET, "/api/v1/menu-items").permitAll()
                .requestMatchers(GET, "/api/v1/menus").permitAll()
                .requestMatchers(GET, "/api/v1/ingredients").permitAll()
                .requestMatchers(GET, "/api/v1/allergens").permitAll()
                .requestMatchers("/api/v1/**").authenticated()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

**Protección a nivel de método (RBAC):**

```java
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> create(@RequestBody @Valid CreateCategoryRequest request) {
        // solo ADMIN puede crear categorías
    }

    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<CategoryResponse>> list() {
        // cualquier persona puede ver categorías
    }
}
```

## Consecuencias

**Positivas:**
- Seguridad stateless y escalable horizontalmente
- Roles y permisos granulares a nivel de método
- Autenticación estándar (JWT + OAuth2 Resource Server)
- Fácil integración con frontends y third parties
- Protección de datos personales (la API solo expone datos del menú, no de clientes)

**Negativas:**
- Complejidad inicial de configuración de JWT y claves asimétricas
- Los tokens JWT tienen expiración fija (no revocables sin blacklist)
- Se debe gestionar el ciclo de vida de refresh tokens
- Dependencia de una solución de manejo de usuarios (tabla usuarios propia o SSO)

**Mitigaciones:**
- Se usará RS256 con rotación de llaves
- Tokens de acceso con expiración corta (15 min) + refresh tokens (7 días)
- La tabla de usuarios se mantendrá simple inicialmente (email + password hash + rol)
- Se considerará migración a Keycloak o Auth0 en el futuro si se requiere SSO

## Referencias

- Spring Security 6 Reference — https://docs.spring.io/spring-security/reference/
- JWT.io — https://jwt.io/
- RFC 7519: JSON Web Token — https://datatracker.ietf.org/doc/html/rfc7519
- OWASP REST Security Cheat Sheet — https://cheatsheetseries.owasp.org/cheatsheets/REST_Security_Cheat_Sheet.html
- Ley 1581 de 2012 (Colombia) — https://www.funcionpublica.gov.co/eva/gestornormativo/norma.php?i=49981
