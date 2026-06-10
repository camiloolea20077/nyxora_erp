# ADR — Bus de eventos de dominio in-process (reactivo)

| Campo | Valor |
|---|---|
| **Código** | AD-R7 (detalle de AD-R3) |
| **Estado** | Aceptado |
| **Contexto** | Sprint 4 (Contabilidad) — se necesita la "interfaz contable" desacoplada |

## Contexto
El ERP es un **monolito modular**: los módulos (compras, facturación, caja…) deben generar
contabilidad **sin acoplarse** al módulo de Contabilidad ni invocarlo directamente. El diseño
(AD-R3) decidió **eventos de dominio in-process**, sin event store ni CQRS completo. Como todo el
runtime es reactivo (WebFlux + R2DBC), el bus también debe ser **no bloqueante**.

## Decisión
Bus propio basado en **Reactor `Sinks`**:

- `DomainEvent` — interfaz marcadora; todo evento lleva `empresaId`, `usuarioId`, `sedeId` (para
  reconstruir el `TenantInfo` fuera del request) y `ocurridoEn`.
- `DomainEventBus` — componente Spring con `Sinks.many().multicast().onBackpressureBuffer()`.
  - `publish(DomainEvent)` — emite al sink (no bloqueante; `tryEmitNext` con reintento).
  - `on(Class<E>)` — `Flux<E>` filtrado por tipo para que cada listener se suscriba a lo suyo.
- **Listeners** = `@Component` que en `@PostConstruct` se suscriben a `bus.on(MiEvento.class)` y
  procesan reactivamente. El procesamiento corre **fuera del Reactor Context del request**, por lo que
  el listener **inyecta el TenantInfo** al contexto desde el propio evento:
  `servicio.hacer(...).contextWrite(TenantContext.write(tenantInfoDelEvento))`.

## Interfaz contable (Sprint 4)
- Evento: `AsientoContableSolicitado` (empresaId, fecha, origenModulo, origenId, descripción, líneas
  débito/crédito balanceadas).
- Listener: `InterfazContableListener` → llama a `ComprobanteService.crearDesdeEvento(...)` y lo
  **confirma**, generando el comprobante. Si falla (periodo cerrado, descuadre), se registra el error
  (no tumba al publicador).
- Endpoint demo: `POST /api/eventos/asiento-demo` publica un `AsientoContableSolicitado` para probar
  el flujo de punta a punta sin tener aún compras/facturación.

## Garantías y límites (v1)
- **En memoria, best-effort**: si el proceso cae, los eventos en vuelo se pierden. Aceptable en v1
  (AD-R3); la fuente de verdad sigue siendo el documento origen, que puede re-emitir.
- **No transaccional con el publicador**: el evento se procesa de forma asíncrona; no comparte la
  transacción del request que lo emite (consistencia eventual dentro del mismo proceso).
- Migración futura a outbox/cola (si se requiere durabilidad) sin cambiar el contrato `DomainEvent`.

## Consecuencias
- Módulos desacoplados: compras/facturación publican; Contabilidad reacciona. Sin dependencia de código.
- Reutiliza el `ComprobanteService` (mismo camino que la API), con el tenant inyectado desde el evento.
