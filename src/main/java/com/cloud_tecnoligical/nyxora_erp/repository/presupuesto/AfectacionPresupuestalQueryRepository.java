package com.cloud_tecnoligical.nyxora_erp.repository.presupuesto;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.AfectacionPresupuestalResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.AfectacionPresupuestalTableDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Repository
public class AfectacionPresupuestalQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("tipo_operacion", "valor", "created_at");

    public AfectacionPresupuestalQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    /** El rubro existe, es de la empresa, no está eliminado y maneja movimiento (hoja imputable). */
    public Mono<Boolean> rubroManejaMovimiento(Long rubroId, Long empresaId) {
        return db.sql("""
                SELECT count(*) AS c FROM rubro_presupuestal
                WHERE id=:id AND empresa_id=:e AND maneja_movimiento = TRUE AND deleted_at IS NULL
                """)
            .bind("id", rubroId).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<AfectacionPresupuestalResponseDto> findById(Long id, Long empresaId) {
        return db.sql("""
                SELECT a.id AS "id", a.rubro_presupuestal_id AS "rubroPresupuestalId", a.tipo_operacion AS "tipoOperacion",
                       a.tercero_id AS "terceroId", a.centro_costo_id AS "centroCostoId", a.proyecto_id AS "proyectoId",
                       a.fuente_financiamiento_id AS "fuenteFinanciamientoId", a.cpc_id AS "cpcId",
                       a.descripcion AS "descripcion", a.valor AS "valor", a.subtotal AS "subtotal", a.saldo AS "saldo",
                       a.origen_modulo AS "origenModulo", a.origen_id AS "origenId", a.created_at AS "createdAt"
                FROM afectacion_presupuestal a WHERE a.id=:id AND a.empresa_id=:e LIMIT 1
                """)
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, AfectacionPresupuestalResponseDto.class));
    }

    /** Suma de valores por tipo_operacion para un rubro (para reconstruir el saldo). */
    public Mono<Map<String, BigDecimal>> sumarPorTipo(Long rubroId, Long empresaId) {
        return db.sql("""
                SELECT tipo_operacion AS "tipo", COALESCE(SUM(valor), 0) AS "total"
                FROM afectacion_presupuestal WHERE rubro_presupuestal_id=:r AND empresa_id=:e
                GROUP BY tipo_operacion
                """)
            .bind("r", rubroId).bind("e", empresaId)
            .fetch().all()
            .collectList()
            .map(rows -> {
                Map<String, BigDecimal> m = new HashMap<>();
                for (Map<String, Object> row : rows) {
                    Object v = row.get("total");
                    BigDecimal total = v instanceof BigDecimal bd ? bd : new BigDecimal(v.toString());
                    m.put((String) row.get("tipo"), total);
                }
                return m;
            });
    }

    @SuppressWarnings("unchecked")
    public Mono<PageResponseDto<AfectacionPresupuestalTableDto>> listByRubro(PageableDto<?> request, Long rubroId, Long empresaId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "created_at";
        String order = "ASC".equalsIgnoreCase(request.getOrder()) ? "ASC" : "DESC";

        String sql = """
                SELECT a.id AS "id", a.rubro_presupuestal_id AS "rubroPresupuestalId", a.tipo_operacion AS "tipoOperacion",
                       a.valor AS "valor", a.created_at AS "createdAt", COUNT(*) OVER() AS total_rows
                FROM afectacion_presupuestal a WHERE a.empresa_id=:e AND a.rubro_presupuestal_id=:r
                ORDER BY a.%s %s OFFSET :offset LIMIT :limit
                """.formatted(orderBy, order);

        return db.sql(sql).bind("e", empresaId).bind("r", rubroId).bind("offset", page * size).bind("limit", size)
            .fetch().all().collectList().map(rows -> {
                long total = rows.isEmpty() ? 0 : ((Number) rows.get(0).get("total_rows")).longValue();
                List<AfectacionPresupuestalTableDto> content = rows.stream()
                    .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, AfectacionPresupuestalTableDto.class))
                    .toList();
                return new PageResponseDto<>(content, page, size, total);
            });
    }
}
