# Plan 007: Vertical slice de Facturación (Sprint 7 / HU-0010) — factura, resolución DIAN, salida de inventario y asiento contable

> **Executor instructions**: Follow this plan step by step. Run every
> verification command and confirm the expected result before moving to the
> next step. If anything in the "STOP conditions" section occurs, stop and
> report — do not improvise. When done, update the status row for this plan
> in `plans/README.md` — unless a reviewer dispatched you and told you they
> maintain the index.
>
> **Drift check (run first)**: `git diff --stat 84c4959..HEAD -- src/main/java/com/cloud_tecnoligical/nyxora_erp src/main/resources/db/migration`
> Si algún archivo citado en "Current state" cambió desde que se escribió este plan,
> compara los extractos contra el código vivo antes de continuar; si no coinciden,
> trátalo como STOP condition.

## Status

- **Priority**: P1
- **Effort**: L
- **Risk**: MED
- **Depends on**: ninguno obligatorio. Coordinación suave con `plans/001` (tests) y `plans/006` (RBAC): si 001 está DONE, añade los tests de integración del "Test plan"; si 006 está DONE, anota los permisos nuevos como indica su patrón.
- **Category**: direction
- **Planned at**: commit `84c4959`, 2026-06-11

## Why this matters

Nyxora tiene implementado el ciclo de gasto (Compras → Inventario → Contabilidad, sprints 1–6),
pero **no puede facturar**: el esquema de facturación (`V11__facturacion_schema.sql`) existe desde
el modelado de BD y **no hay ni una sola clase de aplicación** que lo use. Sin facturación no hay
ciclo de ingreso, no hay cartera, y el ERP no es vendible. Este plan construye el vertical slice
reactivo de Facturación siguiendo exactamente el patrón ya probado en Compras: factura con líneas,
resolución DIAN con numeración atómica, emisión que descuenta inventario y publica el asiento
contable por el bus de eventos, y eventos `FacturaEmitida`/`FacturaAnulada` que el plan 008
(Cartera + Caja) consumirá. Es el Sprint 7 del roadmap (`docs/roadmap-fases-sprints.md:50-51`).

**Alcance deliberado (v1)**: factura "interna" con resolución y numeración. La integración real con
la DIAN (XML UBL, CUFE, proveedor tecnológico) queda explícitamente diferida (pregunta Q8 de
`docs/analisis-erp-referencia/09-validacion-diseno-mvp.md:826`); la tabla `factura_dian` NO se toca.

## Current state

### Esquema de BD (ya aplicado, NO crear migraciones)

`src/main/resources/db/migration/V11__facturacion_schema.sql` define:

- **`resolucion_dian`**: `id, empresa_id, numero_resolucion, prefijo, factura_inicial, factura_final,
  fecha_inicial, fecha_final, clave_tecnica, descripcion, consecutivo_actual (BIGINT NOT NULL DEFAULT 0),
  activo, created_at, updated_at, deleted_at, usuario_creacion, usuario_modificacion`.
  Unique `(empresa_id, numero_resolucion)`.
- **`factura`**: `id, empresa_id, sede_id, vigencia_id, tipo_documento_id, resolucion_dian_id, numero,
  cliente_id (NOT NULL, FK tercero), bodega_id, centro_costo_id, condicion_pago_id, fecha (NOT NULL),
  fecha_vencimiento, observaciones, estado (CHECK: 'borrador','emitida','anulada'; default 'borrador'),
  subtotal, descuento, impuestos, total (NUMERIC(19,4)), activo, created_at, updated_at, deleted_at,
  usuario_creacion, usuario_modificacion`.
- **`factura_linea`**: `id, factura_id (NOT NULL), producto_id (NOT NULL), producto_variante_id,
  descripcion, cantidad (NOT NULL), unidad_medida_id, valor_unitario, descuento_porcentaje,
  descuento_valor, subtotal, impuesto_id, porcentaje_impuesto, valor_impuesto, discrimina_iva,
  total, bodega_id, lote_id, centro_costo_id, created_at, updated_at, deleted_at`.
- **`factura_dian`**: existe pero está FUERA de alcance.

