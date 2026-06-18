# Roadmap Nyxora ERP — Fases y Sprints (alcance COMPLETO)

> Plan de entrega del ERP completo (18 módulos, monolito modular reactivo). Sprints de **2 semanas**.
> **La BD ya está modelada y aplicada** (V1–V25, 126 tablas). Estos sprints construyen el **código**
> (vertical slices reactivos) sobre ese esquema. Salud (his) está excluido.

## Definition of Done (DoD) — por cada HU
- [ ] Vertical slice reactivo: entity (R2DBC) · dtos · mapper (MapStruct EN↔ES) · R2dbcRepository ·
      QueryRepository (SQL nativo, alias `"camelCase"`) · service · controller (`Mono<ResponseEntity<ApiResponse>>`).
- [ ] Multitenant (`empresa_id` del JWT) + soft-delete + cross-tenant=404.
- [ ] Swagger (`@Operation`/`@Schema`) · validaciones `jakarta.validation` · manejo de errores `ApiResponse`.
- [ ] Tests (WebTestClient) verdes · revisión rápida agente `ciberseguridad`.

---

## Cronograma de sprints

### Sprint 0 — Plataforma & DevEx  ✅ (en cierre)
Proyecto WebFlux+R2DBC, utils, Flyway (V1–V25 aplicadas), Swagger, docker-compose (Postgres 5433),
puerto app 8081. Pendiente: CI (build+test) y healthcheck.

### Sprint 1 — Seguridad JWT + Administración  ← **ARRANCA AQUÍ**
- `SecurityConfig` (WebFlux), `JwtService`, `JwtAuthenticationWebFilter` → `TenantContext` (Reactor Context).
- `AuthController` login/refresh (**HU-0001**). RBAC por permiso.
- CRUD: empresa, sede, usuario, rol, permiso, vigencia, parámetro; auditoría automática.

### Sprint 2 — Común: Terceros
- Terceros + satélites (contacto, dirección, cuenta bancaria, clasificación) (**HU-0002**).
- CRUD de catálogos globales (tipo_identificacion, geografía, bancos, etc.).

### Sprint 3 — Común: Productos y Organización  ✅ (código entregado)
> Deuda técnica pendiente del DoD: tests WebTestClient y revisión `ciberseguridad`.
- Productos (+ variantes, proveedores, categoría) (**HU-0003**). Impuestos.
- Centro de costo, dependencia, proyecto, recurso (**HU-0004/0005**).
- Motor de documentos: tipo_documento + **consecutivo atómico** (`FOR UPDATE`) (**HU-0006**). Adjuntos.

### Sprint 4 — Contabilidad  ✅ (código entregado)
> Deuda técnica pendiente del DoD: tests WebTestClient y revisión `ciberseguridad`.
- Plan de cuentas, periodo, comprobante + movimiento (append-only), saldos (proyección) (**HU-0007**).
- **Bus de eventos de dominio in-process** (reactivo) para interfaces contables.

### Sprint 5 — Inventario  ✅ (código entregado)
> Deuda técnica pendiente del DoD: tests WebTestClient y revisión `ciberseguridad`.
- Bodega, ubicación, lote, marca; movimiento_inventario (append-only); saldo/kardex (proyección) (**HU-0009**).

### Sprint 6 — Compras  ✅ (código entregado)
> Deuda técnica pendiente del DoD: tests WebTestClient y revisión `ciberseguridad`.
- Orden de compra + líneas; recepción + líneas → entra a inventario + asiento contable (**HU-0008**).

### Sprint 7 — Facturación  ✅ (código entregado)
> Deuda técnica pendiente del DoD: tests WebTestClient y revisión `ciberseguridad`.
- Factura + líneas + resolución DIAN + factura_dian; salida de inventario; evento a Cartera/Contabilidad (**HU-0010**).

### Sprint 8 — Cartera + Caja  ✅ (código entregado)
> Deuda técnica pendiente del DoD: tests WebTestClient y revisión `ciberseguridad`.
- Cuenta por cobrar, acuerdos (**HU-0011**); caja, recibo (pagos + aplicación a CxC), arqueo (**HU-0012**).
- **Interfaz de Cartera**: listener que consume `CuentaPorCobrarSolicitada` (Facturación) y registra la CxC.

### Sprint 9 — Tesorería + Cuentas por Pagar  ✅ (código entregado)
> Deuda técnica pendiente del DoD: tests WebTestClient y revisión `ciberseguridad`. Extractos/conciliación bancaria diferidos.
- Cuentas bancarias, chequera, egresos (girar/anular) (**HU-0013**); extractos/conciliación diferidos.
- Factura proveedor (DIAN/RADIAN) + eventos, obligación de pago + retenciones; pago vía egreso que reduce el saldo de la obligación (**HU-0014**).

