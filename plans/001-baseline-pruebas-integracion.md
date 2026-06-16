# Plan 001: Establecer baseline de verificación con pruebas de integración (Testcontainers)

> **Executor instructions**: Follow this plan step by step. Run every
> verification command and confirm the expected result before moving to the
> next step. If anything in the "STOP conditions" section occurs, stop and
> report — do not improvise. When done, update the status row for this plan
> in `plans/README.md`.
>
> **Drift check (run first)**: `git diff --stat 84c4959..HEAD -- pom.xml src/test src/main/resources/application.yml`
> If any in-scope file changed since this plan was written, compare the
> "Current state" excerpts against the live code before proceeding; on a
> mismatch, treat it as a STOP condition.

## Status

- **Priority**: P1
- **Effort**: M
- **Risk**: LOW
- **Depends on**: none
- **Category**: tests
- **Planned at**: commit `84c4959`, 2026-06-11

## Why this matters

El repo tiene ~250 clases de producción (37 controllers, contabilidad, inventario, compras) y **una sola prueba**: el `contextLoads` generado por Spring Initializr, que además **falla en un checkout limpio** porque `application.yml` usa placeholders sin default (`${SERVER_PORT}`, `${R2DBC_URL}`, etc.) y requiere PostgreSQL corriendo. Hoy la única verificación posible es `mvnw.cmd -q -B compile`. Los planes 002–006 (seguridad JWT, login multi-tenant, RBAC) tocan autenticación y no pueden ejecutarse con confianza sin una suite que arranque el contexto contra una BD real y ejercite el flujo de login. El propio roadmap del equipo (`docs/roadmap-fases-sprints.md`) registra "tests WebTestClient" como deuda de DoD en todos los sprints.

## Current state

- `src/test/java/com/cloud_tecnoligical/nyxora_erp/NyxoraErpApplicationTests.java` — única prueba:

  ```java
  @SpringBootTest
  class NyxoraErpApplicationTests {
      @Test
      void contextLoads() {
      }
  }
  ```

- `src/main/resources/application.yml` (líneas 4–30) — todos los placeholders SIN default; sin env vars el contexto no arranca:

  ```yaml
  server:
    port: ${SERVER_PORT}
  spring:
    r2dbc:
      url: ${R2DBC_URL}
      username: ${DB_USER}
      password: ${DB_PASSWORD}
    flyway:
      url: ${JDBC_URL}
      user: ${DB_USER}
      password: ${DB_PASSWORD}
  ```

  y (líneas 42–47):

  ```yaml
  app:
    jwt:
      secret: ${JWT_SECRET:nyxora-dev-secret-cambiar-en-produccion-min-32-bytes-0123456789}
    seed-demo: ${APP_SEED_DEMO:true}
  ```

- `pom.xml` — Spring Boot parent `3.5.14`, Java 21. Dependencias de test actuales: `spring-boot-starter-test`, `reactor-test`. **No hay Testcontainers.**
- `src/main/java/com/cloud_tecnoligical/nyxora_erp/config/DevDataSeeder.java` — si `app.seed-demo=true` y la tabla `empresa` está vacía, siembra empresa demo + usuario `admin` con contraseña `admin123` y todos los permisos. Esto sirve para la prueba de login.
- `src/main/java/com/cloud_tecnoligical/nyxora_erp/controller/AuthController.java` — expone `POST /api/auth/login` (body `LoginRequestDto` con `username`/`password`), responde `ApiResponse<TokenResponseDto>` (el token de acceso está en `data.accessToken`; verificar el nombre exacto del campo en `dto/auth/TokenResponseDto.java` antes de asertar).
- `SecurityConfig.java` — rutas públicas: `/api/auth/**`, swagger, actuator; todo lo demás requiere autenticación (401 sin token).
- Hay Docker disponible en el entorno de desarrollo (el proyecto usa `docker-compose.yml` con `postgres:16`).
- Convenciones: capa de test puede ser inglés; mensajes de aserción libres. Inyección por constructor; no usar `@Autowired` de campo en producción (en tests `@Autowired` de campo es aceptable para `WebTestClient`).

## Commands you will need

