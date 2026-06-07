---
description: Crea una nueva Historia de Usuario en docs/hu siguiendo la plantilla del proyecto
---

Crea una nueva Historia de Usuario para: **$ARGUMENTS**

Pasos:
1. Lee `docs/hu/PLANTILLA-HU.md` y respeta su estructura.
2. Asigna el siguiente código correlativo `HU-XXXX` (revisa los existentes en `docs/hu/`).
3. Identifica el módulo del MVP afectado (Administración, Común, Compras, Inventario, Facturación,
   Caja, Cartera, Contabilidad) usando `docs/analisis-erp-referencia/09`.
4. Consulta al agente **base-datos** los campos/tablas reales implicados
   (`.claude/data/diccionario-datos.md`).
5. Completa: objetivo, actor, criterios de aceptación (Gherkin), reglas de negocio, entidades/tablas,
   endpoints propuestos, eventos, dependencias y preguntas abiertas.
6. Guarda el archivo como `docs/hu/HU-XXXX-<slug>.md`.

No implementes código todavía: esta tarea solo produce la HU documentada.
