# Plan 002: Rechazar refresh tokens como tokens de acceso y validar usuario activo en refresh

> **Executor instructions**: Follow this plan step by step. Run every
> verification command and confirm the expected result before moving to the
> next step. If anything in the "STOP conditions" section occurs, stop and
> report — do not improvise. When done, update the status row for this plan
> in `plans/README.md`.
>
> **Drift check (run first)**: `git diff --stat 84c4959..HEAD -- src/main/java/com/cloud_tecnoligical/nyxora_erp/security src/main/java/com/cloud_tecnoligical/nyxora_erp/service/impl/AuthServiceImpl.java src/main/java/com/cloud_tecnoligical/nyxora_erp/repository/auth`
> If any in-scope file changed since this plan was written, compare the
> "Current state" excerpts against the live code before proceeding; on a
> mismatch, treat it as a STOP condition.

## Status

- **Priority**: P1
- **Effort**: S
- **Risk**: LOW
- **Depends on**: plans/001-baseline-pruebas-integracion.md (para los tests de integración; los cambios de código no dependen de él)
- **Category**: security
- **Planned at**: commit `84c4959`, 2026-06-11

## Why this matters

Dos agujeros en el ciclo de vida de tokens:

1. **Un refresh token sirve como token de acceso.** `JwtAuthenticationWebFilter` valida la firma pero nunca comprueba el claim `type`. Un refresh token (vida de 7 días vs 60 minutos del access) presentado como `Authorization: Bearer ...` pasa `.anyExchange().authenticated()` con un `TenantInfo` cuyo `empresaId` es `null` (el refresh no lleva `empresa_id`). Eso anula el diseño de expiración corta y mete `empresa_id = null` en todas las queries del request.
2. **Un usuario desactivado o eliminado puede refrescar para siempre.** `AuthServiceImpl.refresh` usa `usuarioR2dbcRepository.findById(...)`, que NO filtra `activo = true` ni `deleted_at IS NULL`. Deshabilitar o soft-borrar un usuario no corta su sesión: con su refresh token sigue obteniendo access tokens nuevos indefinidamente.

## Current state

- `src/main/java/com/cloud_tecnoligical/nyxora_erp/security/JwtAuthenticationWebFilter.java:37-53` — parsea y autentica sin mirar `type`:

  ```java
  String token = header.substring(7);
  try {
      Claims claims = jwtService.parse(token);
      TenantInfo info = jwtService.toTenantInfo(claims);
      List<SimpleGrantedAuthority> authorities = jwtService.permisos(claims).stream()
          .map(SimpleGrantedAuthority::new)
          .toList();
      var authentication = new UsernamePasswordAuthenticationToken(
          info.getUsuarioId(), null, authorities);
      return chain.filter(exchange)
          .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication))
          .contextWrite(TenantContext.write(info));
  } catch (Exception e) {
      // token inválido/expirado → sigue sin autenticación
      return chain.filter(exchange);
  }
  ```

- `src/main/java/com/cloud_tecnoligical/nyxora_erp/security/JwtService.java:41-66` — el access lleva `claim("type", "access")` y `claim("empresa_id", ...)`; el refresh lleva `claim("type", "refresh")` y NO lleva `empresa_id`.
- `src/main/java/com/cloud_tecnoligical/nyxora_erp/service/impl/AuthServiceImpl.java:60-79` — `refresh(...)` valida `type=refresh` pero carga el usuario con `findById` (sin filtro de activo/borrado):

  ```java
  Long usuarioId = Long.valueOf(((Claims) claims).getSubject());
  return usuarioR2dbcRepository.findById(usuarioId)
      .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.UNAUTHORIZED, "Usuario no válido")))
  ```

- `src/main/java/com/cloud_tecnoligical/nyxora_erp/repository/auth/AuthQueryRepository.java` — ya tiene el patrón a imitar, `findActiveByUsername` (SQL nativo con `DatabaseClient`, alias camelCase entre comillas, filtros `activo = true AND deleted_at IS NULL`, mapeo con `MapperRepository.mapResultSetToObject(row, UsuarioAuthDto.class)`).
- `dto/auth/UsuarioAuthDto.java` — DTO con `id`, `empresaId`, `hashPassword`, `activo`.
- Convenciones del repo: errores esperables = `throw`/`Mono.error` de `GlobalException(HttpStatus, "mensaje en español")`; SQL nativo parametrizado con `:param`; nada bloqueante.

## Commands you will need

| Purpose   | Command                                  | Expected on success |
|-----------|-------------------------------------------|---------------------|
| Compile   | `mvnw.cmd -q -B compile`                  | exit 0              |
| Tests     | `mvnw.cmd -B test`                        | BUILD SUCCESS       |
| Solo auth | `mvnw.cmd -B test -Dtest=AuthFlowIT`      | all pass            |

## Scope

**In scope**:
- `src/main/java/com/cloud_tecnoligical/nyxora_erp/security/JwtAuthenticationWebFilter.java`
- `src/main/java/com/cloud_tecnoligical/nyxora_erp/service/impl/AuthServiceImpl.java`
- `src/main/java/com/cloud_tecnoligical/nyxora_erp/repository/auth/AuthQueryRepository.java`
- `src/test/java/com/cloud_tecnoligical/nyxora_erp/auth/AuthFlowIT.java` (ampliar)
- `src/test/java/com/cloud_tecnoligical/nyxora_erp/security/JwtServiceTest.java` (ampliar si aplica)

**Out of scope**:
- `JwtService.java` — la emisión de tokens NO cambia (los claims ya son correctos).
- `SecurityConfig.java` — las rutas públicas no cambian.
- Rotación/revocación de refresh tokens (lista de revocación, jti) — mejora futura, no improvisar aquí.
- DTOs de auth y `TokenResponseDto`.

