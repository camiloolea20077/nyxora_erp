# Plan 008: Vertical slice de Cartera + Caja (Sprint 8 / HU-0011, HU-0012) — cierra el ciclo de ingreso

> **Executor instructions**: Follow this plan step by step. Run every
> verification command and confirm the expected result before moving to the
> next step. If anything in the "STOP conditions" section occurs, stop and
> report — do not improvise. When done, update the status row for this plan
> in `plans/README.md` — unless a reviewer dispatched you and told you they
> maintain the index.
>
> **Drift check (run first)**: `git diff --stat 84c4959..HEAD -- src/main/java/com/cloud_tecnoligical/nyxora_erp src/main/resources/db/migration`
> Este plan asume que el plan 007 (Facturación) está DONE: los eventos
> `FacturaEmitida`/`FacturaAnulada` deben existir en `src/main/java/.../event/`.
> Si no existen, STOP — ejecuta primero `plans/007-facturacion-vertical-slice.md`.

## Status

- **Priority**: P1
- **Effort**: L
- **Risk**: MED
- **Depends on**: `plans/007-facturacion-vertical-slice.md` (obligatorio). Coordinación suave con 001 (tests) y 006 (RBAC).
- **Category**: direction
- **Planned at**: commit `84c4959`, 2026-06-11

## Why this matters

Con el plan 007 Nyxora factura, pero el dinero "no entra": nada registra la cuenta por cobrar ni el
recaudo. Este plan implementa Cartera (CxC creada automáticamente al emitirse la factura, edades
como proyección) y Caja (recibos que aplican a CxC, medios de pago, arqueo), cerrando el ciclo
**order-to-cash** completo: Compra → Inventario → Factura → CxC → Recibo de caja → Contabilidad.
Es el Sprint 8 del roadmap (`docs/roadmap-fases-sprints.md:53-54`) y el último tramo del MVP de 8
módulos definido en `docs/analisis-erp-referencia/09-validacion-diseno-mvp.md` (§4.6, §4.7).
Al terminar, `EventoDemoController` deja de ser necesario y se retira.

## Current state

### Esquema de BD (ya aplicado en V12/V13 — NO crear migraciones)

`src/main/resources/db/migration/V12__cartera_schema.sql`:

- **`cuenta_por_cobrar`**: `id, empresa_id, cliente_id (NOT NULL, FK tercero), factura_id (nullable),
  cuenta_id (nullable, FK cuenta), fecha_emision (NOT NULL), fecha_vencimiento, dias, valor_total,
  valor_interes, saldo, fecha_ultima_liquidacion, estado (CHECK: 'vigente','en_acuerdo','pagada','anulada'),
  activo, created_at, updated_at, deleted_at, usuario_creacion, usuario_modificacion`.
- **`acuerdo_pago`** y **`acuerdo_pago_cuota`**: existen pero quedan FUERA de alcance (ver Scope).

`src/main/resources/db/migration/V13__caja_schema.sql`:

- **`caja`**: `id, empresa_id, sede_id, usuario_id, codigo (NOT NULL, unique por empresa), nombre,
  estado (CHECK: 'abierta','cerrada'; default 'cerrada'), saldo_inicial, fecha_apertura, fecha_cierre,
  activo, created_at, updated_at, deleted_at, usuario_creacion, usuario_modificacion`.
- **`recibo_caja`**: `id, empresa_id, caja_id (NOT NULL), tipo_documento_id, numero, cliente_id,
  fecha (NOT NULL), valor, estado (CHECK: 'registrado','anulado'), observaciones, activo,
  created_at, updated_at, deleted_at, usuario_creacion, usuario_modificacion`.
- **`recibo_caja_pago`**: `id, recibo_caja_id, forma_pago_id (FK forma_pago, catálogo V4),
  valor, banco_id (FK banco, catálogo V4), numero_cheque, numero_tarjeta, cuenta_bancaria,
  created_at, deleted_at`.
- **`recibo_caja_linea`**: `id, recibo_caja_id, cuenta_por_cobrar_id (NOT NULL), valor_aplicado,
  created_at, deleted_at`.
