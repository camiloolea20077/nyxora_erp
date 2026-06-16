package com.cloud_tecnoligical.nyxora_erp.repository.activosfijos;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.DepreciacionResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.DepreciacionTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Repository
public class DepreciacionQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("fecha_aplicacion", "created_at");

    public DepreciacionQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<BigDecimal> sumByActivo(Long activoFijoId, Long empresaId) {
        return db.sql("SELECT COALESCE(SUM(valor_depreciacion),0) AS s FROM depreciacion WHERE activo_fijo_id=:a AND empresa_id=:e")
            .bind("a", activoFijoId).bind("e", empresaId)
            .map(row -> (BigDecimal) row.get("s")).one();
    }

    public Mono<DepreciacionResponseDto> findById(Long id, Long empresaId) {
        return db.sql("""
                SELECT d.id AS "id", d.activo_fijo_id AS "activoFijoId", d.fecha_aplicacion AS "fechaAplicacion",
                       d.valor_depreciacion AS "valorDepreciacion", d.cuota_depreciacion AS "cuotaDepreciacion",
                       d.periodo_amortizacion AS "periodoAmortizacion", d.unidades_producidas AS "unidadesProducidas",
                       d.created_at AS "createdAt"
                FROM depreciacion d WHERE d.id=:id AND d.empresa_id=:e LIMIT 1
                """)
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, DepreciacionResponseDto.class));
    }

    @SuppressWarnings("unchecked")
    public Mono<PageResponseDto<DepreciacionTableDto>> listByActivo(PageableDto<?> request, Long activoFijoId, Long empresaId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "fecha_aplicacion";
        String order = "ASC".equalsIgnoreCase(request.getOrder()) ? "ASC" : "DESC";

        String sql = """
                SELECT d.id AS "id", d.fecha_aplicacion AS "fechaAplicacion", d.valor_depreciacion AS "valorDepreciacion",
                       d.periodo_amortizacion AS "periodoAmortizacion", COUNT(*) OVER() AS total_rows
                FROM depreciacion d WHERE d.activo_fijo_id=:a AND d.empresa_id=:e
                ORDER BY d.""" + orderBy + " " + order + " OFFSET :offset LIMIT :limit";

        return db.sql(sql)
            .bind("a", activoFijoId).bind("e", empresaId)
            .bind("offset", page * size).bind("limit", size)
            .fetch().all().collectList().map(rows -> {
                long total = rows.isEmpty() ? 0 : ((Number) rows.get(0).get("total_rows")).longValue();
                List<DepreciacionTableDto> content = rows.stream()
                    .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, DepreciacionTableDto.class))
                    .toList();
                return new PageResponseDto<>(content, page, size, total);
            });
    }
}