| Purpose   | Command                            | Expected on success |
|-----------|------------------------------------|---------------------|
| Compile   | `mvnw.cmd -q -B compile`           | exit 0              |
| Tests     | `mvnw.cmd -B test`                 | exit 0, BUILD SUCCESS |
| Docker up | `docker info`                      | exit 0 (daemon corriendo) |

(En PowerShell de Windows usar `.\mvnw.cmd`; en bash `./mvnw`.)

## Scope

**In scope** (the only files you should modify/create):
- `pom.xml` (solo añadir dependencias de test)
- `src/test/java/com/cloud_tecnoligical/nyxora_erp/NyxoraErpApplicationTests.java`
- `src/test/java/com/cloud_tecnoligical/nyxora_erp/AbstractIntegrationTest.java` (create)
- `src/test/java/com/cloud_tecnoligical/nyxora_erp/auth/AuthFlowIT.java` (create)
- `src/test/java/com/cloud_tecnoligical/nyxora_erp/security/JwtServiceTest.java` (create)

**Out of scope** (do NOT touch):
- Cualquier archivo bajo `src/main/` — este plan NO cambia código de producción.
- `src/main/resources/application.yml` — los placeholders se resuelven vía `@DynamicPropertySource`, no editando el yml.
- `docker-compose.yml`.

## Git workflow

- Branch: `advisor/001-baseline-pruebas`
- Estilo de commit observado en `git log`: prefijos en español tipo conventional commits (`feat: ...`, `fix: ...`). Usar p. ej. `test: baseline de integracion con testcontainers`.
- No hacer push ni abrir PR salvo instrucción del operador.

## Steps

### Step 1: Añadir dependencias de Testcontainers al pom