- **`arqueo`**: `id, empresa_id, caja_id, fecha, valor_declarado, valor_sistema, diferencia,
  observaciones, usuario_creacion, created_at` (append-only: sin updated/deleted).

### Piezas existentes sobre las que se construye

- Eventos del plan 007: `event/FacturaEmitida.java` (carga: empresaId, usuarioId, sedeId, facturaId,
  clienteId, numero, fecha, fechaVencimiento, total) y `event/FacturaAnulada.java` (empresaId,
  usuarioId, sedeId, facturaId). **Verifica la carga útil real antes de codificar el listener.**
- Patrón de listener del bus: `src/main/java/com/cloud_tecnoligical/nyxora_erp/event/InterfazContableListener.java` —
  `@Component`, `@PostConstruct` con `bus.on(Evento.class)...subscribe()`, reconstruye el tenant:
  ```java
  TenantInfo tenant = new TenantInfo(ev.getEmpresaId(), ev.getUsuarioId(), ev.getSedeId(), false);
  return servicio.algo(dto)
      .contextWrite(TenantContext.write(tenant))
      .then();
  ```
  con `onErrorResume` que loguea sin tumbar el bus (líneas 33-42). Calca esa forma.
- Asiento contable por evento: `AsientoContableSolicitado` (ver su uso en
  `RecepcionServiceImpl.publicarAsiento`, líneas 212-242) — si el plan 009 ya corrió, la
  publicación pasa por `ContabilidadOutbox` en vez de `eventBus.publish` (adapta y repórtalo).
- Slice patrón (entity/dto/mapper/repos/service/controller): mismos exemplars que el plan 007
  (tabla "Patrón a replicar" en `plans/007-facturacion-vertical-slice.md`) — léela; no se repite aquí.
- Reglas obligatorias: `.claude/rules/00-convenciones-backend.md`, `01-multitenant-softdelete.md`,
  `02-base-datos.md`. En particular: `empresa_id` del `TenantContext`, cross-tenant=404, soft-delete,
  QueryRepository con SQL nativo y alias `"camelCase"`.

## Commands you will need

| Purpose | Command (raíz del repo) | Expected on success |
|---|---|---|
| Compilar | `mvnw.cmd -q -B compile` | exit 0 |
| Tests | `mvnw.cmd -B test` | exit 0 |
| BD local | `docker compose up -d` | Postgres en 5433 |

## Scope

**In scope** (crear, salvo los 2 marcados como modificar/eliminar):

- Entidades: `CuentaPorCobrarEntity`, `CajaEntity`, `ReciboCajaEntity`, `ReciboCajaPagoEntity`,
  `ReciboCajaLineaEntity`, `ArqueoEntity` (en `entity/`).
- `dto/cartera/` y `dto/caja/` (DTOs nuevos).
- `mapper/cartera/`, `mapper/caja/`.
- `repository/cartera/`, `repository/caja/` (R2dbc + Query).
- `service/CuentaPorCobrarService.java`, `CajaService.java`, `ReciboCajaService.java` (+ impls).
- `event/CarteraListener.java` (listener de `FacturaEmitida`/`FacturaAnulada`).
- `controller/CuentaPorCobrarController.java`, `CajaController.java`, `ReciboCajaController.java`.
- `docs/hu/HU-0011-cartera.md`, `docs/hu/HU-0012-caja.md`, `docs/api/sprint8-cartera-caja.http`.
- **Eliminar**: `controller/EventoDemoController.java` (su función demo queda cubierta por el flujo real).
- `plans/README.md` (tu fila de status).

**Out of scope** (NO tocar):

- `acuerdo_pago` / `acuerdo_pago_cuota` — diferido (decisión: primero el flujo de recaudo básico;
  los acuerdos requieren definición de negocio de intereses/cuotas). Déjalo anotado en la HU-0011.
- Tesorería (V16), Cuentas por Pagar (V17) — sprints posteriores.
- Migraciones nuevas — el esquema ya existe.
- Módulo facturación del plan 007 — solo LECTURA (no cambies `FacturaServiceImpl`).
- `MapperRepository.java` (cambio local sin commitear).

