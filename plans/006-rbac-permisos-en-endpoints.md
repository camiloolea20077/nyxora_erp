# Plan 006: Aplicar RBAC — exigir permisos en los endpoints mutadores

> **Executor instructions**: Follow this plan step by step. Run every
> verification command and confirm the expected result before moving to the
> next step. If anything in the "STOP conditions" section occurs, stop and
> report — do not improvise. When done, update the status row for this plan
> in `plans/README.md`.
>
> **Drift check (run first)**: `git diff --stat 84c4959..HEAD -- src/main/java/com/cloud_tecnoligical/nyxora_erp/security/SecurityConfig.java src/main/java/com/cloud_tecnoligical/nyxora_erp/controller`
> On a mismatch with the excerpts below, treat it as a STOP condition.

## Status

- **Priority**: P2
- **Effort**: M
- **Risk**: MED (endpoints que hoy funcionan para cualquier usuario autenticado pasarán a 403 si su rol no tiene el permiso; el rol Administrador demo tiene todos, así que el flujo demo no se rompe)
- **Depends on**: plans/001-baseline-pruebas-integracion.md, plans/002-endurecer-jwt-tipo-token-y-refresh.md
- **Category**: security
- **Planned at**: commit `84c4959`, 2026-06-11

## Why this matters

El sistema tiene un modelo RBAC completo en datos (`permiso`, `rol`, `rol_permiso`, `usuario_rol`, 19 códigos de permiso sembrados en `V3__seed_nucleo.sql`), el login carga los permisos en el JWT y el filtro los convierte en `SimpleGrantedAuthority`… y **nada los consulta**. No hay un solo `@PreAuthorize`/`hasAuthority` en `src/main/java`. Resultado: cualquier usuario autenticado de una empresa puede crear/borrar usuarios, confirmar comprobantes contables, registrar movimientos de inventario y aprobar órdenes de compra. El permiso `compras.orden.aprobar` (separación de funciones: quien crea no aprueba) es decorativo. Este plan conecta el último eslabón: anotaciones de autorización sobre los endpoints mutadores cuyos permisos ya existen en el catálogo.

## Current state

- Permisos sembrados (`src/main/resources/db/migration/V3__seed_nucleo.sql:21-39`):

  ```
  administracion.empresa.gestionar   administracion.sede.gestionar
  administracion.usuario.gestionar   administracion.vigencia.abrir
  administracion.vigencia.cerrar     comun.tercero.gestionar
  comun.producto.gestionar           comun.centro_costo.gestionar
  compras.orden.crear                compras.orden.aprobar
  inventario.movimiento.registrar    facturacion.factura.emitir
  facturacion.factura.anular         caja.recibo.registrar
  caja.arqueo.realizar               cartera.acuerdo.gestionar
  contabilidad.comprobante.confirmar contabilidad.periodo.cerrar
  ```

- `security/JwtAuthenticationWebFilter.java:41-45` — ya materializa los permisos del token como authorities:

  ```java
  List<SimpleGrantedAuthority> authorities = jwtService.permisos(claims).stream()
      .map(SimpleGrantedAuthority::new)
      .toList();
  ```

- `security/SecurityConfig.java` — `@EnableWebFluxSecurity` SIN `@EnableReactiveMethodSecurity`; autorización actual: `.pathMatchers(PUBLICAS).permitAll()` / `.anyExchange().authenticated()`.
- Controllers: clases planas en `controller/`, sin ninguna anotación de autorización. Ejemplo (`controller/TerceroController.java:40-59`):

  ```java
  @PostMapping
  @Operation(summary = "Crear tercero (con clasificación cliente/proveedor/empleado)")
  public Mono<ResponseEntity<ApiResponse<TerceroResponseDto>>> create(@Valid @RequestBody CreateTerceroRequestDto dto) { ... }
  ```