Otras columnas relevantes: `producto.maneja_inventario BOOLEAN NOT NULL DEFAULT TRUE`
(`V6__comun_productos.sql:57`).

### Patrón a replicar (el slice de Compras, Sprint 6)

El equipo ya tiene un patrón completo y consistente. **Cópialo, no inventes**:

| Pieza | Archivo exemplar |
|---|---|
| Entity (R2DBC, campos snake_case) | `src/main/java/com/cloud_tecnoligical/nyxora_erp/entity/OrdenCompraEntity.java` |
| DTOs (inglés camelCase, jakarta.validation) | `src/main/java/com/cloud_tecnoligical/nyxora_erp/dto/compras/` |
| Mapper MapStruct ES→EN | `src/main/java/com/cloud_tecnoligical/nyxora_erp/mapper/compras/OrdenCompraMapper.java` |
| R2dbcRepository mínimo | `src/main/java/com/cloud_tecnoligical/nyxora_erp/repository/compras/OrdenCompraR2dbcRepository.java` |
| QueryRepository (DatabaseClient, SQL nativo, alias `"camelCase"`, allowlist `SORTABLE`) | `src/main/java/com/cloud_tecnoligical/nyxora_erp/repository/compras/OrdenCompraQueryRepository.java` |
| Service encabezado+líneas con `TransactionalOperator` | `src/main/java/com/cloud_tecnoligical/nyxora_erp/service/impl/OrdenCompraServiceImpl.java` |
| Service que genera inventario + publica asiento | `src/main/java/com/cloud_tecnoligical/nyxora_erp/service/impl/RecepcionServiceImpl.java` |
| Controller (`ApiResponse`, rutas REST + acciones POST) | `src/main/java/com/cloud_tecnoligical/nyxora_erp/controller/OrdenCompraController.java` |
| Numeración atómica `FOR UPDATE` | `src/main/java/com/cloud_tecnoligical/nyxora_erp/repository/documento/ConsecutivoQueryRepository.java` |

Extractos clave que vas a imitar:

`OrdenCompraEntity.java:15-23` — convención de entidad:
```java
@Table("orden_compra")
@Getter
@Setter
public class OrdenCompraEntity {
    @Id
    private Long id;
    @Column("empresa_id")           private Long empresa_id;
    ...
}
```

`OrdenCompraMapper.java:11-15` — convención de mapper:
```java
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrdenCompraMapper {
    @Mappings({ @Mapping(source = "sede_id", target = "sedeId"), ... })
```

`RecepcionServiceImpl.java:146-148` — el flujo transaccional + evento DESPUÉS del commit:
```java
return flujo.as(tx::transactional)
    .then(Mono.fromRunnable(() -> publicarAsiento(params, rec, lineas, t)))
    .then(cargarRespuesta(id, t.getEmpresaId()));
```

`RecepcionServiceImpl.java:236-241` — construcción y publicación del asiento:
```java
AsientoContableSolicitado evento = new AsientoContableSolicitado(
    t.getEmpresaId(), t.getUsuarioId(), t.getSedeId(),
    params.getPeriodoContableId(), rec.getFecha(),
    "Recepción de compra #" + rec.getId(), "compras", rec.getId(),
    List.of(debito, credito));
eventBus.publish(evento);
```

`ConsecutivoQueryRepository.java:38-45` — bloqueo de fila para numeración (DEBE correr dentro
de la transacción del `TransactionalOperator`):
```java
Mono<Long> bloquearYLeer = db.sql("""
        SELECT ultimo_numero FROM consecutivo
        WHERE tipo_documento_id=:t AND sede_id=:s AND vigencia_id=:v
        FOR UPDATE
        """)
```

`MovimientoInventarioEntity` — campos que setea `RecepcionServiceImpl.movimientoEntrada`
(`RecepcionServiceImpl.java:179-200`): `empresa_id, bodega_id, ubicacion_id, producto_id,
producto_variante_id, lote_id, tipo, fecha, cantidad, costo_unitario, subtotal, total, descripcion,
origen_modulo, origen_id, created_at, usuario_creacion`. Para **salida**, la convención del módulo
inventario es `tipo="salida"` con **cantidad negativa** (ver
`MovimientoInventarioServiceImpl.cantidadConSigno`, líneas 163-186: salida → `c.negate()`).