## Git workflow

- Branch: `feature/sprint8-cartera-caja`.
- Commits `feat: cartera - <qué>` / `feat: caja - <qué>`. No push/PR sin instrucción del operador.

## Steps

### Step 1: Slice de Cartera (lectura + listener)

1. `CuentaPorCobrarEntity` con todas las columnas de V12 (convención `@Table`/`@Column` snake_case).
2. DTOs: `CuentaPorCobrarResponseDto`, `CuentaPorCobrarTableDto` (id, clienteNombre, facturaNumero,
   fechaEmision, fechaVencimiento, valorTotal, saldo, estado), `EdadCarteraDto` (clienteId,
   clienteNombre, rango0a30, rango31a60, rango61a90, rangoMas90, total).
3. `CuentaPorCobrarR2dbcRepository` + `CuentaPorCobrarQueryRepository`:
   - `list(PageableDto, empresaId)` con JOIN a `tercero` y `factura` (filtros: clienteId, estado).
   - `findActiveById(id, empresaId)`.
   - `edades(empresaId)` → `Flux<EdadCarteraDto>`: proyección con
     `SUM(saldo) FILTER (WHERE CURRENT_DATE - COALESCE(fecha_vencimiento, fecha_emision) BETWEEN ...)`
     agrupado por cliente, solo `estado = 'vigente' AND deleted_at IS NULL AND empresa_id=:e`.
     (Las edades NUNCA se almacenan: regla RCar4 del diseño, `09-validacion-diseno-mvp.md:382`.)
4. `CuentaPorCobrarService(+Impl)`: `findById`, `list`, `edades`. SIN create/update públicos:
   la CxC nace solo del evento (los ajustes manuales/saldos iniciales son deuda anotada en la HU).
5. `CarteraListener` en `event/` (calca `InterfazContableListener`):
   - `on(FacturaEmitida)` → crea `cuenta_por_cobrar`: cliente_id, factura_id, fecha_emision=fecha,
     fecha_vencimiento, valor_total=total, saldo=total, estado='vigente', activo=true,
     created_at=now, usuario_creacion=usuarioId del evento. **Idempotencia**: antes de insertar,
     consulta si ya existe CxC activa con ese `factura_id` (query en el QueryRepository); si existe,
     loguea y no dupliques (el bus puede reintentar con el plan 009).
   - `on(FacturaAnulada)` → si la CxC de esa factura existe y su saldo == valor_total (sin recaudos),
     estado='anulada'; si ya tiene recaudos aplicados, loguea ERROR y no toques nada (caso a resolver
     con reversa manual — anótalo en la HU).
6. `CuentaPorCobrarController` en `/api/cuentas-por-cobrar`: GET `/{id}`, POST `/list`, GET `/edades`.

**Verify**: `mvnw.cmd -q -B compile` → exit 0.

### Step 2: Slice de Caja (maestro + apertura/cierre)

1. `CajaEntity`, DTOs (`CreateCajaRequestDto`: sedeId, codigo `@NotBlank`, nombre, saldoInicial;
   `UpdateCajaRequestDto`; `CajaResponseDto`; `CajaTableDto`), mapper, repos
   (`CajaR2dbcRepository`, `CajaQueryRepository` con list/findActiveById).
2. `CajaService(+Impl)`: CRUD + `abrir(id)` (solo `cerrada` → `abierta`, setea `fecha_apertura=now`,
   `usuario_id` = usuario del tenant) y `cerrar(id)` (solo `abierta` → `cerrada`, `fecha_cierre=now`).
   Transiciones inválidas → `GlobalException(BAD_REQUEST, ...)` en español.
3. `CajaController` en `/api/cajas`: POST, PUT, DELETE `/{id}`, GET `/{id}`, POST `/list`,
   POST `/{id}/abrir`, POST `/{id}/cerrar`.

**Verify**: `mvnw.cmd -q -B compile` → exit 0.

### Step 3: Recibo de caja (aplicación a CxC) + arqueo