- `DevDataSeeder` asigna TODOS los permisos al rol Administrador demo → el usuario `admin` de las pruebas no pierde acceso.
- Decisión de alcance: solo endpoints **mutadores** (POST/PUT/PATCH/DELETE, excluyendo los `POST /list` de paginación que son lecturas) y solo donde EXISTE un código de permiso en el catálogo. Las lecturas siguen con `authenticated()`. No inventar códigos de permiso nuevos.

## Commands you will need

| Purpose   | Command                            | Expected on success |
|-----------|------------------------------------|---------------------|
| Compile   | `mvnw.cmd -q -B compile`           | exit 0              |
| Tests     | `mvnw.cmd -B test`                 | BUILD SUCCESS       |

## Scope

**In scope**:
- `src/main/java/com/cloud_tecnoligical/nyxora_erp/security/SecurityConfig.java` (añadir `@EnableReactiveMethodSecurity`)
- Controllers listados en el paso 2 (solo añadir `@PreAuthorize` a métodos mutadores)
- `src/test/java/com/cloud_tecnoligical/nyxora_erp/security/RbacIT.java` (create)

**Out of scope**:
- NO crear permisos nuevos ni migraciones — solo usar los 19 códigos existentes.
- Controllers sin código de permiso aplicable (p. ej. `ParametroController`, `CatalogoController`, `AdjuntoController`, `MarcaController`, `LoteController`, `RecursoController`...): quedan `authenticated()` y se listan como deuda en el reporte final.
- Endpoints de lectura (GET y `POST /list`).
- `GlobalExceptionHandler` (la respuesta 403 de Spring Security es aceptable; si el operador quiere envolverla en `ApiResponse`, es un plan aparte).
- La regla cross-tenant=404 NO aplica aquí: 403 por falta de permiso dentro de la PROPIA empresa es correcto y no revela datos de otras empresas.

## Git workflow

- Branch: `advisor/006-rbac-endpoints`
- Commit: `feat: RBAC por permisos en endpoints mutadores`
- No push/PR sin instrucción del operador.

## Steps

### Step 1: Habilitar method security reactiva

En `SecurityConfig`, añadir la anotación de clase
`@org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity`
junto a `@EnableWebFluxSecurity`.

**Verify**: `mvnw.cmd -q -B compile` → exit 0.

### Step 2: Anotar los endpoints mutadores

Para cada controller, añadir `@PreAuthorize("hasAuthority('<codigo>')")` SOLO en métodos `@PostMapping` (excepto rutas `/list`), `@PutMapping`, `@PatchMapping`, `@DeleteMapping`. Import: `org.springframework.security.access.prepost.PreAuthorize`. Mapa permiso→controller:

| Permiso | Controllers (métodos mutadores) |
|---|---|
| `administracion.empresa.gestionar` | `EmpresaController` |
| `administracion.sede.gestionar` | `SedeController` |
| `administracion.usuario.gestionar` | `UsuarioController`, `RolController`, `PermisoController` (si tiene mutadores) |
| `administracion.vigencia.abrir` | `VigenciaController` — método de creación/apertura |
| `administracion.vigencia.cerrar` | `VigenciaController` — método de cierre (si existe; si la distinción abrir/cerrar no es separable por método, usar `abrir` para create y `cerrar` para el endpoint de cierre, y reportar lo ambiguo) |
| `comun.tercero.gestionar` | `TerceroController`, `TerceroSatelitesController` |
| `comun.producto.gestionar` | `ProductoController`, `ProductoSatelitesController`, `CategoriaController` |
| `comun.centro_costo.gestionar` | `CentroCostoController`, `DependenciaController`, `ProyectoController` |
| `compras.orden.crear` | `OrdenCompraController` — create/update; `RecepcionController` |
| `compras.orden.aprobar` | `OrdenCompraController` — método de aprobación (buscar `aprobar` en el archivo) |
| `inventario.movimiento.registrar` | `MovimientoInventarioController`, `SaldoInventarioController` (recalcular), `BodegaController`, `UbicacionController` |
| `contabilidad.comprobante.confirmar` | `ComprobanteController` — método de confirmación; el create puede quedar `authenticated()` si no hay código mejor (reportar la decisión) |
| `contabilidad.periodo.cerrar` | `PeriodoContableController` — método de cierre |

