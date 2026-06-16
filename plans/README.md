# Implementation Plans

Generados por el skill `improve` el 2026-06-11, auditoría nivel `standard` sobre el commit `84c4959`.
Ejecutar en el orden de la tabla salvo que las dependencias indiquen otra cosa. Cada ejecutor:
leer el plan completo antes de empezar, respetar sus STOP conditions y actualizar su fila al terminar.

Invocación no interactiva: se escribieron planes para los 6 hallazgos de mayor leverage
(selección por defecto del skill; el resto de hallazgos queda registrado abajo).

## Execution order & status

| Plan | Title | Priority | Effort | Depends on | Status |
|------|-------|----------|--------|------------|--------|
| 001  | Baseline de pruebas de integración (Testcontainers) | P1 | M | — | TODO |
| 002  | Rechazar refresh tokens como access + usuario activo en refresh | P1 | S | 001 (tests) | TODO |
| 003  | Eliminar secreto JWT por defecto y seeder demo opt-in | P1 | S | — (coordinar con 001) | TODO |
| 004  | Login determinista: username único global (V29) | P1 | M | 001 | TODO |
| 005  | No filtrar detalles internos en respuestas de error | P2 | S | 001 | TODO |
| 006  | RBAC: permisos en endpoints mutadores | P2 | M | 001, 002 | TODO |
| 007  | Vertical slice de Facturación (Sprint 7 / HU-0010) | P1 | L | — (coordinar 001/006) | TODO |
| 008  | Vertical slice de Cartera + Caja (Sprint 8 / HU-0011/0012) | P1 | L | 007 | TODO |
| 009  | Outbox durable para eventos de dominio | P2 | M | — (recomendado tras 007) | TODO |

Status values: TODO | IN PROGRESS | DONE | BLOCKED (con motivo) | REJECTED (con justificación)

## Dependency notes

- **001 primero**: es el baseline de verificación; los demás planes añaden tests que extienden su `AbstractIntegrationTest`. 002/003 pueden aplicar sus cambios de código sin 001, pero quedarían sin tests de integración.
- **003 ↔ 001**: si 003 corre después de 001, debe añadir `JWT_SECRET` al `@DynamicPropertySource` de `AbstractIntegrationTest` (paso 4 de 003). Si corre antes, el paso 2 de 001 trae la nota correspondiente.
- **006 requiere 002**: el RBAC asume que solo tokens de tipo `access` (con permisos) autentican.
- **004** crea la migración `V29`; cualquier otro plan/HU que cree migraciones debe coordinar numeración (009 crea una migración: usar el siguiente número libre real al ejecutar).
- **008 requiere 007**: consume los eventos `FacturaEmitida`/`FacturaAnulada` que 007 define, y retira `EventoDemoController` solo cuando el ciclo real existe.
- **009 después de 007 (recomendado)**: migra todos los publicadores de eventos al outbox de una pasada; si corre antes, 007/008 deben publicar vía `OutboxPublisher` (sus STOP conditions lo contemplan).
- **Orden sugerido para el objetivo "ERP completo"**: 001 → 003 → 002 → 007 → 008 → 004/005/006 en paralelo con → 009. Lo mínimo para demo vendible: 007+008; lo mínimo para producción: todo lo anterior.

## Hallazgos sin plan (registrados para no re-auditar)