1. Entidades `ReciboCajaEntity`, `ReciboCajaPagoEntity`, `ReciboCajaLineaEntity`, `ArqueoEntity`.
2. DTOs: `CreateReciboCajaRequestDto` (cajaId `@NotNull`, tipoDocumentoId, numero, clienteId
   `@NotNull`, fecha `@NotNull`, observaciones, pagos `@NotEmpty List<CreateReciboCajaPagoDto>`
   {formaPagoId, valor `@NotNull @Positive`, bancoId, numeroCheque, numeroTarjeta, cuentaBancaria},
   lineas `@NotEmpty List<CreateReciboCajaLineaDto>` {cuentaPorCobrarId `@NotNull`, valorAplicado
   `@NotNull @Positive`}, y params contables opcionales: periodoContableId, cuentaCajaId,
   cuentaCarteraId). `ReciboCajaResponseDto` (+pagos +lineas), `ReciboCajaTableDto`.
   `CreateArqueoRequestDto` (cajaId, valorDeclarado, observaciones), `ArqueoResponseDto`.
3. Repos: R2dbc por entidad + `ReciboCajaQueryRepository` (list/findActiveById/listPagos/listLineas)
   y en `CajaQueryRepository` un helper `valorSistema(cajaId, empresaId)` → `Mono<BigDecimal>`:
   `saldo_inicial + COALESCE(SUM(rc.valor),0)` de recibos `estado='registrado' AND deleted_at IS NULL`
   creados desde `fecha_apertura` de la caja.
4. `ReciboCajaService(+Impl).create(dto)` — el corazón. Dentro de UNA transacción
   (`TransactionalOperator`, patrón `RecepcionServiceImpl.confirmar`):
   1. Caja existe, es de la empresa y está `abierta` (si no: 400 "La caja no está abierta").
   2. Cliente existe en la empresa (helper `terceroExisteEnEmpresa`, copia el SQL de
      `OrdenCompraQueryRepository`).
   3. `valor = Σ pagos`; exigir `Σ lineas.valorAplicado == valor` (400 "Los pagos no cuadran con las aplicaciones").
   4. Por cada línea: cargar la CxC (de la empresa, `estado='vigente'` o `'en_acuerdo'`, activa);
      exigir `valorAplicado <= saldo` (400 "La aplicación supera el saldo de la cuenta por cobrar").
      Descontar: `saldo = saldo − valorAplicado`; si queda 0 → `estado='pagada'`;
      `usuario_modificacion`, `updated_at`. Guardar CxC.
   5. Insertar encabezado (`estado='registrado'`), pagos y líneas.
   6. Tras commit: si params contables completos, publicar `AsientoContableSolicitado`
      (débito `cuentaCajaId` por valor; crédito `cuentaCarteraId` por valor;
      `origen_modulo="caja"`, `origen_id=recibo.getId()`; descripcion "Recibo de caja #N").
5. `anular(id)`: solo `registrado` → `anulado`; en la misma transacción, reversar las aplicaciones
   (saldo += valorAplicado; si estaba `pagada` → `vigente`). No borres pagos/líneas (historia).
6. `arquear(dto)`: caja `abierta`; `valor_sistema = valorSistema(cajaId)`;
   `diferencia = valorDeclarado − valorSistema`; insertar `arqueo` (append-only, sin update/delete).
7. `ReciboCajaController` en `/api/recibos-caja`: POST, GET `/{id}`, POST `/list`,
   POST `/{id}/anular`. Arqueo dentro de `CajaController`: POST `/api/cajas/{id}/arqueos` y
   GET `/api/cajas/{id}/arqueos` (lista).

**Verify**: `mvnw.cmd -q -B compile` → exit 0.

### Step 4: Retirar el controller demo

Elimina `src/main/java/com/cloud_tecnoligical/nyxora_erp/controller/EventoDemoController.java`
(era un atajo para probar el bus sin módulos emisores; ya hay tres emisores reales: recepciones,
facturas y recibos). Si algo más lo referencia (no debería), STOP.

**Verify**: `mvnw.cmd -q -B compile` → exit 0 y
`grep -rn "EventoDemoController" src/` → sin matches.