### Reglas obligatorias del repo

- `.claude/rules/00-convenciones-backend.md` — reactivo puro, entidades español snake_case,
  DTOs/servicios inglés camelCase, errores con `GlobalException(HttpStatus, msg)`, sin `@Autowired`
  en campo, repositorio R2DBC mínimo + QueryRepository con SQL nativo.
- `.claude/rules/01-multitenant-softdelete.md` — `empresa_id` SIEMPRE del `TenantContext` (PROHIBIDO
  en DTOs de request), cross-tenant = 404 con el mismo mensaje, soft-delete con `deleted_at`,
  `movimiento_inventario`/`movimiento_contable` append-only.
- `.claude/rules/02-base-datos.md` — no editar migraciones aplicadas (este plan NO crea migraciones).

## Commands you will need

| Purpose | Command (desde la raíz del repo) | Expected on success |
|---|---|---|
| Compilar | `mvnw.cmd -q -B compile` | exit 0, sin errores |
| Tests | `mvnw.cmd -B test` | exit 0 (la suite actual es mínima) |
| BD local (si pruebas manuales) | `docker compose up -d` | Postgres en puerto 5433 |

## Scope

**In scope** (crear; ningún archivo existente se modifica salvo los dos indicados):

- `src/main/java/com/cloud_tecnoligical/nyxora_erp/entity/ResolucionDianEntity.java`
- `src/main/java/com/cloud_tecnoligical/nyxora_erp/entity/FacturaEntity.java`
- `src/main/java/com/cloud_tecnoligical/nyxora_erp/entity/FacturaLineaEntity.java`
- `src/main/java/com/cloud_tecnoligical/nyxora_erp/dto/facturacion/` (todos los DTOs nuevos)
- `src/main/java/com/cloud_tecnoligical/nyxora_erp/mapper/facturacion/` (mappers nuevos)
- `src/main/java/com/cloud_tecnoligical/nyxora_erp/repository/facturacion/` (repos nuevos)
- `src/main/java/com/cloud_tecnoligical/nyxora_erp/service/ResolucionDianService.java`, `FacturaService.java`
- `src/main/java/com/cloud_tecnoligical/nyxora_erp/service/impl/ResolucionDianServiceImpl.java`, `FacturaServiceImpl.java`
- `src/main/java/com/cloud_tecnoligical/nyxora_erp/event/FacturaEmitida.java`, `FacturaAnulada.java`
- `src/main/java/com/cloud_tecnoligical/nyxora_erp/controller/ResolucionDianController.java`, `FacturaController.java`
- `docs/hu/HU-0010-facturacion.md` (siguiendo `docs/hu/PLANTILLA-HU.md`)
- `docs/api/sprint7-facturacion.http`
- `plans/README.md` (solo tu fila de status)

**Out of scope** (NO tocar, aunque parezca relacionado):

- `factura_dian` (tabla, entidad, lógica DIAN real) — diferido por decisión de producto (Q8).
- `cuenta_por_cobrar` / Cartera / Caja — eso es el plan 008. Aquí solo PUBLICAS `FacturaEmitida`;
  no crees ningún listener.
- `src/main/resources/db/migration/` — el esquema ya existe; cero migraciones nuevas.
- `EventoDemoController` — no lo retires todavía (sigue siendo útil hasta que 008 cierre el ciclo).
- `MapperRepository.java` — tiene un cambio local sin commitear; no lo toques.
- Módulos compras/inventario/contabilidad existentes — solo LECTURA de sus repos para validar referencias.

## Git workflow

- Branch: `feature/sprint7-facturacion` (el repo trabaja sobre `main` con commits `feat:`/`fix:` en español).
- Un commit por paso lógico, estilo observado: `feat: facturacion - <qué>` (ej. del log: `feat: se termina hasta sprint 6`).
- NO hacer push ni abrir PR salvo instrucción del operador.

## Steps

### Step 1: Entidades

