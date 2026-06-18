package com.cloud_tecnoligical.nyxora_erp.repository.nomina;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.AportePilaDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.LiquidacionDetalleDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.LiquidacionNominaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.LiquidacionNominaTableDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class LiquidacionNominaQueryRepository {

    /** Proyección de una vinculación activa para liquidar. */
    public record VinculacionRow(Long vinculacionId, Long empleadoId, BigDecimal sueldo) {}

    /** Proyección de una novedad pendiente de liquidar. */
    public record NovedadRow(Long vinculacionId, Long empleadoId, Long conceptoNominaId,
                             String clase, BigDecimal cantidadValor) {}

    /** Total liquidado por clase de concepto. */
    public record ResumenClase(String clase, BigDecimal total) {}

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("fecha", "anio", "mes", "created_at");

    public LiquidacionNominaQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<LiquidacionNominaResponseDto> findActiveById(Long id, Long empresaId) {
        return db.sql("""
                SELECT l.id AS "id", l.grupo_nomina_id AS "grupoNominaId", l.anio AS "anio", l.mes AS "mes",
                       l.periodo AS "periodo", l.fecha AS "fecha", l.estado AS "estado",
                       l.activo AS "active", l.created_at AS "createdAt"
                FROM liquidacion_nomina l WHERE l.id=:id AND l.empresa_id=:e AND l.deleted_at IS NULL LIMIT 1
                """)
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, LiquidacionNominaResponseDto.class));
    }

    @SuppressWarnings("unchecked")
    public Mono<PageResponseDto<LiquidacionNominaTableDto>> list(PageableDto<?> request, Long empresaId, Long grupoNominaId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "fecha";
        String order = "ASC".equalsIgnoreCase(request.getOrder()) ? "ASC" : "DESC";

        StringBuilder sql = new StringBuilder("""
                SELECT l.id AS "id", l.anio AS "anio", l.mes AS "mes", l.periodo AS "periodo",
                       l.fecha AS "fecha", l.estado AS "estado", l.activo AS "active", COUNT(*) OVER() AS total_rows
                FROM liquidacion_nomina l WHERE l.empresa_id=:e AND l.deleted_at IS NULL
                """);
        if (grupoNominaId != null) {
            sql.append(" AND l.grupo_nomina_id = :grupoNominaId ");
        }
        sql.append(" ORDER BY l.").append(orderBy).append(" ").append(order);
        sql.append(" OFFSET :offset LIMIT :limit");

        var spec = db.sql(sql.toString()).bind("e", empresaId);
        if (grupoNominaId != null) {
            spec = spec.bind("grupoNominaId", grupoNominaId);
        }
        spec = spec.bind("offset", page * size).bind("limit", size);

        return spec.fetch().all().collectList().map(rows -> {
            long total = rows.isEmpty() ? 0 : ((Number) rows.get(0).get("total_rows")).longValue();
            List<LiquidacionNominaTableDto> content = rows.stream()
                .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, LiquidacionNominaTableDto.class))
                .toList();
            return new PageResponseDto<>(content, page, size, total);
        });
    }

    // ==================== Helpers de liquidar ====================

    public Flux<VinculacionRow> findVinculacionesActivas(Long empresaId, Long grupoNominaId) {
        String sql = "SELECT v.id AS id, v.empleado_id AS empleado_id, v.sueldo AS sueldo "
            + "FROM vinculacion v WHERE v.empresa_id=:e AND v.deleted_at IS NULL AND v.activo = TRUE "
            + (grupoNominaId != null ? " AND v.grupo_nomina_id = :g " : "");
        var spec = db.sql(sql).bind("e", empresaId);
        if (grupoNominaId != null) spec = spec.bind("g", grupoNominaId);
        return spec.map(row -> new VinculacionRow(
                ((Number) row.get("id")).longValue(),
                ((Number) row.get("empleado_id")).longValue(),
                (BigDecimal) row.get("sueldo")))
            .all();
    }

    public Flux<NovedadRow> findNovedadesPendientes(Long empresaId, Long grupoNominaId) {
        String sql = """
                SELECT n.vinculacion_id AS vinculacion_id, v.empleado_id AS empleado_id,
                       n.concepto_nomina_id AS concepto_nomina_id, c.clase AS clase, n.cantidad_valor AS cantidad_valor
                FROM novedad_nomina n
                JOIN vinculacion v ON v.id = n.vinculacion_id
                JOIN concepto_nomina c ON c.id = n.concepto_nomina_id
                WHERE n.empresa_id=:e AND n.anulado = FALSE AND n.deleted_at IS NULL AND n.fecha_aplicada IS NULL
                  AND v.deleted_at IS NULL
                """ + (grupoNominaId != null ? " AND v.grupo_nomina_id = :g " : "");
        var spec = db.sql(sql).bind("e", empresaId);
        if (grupoNominaId != null) spec = spec.bind("g", grupoNominaId);
        return spec.map(row -> new NovedadRow(
                ((Number) row.get("vinculacion_id")).longValue(),
                ((Number) row.get("empleado_id")).longValue(),
                ((Number) row.get("concepto_nomina_id")).longValue(),
                (String) row.get("clase"),
                (BigDecimal) row.get("cantidad_valor")))
            .all();
    }

    public Mono<Long> marcarNovedadesAplicadas(Long empresaId, Long grupoNominaId, LocalDate fecha) {
        String sql = """
                UPDATE novedad_nomina SET fecha_aplicada=:f
                WHERE empresa_id=:e AND anulado = FALSE AND deleted_at IS NULL AND fecha_aplicada IS NULL
                  AND vinculacion_id IN (
                    SELECT id FROM vinculacion WHERE empresa_id=:e AND deleted_at IS NULL
                """ + (grupoNominaId != null ? " AND grupo_nomina_id = :g " : "") + ")";
        var spec = db.sql(sql).bind("e", empresaId).bind("f", fecha);
        if (grupoNominaId != null) spec = spec.bind("g", grupoNominaId);
        return spec.fetch().rowsUpdated();
    }

    public Flux<ResumenClase> resumenPorClase(Long liquidacionId, Long empresaId) {
        return db.sql("""
                SELECT c.clase AS clase, COALESCE(SUM(d.valor),0) AS total
                FROM liquidacion_nomina_detalle d
                JOIN concepto_nomina c ON c.id = d.concepto_nomina_id
                WHERE d.liquidacion_nomina_id=:id AND d.empresa_id=:e
                GROUP BY c.clase
                """)
            .bind("id", liquidacionId).bind("e", empresaId)
            .map(row -> new ResumenClase((String) row.get("clase"), (BigDecimal) row.get("total")))
            .all();
    }

    public Mono<List<LiquidacionDetalleDto>> listDetalle(Long liquidacionId, Long empresaId) {
        return db.sql("""
                SELECT d.id AS "id", d.empleado_id AS "empleadoId", emp.nombre AS "empleadoNombre",
                       d.concepto_nomina_id AS "conceptoNominaId", con.nombre AS "conceptoNombre", con.clase AS "clase",
                       d.base AS "base", d.cantidad AS "cantidad", d.valor AS "valor",
                       d.valor_empleado AS "valorEmpleado", d.valor_patrono AS "valorPatrono", d.tipo_aporte AS "tipoAporte"
                FROM liquidacion_nomina_detalle d
                JOIN concepto_nomina con ON con.id = d.concepto_nomina_id
                LEFT JOIN tercero emp ON emp.id = d.empleado_id
                WHERE d.liquidacion_nomina_id=:id AND d.empresa_id=:e
                ORDER BY d.empleado_id, d.id
                """)
            .bind("id", liquidacionId).bind("e", empresaId).fetch().all()
            .map(r -> MapperRepository.mapResultSetToObject(r, LiquidacionDetalleDto.class))
            .collectList();
    }

    public Mono<List<AportePilaDto>> listPila(Long liquidacionId, Long empresaId) {
        return db.sql("""
                SELECT p.id AS "id", p.empleado_id AS "empleadoId", emp.nombre AS "empleadoNombre",
                       p.tipo_aporte AS "tipoAporte", p.ibc AS "ibc",
                       p.valor_empleado AS "valorEmpleado", p.valor_patrono AS "valorPatrono"
                FROM aporte_pila p
                LEFT JOIN tercero emp ON emp.id = p.empleado_id
                WHERE p.liquidacion_nomina_id=:id AND p.empresa_id=:e
                ORDER BY p.empleado_id, p.tipo_aporte
                """)
            .bind("id", liquidacionId).bind("e", empresaId).fetch().all()
            .map(r -> MapperRepository.mapResultSetToObject(r, AportePilaDto.class))
            .collectList();
    }
}
