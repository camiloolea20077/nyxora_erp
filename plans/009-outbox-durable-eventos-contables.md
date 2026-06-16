# Plan 009: Outbox durable para los eventos de dominio (no perder asientos contables ni CxC)

> **Executor instructions**: Follow this plan step by step. Run every
> verification command and confirm the expected result before moving to the
> next step. If anything in the "STOP conditions" section occurs, stop and
> report — do not improvise. When done, update the status row for this plan
> in `plans/README.md` — unless a reviewer dispatched you and told you they
> maintain the index.
>
> **Drift check (run first)**: `git diff --stat 84c4959..HEAD -- src/main/java/com/cloud_tecnoligical/nyxora_erp/event src/main/java/com/cloud_tecnoligical/nyxora_erp/service/impl/RecepcionServiceImpl.java src/main/resources/db/migration`
> Compara los extractos de "Current state" contra el código vivo. Este plan
> modifica el bus de eventos: si los planes 007/008 ya corrieron, hay más
> publicadores que migrar (está contemplado en los pasos).

## Status

- **Priority**: P2
- **Effort**: M
- **Risk**: MED
- **Depends on**: ninguno técnico. **Recomendado después de 007** (y de 008 si es posible) para
  migrar todos los publicadores de una vez. Coordinar numeración de migración con `plans/004` (que reserva V29).
- **Category**: direction / tech-debt
- **Planned at**: commit `84c4959`, 2026-06-11

## Why this matters

Hoy el bus de eventos es **in-memory y best-effort** (ADR AD-R7,
`docs/arquitectura/adr-bus-eventos-dominio.md:37-41`): si el proceso cae —o el listener falla— entre
que Compras/Facturación publica `AsientoContableSolicitado` y Contabilidad lo procesa, **el asiento
se pierde sin rastro**. Eso era aceptable en demo; con facturación real (plan 007) y cartera (plan
008) significa contabilidad incompleta y CxC fantasma: un defecto de integridad financiera, lo
peor que le puede pasar a un ERP. Este plan añade una **tabla outbox** donde cada evento se persiste
ANTES de publicarse, un poller reactivo que reintenta los pendientes y marcado de procesados. El
contrato `DomainEvent` y los listeners actuales se conservan (la migración a outbox estaba prevista
en el propio ADR: "Migración futura a outbox/cola... sin cambiar el contrato DomainEvent").

## Current state

- `src/main/java/com/cloud_tecnoligical/nyxora_erp/event/DomainEventBus.java` — `Sinks.many()
  .multicast().onBackpressureBuffer()`; `publish(DomainEvent)` con `tryEmitNext` (líneas 20-29);
  `on(Class<E>)` devuelve `Flux<E>` filtrado (líneas 32-34). **No se modifica.**
- `src/main/java/com/cloud_tecnoligical/nyxora_erp/event/DomainEvent.java` — interfaz marcadora
  (empresaId, usuarioId, sedeId, ocurridoEn).
- `src/main/java/com/cloud_tecnoligical/nyxora_erp/event/AsientoContableSolicitado.java` — campos
  `final` + `@Getter` + constructor de 9 argumentos (líneas 16-45). No es deserializable por
  Jackson tal cual (sin no-args constructor ni `@JsonCreator`).
- `src/main/java/com/cloud_tecnoligical/nyxora_erp/event/InterfazContableListener.java` — se
  suscribe en `@PostConstruct` (líneas 33-42), procesa con `comprobanteService.crearYConfirmar(dto)`
  reconstruyendo el tenant, y en error solo loguea (`onErrorResume(e -> Mono.empty())`).
- Publicadores actuales de `AsientoContableSolicitado`:
  - `service/impl/RecepcionServiceImpl.java` — método `publicarAsiento` (líneas 212-242), invocado
    como `Mono.fromRunnable(() -> publicarAsiento(...))` tras el commit (línea 147).
  - `controller/EventoDemoController.java` — endpoint demo (si el plan 008 corrió, ya no existe).
  - Si 007/008 corrieron: `FacturaServiceImpl` (asiento + `FacturaEmitida`/`FacturaAnulada`) y
    `ReciboCajaServiceImpl` (asiento). `CarteraListener` consume `FacturaEmitida`/`FacturaAnulada`.
