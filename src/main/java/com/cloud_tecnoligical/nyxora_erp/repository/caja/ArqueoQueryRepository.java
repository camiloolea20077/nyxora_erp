package com.cloud_tecnoligical.nyxora_erp.repository.caja;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.caja.ArqueoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.caja.ArqueoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Repository
public class ArqueoQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("fecha", "created_at");

    public ArqueoQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<ArqueoResponseDto> findById(Long id, Long empresaId) {
        return db.sql("""
                SELECT a.id AS "id", a.caja_id AS "cajaId", a.fecha AS "fecha",
                       a.valor_declarado AS "valorDeclarado", a.valor_sistema AS "valorSistema",
                       a.diferencia AS "diferencia", a.observaciones AS "observaciones", a.created_at AS "createdAt"
                FROM arqueo a WHERE a.id=:id AND a.empresa_id=:e LIMIT 1
                """)
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, ArqueoResponseDto.class));
    }

    @SuppressWarnings("unchecked")
    public Mono<PageResponseDto<ArqueoTableDto>> list(PageableDto<?> request, Long empresaId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "fecha";
        String order = "ASC".equalsIgnoreCase(request.getOrder()) ? "ASC" : "DESC";

        String sql = """
                SELECT a.id AS "id", a.caja_id AS "cajaId", a.fecha AS "fecha",
                       a.valor_declarado AS "valorDeclarado", a.valor_sistema AS "valorSistema",
                       a.diferencia AS "diferencia", COUNT(*) OVER() AS total_rows
                FROM arqueo a WHERE a.empresa_id=:e
                ORDER BY a.%s %s OFFSET :offset LIMIT :limit
                """.formatted(orderBy, order);

        return db.sql(sql).bind("e", empresaId).bind("offset", page * size).bind("limit", size)
            .fetch().all().collectList().map(rows -> {
                long total = rows.isEmpty() ? 0 : ((Number) rows.get(0).get("total_rows")).longValue();
                List<ArqueoTableDto> content = rows.stream()
                    .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, ArqueoTableDto.class))
                    .toList();
                return new PageResponseDto<>(content, page, size, total);
            });
    }
}