### Sprint 10 — Costos + Presupuesto  ✅ (código backend entregado)
> Deuda técnica pendiente del DoD: tests WebTestClient y revisión `ciberseguridad`. Frontend pendiente.
- Costos: `recurso` ya entregado en Sprint 3 (HU-0005), sin trabajo nuevo.
- Presupuesto (**HU-0015**): fuente de financiamiento, CPC, rubro presupuestal (jerárquico); afectación
  (cadena CDP→compromiso→obligación→pago, append-only); saldo (apropiación + ejecución recalculable); PAC.

### Sprint 11 — Activos Fijos + Contratación  ✅ (código backend entregado)
> Deuda técnica pendiente del DoD: tests WebTestClient y revisión `ciberseguridad`. Frontend pendiente.
- Activos Fijos (**HU-0016**): póliza de seguro (maestro), activo fijo, depreciación (append-only que
  recalcula el acumulado/valor en libros), responsables y pólizas como sub-recursos.
- Contratación (**HU-0017**): modalidad y plantilla de cláusula (catálogos), contrato (+ cláusulas que
  se reemplazan en update), cambio de estado y pólizas de contrato (reutiliza `poliza_seguro`).

### Sprint 12 — Talento Humano  ✅ (código backend entregado)
> Deuda técnica pendiente del DoD: tests WebTestClient y revisión `ciberseguridad`. Frontend pendiente.
- Empleado (= tercero) con satélites: estudios, familiares, historia laboral; evaluación de desempeño (**HU-0018**).
  `nivel_estudio` expuesto vía catálogo genérico (`/api/catalogos/nivel-estudio`).

### Sprint 13 — Nómina I  ✅ (código backend entregado)
> Deuda técnica pendiente del DoD: tests WebTestClient y revisión `ciberseguridad`. Frontend pendiente.
- Cargo, grupo de nómina, concepto (fórmula + clase), vinculación del empleado, novedades (incl. embargos, con anular) (**HU-0019**).

### Sprint 14 — Nómina II  ✅ (código backend entregado)
> Deuda técnica pendiente del DoD: tests WebTestClient y revisión `ciberseguridad`. Frontend pendiente.
> Motor de fórmulas (valores desde novedades/sueldo en v1) e interfaz presupuestal por evento, diferidos.
- Liquidación + detalle (append-only), aportes PILA; **liquidar** y **contabilizar** (interfaz contable por evento `AsientoContableSolicitado`) (**HU-0020**).

### Sprint 15 — Académico + Jurídico  ✅ (código backend entregado)
> Deuda técnica pendiente del DoD: tests WebTestClient y revisión `ciberseguridad`. Frontend pendiente.
- Programas, asignaturas (+ asociación a programa), grupos, carga docente → evento a Nómina (`CargaDocenteRegistrada`) (**HU-0021**).
- Procesos disciplinarios con faltas/descargos/notificaciones + cambio de estado; catálogos de clasificación y falta (**HU-0022**).

### Sprint 16 — Cierres, Reportería y Hardening  ✅ (código backend entregado)
> Deuda técnica pendiente del DoD: tests WebTestClient y revisión `ciberseguridad`. Frontend pendiente.
> Cierre de vigencia (anual), edades de cartera por tramos y tuning/pruebas de carga, diferidos.
- Cierre de periodo **orquestado** (valida borradores → recalcula saldos → cierra); estados financieros básicos
  (balance general, estado de resultados); reportes de cartera y ejecución presupuestal (**HU-0023**).

---

## Resumen
| Fase | Sprints | Módulos |
|---|---|---|
| Plataforma | 0 | base |
| Seguridad + Núcleo | 1–3 | Administración, Común |
| Finanzas núcleo | 4 | Contabilidad |
| Operación | 5–7 | Inventario, Compras, Facturación |
| Recaudo/Pago | 8–9 | Cartera, Caja, Tesorería, CxP |
| Público/Activos | 10–11 | Costos, Presupuesto, Activos Fijos, Contratación |
| Capital humano | 12–15 | Talento Humano, Nómina, Académico, Jurídico |
| Cierre | 16 | Cierres, reportería, hardening |

**~16 sprints ≈ 8 meses.** Cada sprint entrega los vertical slices reactivos de su(s) módulo(s)
sobre la BD ya existente, cumpliendo el DoD. Orden guiado por dependencias (núcleo → contabilidad
sumidero → operación → recaudo → capital humano).