Crea las 3 entidades calcando `OrdenCompraEntity.java` (anotaciones `@Table`, `@Id`, `@Column`,
Lombok `@Getter`/`@Setter` — NUNCA `@Data`), una clase por tabla con TODAS las columnas de
`V11__facturacion_schema.sql` listadas en "Current state". Tipos: `Long` para ids/FKs, `String`
para varchar, `LocalDate` para DATE, `LocalDateTime` para TIMESTAMP, `BigDecimal` para NUMERIC,
`Boolean` para BOOLEAN.

**Verify**: `mvnw.cmd -q -B compile` → exit 0.

### Step 2: DTOs (`dto/facturacion/`)

Inglés camelCase, validaciones `jakarta.validation`, `@Schema` de swagger como en `dto/compras/`:

- `CreateResolucionDianRequestDto` (numeroResolucion `@NotBlank`, prefijo, facturaInicial,
  facturaFinal, fechaInicial, fechaFinal, claveTecnica, descripcion), `UpdateResolucionDianRequestDto`
  (id `@NotNull` + los mismos), `ResolucionDianResponseDto` (+ consecutivoActual, active, createdAt).
- `CreateFacturaRequestDto`: sedeId, vigenciaId, tipoDocumentoId, resolucionDianId `@NotNull`,
  clienteId `@NotNull`, bodegaId, centroCostoId, condicionPagoId, fecha `@NotNull`, fechaVencimiento,
  observaciones, lineas `@NotEmpty @Valid List<CreateFacturaLineaDto>`. **SIN `numero`** (lo asigna
  la emisión) y **SIN empresaId** (regla 01).
- `CreateFacturaLineaDto`: productoId `@NotNull`, productoVarianteId, descripcion, cantidad
  `@NotNull @Positive`, unidadMedidaId, valorUnitario `@NotNull @PositiveOrZero`,
  descuentoValor, impuestoId, porcentajeImpuesto, impuestoValor, bodegaId, loteId, centroCostoId.
- `UpdateFacturaRequestDto` (id `@NotNull` + campos de create).
- `FacturaResponseDto` (campos del encabezado + numero + estado + totales + `List<FacturaLineaResponseDto> lineas`),
  `FacturaLineaResponseDto`, `FacturaTableDto` (id, numero, clienteNombre, fecha, estado, total).
- `EmitirFacturaRequestDto`: **todos opcionales** — periodoContableId, cuentaIngresoId,
  cuentaCarteraId, cuentaImpuestoId. (Mismo enfoque que `ConfirmarRecepcionRequestDto`: si faltan,
  se emite sin asiento contable.)

**Verify**: `mvnw.cmd -q -B compile` → exit 0.

### Step 3: Mappers MapStruct (`mapper/facturacion/`)

`ResolucionDianMapper`, `FacturaMapper`, `FacturaLineaMapper` calcando `OrdenCompraMapper.java`
(`componentModel = "spring"`, `@Mapping(source = "snake_case", target = "camelCase")`,
en `FacturaMapper.toResponseDto` ignora `lineas`). Para `FacturaLineaMapper` añade
`toEntity(CreateFacturaLineaDto)` como en `OrdenCompraLineaMapper`.

**Verify**: `mvnw.cmd -q -B compile` → exit 0 (MapStruct genera los impl en `target/generated-sources`).

### Step 4: Repositorios

1. `ResolucionDianR2dbcRepository`, `FacturaR2dbcRepository`, `FacturaLineaR2dbcRepository`
   — `extends R2dbcRepository<Entity, Long>`, sin métodos extra (regla 00).
2. `ResolucionDianQueryRepository` con:
   - `findActiveById(id, empresaId)` / `list(PageableDto, empresaId)` — SQL nativo parametrizado,
     `deleted_at IS NULL AND empresa_id = :empresaId`, alias `AS "camelCase"`, allowlist `SORTABLE`
     para `ORDER BY` (copiar estructura de `OrdenCompraQueryRepository`).
   - `siguienteNumero(resolucionId, empresaId)` → `Mono<Long>`: calca el patrón de
     `ConsecutivoQueryRepository.incrementarYObtener` pero sobre la fila de `resolucion_dian`:
     `SELECT consecutivo_actual FROM resolucion_dian WHERE id=:id AND empresa_id=:e AND deleted_at IS NULL FOR UPDATE`,
     luego `UPDATE resolucion_dian SET consecutivo_actual=:n WHERE id=:id`. Devuelve el nuevo valor.
     Documenta en el Javadoc que DEBE ejecutarse dentro de la transacción del service.
