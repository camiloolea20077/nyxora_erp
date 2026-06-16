# Plan 004: Hacer determinista el login — username único global

> **Executor instructions**: Follow this plan step by step. Run every
> verification command and confirm the expected result before moving to the
> next step. If anything in the "STOP conditions" section occurs, stop and
> report — do not improvise. When done, update the status row for this plan
> in `plans/README.md`.
>
> **Drift check (run first)**: `git diff --stat 84c4959..HEAD -- src/main/resources/db/migration src/main/java/com/cloud_tecnoligical/nyxora_erp/repository/auth/AuthQueryRepository.java src/main/java/com/cloud_tecnoligical/nyxora_erp/repository/usuario/UsuarioQueryRepository.java src/main/java/com/cloud_tecnoligical/nyxora_erp/service/impl/UsuarioServiceImpl.java`
> Además: si existe alguna migración con número mayor a V28, renumerar la nueva
> migración de este plan al siguiente número libre. On a mismatch with the
> excerpts below, treat it as a STOP condition.

## Status

- **Priority**: P1
- **Effort**: M
- **Risk**: MED (añade una restricción de unicidad sobre datos existentes; mitigado con verificación previa de duplicados)
- **Depends on**: plans/001-baseline-pruebas-integracion.md (tests)
- **Category**: bug (correctness multi-tenant)
- **Planned at**: commit `84c4959`, 2026-06-11

## Why this matters

El esquema permite el mismo `username` en empresas distintas (`UNIQUE (empresa_id, username)` en `V1__administracion_schema.sql:70`), pero el login busca **solo por username con `LIMIT 1` y sin empresa**. Si dos empresas crean un usuario `jperez`, el login resuelve siempre a la fila que la BD devuelva primero: el segundo `jperez` **no puede iniciar sesión nunca** (su contraseña se compara contra el hash de otra persona). Es una bomba de tiempo silenciosa para un ERP multi-tenant: no falla hoy con una empresa, falla el día que la segunda empresa registra un username repetido, y el síntoma ("credenciales inválidas" intermitente) es difícil de diagnosticar.

Decisión adoptada: **username único global** (coincide con la UX actual de login solo con username). Alternativa considerada y descartada por ahora: pedir NIT/código de empresa en el login (cambia el contrato del API y el frontend; mayor blast radius).

## Current state

- `src/main/resources/db/migration/V1__administracion_schema.sql:70` — `CONSTRAINT uq_usuario_username UNIQUE (empresa_id, username)` (unicidad solo por empresa). Migración aplicada = inmutable; el cambio va en una migración NUEVA.
- `src/main/java/com/cloud_tecnoligical/nyxora_erp/repository/auth/AuthQueryRepository.java:25-41` — el login:

  ```java
  public Mono<UsuarioAuthDto> findActiveByUsername(String username) {
      String sql = """
          SELECT u.id AS "id", u.empresa_id AS "empresaId",
                 u.hash_password AS "hashPassword", u.activo AS "activo"
          FROM usuario u
          WHERE u.username = :username
            AND u.activo = true
            AND u.deleted_at IS NULL
          LIMIT 1
          """;
  ```

- `src/main/java/com/cloud_tecnoligical/nyxora_erp/repository/usuario/UsuarioQueryRepository.java:28-35` — el check de duplicado al CREAR usuario es por empresa (deja crear el duplicado cross-empresa que rompe el login):

  ```java
  public Mono<Boolean> existsActiveByUsername(String username, Long empresaId) {
      return db.sql("""
              SELECT count(*) AS c FROM usuario
              WHERE username = :username AND empresa_id = :empresaId AND deleted_at IS NULL
              """)
  ```