- Migraciones: la última aplicada es `V28__seed_tipos_tercero_entidades.sql`; el plan 004 reserva
  `V29`. **Antes de crear la migración, lista `src/main/resources/db/migration` y usa el siguiente
  número libre real** (probablemente V29 o V30).
- Convenciones: `.claude/rules/00-convenciones-backend.md` (reactivo, QueryRepository con
  `DatabaseClient`), `02-base-datos.md` (migración inmutable, `COMMENT ON`, índice en `empresa_id`).
  Jackson (`ObjectMapper`) ya está en el classpath vía spring-boot-starter-webflux.

## Commands you will need

| Purpose | Command (raíz del repo) | Expected on success |
|---|---|---|
| Compilar | `mvnw.cmd -q -B compile` | exit 0 |
| Tests | `mvnw.cmd -B test` | exit 0 |
| BD local (valida la migración) | `docker compose up -d` y arrancar la app | Flyway aplica la nueva versión sin error |

## Scope

**In scope**:

- `src/main/resources/db/migration/V<n>__comun_evento_outbox.sql` (n = siguiente libre).
- `src/main/java/com/cloud_tecnoligical/nyxora_erp/entity/EventoOutboxEntity.java`.
- `src/main/java/com/cloud_tecnoligical/nyxora_erp/repository/evento/EventoOutboxR2dbcRepository.java`
  y `EventoOutboxQueryRepository.java`.
- `src/main/java/com/cloud_tecnoligical/nyxora_erp/event/OutboxPublisher.java` (nuevo componente).
- `src/main/java/com/cloud_tecnoligical/nyxora_erp/event/OutboxPoller.java` (nuevo componente).
- Modificar: `AsientoContableSolicitado.java` (hacerlo (de)serializable + campo `outboxId`),
  `InterfazContableListener.java` (marcar procesado/fallido),
  `RecepcionServiceImpl.java` (publicar vía outbox),
  y si existen: `FacturaServiceImpl.java`, `ReciboCajaServiceImpl.java`, `FacturaEmitida.java`,
  `FacturaAnulada.java`, `CarteraListener.java`, `EventoDemoController.java` (mismo cambio mecánico).
- `docs/arquitectura/adr-bus-eventos-dominio.md` — añadir sección "Actualización: outbox durable"
  (no reescribir el ADR; los ADR se enmiendan, no se editan en silencio).
- `plans/README.md` (tu fila).

**Out of scope** (NO tocar):

- `DomainEventBus.java` — el transporte in-process se conserva tal cual.
- Kafka/RabbitMQ/colas externas — explícitamente NO (AD-R3 sigue vigente: monolito).
- Reintentos con backoff sofisticado, DLQ, métricas — v1 simple: contador de intentos + estado `error`.
- `MapperRepository.java` (cambio local sin commitear).

## Git workflow

- Branch: `feature/outbox-eventos-dominio`. Commits `feat: outbox - <qué>`. Sin push/PR sin instrucción.

## Steps

### Step 1: Migración de la tabla outbox

Lista `src/main/resources/db/migration`, toma el siguiente número libre `n` y crea
`V<n>__comun_evento_outbox.sql`:

```sql
CREATE TABLE evento_outbox (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id    BIGINT       NOT NULL,
    tipo_evento   VARCHAR(120) NOT NULL,            -- nombre simple de la clase del evento
    payload       JSONB        NOT NULL,            -- evento serializado (Jackson)
    estado        VARCHAR(15)  NOT NULL DEFAULT 'pendiente',
    intentos      INT          NOT NULL DEFAULT 0,
    ultimo_error  TEXT,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at  TIMESTAMP,
    CONSTRAINT fk_outbox_empresa FOREIGN KEY (empresa_id) REFERENCES empresa (id),
    CONSTRAINT ck_outbox_estado  CHECK (estado IN ('pendiente', 'procesado', 'error'))
);
CREATE INDEX ix_outbox_pendientes ON evento_outbox (estado, created_at);
CREATE INDEX ix_outbox_empresa    ON evento_outbox (empresa_id);
COMMENT ON TABLE evento_outbox IS 'Outbox de eventos de dominio: persistidos antes de publicarse al bus in-process; el poller reintenta pendientes (enmienda al ADR AD-R7).';
COMMENT ON COLUMN evento_outbox.estado IS 'pendiente → procesado | error (tras 5 intentos fallidos)';
```

