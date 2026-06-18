package com.cloud_tecnoligical.nyxora_erp.repository.academico;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.academico.GrupoAcademicoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.GrupoAcademicoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Repository
public class GrupoAcademicoQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("codigo", "nombre", "periodo", "created_at");

    public GrupoAcademicoQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<Boolean> existsActivoEnEmpresa(Long id, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM grupo_academico WHERE id=:id AND empresa_id=:e AND deleted_at IS NULL")
            .bind("id", id).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<GrupoAcademicoResponseDto> findActiveById(Long id, Long empresaId) {
        return db.sql("""
                SELECT g.id AS "id", g.programa_academico_id AS "programaAcademicoId", g.codigo AS "codigo",
                       g.nombre AS "nombre", g.periodo AS "periodo", g.activo AS "active", g.created_at AS "createdAt"
                FROM grupo_academico g WHERE g.id=:id AND g.empresa_id=:e AND g.deleted_at IS NULL LIMIT 1
                """)
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, GrupoAcademicoResponseDto.class));
    }

    @SuppressWarnings("unchecked")
    public Mono<PageResponseDto<GrupoAcademicoTableDto>> list(PageableDto<?> request, Long empresaId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String search = request.getSearch() != null ? request.getSearch().trim() : null;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "nombre";
        String order = "DESC".equalsIgnoreCase(request.getOrder()) ? "DESC" : "ASC";

        StringBuilder sql = new StringBuilder("""
                SELECT g.id AS "id", g.codigo AS "codigo", g.nombre AS "nombre", g.periodo AS "periodo",
                       g.activo AS "active", COUNT(*) OVER() AS total_rows
                FROM grupo_academico g WHERE g.empresa_id=:e AND g.deleted_at IS NULL
                """);
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (LOWER(g.nombre) LIKE LOWER(:search) OR LOWER(g.codigo) LIKE LOWER(:search)) ");
        }
        sql.append(" ORDER BY g.").append(orderBy).append(" ").append(order).append(" NULLS LAST");
        sql.append(" OFFSET :offset LIMIT :limit");

        var spec = db.sql(sql.toString()).bind("e", empresaId);
        if (search != null && !search.isEmpty()) {
            spec = spec.bind("search", "%" + search + "%");
        }
        spec = spec.bind("offset", page * size).bind("limit", size);

        return spec.fetch().all().collectList().map(rows -> {
            long total = rows.isEmpty() ? 0 : ((Number) rows.get(0).get("total_rows")).longValue();
            List<GrupoAcademicoTableDto> content = rows.stream()
                .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, GrupoAcademicoTableDto.class))
                .toList();
            return new PageResponseDto<>(content, page, size, total);
        });
    }
}