3. `FacturaQueryRepository` con: `findActiveById`, `list` (JOIN a `tercero` para `clienteNombre`),
   `listLineas(facturaId)`, `borrarLineas(facturaId)` (DELETE físico de líneas solo en estado
   borrador, igual que `OrdenCompraQueryRepository.borrarLineas`), y los helpers de validación:
   - `terceroExisteEnEmpresa(id, empresaId)` y `countProductosEnEmpresa(ids, empresaId)`
     (copia el SQL de `OrdenCompraQueryRepository`).
   - `productosQueManejanInventario(Set<Long> ids, empresaId)` → `Flux<Long>`:
     `SELECT id FROM producto WHERE id IN (:ids) AND empresa_id=:e AND maneja_inventario = TRUE AND deleted_at IS NULL`.
   - `cantidadDisponible(productoId, bodegaId, empresaId)` → `Mono<BigDecimal>`:
     `SELECT COALESCE(SUM(cantidad),0) FROM movimiento_inventario WHERE producto_id=:p AND bodega_id=:b AND empresa_id=:e`.

**Verify**: `mvnw.cmd -q -B compile` → exit 0.

### Step 5: Eventos de dominio

En `event/`, crea `FacturaEmitida` y `FacturaAnulada` implementando `DomainEvent` con el mismo
estilo de `AsientoContableSolicitado.java` (campos finales + `@Getter` + constructor; `ocurridoEn`
asignado en el constructor). Carga útil de `FacturaEmitida` (lo que Cartera necesitará sin
re-consultar): `empresaId, usuarioId, sedeId, facturaId, clienteId, numero, fecha, fechaVencimiento,
total`. `FacturaAnulada`: `empresaId, usuarioId, sedeId, facturaId`.

NO crees ningún listener para estos eventos (los crea el plan 008). El bus tolera publicar sin
suscriptores (`DomainEventBus.publish`, `FAIL_ZERO_SUBSCRIBER` ignorado).

**Verify**: `mvnw.cmd -q -B compile` → exit 0.

### Step 6: Services

`ResolucionDianService(+Impl)`: CRUD estándar (create/update/delete lógico/findById/list) calcando
cualquier service maestro (p. ej. `ImpuestoServiceImpl`). Reglas: `numero_resolucion` no duplicado
en la empresa (atrapa `DataIntegrityViolationException` o pre-consulta); update no permite bajar
`factura_final` por debajo de `consecutivo_actual`.

`FacturaService(+Impl)` calcando `OrdenCompraServiceImpl` + `RecepcionServiceImpl`:

- `create(dto)`: estado `borrador`, totales calculados con la misma aritmética de
  `OrdenCompraServiceImpl.aplicarTotales/totalesLinea` (subtotal = Σ(cantidad·unitario − descuento);
  total = subtotal + impuestos), validar cliente (`terceroExisteEnEmpresa`), productos
  (`countProductosEnEmpresa`) y resolución (existe, activa, no eliminada, de la empresa).
  Encabezado + líneas dentro de `tx::transactional`. `numero` queda NULL en borrador.
- `update(dto)` / `delete(id)`: solo en estado `borrador` (mensajes en español, mismo formato que
  `OrdenCompraServiceImpl:95,126`).
- `findById(id)` / `list(request)`: vía QueryRepository; cross-tenant y soft-delete → 404
  "Factura no encontrada" (calca `cargarEntidad` de `OrdenCompraServiceImpl:236-245`).