- `src/main/java/com/cloud_tecnoligical/nyxora_erp/service/impl/UsuarioServiceImpl.java:60-64` — usa ese check y responde `"Ya existe un usuario con ese nombre"`.
- Última migración existente: `V28__seed_tipos_tercero_entidades.sql`. Convención de nombres (regla 02): `V<n>__<modulo>_<proposito>.sql`, en `src/main/resources/db/migration/`.
- Regla 02: mantener sincronizado `.claude/data/diccionario-datos.md` con cada migración.

## Commands you will need

| Purpose   | Command                                  | Expected on success |
|-----------|-------------------------------------------|---------------------|
| Compile   | `mvnw.cmd -q -B compile`                  | exit 0              |
| Tests     | `mvnw.cmd -B test`                        | BUILD SUCCESS (las migraciones se aplican en el contenedor de pruebas; si V29 está mal, Flyway falla aquí) |
| BD local  | `docker compose up -d postgres`           | contenedor healthy   |

## Scope

**In scope**:
- `src/main/resources/db/migration/V29__administracion_username_unico_global.sql` (create)
- `src/main/java/com/cloud_tecnoligical/nyxora_erp/repository/usuario/UsuarioQueryRepository.java` (método `existsActiveByUsername`)
- `src/main/java/com/cloud_tecnoligical/nyxora_erp/service/impl/UsuarioServiceImpl.java` (llamada y mensaje)
- `.claude/data/diccionario-datos.md` (documentar el índice nuevo)
- `src/test/java/com/cloud_tecnoligical/nyxora_erp/usuario/UsuarioUniquenessIT.java` (create)

**Out of scope**:
- `V1__administracion_schema.sql` y cualquier migración ya aplicada — inmutables (regla 02). NO editarlas.
- `AuthQueryRepository.findActiveByUsername` — con unicidad global el `LIMIT 1` pasa a ser correcto; no cambiarlo.
- El contrato del API de login (`LoginRequestDto`) — no añadir campos.
- `DevDataSeeder` — solo siembra en BD vacía; no le afecta.

## Git workflow

- Branch: `advisor/004-username-unico-global`
- Commit: `fix: username unico global para login multi-tenant determinista`
- No push/PR sin instrucción del operador.

## Steps

### Step 1: Migración V29 con verificación de duplicados incluida

Crear `src/main/resources/db/migration/V29__administracion_username_unico_global.sql`:

```sql
-- Unicidad GLOBAL de username entre usuarios no eliminados.
-- Motivo: el login busca por username sin empresa; con duplicados cross-empresa
-- el segundo usuario no puede autenticarse (AuthQueryRepository.findActiveByUsername).
-- Si esta migración falla por duplicados existentes, resolverlos manualmente
-- (renombrar usernames) antes de reintentar; ver bloque DO de diagnóstico.

DO $$
DECLARE dup RECORD;
BEGIN
    FOR dup IN
        SELECT username, count(*) AS n
        FROM usuario
        WHERE deleted_at IS NULL
        GROUP BY username
        HAVING count(*) > 1
    LOOP
        RAISE EXCEPTION 'username duplicado entre empresas: % (% filas). Renombrar antes de aplicar V29.',
            dup.username, dup.n;
    END LOOP;
END $$;

CREATE UNIQUE INDEX uq_usuario_username_global
    ON usuario (username)
    WHERE deleted_at IS NULL;

COMMENT ON INDEX uq_usuario_username_global IS
    'Login global por username: unicidad entre todas las empresas (solo usuarios no eliminados).';
```

Notas de diseño: índice **parcial** (`WHERE deleted_at IS NULL`) para que un username de un usuario soft-borrado pueda reutilizarse; la constraint vieja `uq_usuario_username (empresa_id, username)` se conserva (no estorba).

**Verify**: `mvnw.cmd -B test -Dtest=NyxoraErpApplicationTests` → BUILD SUCCESS (Flyway aplica V29 sobre el contenedor limpio).

### Step 2: Endurecer el check de creación de usuarios

En `UsuarioQueryRepository.existsActiveByUsername`, quitar el filtro por empresa para que el check coincida con la nueva regla global:

```java
public Mono<Boolean> existsActiveByUsername(String username) {
    return db.sql("""
            SELECT count(*) AS c FROM usuario
            WHERE username = :username AND deleted_at IS NULL
            """)
        .bind("username", username)
        .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
}
```

Actualizar el llamador en `UsuarioServiceImpl.create` (hoy `existsActiveByUsername(dto.getUsername(), t.getEmpresaId())` → `existsActiveByUsername(dto.getUsername())`). Mantener el mensaje `"Ya existe un usuario con ese nombre"`. Buscar otros llamadores: `grep -rn "existsActiveByUsername" src/main/java/` y actualizarlos todos.

**Verify**: `mvnw.cmd -q -B compile` → exit 0 y `grep -rn "existsActiveByUsername(.*empresaId" src/main/java/` → sin resultados.

### Step 3: Documentar en el diccionario de datos

Añadir en `.claude/data/diccionario-datos.md`, en la sección de la tabla `usuario`, una línea sobre el índice `uq_usuario_username_global` (único, parcial sobre `deleted_at IS NULL`, motivo: login global). Seguir el formato de las entradas existentes del archivo.

**Verify**: `grep -n "uq_usuario_username_global" .claude/data/diccionario-datos.md` → 1 resultado.

### Step 4: Test de integración

Crear `src/test/java/com/cloud_tecnoligical/nyxora_erp/usuario/UsuarioUniquenessIT.java` extendiendo `AbstractIntegrationTest` (plan 001). Casos (vía `DatabaseClient` inyectado o el API REST con el token del admin demo):

1. Insertar (SQL directo) una segunda empresa y un usuario con username distinto → OK.
2. Intentar insertar en la segunda empresa un usuario con el username `admin` (existente en la empresa demo) → la BD rechaza con violación de unicidad (asertar el error), o el endpoint `POST /api/usuarios` devuelve 400 con "Ya existe un usuario con ese nombre".
3. Login `admin/admin123` sigue funcionando (sanity).

**Verify**: `mvnw.cmd -B test` → BUILD SUCCESS incluyendo los casos nuevos.

## Test plan

Casos en el paso 4; patrón estructural: `AuthFlowIT` (plan 001). Verificación: `mvnw.cmd -B test` → todo verde.

## Done criteria

- [ ] Existe `V29__administracion_username_unico_global.sql` y la suite (que migra desde cero) pasa
- [ ] `existsActiveByUsername` ya no recibe `empresaId` (grep del paso 2 sin resultados)
- [ ] Test que demuestra el rechazo del username duplicado cross-empresa pasa
- [ ] `.claude/data/diccionario-datos.md` documenta el índice
- [ ] `mvnw.cmd -B test` exit 0
- [ ] Solo archivos in-scope modificados (`git status`); fila actualizada en `plans/README.md`

## STOP conditions

- Ya existe una migración V29 (u otra posterior) en el repo — renumerar y, si toca `usuario`, reportar antes de seguir.
- El operador indica que hay entornos con datos reales que contienen usernames duplicados cross-empresa — la migración fallará allí por diseño; reportar para coordinar la limpieza, no debilitar la verificación.
- Se descubre que el producto REQUIERE usernames repetidos entre empresas (decisión de negocio contraria) — este plan implementa la alternativa (a); la alternativa (b) (login con identificador de empresa) es otro plan.
- `UsuarioServiceImpl.create` tiene más validaciones de username de las citadas (drift).

## Maintenance notes

- Si en el futuro se implementa "login por empresa" (multi-tenant con subdominios, por ejemplo), revertir esto requiere: borrar el índice en una migración nueva y rehacer `findActiveByUsername` con empresa.
- Revisor: confirmar que el seeder demo y los flujos de creación de usuario muestran un mensaje claro cuando el username está tomado por OTRA empresa (no revelar la existencia de la otra empresa: el mensaje genérico actual "Ya existe un usuario con ese nombre" cumple).