### Step 5: Documentación

- `docs/hu/HU-0011-cartera.md` y `docs/hu/HU-0012-caja.md` con `docs/hu/PLANTILLA-HU.md`
  (anota las deudas diferidas: acuerdos de pago, saldos iniciales de CxC, anulación de factura
  con recaudos aplicados).
- `docs/api/sprint8-cartera-caja.http` calcando `docs/api/sprint6-compras.http`: flujo completo
  (crear caja → abrir → emitir factura del 007 → ver CxC creada → recibo aplicado → CxC pagada →
  arqueo → cerrar caja → edades).

**Verify**: archivos creados; compile en exit 0.

## Test plan

- Si `plans/001` está DONE — `ReciboCajaIntegrationTest` y `CarteraIntegrationTest` sobre su
  `AbstractIntegrationTest`: (1) `FacturaEmitida` publicado → CxC creada con saldo=total
  (usa `Awaitility` o reintento con timeout: el bus es asíncrono); (2) recibo que aplica parcial →
  saldo decrece, estado sigue `vigente`; (3) aplicación total → `pagada`; (4) aplicación > saldo → 400;
  (5) recibo sobre caja cerrada → 400; (6) anular recibo → saldo restaurado; (7) CxC de otra
  empresa → 404; (8) edades devuelve buckets correctos.
- Si NO está DONE: verificación manual con `docs/api/sprint8-cartera-caja.http` y deja los casos
  como pendientes en las HU.
- Siempre: `mvnw.cmd -B test` → exit 0.

## Done criteria

- [ ] `mvnw.cmd -q -B compile` y `mvnw.cmd -B test` exit 0.
- [ ] `CarteraListener` suscrito a `FacturaEmitida` y `FacturaAnulada` con reconstrucción de tenant.
- [ ] Flujo recibo→CxC transaccional (un solo `tx::transactional` cubre validación+aplicación+insert).
- [ ] `grep -rn "empresaId" src/main/java/com/cloud_tecnoligical/nyxora_erp/dto/cartera src/main/java/com/cloud_tecnoligical/nyxora_erp/dto/caja` → sin matches.
- [ ] `EventoDemoController.java` eliminado.
- [ ] Sin migraciones nuevas (`git status` sobre `db/migration`).
- [ ] HU-0011, HU-0012 y `sprint8-cartera-caja.http` creados.
- [ ] Fila 008 de `plans/README.md` actualizada.

## STOP conditions

- `FacturaEmitida`/`FacturaAnulada` no existen o su carga útil no trae clienteId/total
  (el plan 007 no corrió o cambió el contrato).
- Las columnas reales de V12/V13 difieren de las listadas arriba.
- `TenantInfo` cambió de constructor (el plan 002/006 puede haberlo tocado) — adapta solo si el
  cambio es obvio (p. ej. un parámetro extra de permisos); si no, STOP.
- Alguna verificación falla dos veces.
- Para cuadrar algo necesitas modificar `FacturaServiceImpl` u otro archivo out-of-scope.

## Maintenance notes

- **Concurrencia de aplicación a CxC**: dos recibos simultáneos sobre la misma CxC pueden leer el
  mismo saldo. La transacción reduce la ventana pero no la elimina; el endurecimiento correcto es
  `SELECT ... FOR UPDATE` sobre la fila de CxC dentro de la transacción del recibo (mismo patrón de
  `ConsecutivoQueryRepository`). Si el ejecutor puede hacerlo sin salirse del scope, hágalo; si no,
  quedará como follow-up prioritario — el revisor debe decidir.
- El plan 009 (outbox) hará durables los eventos: cuando corra, la creación de CxC dejará de poder
  "perderse" si el proceso cae entre la emisión y el listener. Hasta entonces, ese hueco existe y
  está documentado en el ADR AD-R7.
- Siguiente extensión natural del módulo: acuerdos de pago (tablas ya existen), saldos iniciales de
  cartera (factura_id nullable ya lo permite) y nota crédito (anulación de factura con recaudos).
