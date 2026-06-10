package com.cloud_tecnoligical.nyxora_erp.repository.contabilidad;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.PeriodoContableResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.PeriodoContableTableDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Repository
public class PeriodoContableQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("anio", "mes", "estado", "created_at");

    public PeriodoContableQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<Boolean> existsByAnioMes(Integer anio, Integer mes, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM periodo_contable WHERE anio=:a AND mes=:m AND empresa_id=:e")
            .bind("a", anio).bind("m", mes).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    /** Estado del periodo si es de la empresa; vacío si no existe/ajeno. */
    public Mono<String> findEstado(Long id, Long empresaId) {
        return db.sql("SELECT estado FROM periodo_contable WHERE id=:id AND empresa_id=:e")
            .bind("id", id).bind("e", empresaId)
            .map(row -> (String) row.get("estado")).one();
    }

    public Mono<PeriodoContableResponseDto> findById(Long id, Long empresaId) {
        return db.sql("""
                SELECT p.id AS "id", p.vigencia_id AS "vigenciaId", p.anio AS "anio", p.mes AS "mes",
                       p.estado AS "estado", p.fecha_cierre AS "fechaCierre", p.created_at AS "createdAt"
                FROM periodo_contable p WHERE p.id=:id AND p.empresa_id=:e LIMIT 1
                """)
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, PeriodoContableResponseDto.class));
    }

    @SuppressWarnings("unchecked")
    public Mono<PageResponseDto<PeriodoContableTableDto>> list(PageableDto<?> request, Long empresaId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "anio";
        String order = "ASC".equalsIgnoreCase(request.getOrder()) ? "ASC" : "DESC";

        String sql = """
                SELECT p.id AS "id", p.vigencia_id AS "vigenciaId", p.anio AS "anio", p.mes AS "mes",
                       p.estado AS "estado", COUNT(*) OVER() AS total_rows
                FROM periodo_contable p WHERE p.empresa_id=:e
                ORDER BY p.%s %s, p.mes %s OFFSET :offset LIMIT :limit
                """.formatted(orderBy, order, order);

        return db.sql(sql).bind("e", empresaId)
            .bind("offset", page * size).bind("limit", size)
            .fetch().all().collectList().map(rows -> {
                long total = rows.isEmpty() ? 0 : ((Number) rows.get(0).get("total_rows")).longValue();
                List<PeriodoContableTableDto> content = rows.stream()
                    .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, PeriodoContableTableDto.class))
                    .toList();
                return new PageResponseDto<>(content, page, size, total);
            });
    }
}