## Git workflow

- Branch: `advisor/002-jwt-tipo-token`
- Commit estilo repo (español, conventional): `fix: rechazar refresh token como access y validar usuario activo en refresh`
- No push/PR sin instrucción del operador.

## Steps

### Step 1: Validar `type=access` y `empresa_id` en el filtro

En `JwtAuthenticationWebFilter.filter`, después de `Claims claims = jwtService.parse(token);` y antes de construir el `TenantInfo`, añadir:

```java
if (!"access".equals(claims.get("type")) || claims.get("empresa_id") == null) {
    // No es un token de acceso válido → continúa sin autenticación (401 en rutas protegidas)
    return chain.filter(exchange);
}
```

No lanzar excepción: el contrato existente del filtro es "token inválido → seguir sin autenticación" y `SecurityConfig` produce el 401.

**Verify**: `mvnw.cmd -q -B compile` → exit 0.

### Step 2: Añadir `findActiveById` a AuthQueryRepository

Siguiendo el patrón exacto de `findActiveByUsername` en el mismo archivo:

```java
public Mono<UsuarioAuthDto> findActiveById(Long usuarioId) {
    String sql = """
        SELECT u.id            AS "id",
               u.empresa_id    AS "empresaId",
               u.hash_password AS "hashPassword",
               u.activo        AS "activo"
        FROM usuario u
        WHERE u.id = :id
          AND u.activo = true
          AND u.deleted_at IS NULL
        LIMIT 1
        """;
    return db.sql(sql)
        .bind("id", usuarioId)
        .fetch().one()
        .map(row -> MapperRepository.mapResultSetToObject(row, UsuarioAuthDto.class));
}
```

Nota: `UsuarioAuthDto` no tiene `username`; en el paso 3 se ve cómo obtenerlo.

**Verify**: `mvnw.cmd -q -B compile` → exit 0.

### Step 3: Usar el repositorio filtrado en `refresh`

En `AuthServiceImpl.refresh`, reemplazar `usuarioR2dbcRepository.findById(usuarioId)` por `authQueryRepository.findActiveById(usuarioId)`, manteniendo el `switchIfEmpty(... "Usuario no válido")`. El bloque interior usa hoy `usuario.getEmpresa_id()` y `usuario.getUsername()` de la entidad. Con el DTO: `empresaId` viene de `UsuarioAuthDto.getEmpresaId()`. Para `username` hay dos opciones; usar la (a):

(a) Añadir `u.username AS "username"` al SELECT del paso 2 y un campo `private String username;` a `UsuarioAuthDto` (con getter/setter o Lombok según esté escrito ese DTO — revisarlo y replicar su estilo).

Tras el cambio, la dependencia `usuarioR2dbcRepository` puede quedar sin uso en `AuthServiceImpl`; si es el único uso, eliminar el campo y el parámetro del constructor.

**Verify**: `mvnw.cmd -q -B compile` → exit 0.

### Step 4: Tests

En `AuthFlowIT` (creado por el plan 001) añadir:

1. `refresh_token_no_sirve_como_access`: hacer login (`admin/admin123`), tomar el **refresh** token de la respuesta y usarlo como `Authorization: Bearer` contra `POST /api/terceros/list` → esperar 401.
2. `refresh_devuelve_nuevos_tokens`: `POST /api/auth/refresh` con el refresh token válido → 200 y nuevo access token utilizable.
3. (Opcional si hay endpoint para desactivar usuarios sin fricción; si no, omitir y anotarlo en el commit) usuario desactivado → refresh devuelve 401.

**Verify**: `mvnw.cmd -B test` → BUILD SUCCESS, incluye los tests nuevos.

## Test plan

Casos listados en el paso 4; patrón estructural: los tests existentes de `AuthFlowIT` (plan 001). Verificación: `mvnw.cmd -B test -Dtest=AuthFlowIT` → todos pasan.

## Done criteria

- [ ] `mvnw.cmd -q -B compile` exit 0
- [ ] `mvnw.cmd -B test` exit 0; existe y pasa `refresh_token_no_sirve_como_access`
- [ ] `JwtAuthenticationWebFilter` contiene la comprobación `"access".equals(claims.get("type"))`
- [ ] `AuthServiceImpl.refresh` ya no usa `findById` sin filtros (grep: `grep -n "usuarioR2dbcRepository.findById" src/main/java/com/cloud_tecnoligical/nyxora_erp/service/impl/AuthServiceImpl.java` → sin resultados)
- [ ] Solo archivos in-scope modificados (`git status`)
- [ ] Fila actualizada en `plans/README.md`

## STOP conditions

- El plan 001 no está ejecutado y no existe `AuthFlowIT` — implementar igualmente los pasos 1–3 y reportar que los tests de integración quedaron pendientes (no inventar otra infraestructura de test).
- `UsuarioAuthDto` tiene una estructura distinta a la descrita (sin `empresaId`/`hashPassword`/`activo`).
- Algún consumidor adicional de `usuarioR2dbcRepository` dentro de `AuthServiceImpl` que no sea el `refresh` (revisar antes de eliminar la dependencia).

## Maintenance notes

- Si más adelante se implementa rotación/revocación de refresh tokens (jti + lista de revocación), este `refresh` es el punto de integración.
- Revisor: confirmar que el filtro sigue dejando pasar requests SIN header Authorization (rutas públicas) — el cambio solo afecta tokens presentes con `type != access`.
- El plan 006 (RBAC) asume que solo tokens `access` llegan autenticados; este plan es prerequisito lógico.
