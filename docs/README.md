# Documentación — Nyxora ERP

ERP monolito modular reactivo (Spring WebFlux + R2DBC + PostgreSQL).

## Índice

| Carpeta | Contenido |
|---|---|
| [`analisis-erp-referencia/`](analisis-erp-referencia/00-README-resumen-ejecutivo.md) | Análisis del ERP de referencia y diseño del MVP (fases 00–12). |
| [`arquitectura/`](arquitectura/vision-general.md) | Visión de arquitectura y ADRs. |
| [`base-datos/`](base-datos/) | Diseño y diccionario de datos extendido. |
| [`hu/`](hu/PLANTILLA-HU.md) | Historias de Usuario (plantilla + proyectadas). |
| [`api/`](api/swagger.md) | Documentación de la API (Swagger/OpenAPI). |

## Cómo empezar
1. Lee la arquitectura: [`arquitectura/vision-general.md`](arquitectura/vision-general.md).
2. Revisa el diseño del MVP: [`analisis-erp-referencia/09-validacion-diseno-mvp.md`](analisis-erp-referencia/09-validacion-diseno-mvp.md).
3. Para implementar, sigue las reglas en `../.claude/rules/` y usa los agentes en `../.claude/agents/`.

## Roadmap de documentación
- [x] Análisis del ERP de referencia (00–08).
- [x] Diseño del MVP, validación SQL y migraciones del núcleo (09–11).
- [x] Vertical slice de referencia (12).
- [ ] HUs proyectadas por módulo del MVP (en `hu/`).
- [ ] ADRs de las decisiones reactivas (en `arquitectura/`).
- [ ] Diccionario de datos extendido por módulo transaccional.