En `pom.xml`, dentro de `<dependencies>`, añadir (scope test; las versiones las gestiona el BOM del parent de Spring Boot, no declarar `<version>`):

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-testcontainers</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
```

**Verify**: `mvnw.cmd -q -B compile` → exit 0.

### Step 2: Crear la clase base de integración

Crear `src/test/java/com/cloud_tecnoligical/nyxora_erp/AbstractIntegrationTest.java`:

```java
package com.cloud_tecnoligical.nyxora_erp;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractIntegrationTest {

    // Contenedor único compartido por toda la suite (patrón singleton container).
    static final PostgreSQLContainer<?> POSTGRES =
        new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("nyxora_test")
            .withUsername("nyxora")
            .withPassword("nyxora");

    static {
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        // Resuelve los placeholders de application.yml sin tocar el archivo.
        registry.add("SERVER_PORT", () -> "0");
        registry.add("JDBC_URL", POSTGRES::getJdbcUrl);
        registry.add("R2DBC_URL", () -> "r2dbc:postgresql://" + POSTGRES.getHost()
            + ":" + POSTGRES.getMappedPort(5432) + "/nyxora_test");
        registry.add("DB_USER", POSTGRES::getUsername);
        registry.add("DB_PASSWORD", POSTGRES::getPassword);
        // seed-demo=true siembra admin/admin123 (DevDataSeeder) para la prueba de login.
        registry.add("APP_SEED_DEMO", () -> "true");
    }
}
```

Nota: NO registrar `JWT_SECRET`; `application.yml` tiene default de dev. Si el plan 003 ya se ejecutó (el default fue eliminado), registrar también `registry.add("JWT_SECRET", () -> "secreto-de-pruebas-de-integracion-minimo-32-bytes!!");`.

**Verify**: `mvnw.cmd -q -B compile` → exit 0 (el código de test compila con `mvnw.cmd -B test-compile`; usar ese goal: exit 0).

### Step 3: Convertir contextLoads en prueba de integración real

Editar `NyxoraErpApplicationTests` para que extienda la base:

```java
class NyxoraErpApplicationTests extends AbstractIntegrationTest {
    @Test
    void contextLoads() {
    }
}
```

(Quitar la anotación `@SpringBootTest` propia: la hereda de la base.)

**Verify**: `mvnw.cmd -B test -Dtest=NyxoraErpApplicationTests` → BUILD SUCCESS. Esto valida que las 28 migraciones Flyway aplican sobre PostgreSQL 16 limpio y el contexto arranca.

### Step 4: Prueba del flujo de autenticación

Crear `src/test/java/com/cloud_tecnoligical/nyxora_erp/auth/AuthFlowIT.java` con `WebTestClient` (autowired; con `webEnvironment = RANDOM_PORT` se puede inyectar `@Autowired WebTestClient`... si la inyección directa falla, añadir `@AutoConfigureWebTestClient` a `AbstractIntegrationTest`). Casos:

1. `protected_endpoint_sin_token_devuelve_401`: `GET /api/terceros/1` sin header → `expectStatus().isUnauthorized()`.
2. `login_con_credenciales_invalidas_devuelve_401`: `POST /api/auth/login` body `{"username":"admin","password":"incorrecta"}` → 401.
3. `login_y_acceso_con_token`: `POST /api/auth/login` body `{"username":"admin","password":"admin123"}` → 200; extraer el access token del JSON de respuesta (`ApiResponse<TokenResponseDto>`: inspeccionar `dto/auth/TokenResponseDto.java` para el nombre exacto del campo, p. ej. `data.accessToken`); luego `POST /api/terceros/list` con header `Authorization: Bearer <token>` y body `{"page":0,"rows":10}` → 200.

**Verify**: `mvnw.cmd -B test -Dtest=AuthFlowIT` → 3 tests, todos pasan.

### Step 5: Prueba unitaria de JwtService

Crear `src/test/java/com/cloud_tecnoligical/nyxora_erp/security/JwtServiceTest.java` (unitaria pura, sin Spring; instanciar `new JwtService("un-secreto-de-pruebas-de-al-menos-32-bytes!!", 60, 7)`). Casos:

1. `generateAccess` + `parse` roundtrip: claims `empresa_id`, `type=access`, subject = usuarioId.
2. `generateRefresh` + `parse`: claim `type=refresh`.
3. `toTenantInfo` reconstruye empresaId/usuarioId/superAdmin.
4. `parse` con token firmado con OTRO secreto → lanza excepción (`assertThrows`).

**Verify**: `mvnw.cmd -B test -Dtest=JwtServiceTest` → 4 tests pasan.

### Step 6: Suite completa

**Verify**: `mvnw.cmd -B test` → BUILD SUCCESS, 0 failures, 0 errors (8+ tests en total).

## Test plan

Este plan ES el plan de pruebas; los casos están en los pasos 3–5. No hay test existente que sirva de patrón (este plan crea el primero); los futuros tests de integración deben extender `AbstractIntegrationTest`.

## Done criteria

- [ ] `mvnw.cmd -q -B compile` exit 0
- [ ] `mvnw.cmd -B test` exit 0 con ≥8 tests ejecutados y 0 fallos
- [ ] Existen `AbstractIntegrationTest.java`, `AuthFlowIT.java`, `JwtServiceTest.java`
- [ ] Ningún archivo bajo `src/main/` modificado (`git status`)
- [ ] Fila de estado actualizada en `plans/README.md`

## STOP conditions

Stop and report back if:

- `docker info` falla (no hay daemon Docker disponible) — Testcontainers no puede correr.
- Las migraciones Flyway fallan sobre PostgreSQL 16 limpio en el paso 3 (eso sería un hallazgo nuevo: migraciones rotas en checkout limpio).
- El login del paso 4 devuelve 401 con `admin/admin123` (indicaría que `DevDataSeeder` no sembró; revisar logs del contexto antes de continuar, y si la causa no es obvia, reportar).
- La inyección de `WebTestClient` falla incluso con `@AutoConfigureWebTestClient`.

## Maintenance notes

- Los planes 002, 004, 005 y 006 añaden tests que extienden `AbstractIntegrationTest`; cualquier cambio a esta clase los afecta.
- Si el plan 003 elimina el default de `JWT_SECRET`, esta suite necesita registrar `JWT_SECRET` en `@DynamicPropertySource` (ver nota del paso 2).
- Deuda diferida adelante: CI (GitHub Actions) que ejecute `mvnw -B test` — el roadmap ya lo lista como pendiente.