- **[BUG-02] `super_admin` inalcanzable**: `EmpresaServiceImpl:42,78,96,108` exige `isSuperAdmin()`, pero `AuthServiceImpl` construye `TenantInfo(..., false)` siempre y no existe columna `super_admin` en `usuario` (ninguna migración la define). Gestión de empresas vía API está muerta. Requiere decisión de producto (columna en BD + flujo de provisión) — convertir en HU, no en fix mecánico.
- **[BUG-03] Multi-write sin transacción + DELETE físico en `tercero_clasificacion`**: `TerceroServiceImpl.create/update` (save + `setClasificacion`) sin `TransactionalOperator`; `TerceroQueryRepository.setClasificacion:162` hace `DELETE` físico mientras las lecturas filtran `deleted_at IS NULL` (inconsistencia con regla 01). Effort S-M; abordar al tocar el módulo terceros.
- **[DEBT-01] `MapperRepository.mapResultSetToObject` convierte NULL→0/0L/""** (líneas 105-130): ids nulos salen como `0` en las respuestas. Es convención heredada del equipo; investigar consumidores antes de cambiar (riesgo de romper el frontend). Nota: el archivo tiene un cambio local sin commitear (+13 líneas).
- **[DEBT-02] `PageableDto` sin `@Min`/máximo de `rows`**: page/rows negativos o enormes llegan a `OFFSET`/`LIMIT` (error de BD o fetch sin límite). Effort S.
- **[DX-01] `CLAUDE.md` desactualizado**: dice "Pendiente: seguridad JWT" y "Migraciones V1–V3" cuando hay V28 y el paquete `security/` completo; los agentes del repo se guían por él. Effort S — actualizar tras aterrizar 002/003. Añadir también `.env.example` al onboarding y CI (el roadmap ya lo lista como pendiente).
- **Higiene**: archivo extraviado `src/main/java/.../entity/.claude/settings.local.json` (no versionado); eliminarlo del árbol.

## Findings considered and rejected

- **CORS `localhost:*` con credenciales** (`SecurityConfig:49`): explícitamente documentado como configuración de dev a restringir en producción — by design por ahora.
- **Bus de eventos en memoria best-effort** (`DomainEventBus`): decisión documentada (ADR AD-R7). Se registra como dirección futura (outbox durable), no como defecto.
- **`/actuator/**` en rutas públicas** (`SecurityConfig:23`): actuator no es dependencia del pom; la entrada es inocua hoy. Revisar si algún día se añade el starter.
- **bcrypt bloqueante en event loop**: falso — `AuthServiceImpl` y `UsuarioServiceImpl` ya usan `Schedulers.boundedElastic()` para `encode`/`matches`.
- **Inyección SQL en QueryRepositories**: no encontrada — `search` va parametrizado en los 22 repos y `ORDER BY` usa allowlist `SORTABLE` en los 24; patrón consistente.

## Dirección (opciones para el mantenedor, no defectos)

1. **CI mínima (GitHub Actions: `mvnw -B test` + docker)** — el roadmap (`docs/roadmap-fases-sprints.md:20`) ya la lista como pendiente; con el plan 001 la suite existe y la CI es una hora de trabajo con retorno inmediato.
2. **Slice de facturación** — convertido en **plan 007**.
3. **Outbox durable para `AsientoContableSolicitado`** — convertido en **plan 009**.

---

# Dirección y roadmap (auditoría `deep` de dirección, 2026-06-11, commit `84c4959`)

Objetivo del mantenedor: ERP "potente, completo y competitivo". Esta sección resume el estado real
frente al diseño (`docs/analisis-erp-referencia/`, `docs/roadmap-fases-sprints.md`) y prioriza lo que sigue.

## Matriz de cobertura (verificada contra código y migraciones, no contra docs)

Niveles: **A** = código + esquema · **B** = solo esquema BD · **C** = solo documentado · **D** = ni documentado.

