package com.cloud_tecnoligical.nyxora_erp.repository.academico;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.academico.ProgramaAcademicoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.ProgramaAcademicoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Repository
public class ProgramaAcademicoQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("codigo", "nombre", "created_at");

    public ProgramaAcademicoQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<Boolean> existsActivoEnEmpresa(Long id, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM programa_academico WHERE id=:id AND empresa_id=:e AND deleted_at IS NULL")
            .bind("id", id).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<ProgramaAcademicoResponseDto> findActiveById(Long id, Long empresaId) {
        return db.sql("""
                SELECT p.id AS "id", p.codigo AS "codigo", p.nombre AS "nombre", p.tipo_programa AS "tipoPrograma",
                       p.modalidad AS "modalidad", p.centro_costo_programa_id AS "centroCostoProgramaId",
                       p.centro_costo_facultad_id AS "centroCostoFacultadId", p.registro_academico AS "registroAcademico",
                       p.descripcion AS "descripcion", p.activo AS "active", p.created_at AS "createdAt"
                FROM programa_academico p WHERE p.id=:id AND p.empresa_id=:e AND p.deleted_at IS NULL LIMIT 1
                """)
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, ProgramaAcademicoResponseDto.class));
    }

    @SuppressWarnings("unchecked")
    public Mono<PageResponseDto<ProgramaAcademicoTableDto>> list(PageableDto<?> request, Long empresaId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String search = request.getSearch() != null ? request.getSearch().trim() : null;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "nombre";
        String order = "DESC".equalsIgnoreCase(request.getOrder()) ? "DESC" : "ASC";

        StringBuilder sql = new StringBuilder("""
                SELECT p.id AS "id", p.codigo AS "codigo", p.nombre AS "nombre", p.tipo_programa AS "tipoPrograma",
                       p.modalidad AS "modalidad", p.activo AS "active", COUNT(*) OVER() AS total_rows
                FROM programa_academico p WHERE p.empresa_id=:e AND p.deleted_at IS NULL
                """);
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (LOWER(p.nombre) LIKE LOWER(:search) OR LOWER(p.codigo) LIKE LOWER(:search)) ");
        }
        sql.append(" ORDER BY p.").append(orderBy).append(" ").append(order);
        sql.append(" OFFSET :offset LIMIT :limit");

        var spec = db.sql(sql.toString()).bind("e", empresaId);
        if (search != null && !search.isEmpty()) {
            spec = spec.bind("search", "%" + search + "%");
        }
        spec = spec.bind("offset", page * size).bind("limit", size);

        return spec.fetch().all().collectList().map(rows -> {
            long total = rows.isEmpty() ? 0 : ((Number) rows.get(0).get("total_rows")).longValue();
            List<ProgramaAcademicoTableDto> content = rows.stream()
                .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, ProgramaAcademicoTableDto.class))
                .toList();
            return new PageResponseDto<>(content, page, size, total);
        });
    }
}
