package com.cloud_tecnoligical.nyxora_erp.repository.contratacion;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.ClausulaPlantillaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.ClausulaPlantillaTableDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Repository
public class ClausulaPlantillaQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("numero", "nombre", "tipo_clausula", "created_at");

    public ClausulaPlantillaQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<ClausulaPlantillaResponseDto> findActiveById(Long id, Long empresaId) {
        return db.sql("""
                SELECT c.id AS "id", c.tipo_clausula AS "tipoClausula", c.numero AS "numero", c.orden AS "orden",
                       c.nombre AS "nombre", c.texto AS "texto", c.activo AS "active", c.created_at AS "createdAt"
                FROM clausula_plantilla c WHERE c.id=:id AND c.empresa_id=:e AND c.deleted_at IS NULL LIMIT 1
                """)
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, ClausulaPlantillaResponseDto.class));
    }

    @SuppressWarnings("unchecked")
    public Mono<PageResponseDto<ClausulaPlantillaTableDto>> list(PageableDto<?> request, Long empresaId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String search = request.getSearch() != null ? request.getSearch().trim() : null;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "numero";
        String order = "DESC".equalsIgnoreCase(request.getOrder()) ? "DESC" : "ASC";

        StringBuilder sql = new StringBuilder("""
                SELECT c.id AS "id", c.tipo_clausula AS "tipoClausula", c.numero AS "numero", c.nombre AS "nombre",
                       c.activo AS "active", COUNT(*) OVER() AS total_rows
                FROM clausula_plantilla c WHERE c.empresa_id=:e AND c.deleted_at IS NULL
                """);
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (LOWER(c.nombre) LIKE LOWER(:search) OR LOWER(c.numero) LIKE LOWER(:search)) ");
        }
        sql.append(" ORDER BY c.").append(orderBy).append(" ").append(order);
        sql.append(" OFFSET :offset LIMIT :limit");

        var spec = db.sql(sql.toString()).bind("e", empresaId);
        if (search != null && !search.isEmpty()) {
            spec = spec.bind("search", "%" + search + "%");
        }
        spec = spec.bind("offset", page * size).bind("limit", size);

        return spec.fetch().all().collectList().map(rows -> {
            long total = rows.isEmpty() ? 0 : ((Number) rows.get(0).get("total_rows")).longValue();
            List<ClausulaPlantillaTableDto> content = rows.stream()
                .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, ClausulaPlantillaTableDto.class))
                .toList();
            return new PageResponseDto<>(content, page, size, total);
        });
    }
}
