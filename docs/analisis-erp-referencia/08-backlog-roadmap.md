# Fase 8 — Backlog inicial, orden de implementación y preguntas pendientes

## 8.1 Resultados finales (consolidado de las fases)

1. **Resumen ejecutivo** → [00](00-README-resumen-ejecutivo.md)
2. **Inventario técnico** → [01](01-inventario-tecnico.md)
3. **Mapa de módulos** → [02](02-clasificacion-modulos.md)
4. **Mapa de dependencias** → [04](04-dependencias.md)
5. **Procesos de negocio** → [03](03-procesos-negocio.md)
6. **Reglas de negocio inferidas** → ver 8.2
7. **Riesgos técnicos** → [05](05-evaluacion-calidad.md)
8. **Recomendaciones** → [05](05-evaluacion-calidad.md) y [06](06-propuesta-nuevo-erp.md)
9. **Modelo conceptual** → [06 §6.4](06-propuesta-nuevo-erp.md)
10. **Modelo lógico** → [06 §6.5](06-propuesta-nuevo-erp.md)
11. **Diagrama ER (Mermaid)** → [06 §6.5](06-propuesta-nuevo-erp.md)
12. **Backlog** → 8.3
13. **Orden de implementación** → 8.4
14. **Preguntas pendientes** → 8.5

## 8.2 Reglas de negocio inferidas (a validar con el cliente)

| # | Regla inferida | Evidencia |
|---|---|---|
| R1 | Toda operación pertenece a una **organización**, **sede** y **vigencia**; requiere vigencia abierta | `empresa_id`/`sede_id`/`vigencia` transversales + apertura/cierre |
| R2 | Cada transacción es un **documento** con tipo, consecutivo, estado y workflow | `com_encabezados_documentos` + consecutivos + workflow |
| R3 | Al aprobarse, el documento **genera interfaz contable y presupuestal** | `*_detalles_contabilidad`/`*_detalles_presupuesto` en cada módulo |
| R4 | Numeración **única por tipo/sede/vigencia** | `com_documentos_consecutivos` |
| R5 | Presupuesto sigue cadena **CDP → compromiso → obligación → pago** con saldos | `pre_saldos_*`, `pre_pac_*` |
| R6 | Contabilidad mantiene **saldos por tercero, centro de costo, recurso y proyecto** | `con_saldos_*` |
| R7 | Facturación de salud usa **manuales tarifarios, copagos/cuotas, capitación y autorizaciones** | `fac_manuales_tarifarios`, `fac_contratos_copagos_cuotas`, `valor_capitado` |
| R8 | Facturación electrónica y CxP se integran con **DIAN/RADIAN** (eventos, errores) | `fac_facturas_electronicas`, `cpp_eventos_facturas_dian`, `prv_correos_radian` |
| R9 | Nómina liquida por **conceptos con bases**, novedades, embargos y genera **PILA** | `nom_conceptos_bases`, `nom_novedades*`, `nom_pila_*` |
| R10 | La **carga docente** alimenta la nómina de catedráticos | `aca_cargas_academicas`, `nom_tarifas_catedras` |
| R11 | Existe **borrado lógico** universal (no se eliminan registros físicamente) | `softDeletes` masivo |
| R12 | Inventario se valoriza y **cierra por periodo**; activos se **deprecian** | `inv_cierres`, `acf_detalles_depreciaciones` |
| R13 | Cierres encadenados: inventario/activos → contabilidad → mes | `*_cierres`, `adm_cierres_mes` |

## 8.3 Backlog inicial (épicas → historias)

### ÉPICA 0 — Plataforma y fundaciones
- [ ] Definir stack, repos y CI/CD; PostgreSQL + event bus.
- [ ] Modelo de **multitenencia** (`organizacion`) + multisede + RLS.
- [ ] Módulo **Identidad y Acceso** (RBAC/ABAC, JWT, auditoría de login).
- [ ] **Auditoría universal** + event store (obligatoria desde el inicio).
- [ ] Convenciones de migraciones con **FK/índices/constraints** desde el día 1.

### ÉPICA 1 — Núcleo de dominio
- [ ] `Organización` (org, sede, dependencia, **vigencia** + estados de apertura/cierre).
- [ ] `Catálogos Maestros`: **tercero + tercero_rol**, producto + variantes, centro de costo, proyecto.
- [ ] `Catálogos tipados` (lista/lista_item) en reemplazo del EAV.
- [ ] **Motor de Documentos**: `tipo_documento`, `documento` base, `documento_linea`,
      extensiones por tipo, **consecutivos transaccionales**, **máquina de estados**, workflow, eventos.