Es **append-mostly**: sin `deleted_at` (los procesados se conservan como rastro; una purga
programada es follow-up).

**Verify**: con `docker compose up -d` y la app arrancando, Flyway aplica `V<n>` sin error
(log `Successfully applied ... migration`). Si no puedes arrancar la app, al menos
`mvnw.cmd -q -B compile` → exit 0 y revisa el SQL a mano contra `02-base-datos.md`.

### Step 2: Entidad y repositorios

1. `EventoOutboxEntity` (`@Table("evento_outbox")`, columnas snake_case como
   `OrdenCompraEntity.java`; `payload` como `String` — R2DBC Postgres mapea JSONB↔String con
   cast: en los INSERT/UPDATE del QueryRepository usa `:payload::jsonb`).
2. `EventoOutboxR2dbcRepository extends R2dbcRepository<EventoOutboxEntity, Long>`.
3. `EventoOutboxQueryRepository` (en `repository/evento/`, `DatabaseClient`):
   - `insertar(empresaId, tipoEvento, payloadJson)` → `Mono<Long>`:
     `INSERT INTO evento_outbox (empresa_id, tipo_evento, payload) VALUES (:e, :t, :p::jsonb) RETURNING id`.
   - `marcarProcesado(id)` → `UPDATE evento_outbox SET estado='procesado', processed_at=now() WHERE id=:id`.
   - `registrarFallo(id, error)` → `UPDATE evento_outbox SET intentos=intentos+1, ultimo_error=:err,
     estado = CASE WHEN intentos+1 >= 5 THEN 'error' ELSE 'pendiente' END WHERE id=:id`.
   - `reclamarPendientes(limite)` → `Flux<EventoOutboxEntity>`:
     ```sql
     UPDATE evento_outbox SET intentos = intentos
     WHERE id IN (
         SELECT id FROM evento_outbox
         WHERE estado='pendiente' AND created_at < now() - INTERVAL '60 seconds'
         ORDER BY created_at
         LIMIT :limite
         FOR UPDATE SKIP LOCKED
     )
     RETURNING id, empresa_id, tipo_evento, payload::text AS payload, estado, intentos, ultimo_error, created_at, processed_at
     ```
     (`SKIP LOCKED` evita doble proceso si algún día hay 2 instancias; el `UPDATE` no-op sirve
     para usar `RETURNING` sobre filas bloqueadas en una sola sentencia).

**Verify**: `mvnw.cmd -q -B compile` → exit 0.

### Step 3: Eventos serializables + outboxId

En `AsientoContableSolicitado.java` (y `FacturaEmitida`/`FacturaAnulada` si existen):

- Quita `final` de los campos, añade `@Setter` y un constructor sin argumentos (Jackson), conserva
  el constructor completo existente (los publicadores no cambian su construcción).
- Añade `private Long outboxId;` con getter/setter, anotado `@JsonIgnore` NO — debe viajar:
  déjalo serializable pero documenta que lo setea `OutboxPublisher`, no el publicador.
- `ocurridoEn` deja de asignarse solo en el constructor completo: en el no-args queda null y
  Jackson lo puebla.

**Verify**: `mvnw.cmd -q -B compile` → exit 0.

### Step 4: OutboxPublisher

Nuevo `event/OutboxPublisher.java`:

```java
@Component
public class OutboxPublisher {
    private final EventoOutboxQueryRepository outboxRepo;
    private final DomainEventBus bus;
    private final ObjectMapper objectMapper; // bean de Spring

    /** Persiste el evento (pendiente) y luego lo emite al bus in-process. */
    public Mono<Void> publish(DomainEvent event) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(event))
            .flatMap(json -> outboxRepo.insertar(event.getEmpresaId(),
                                                 event.getClass().getSimpleName(), json))
            .doOnNext(id -> { setOutboxId(event, id); bus.publish(event); })
            .then();
    }
}
```

`setOutboxId` por instanceof sobre los tipos conocidos (o añade `default void setOutboxId(Long id)`
/ `default Long getOutboxId()` a `DomainEvent` y que cada evento lo implemente — elige UNA opción y
sé consistente). Registrar el `ObjectMapper` configurado con `JavaTimeModule` (el de Spring Boot ya
lo trae; inyéctalo, no hagas `new ObjectMapper()`).

Nota de diseño (documéntala en el Javadoc): la inserción en outbox NO comparte la transacción del
documento origen (los publicadores hoy publican después del commit). Si la app cae entre el commit
del documento y el insert del outbox, ese hueco persiste — es el mismo de hoy, pero ahora la ventana
es de milisegundos y lo normal (caída entre publicación y consumo) queda cubierto. Mover el insert
dentro de la transacción del documento es el siguiente nivel y queda como follow-up explícito.

**Verify**: `mvnw.cmd -q -B compile` → exit 0.

### Step 5: Listeners marcan resultado

En `InterfazContableListener.procesar` (y `CarteraListener` si existe):

- Tras éxito: si `ev.getOutboxId() != null` → `outboxRepo.marcarProcesado(ev.getOutboxId())`.
- En el `onErrorResume`: si `outboxId != null` → `outboxRepo.registrarFallo(outboxId, e.getMessage())`
  (y se sigue tragando el error para no tumbar el bus).
- Si `outboxId == null` (evento publicado directo al bus, p. ej. desde un test), comportamiento
  actual sin cambios.

**Verify**: `mvnw.cmd -q -B compile` → exit 0.

### Step 6: Poller de reintento

Nuevo `event/OutboxPoller.java`: `@Component` que en `@PostConstruct` se suscribe a
`Flux.interval(Duration.ofSeconds(30))` → `reclamarPendientes(20)` → por cada fila, deserializar
según `tipo_evento` (mapa estático `Map<String, Class<? extends DomainEvent>>` con los eventos
conocidos: `AsientoContableSolicitado`, y `FacturaEmitida`/`FacturaAnulada` si existen), setear
`outboxId` y `bus.publish(...)`. Si `tipo_evento` no está en el mapa → `registrarFallo` con
"tipo de evento desconocido". Errores del poller se loguean y no interrumpen el intervalo
(`onErrorResume` por elemento Y `onErrorContinue`/retry en el flujo del intervalo). Guarda el
`Disposable` y libéralo en `@PreDestroy`.

**Verify**: `mvnw.cmd -q -B compile` → exit 0.

### Step 7: Migrar los publicadores

Cambia cada `eventBus.publish(evento)` de eventos de dominio de NEGOCIO a
`outboxPublisher.publish(evento)` (reactivo: enchúfalo a la cadena con `.then(outboxPublisher.publish(ev))`
en lugar de `Mono.fromRunnable`):

- `RecepcionServiceImpl.publicarAsiento` (líneas 212-242): el método pasa de `void` a `Mono<Void>`
  y la llamada en `confirmar` (línea 147) cambia de
  `.then(Mono.fromRunnable(() -> publicarAsiento(...)))` a `.then(publicarAsiento(...))`.
- `EventoDemoController` (si aún existe), `FacturaServiceImpl` y `ReciboCajaServiceImpl` (si 007/008
  corrieron): mismo cambio mecánico.

**Verify**: `mvnw.cmd -q -B compile` → exit 0 y
`grep -rn "eventBus.publish" src/main/java --include=*ServiceImpl.java` → sin matches
(los services ya no publican directo; el bus solo lo usan `OutboxPublisher` y `OutboxPoller`).