- `emitir(id, EmitirFacturaRequestDto params)` — el corazón del plan. Dentro de UNA transacción:
  1. Cargar factura (404 si no es de la empresa); exigir estado `borrador`; exigir líneas no vacías.
  2. Validar resolución: activa, `deleted_at IS NULL`; si `fecha_inicial`/`fecha_final` no son null,
     `factura.fecha` dentro del rango; si `factura_final` no es null, `consecutivo_actual < factura_final`
     (si no: `GlobalException(BAD_REQUEST, "La resolución DIAN no está vigente o agotó su rango")`).
  3. `siguienteNumero(...)` (FOR UPDATE) → `numero = (prefijo == null ? "" : prefijo) + nuevo`.
     Si `factura_inicial` no es null y `nuevo < factura_inicial`, usa
     `factura_inicial + (nuevo - 1)` NO — STOP: en ese caso simplemente valida
     `nuevo >= factura_inicial` y si no se cumple, setea el consecutivo al valor de
     `factura_inicial` (primera emisión de la resolución). Implementa esto en
     `siguienteNumero`: `nuevo = GREATEST(consecutivo_actual + 1, COALESCE(factura_inicial, 1))`.
  4. Inventario: para cada línea cuyo producto esté en `productosQueManejanInventario`,
     resolver `bodega = linea.bodega_id ?? factura.bodega_id`; si es null →
     `GlobalException(BAD_REQUEST, "La línea requiere bodega para descontar inventario")`.
     Validar `cantidadDisponible(...) >= cantidad` → si no:
     `GlobalException(BAD_REQUEST, "Inventario insuficiente para el producto")`.
     Insertar `MovimientoInventarioEntity` con `tipo="salida"`, `cantidad = cantidad.negate()`
     (convención del módulo), `costo_unitario = 0` best-effort (el costeo promedio es seguimiento
     del módulo inventario), `origen_modulo="facturacion"`, `origen_id=factura.getId()`,
     descripción `"Factura #" + numero`. Usa `MovimientoInventarioR2dbcRepository` directamente,
     igual que `RecepcionServiceImpl` (línea 174).
  5. Estado → `emitida`, `usuario_modificacion`, `updated_at`; save.
  6. **Después del commit** (`.as(tx::transactional).then(Mono.fromRunnable(...))`):
     publicar `AsientoContableSolicitado` SOLO si `params` trae `periodoContableId`,
     `cuentaCarteraId` y `cuentaIngresoId`: débito `cuentaCarteraId` por `total`; crédito
     `cuentaIngresoId` por `total − impuestos`; si `impuestos > 0`, crédito `cuentaImpuestoId`
     por `impuestos` (si `impuestos > 0` y no hay `cuentaImpuestoId`, acredita todo a ingreso).
     Y SIEMPRE publicar `FacturaEmitida`.
- `anular(id, EmitirFacturaRequestDto params)`: solo desde `emitida`. En una transacción: crear
  movimientos de inventario `tipo="ajuste"` con `cantidad` positiva (reversa de cada salida que la
  emisión generó — consúltalos por `origen_modulo='facturacion' AND origen_id=:id`, patrón de
  `MovimientoInventarioServiceImpl.reversar`, líneas 100-125), estado → `anulada`. Tras commit:
  si params trae cuentas, publicar asiento inverso (débito ingreso / crédito cartera); SIEMPRE
  publicar `FacturaAnulada`. La factura emitida NUNCA se edita ni se borra (regla RF4 del diseño).

**Verify**: `mvnw.cmd -q -B compile` → exit 0.

### Step 7: Controllers

- `ResolucionDianController` en `/api/resoluciones-dian`: POST, PUT, DELETE `/{id}`, GET `/{id}`,
  POST `/list`. Calca la forma exacta de `OrdenCompraController` (envoltura
  `Mono<ResponseEntity<ApiResponse<T>>>`, `@Tag`, `@Operation`, mensajes en español).
- `FacturaController` en `/api/facturas`: POST (crear borrador), PUT (actualizar borrador),
  DELETE `/{id}` (lógico, solo borrador), GET `/{id}` (con líneas), POST `/list`,
  POST `/{id}/emitir` (body opcional `EmitirFacturaRequestDto`),
  POST `/{id}/anular` (body opcional `EmitirFacturaRequestDto`).

**Verify**: `mvnw.cmd -q -B compile` → exit 0.

### Step 8: Documentación

