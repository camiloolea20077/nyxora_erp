# Plan 003: Eliminar el secreto JWT por defecto y desactivar el seeder demo por defecto

> **Executor instructions**: Follow this plan step by step. Run every
> verification command and confirm the expected result before moving to the
> next step. If anything in the "STOP conditions" section occurs, stop and
> report — do not improvise. When done, update the status row for this plan
> in `plans/README.md`.
>
> **Drift check (run first)**: `git diff --stat 84c4959..HEAD -- src/main/resources/application.yml src/main/java/com/cloud_tecnoligical/nyxora_erp/security/JwtService.java src/main/java/com/cloud_tecnoligical/nyxora_erp/config/DevDataSeeder.java`
> If any in-scope file changed since this plan was written, compare the
> "Current state" excerpts against the live code before proceeding; on a
> mismatch, treat it as a STOP condition.

## Status

- **Priority**: P1
- **Effort**: S
- **Risk**: MED (rompe arranques que dependían de los defaults; mitigado con `.env.example` y mensajes de error claros)
- **Depends on**: none (coordinar con 001: su `AbstractIntegrationTest` deberá registrar `JWT_SECRET` tras este cambio)
- **Category**: security
- **Planned at**: commit `84c4959`, 2026-06-11

## Why this matters

Dos defaults peligrosos viajan en el repositorio y se activan solos si faltan variables de entorno en un despliegue:

1. **Secreto JWT por defecto committeado.** `application.yml:44` define un fallback para `JWT_SECRET`. Ese valor es público (está en git); si un despliegue olvida la variable, cualquiera puede firmar tokens válidos (incluido un claim `super_admin: true` o cualquier `empresa_id`) y entrar como quien quiera. Un secreto committeado se considera quemado.
2. **Seeder demo activado por defecto.** `application.yml:47` (`seed-demo: ${APP_SEED_DEMO:true}`) + `DevDataSeeder` crean el usuario `admin` con contraseña `admin123` y TODOS los permisos en cuanto la BD está vacía. En un despliegue nuevo sin `APP_SEED_DEMO=false`, producción nace con credenciales conocidas.

El arreglo es hacer ambos opt-in: sin `JWT_SECRET` la app no arranca (fail-fast con mensaje claro), y el seeder solo corre con `APP_SEED_DEMO=true` explícito.

## Current state

- `src/main/resources/application.yml:41-47`:

  ```yaml
  # Seguridad JWT (secreto por env en producción)
  app:
    jwt:
      secret: ${JWT_SECRET:nyxora-dev-secret-cambiar-en-produccion-min-32-bytes-0123456789}
      access-exp-min: ${JWT_ACCESS_EXP_MIN:60}
      refresh-exp-days: ${JWT_REFRESH_EXP_DAYS:7}
    seed-demo: ${APP_SEED_DEMO:true}   # crea empresa + usuario admin demo al arrancar (dev)
  ```

- `src/main/java/com/cloud_tecnoligical/nyxora_erp/security/JwtService.java:27-35` — constructor que consume el secreto:

  ```java
  public JwtService(
      @Value("${app.jwt.secret}") String secret,
      @Value("${app.jwt.access-exp-min}") long accessExpMin,
      @Value("${app.jwt.refresh-exp-days}") long refreshExpDays
  ) {
      this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
      ...
  }
  ```

  (`Keys.hmacShaKeyFor` ya rechaza claves < 256 bits lanzando `WeakKeyException`, pero con un mensaje genérico en inglés.)

- `src/main/java/com/cloud_tecnoligical/nyxora_erp/config/DevDataSeeder.java:28-33` — ya lee `@Value("${app.seed-demo:false}")` con default propio `false`; el `true` efectivo viene del yml.
- Existe `.env` en la raíz (ignorado por `.gitignore`, línea `.env`) con las variables reales de dev. **No existe `.env.example`.**
- `README` de setup: no hay (solo `HELP.md` generado). `CLAUDE.md` documenta comandos.

## Commands you will need

| Purpose   | Command                            | Expected on success |
|-----------|------------------------------------|---------------------|
| Compile   | `mvnw.cmd -q -B compile`           | exit 0              |
| Tests     | `mvnw.cmd -B test`                 | BUILD SUCCESS       |

## Scope

**In scope**:
- `src/main/resources/application.yml`
- `src/main/java/com/cloud_tecnoligical/nyxora_erp/security/JwtService.java`
- `.env.example` (create — SOLO placeholders, jamás valores reales)
- `src/test/java/com/cloud_tecnoligical/nyxora_erp/AbstractIntegrationTest.java` (si existe, añadir `JWT_SECRET` de pruebas)
- `src/test/java/com/cloud_tecnoligical/nyxora_erp/security/JwtServiceTest.java` (si existe, ampliar)

**Out of scope**:
- `.env` real del desarrollador — NO leer su contenido, NO committearlo, NO copiarlo a ningún archivo.
- `DevDataSeeder.java` — su default propio ya es `false`; basta cambiar el yml.
- `docker-compose.yml` y credenciales de la BD local.
- Rotación del secreto en entornos desplegados (acción operativa: anotar en el commit/PR que cualquier entorno que haya corrido con el default debe rotar `JWT_SECRET`).