### ÉPICA 2 — Finanzas núcleo
- [ ] `Contabilidad`: plan de cuentas, comprobante, movimiento (append-only), saldos (proyección),
      cierres, EEFF, exógena DIAN.
- [ ] `Presupuesto`: plan, rubro, fuentes, cadena CDP→compromiso→obligación→pago, PAC, cierres.
- [ ] **Bus de interfaces** contable/presupuestal por eventos de documento.
- [ ] `Tesorería`, `Caja`, `Cartera`, `Cuentas por Pagar` (con integración DIAN/RADIAN).

### ÉPICA 3 — Operación
- [ ] `Compras y Contratación` (plan adquisición, estudio previo, cuadro comparativo, contrato, pólizas).
- [ ] `Inventario` (bodegas, lotes, movimientos, kardex, cierres).
- [ ] `Activos Fijos` (alta, depreciación, baja).
- [ ] `Facturación` (FE DIAN, tarifas, manuales tarifarios, recaudo).

### ÉPICA 4 — Capital humano
- [ ] `Nómina` (procesos, conceptos, novedades, liquidación idempotente, PILA).
- [ ] `Talento Humano` (hoja de vida, evaluación, disciplinarios).

### ÉPICA 5 — Verticales opcionales
- [ ] `Académico` (programas, asignaturas, carga docente → evento a Nómina).
- [ ] `Clínico/Salud` (atención, contratos EPS, copagos, autorizaciones, capitación; **datos cifrados**).
- [ ] `Privados/Especializados` (extensiones por cliente sin tocar núcleo).

### ÉPICA 6 — Reportería y cierre
- [ ] Servicio de **cierre por vigencia** orquestado.
- [ ] Estados financieros, exógena, reportes presupuestales y de cartera (sobre proyecciones).

## 8.4 Orden recomendado de implementación

```
1. ÉPICA 0  Plataforma (multitenancy, identidad, auditoría, convenciones)
2. ÉPICA 1  Núcleo de dominio (organización, catálogos, motor de documentos)
3. ÉPICA 2  Contabilidad + Presupuesto + bus de interfaces  ← columna vertebral
4. ÉPICA 2  Tesorería / Caja / Cartera / CxP
5. ÉPICA 3  Compras-Contratación → Inventario → Facturación → Activos fijos
6. ÉPICA 4  Nómina → Talento humano
7. ÉPICA 5  Verticales (Académico, Clínico) según cliente objetivo
8. ÉPICA 6  Reportería y cierres
```

🧠 **Razonamiento del orden:** el resto del ERP depende de Contabilidad y Presupuesto (sumideros
de todas las interfaces); por eso van inmediatamente después del núcleo. Las verticales (salud,
académico) se dejan al final porque son **opcionales por tipo de cliente** (empresa / entidad
pública / universidad) y deben acoplarse al núcleo solo por eventos.

## 8.5 Preguntas pendientes que requieren validación humana

1. **Cliente objetivo prioritario** del nuevo ERP: ¿empresa privada, entidad pública,
   universidad o IPS de salud? Define qué verticales son núcleo vs. opcionales.
2. ¿Se debe mantener **compatibilidad de migración de datos** desde la BD actual? ¿Qué volumen?
3. **Normativa aplicable:** ¿qué versiones DIAN (FE/RADIAN/exógena), PILA, NIIF y catálogo
   presupuestal público se deben soportar y en qué países?
4. **Multitenancy:** ¿una BD por organización o BD compartida con RLS? (depende de escala y aislamiento).
5. ¿El **módulo clínico** maneja historia clínica completa o solo lo facturable? (impacta cifrado y normativa de habeas data/salud).
6. ¿Qué **integraciones externas** existen hoy (Academusoft, bancos, operadores PILA, proveedores FE)?
7. **Reglas exactas de estados** y aprobaciones por tipo de documento (hoy implícitas en código PHP).
8. **Reglas de numeración** (reinicio por vigencia, prefijos por sede, resoluciones).
9. ¿Qué **reportes legales/regulatorios** son obligatorios (insumo para diseñar proyecciones)?
10. Priorización de **deuda funcional**: ¿qué módulos del sistema actual están realmente en uso
    productivo y cuáles abandonados (los módulos vacíos)?

---

### Rastro del análisis
Documentación generada el 2026-06-05 mediante análisis estático del repositorio (rama `develop`),
sin ejecutar DDL/DML sobre la base analizada. Conjunto completo en `docs/analisis-erp-referencia/`.