1. `docs/hu/HU-0010-facturacion.md` siguiendo `docs/hu/PLANTILLA-HU.md` (mira `docs/hu/HU-0008-compras.md`
   como ejemplo de tono): actor, flujo borrador→emitida→anulada, criterios de aceptación (numeración
   por resolución, salida de inventario, asiento, eventos), y la nota explícita de que `factura_dian`
   /DIAN real quedan para una fase posterior.
2. `docs/api/sprint7-facturacion.http` calcando `docs/api/sprint6-compras.http`: crear resolución,
   crear factura borrador, emitir (con y sin params contables), consultar, anular, listar.

**Verify**: ambos archivos existen; `mvnw.cmd -q -B compile` sigue en exit 0.

## Test plan

- Si `plans/001` (baseline Testcontainers) está DONE: añade
  `src/test/java/.../FacturaIntegrationTest.java` extendiendo su `AbstractIntegrationTest` con:
  (1) crear resolución + factura borrador → 201; (2) emitir → 200, `numero` con prefijo y
  consecutivo, movimiento de inventario `salida` creado, saldo descontado; (3) emitir factura ya
  emitida → 400; (4) emitir sin inventario suficiente → 400; (5) factura de otra empresa → 404;
  (6) anular → movimiento de reversa + estado `anulada`.
- Si `plans/001` NO está DONE: deja los casos anteriores listados como pendientes en la HU-0010 y
  verifica manualmente con `docs/api/sprint7-facturacion.http` contra `docker compose up -d`
  (documenta en el commit que la verificación fue manual). No bloquees el plan por esto.
- Verificación mínima siempre: `mvnw.cmd -B test` → exit 0.

## Done criteria

- [ ] `mvnw.cmd -q -B compile` exit 0.
- [ ] `mvnw.cmd -B test` exit 0.
- [ ] Existen los 2 controllers, 2 services (+impl), 3 entidades, 3+ repos, mappers y DTOs listados en Scope.
- [ ] `grep -rn "empresaId" src/main/java/com/cloud_tecnoligical/nyxora_erp/dto/facturacion/` → sin matches (regla 01).
- [ ] Ninguna migración nueva en `src/main/resources/db/migration` (`git status`).
- [ ] `FacturaEmitida`/`FacturaAnulada` se publican en `FacturaServiceImpl` y NO existe ningún listener nuevo.
- [ ] `docs/hu/HU-0010-facturacion.md` y `docs/api/sprint7-facturacion.http` creados.
- [ ] Fila 007 de `plans/README.md` actualizada.

## STOP conditions

Stop y reporta (no improvises) si:

- Las columnas reales de `V11__facturacion_schema.sql` no coinciden con las listadas aquí
  (alguien creó una migración que las altera).
- `MovimientoInventarioEntity` no tiene alguno de los campos que `RecepcionServiceImpl.movimientoEntrada`
  setea (drift en inventario).
- El constructor de `AsientoContableSolicitado` cambió de firma (el plan 009 lo modifica; si 009
  ya corrió, adapta la publicación a su nueva API `ContabilidadOutbox` y dilo en el reporte).
- Necesitas tocar un archivo fuera del Scope para compilar.
- Un paso de compilación falla dos veces tras intento razonable de arreglo.

## Maintenance notes

- El plan 008 (Cartera+Caja) consume `FacturaEmitida`/`FacturaAnulada`: si cambias su carga útil,
  coordina con ese plan.
- El plan 009 (outbox durable) cambiará `eventBus.publish(asiento)` por una API durable; la
  publicación del asiento en `FacturaServiceImpl` deberá migrarse igual que la de `RecepcionServiceImpl`.
- Deuda aceptada y conocida: el asiento requiere que el CLIENTE de la API pase las cuentas contables
  (`EmitirFacturaRequestDto`), igual que en recepciones. La parametrización contable por tipo de
  documento (tabla de cuentas por defecto) es un siguiente paso del roadmap — no lo resuelvas aquí.
- `costo_unitario = 0` en la salida de venta implica kardex sin costo de venta real; el costeo
  promedio en salidas es deuda explícita del módulo inventario.
- Revisor: vigilar (1) que `siguienteNumero` corra DENTRO de la transacción (sin eso el FOR UPDATE
  no serializa), (2) cross-tenant=404 en `emitir`/`anular`, (3) que la emisión no sea posible dos veces.