## Git workflow

- Branch: `advisor/003-guardas-produccion`
- Commit: `fix: exigir JWT_SECRET explicito y seeder demo opt-in`
- No push/PR sin instrucción del operador.

## Steps

### Step 1: Quitar los defaults del yml

En `application.yml` cambiar las dos líneas:

```yaml
      secret: ${JWT_SECRET}
  seed-demo: ${APP_SEED_DEMO:false}   # crea empresa + usuario admin demo al arrancar (SOLO dev, opt-in)
```

**Verify**: `mvnw.cmd -q -B compile` → exit 0.

### Step 2: Fail-fast con mensaje claro en JwtService

En el constructor de `JwtService`, antes de `Keys.hmacShaKeyFor`, validar:

```java
if (secret == null || secret.isBlank()) {
    throw new IllegalStateException(
        "JWT_SECRET no configurado. Defina la variable de entorno JWT_SECRET (mínimo 32 bytes).");
}
if (secret.getBytes(StandardCharsets.UTF_8).length < 32) {
    throw new IllegalStateException(
        "JWT_SECRET demasiado corto: se requieren mínimo 32 bytes para HS256.");
}
```

**Verify**: `mvnw.cmd -q -B compile` → exit 0.

### Step 3: Crear `.env.example`

Crear en la raíz `.env.example` con placeholders (alineado con `docker-compose.yml`, que expone PostgreSQL en el puerto host 5433, BD `nyxora_erp`, usuario `nyxora`):

```dotenv
# Copiar a .env y ajustar. NUNCA committear .env.
SERVER_PORT=8081
R2DBC_URL=r2dbc:postgresql://localhost:5433/nyxora_erp
JDBC_URL=jdbc:postgresql://localhost:5433/nyxora_erp
DB_USER=nyxora
DB_PASSWORD=cambiar-aqui
# Generar uno propio: openssl rand -base64 48
JWT_SECRET=generar-un-secreto-aleatorio-de-minimo-32-bytes
JWT_ACCESS_EXP_MIN=60
JWT_REFRESH_EXP_DAYS=7
# Sembrar datos demo (admin/admin123) SOLO en desarrollo
APP_SEED_DEMO=true
```

**Verify**: `git status` muestra `.env.example` como nuevo; `.env` NO aparece en `git status` (sigue ignorado).

### Step 4: Ajustar la suite de tests (si el plan 001 ya corrió)

Si existe `src/test/java/com/cloud_tecnoligical/nyxora_erp/AbstractIntegrationTest.java`, añadir en su `@DynamicPropertySource`:

```java
registry.add("JWT_SECRET", () -> "secreto-de-pruebas-de-integracion-minimo-32-bytes!!");
```

Si existe `JwtServiceTest`, añadir dos casos: constructor con secreto en blanco → `IllegalStateException`; con secreto de 10 bytes → `IllegalStateException`.

**Verify**: `mvnw.cmd -B test` → BUILD SUCCESS (si el plan 001 no corrió aún: `mvnw.cmd -q -B compile` → exit 0 y anotar que los tests quedaron para después de 001).

## Test plan

- Unit: 2 casos nuevos en `JwtServiceTest` (secreto vacío / corto → `IllegalStateException`).
- Integración: la suite del plan 001 sigue en verde con el `JWT_SECRET` de pruebas registrado.
- Verificación: `mvnw.cmd -B test` → todo pasa.

## Done criteria

- [ ] `grep -n "nyxora-dev-secret" src/main/resources/application.yml` → sin resultados
- [ ] `grep -n "APP_SEED_DEMO:false" src/main/resources/application.yml` → 1 resultado
- [ ] `JwtService` lanza `IllegalStateException` con mensaje en español si el secreto falta o es corto
- [ ] Existe `.env.example` solo con placeholders (ningún valor real de `.env`)
- [ ] `mvnw.cmd -B test` exit 0 (o compile exit 0 si 001 no está hecho, con nota)
- [ ] Fila actualizada en `plans/README.md`

## STOP conditions

- Cualquier paso requiere leer o copiar el contenido del `.env` real → no hacerlo; los placeholders bastan.
- El arranque local del operador depende del default eliminado y no hay `.env` — avisar antes de mergear (el `.env.example` es la mitigación).
- `DevDataSeeder` resulta tener otro mecanismo de activación distinto a `app.seed-demo` (drift).

## Maintenance notes

- **Acción operativa obligatoria**: cualquier entorno que haya corrido con el secreto por defecto debe rotar `JWT_SECRET` (el valor está quemado en el historial de git). Incluir esta nota en el mensaje de commit.
- Revisor: verificar que ningún perfil (`application-*.yml` futuro) reintroduzca un default para el secreto.
- `CLAUDE.md` debería mencionar `.env.example` en el onboarding (ver hallazgo DX-01 del índice).
