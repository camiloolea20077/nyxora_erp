# Fase 5 — Evaluación de calidad y riesgos

Escala: ⭐ (1) muy deficiente · ⭐⭐⭐ (3) aceptable · ⭐⭐⭐⭐⭐ (5) excelente.

| Criterio | Calif. | Sustento (🔎 observado) |
|---|---|---|
| Normalización | ⭐⭐ | Tabla-Dios `com_encabezados_documentos` (~50 col.); EAV en formularios y `prv_listas_*`; columnas genéricas repetidas |
| Claridad de nombres | ⭐⭐ | Buen uso de prefijos, pero `fecha1/2`, `com_sede2_id`, `com_tercero1_id`, `comment('')` vacíos, typos en migraciones (`foreidns`, `procutos`) |
| Seguridad | ⭐⭐⭐ | Sentinel + JWT + roles; pero estados/permisos como enteros mágicos y EAV de objetos |
| Auditoría | ⭐⭐⭐ | `owen-it` en 150/369 modelos + `audits_login` + logs de proceso; cobertura parcial |
| Rendimiento | ⭐⭐ | Tabla-Dios ancha y muy escrita; pocos índices explícitos; saldos materializados ayudan pero se desincronizan |
| Escalabilidad | ⭐⭐⭐ | Multiempresa/multisede nativos; pero todo confluye en pocas tablas centrales (hotspots) |
| Mantenibilidad | ⭐⭐ | 1.647 migraciones, módulo `Custom` parche-global, fronteras violadas, Laravel 5.7 (EOL) |
| Separación por módulos | ⭐⭐ | Nominalmente modular, pero tablas creadas en módulos ajenos y duplicadas |
| Integridad referencial | ⭐ | Solo ~20% de migraciones declaran FK; mayoría por convención; FKs retroactivas |
| Manejo de estados | ⭐⭐ | `tinyInteger estado` sin enum/máquina formal; significado disperso en código |
| Manejo de vigencias | ⭐⭐⭐⭐ | `com_vigencias` + apertura/cierre bien establecido y transversal |
| Multiempresa | ⭐⭐⭐⭐ | `empresa_id` en 1.222 puntos; `prv_empresas` |
| Multisede | ⭐⭐⭐⭐ | `sede_id` en 750 puntos; `com_sedes(_modulos)` |
| Borrado lógico | ⭐⭐⭐⭐ | `softDeletes` en 1.572 migraciones |

## 5.1 Problemas detectados (catálogo según la checklist solicitada)

### Duplicidad de información
- 🔎 `ctr_contratos` se crea en **Inventario** y en **Contratación**.
- 🔎 `fac_tarifas`, `com_subcentros`, `com_sedes_users` recreadas/alteradas desde **Custom**.
- 🔎 Saldos materializados (`con_saldos_*`, `pre_saldos_*`, `inv_saldos_productos`) duplican
  información derivable de los movimientos → riesgo de descuadre.
- 🔎 Interfaces dobles `*_detalles_contabilidad` **y** `*_detalles_contables` en varios módulos.

### Tablas demasiado grandes
- 🔴 `com_encabezados_documentos`: ~50 columnas heterogéneas (presupuesto + inventario +
  contratos + tesorería + salud + AIU). Es el principal antipatrón del esquema.

### Falta de normalización
- EAV: `prv_listas_tipos/_elementos`, `prv_maestros_conceptos`, formularios dinámicos
  (`com_*_formularios_dinamicos`, `com_preguntas/respuestas*`).
- Campos genéricos multipropósito (`fecha1`, `fecha2`, `documento_externo` JSON).

### Dependencias circulares
- Documento → referencias → documento; documento → workflow → documento; interfaces que
  re-referencian el documento origen. Dificulta reversa/borrado atómico.

### Ausencia de llaves foráneas
- 🔴 ~80% de migraciones sin FK. Integridad delegada a la aplicación. Riesgo de huérfanos.

### Nombres poco claros
- `fecha1/2`, `com_sede2_id`, `com_tercero1/2_id`, `cgrs`, `supervencion_intervencion`,
  `comment('')` vacíos, typos en nombres de archivo de migración.

### Mezcla de módulos
- Conceptos de **salud** dentro del núcleo `com_` (`com_tipos_actividades_salud`,
  `com_supervencion_intervencion`, `cir_*`).
- Pólizas (`com_*_polizas_seguros`) creadas desde **Compras**.
- Nómina creada desde **Académico**.

### Falta de auditoría
- 219/369 modelos **sin** auditoría owen-it. Sin trazabilidad uniforme de cambios.

### Riesgos de seguridad
- Laravel **5.7** y PHP **7.1** (ambos **fin de vida**, sin parches de seguridad).
- Datos sensibles (salud — historia/diagnósticos `his_diagnosticos`, huellas
  `com_terceros_huellas`) sin evidencia de cifrado a nivel de columna.
- Permisos/objetos por id genérico (EAV) dificultan auditar quién accede a qué.

### Problemas de rendimiento
- Hotspots de escritura en tablas centrales; saldos materializados que requieren recálculo;
  pocos índices explícitos; `decimal(20,5)` y `json` en tabla muy consultada.

## 5.2 Fortalezas a preservar (conceptos válidos del negocio)

1. **Vigencias** como dimensión fiscal transversal.
2. **Multiempresa + multisede** nativos.
3. **Borrado lógico** generalizado.
4. **Patrón de interfaces** hacia Contabilidad y Presupuesto (idea correcta, implementación a mejorar).
5. **Maestro único de terceros** (clientes/proveedores/empleados/pacientes unificados).
6. **Numeración de documentos** por tipo/sede/vigencia.
7. **Saldos por múltiples ejes** (tercero, centro de costo, recurso, proyecto) — concepto útil para reportería.