Ejemplo aplicado a `TerceroController.create`:

```java
@PostMapping
@PreAuthorize("hasAuthority('comun.tercero.gestionar')")
@Operation(summary = "Crear tercero (con clasificación cliente/proveedor/empleado)")
public Mono<ResponseEntity<ApiResponse<TerceroResponseDto>>> create(@Valid @RequestBody CreateTerceroRequestDto dto) {
```

Antes de anotar cada controller, ABRIRLO y revisar sus métodos reales — la tabla anterior es el mapa de intención, no una lista de firmas. Anotar también en una lista (para el reporte final) los controllers mutadores que quedaron SIN permiso aplicable.

**Verify**: `mvnw.cmd -q -B compile` → exit 0.

### Step 3: Test de integración RBAC

Crear `src/test/java/com/cloud_tecnoligical/nyxora_erp/security/RbacIT.java` extendiendo `AbstractIntegrationTest` (plan 001). Preparación: con SQL directo (`DatabaseClient`), crear en la empresa demo un rol `Consulta` SIN permisos, un tercero y un usuario `consulta`/hash de `consulta123` (imitar los INSERT de `DevDataSeeder.sembrar`), asignarle el rol. Casos:

1. Login `consulta/consulta123` → 200 (autentica).
2. `POST /api/terceros` con token de `consulta` → **403**.
3. `POST /api/terceros` con token de `admin` (todos los permisos) → 201.
4. `POST /api/terceros/list` con token de `consulta` → 200 (las lecturas no se restringen).

**Verify**: `mvnw.cmd -B test -Dtest=RbacIT` → 4 tests pasan.

### Step 4: Suite completa y barrido

**Verify**: `mvnw.cmd -B test` → BUILD SUCCESS. Barrido final:
`grep -rln "PreAuthorize" src/main/java/com/cloud_tecnoligical/nyxora_erp/controller/ | wc -l` → ≥ 15 archivos.

## Test plan

Casos en el paso 3; patrón estructural: `AuthFlowIT` (plan 001). Verificación: `mvnw.cmd -B test` → todo verde, incluidos los tests de los planes anteriores (regresión: el admin demo conserva acceso).

## Done criteria

- [ ] `SecurityConfig` tiene `@EnableReactiveMethodSecurity`
- [ ] Todos los métodos POST/PUT/PATCH/DELETE (no `/list`) de los controllers de la tabla llevan `@PreAuthorize` con un código del catálogo V3
- [ ] `RbacIT` pasa: usuario sin permiso → 403; admin → éxito; lecturas → 200
- [ ] `mvnw.cmd -B test` exit 0
- [ ] Reporte final incluye la lista de controllers mutadores SIN permiso en el catálogo (deuda para una futura ampliación del catálogo)
- [ ] Fila actualizada en `plans/README.md`

## STOP conditions

- `@PreAuthorize` no se evalúa (el test 2 devuelve 201 en vez de 403) tras habilitar method security — posible incompatibilidad con cómo el filtro construye la `Authentication`; reportar con el diagnóstico, no parchear con hacks.
- El plan 002 no está aplicado (el filtro aceptaría refresh tokens sin permisos como autenticados — el RBAC se construiría sobre arena); reportar y pedir orden.
- Más de ~3 endpoints no encajan en ningún código de permiso y parecen críticos (p. ej. contabilidad) — listar y preguntar antes de dejarlos abiertos.

## Maintenance notes

- Toda HU nueva con endpoints mutadores debe declarar su código de permiso (catálogo `permiso`) y su `@PreAuthorize`; añadir esto a las reglas del equipo (`.claude/rules/`) sería un buen follow-up.
- El catálogo de 19 permisos se quedará corto (parametros, catálogos, adjuntos, marcas/lotes...); la ampliación requiere migración de seed + UI de asignación de roles.
- Revisor: comprobar que ningún `@PreAuthorize` cita un código que no exista en `V3__seed_nucleo.sql` (un typo = endpoint inaccesible para todos).