### Step 8: Enmienda al ADR

Añade al final de `docs/arquitectura/adr-bus-eventos-dominio.md` una sección
`## Actualización <fecha> — Outbox durable` (6-10 líneas): qué se añadió (tabla `evento_outbox`,
`OutboxPublisher`, poller con `SKIP LOCKED`, 5 intentos → `error`), qué garantía nueva existe
(evento publicado = evento persistido; crash entre publicación y consumo se recupera) y qué hueco
queda (insert fuera de la transacción del documento origen).

**Verify**: sección presente; `mvnw.cmd -q -B compile` exit 0.

## Test plan

- Si `plans/001` (Testcontainers) está DONE — `OutboxIntegrationTest`:
  (1) publicar vía `OutboxPublisher` → fila `pendiente` creada y luego `procesado` (poll con timeout);
  (2) evento cuyo listener falla (p. ej. asiento descuadrado) → `intentos` incrementa y `ultimo_error`
  poblado; (3) fila `pendiente` vieja sin suscriptor inicial → el poller la re-publica y termina
  `procesado`; (4) serialización round-trip de `AsientoContableSolicitado` con `ObjectMapper`
  (este caso es test unitario puro, no necesita BD: escríbelo SIEMPRE, en
  `src/test/java/.../event/AsientoContableSolicitadoSerializationTest.java`).
- Si 001 NO está DONE: escribe al menos el test unitario (4) y verifica el flujo manualmente:
  arranca la app, confirma una recepción con params contables, y revisa
  `SELECT id, tipo_evento, estado, intentos FROM evento_outbox;` → fila `procesado`.
- Siempre: `mvnw.cmd -B test` → exit 0.

## Done criteria

- [ ] `mvnw.cmd -q -B compile` y `mvnw.cmd -B test` exit 0.
- [ ] Migración `V<n>__comun_evento_outbox.sql` creada y aplica limpia (log Flyway o revisión SQL).
- [ ] `grep -rn "eventBus.publish" src/main/java --include=*ServiceImpl.java` → sin matches.
- [ ] Test de serialización round-trip de `AsientoContableSolicitado` existe y pasa.
- [ ] Flujo manual o de integración demuestra fila `procesado` tras confirmar una recepción.
- [ ] ADR enmendado con la sección de actualización.
- [ ] Fila 009 de `plans/README.md` actualizada.

## STOP conditions

- El número de migración elegido ya existe en `db/migration` al momento de crear el archivo
  (re-lista y toma el siguiente).
- `DomainEvent`/`DomainEventBus` cambiaron de forma incompatible con los extractos de
  "Current state".
- La serialización de `CreateMovimientoContableDto` (payload del asiento) falla por algún tipo no
  soportado — repórtalo con el campo exacto en vez de cambiar el DTO.
- Spring no expone un bean `ObjectMapper` inyectable (no debería pasar con WebFlux).
- Una verificación falla dos veces.

## Maintenance notes

- **Follow-up 1 (el hueco restante)**: insertar la fila outbox DENTRO de la transacción del
  documento origen (pasar el `TransactionalOperator` o encadenar antes del commit) — elimina la
  última ventana de pérdida. Requiere refactor de los services emisores.
- **Follow-up 2**: purga programada de filas `procesado` antiguas (la tabla crece sin límite).
- **Follow-up 3**: pantalla/endpoint de administración de filas `error` (re-encolar manualmente).
- Cualquier evento de dominio NUEVO debe: (a) ser Jackson-(de)serializable, (b) registrarse en el
  mapa del `OutboxPoller`, (c) publicarse vía `OutboxPublisher`. Documentado en el ADR enmendado.
- Revisor: verificar que el poller usa `SKIP LOCKED` (sin eso, dos instancias duplicarían asientos)
  y que el listener de contabilidad sigue siendo idempotente-por-reintento razonable (un reintento
  tras éxito parcial puede duplicar un comprobante; la consulta previa por `origen_modulo/origen_id`
  en `ComprobanteService` sería el endurecimiento natural — anotado, no exigido aquí).