| Capacidad | Nivel | Evidencia |
|---|---|---|
| Administración (empresa, sede, usuario, rol, permiso, vigencia, parámetro) | A | Controllers/services + V1 — pero RBAC sin aplicar (plan 006), `super_admin` muerto (BUG-02), **auditoría automática prometida en Sprint 1 y NO implementada** (0 clases `Auditoria*`) |
| Auth JWT multitenant reactivo | A | `security/` completo; endurecimiento en planes 002–004 |
| Común: terceros, productos, impuestos, organización, motor de documentos (consecutivo `FOR UPDATE`), adjuntos, catálogos | A | V2–V7 + slices completos (HU-0002–0006) |
| Contabilidad (plan de cuentas, periodo, comprobante, movimiento append-only, saldos proyección) + bus de eventos | A | V8 + slice + `DomainEventBus`/`InterfazContableListener` (ADR AD-R7) |
| Inventario (bodega, ubicación, lote, marca, movimiento append-only, kardex, saldo) | A | V9 + slice (HU-0009) |
| Compras (orden + recepción → inventario + asiento por evento) | A | V10 + slice (HU-0008) |
| **Facturación** | **B** | V11 sin una sola clase → **plan 007** |
| **Cartera** / **Caja** | **B** | V12/V13 sin código → **plan 008** |
| Tesorería, CxP, Costos, Presupuesto, Activos fijos, Contratación, THU, Nómina, Académico, Jurídico | B | V14–V23 sin código ni HU |
| Cierres orquestados, estados financieros, reportería fiscal (exógena) | C | Sprint 16 del roadmap; sin esquema dedicado |
| **Frontend (Angular SPA del diseño Fase 9)** | **A** | **CORRECCIÓN (auditoría frontend 2026-06-11)**: SÍ existe — repo hermano `D:\Proyectos Camilo\erp_camilo\nyxora_erp_frontend` (Angular 20 + PrimeNG, ~30 pantallas CRUD cubriendo todos los módulos con backend, login/guard/interceptor). Hallazgos y planes propios en `nyxora_erp_frontend\plans\` (F01–F03) |
| CI/CD | C | `.github/` sin workflows; "pendiente" desde Sprint 0 |
| Tests (DoD de TODOS los sprints) | C | Solo `NyxoraErpApplicationTests` (context load) → plan 001 |
| Facturación electrónica DIAN real (UBL/CUFE/proveedor tecnológico) | C (parcial B) | Tabla `factura_dian` como metadata; FE real diferida (Q8, fase 9 §7) |
| Nómina electrónica DIAN, PILA operativa | B/C | `aporte_pila` en V21; sin código ni decisión normativa (pregunta 3, fase 8 §8.5) |
| Multi-moneda | **D** | Ningún doc ni columna de moneda/tasa de cambio |
| POS (punto de venta), parametrización contable por tipo de documento, RLS en Postgres | D / C | Caja es recaudo, no POS; las cuentas contables hoy viajan en cada request; RLS solo mencionada en backlog Épica 0 |

## Estado real de los sprints (vs. lo que dicen los docs)

- **Sprint 0**: "en cierre" desde el inicio — CI y healthcheck siguen pendientes (sin workflows).
- **Sprints 1–6**: el código sí está entregado (verificado: 33 controllers, 33 services). Pero la
  **deuda declarada del DoD nunca se pagó en ningún sprint**: cero tests WebTestClient, cero revisión
  de ciberseguridad, RBAC anunciado en Sprint 1 sin aplicar a endpoints, auditoría automática de
  Sprint 1 inexistente. Los planes 001–006 son exactamente ese pago.
- **Sprints 7–8**: siguientes en el orden correcto del roadmap (el orden núcleo → contabilidad →
  operación → recaudo es sólido y se mantiene). Planes 007–008.
- **Sprints 9–16**: solo esquema. El cronograma "~16 sprints ≈ 8 meses" es optimista si la deuda de
  DoD sigue acumulándose; con 001 + CI, el costo marginal de cumplir el DoD por sprint baja mucho.

## Gap competitivo (honesto) — qué falta para competir con Siigo / World Office / SAP B1 / Odoo

Brechas en orden de impacto comercial para el mercado colombiano/latam:

1. **Sin facturación no hay producto** — resuelto por 007+008 (ciclo order-to-cash completo).
2. ~~Sin frontend no hay demo~~ **CORREGIDO (2026-06-11)**: el frontend SÍ existe
   (`nyxora_erp_frontend`, Angular 20 + PrimeNG, cobertura completa de los módulos con backend,
   typecheck limpio). La brecha real es menor: refresh token sin usar, RBAC de UI sin aplicar y
   cero tests — planes F01–F03 en `nyxora_erp_frontend\plans\`. Cuando aterricen 007/008 (backend),
   el frontend debe crecer facturación/cartera/caja (el menú ya tiene los ítems en placeholder).
3. **Facturación electrónica DIAN real** — en Colombia es requisito legal para vender a casi
   cualquier negocio. Camino pragmático para equipo pequeño: integrar un proveedor tecnológico
   habilitado vía API (no certificarse como proveedor propio); la tabla `factura_dian` ya está
   pensada para los estados de acuse. Bloqueada por la pregunta normativa 3 (fase 8 §8.5).
4. **Parametrización contable** — hoy el cliente de la API debe enviar las cuentas contables en cada
   recepción/factura/recibo. Ningún contador acepta eso. Tabla de "interfaces contables" por tipo de
   documento (cuenta inventario/ingreso/cartera/caja/impuesto por defecto) es esfuerzo M y elimina
   la fricción nº 1 de uso real.
5. **Reportería fiscal y financiera** — balance de prueba, estados financieros básicos, certificados
   de retención, exógena. Siigo/World Office ganan aquí; las proyecciones (`saldo_contable`) ya dan
   la base.
6. **Nómina (+ nómina electrónica DIAN)** — segundo requisito legal colombiano; esquema V20–V21 listo,
   esfuerzo XL, posponer hasta tener 1–5 clientes facturando.
7. **Multi-moneda** — no documentado siquiera; relevante solo si el target supera pymes locales.
   Decidir explícitamente "no por ahora" y documentarlo.
8. **Ventajas reales a explotar**: multi-tenant nativo (SaaS desde el día 1, cosa que World Office
   no tiene bien resuelta), stack moderno reactivo, append-only + proyecciones recalculables
   (auditabilidad superior al ERP de referencia), y fronteras por eventos que permiten crecer módulos
   sin romper el monolito.

## Roadmap priorizado (justificación de negocio)

| # | Hito | Por qué ahora | Esfuerzo | Plan |
|---|---|---|---|---|
| 1 | Pagar deuda DoD mínima (tests + secretos + JWT) | Sin baseline verificable, cada sprint nuevo agrava el riesgo | M | 001–003 |
| 2 | **Facturación** (Sprint 7) | Sin ciclo de ingreso no hay ERP vendible | L | **007** |
| 3 | **Cartera + Caja** (Sprint 8) | Cierra order-to-cash; primera demo de negocio completa | L | **008** |
| 4 | Outbox durable | Integridad contable antes de operar con datos reales | M | **009** |
| 5 | CI mínima + RBAC + username único | Hardening para multiusuario real | S+M | 004–006 + 1h CI |
| 6 | Parametrización contable por tipo de documento | Usabilidad contable real (hoy las cuentas viajan en el request) | M | HU futura |
| 7 | Frontend: pantallas de facturación/cartera/caja sobre la SPA existente (+ F01–F03) | El frontend ya existe (`nyxora_erp_frontend`); falta endurecerlo (F01–F03) y darle UI al order-to-cash de 007/008 | L | F01–F03 + HU futura |
| 8 | FE DIAN vía proveedor tecnológico | Requisito legal CO; `factura_dian` ya lo modela | L | HU futura (tras cerrar pregunta normativa) |
| 9 | Reportería financiera básica (balance de prueba, EEFF) | Paridad mínima con competidores | M | HU futura |
| 10 | Tesorería/CxP → Nómina → resto de V14–V23 | Orden del roadmap original, sigue siendo correcto | XL | sprints 9–16 |

Lo descartado conscientemente por ahora: multi-moneda, POS, verticales académico/jurídico/salud
(fase 8 los marca como opcionales por tipo de cliente), microservicios/colas externas (AD-1/AD-R3 vigentes).
