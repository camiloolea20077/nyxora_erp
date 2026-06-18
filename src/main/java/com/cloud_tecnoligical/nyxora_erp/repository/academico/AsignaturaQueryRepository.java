package com.cloud_tecnoligical.nyxora_erp.repository.academico;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.academico.AsignaturaProgramaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.AsignaturaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.AsignaturaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Repository
public class AsignaturaQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("codigo", "nombre", "created_at");

    public AsignaturaQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<Boolean> existsByCodigo(String codigo, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM asignatura WHERE codigo=:c AND empresa_id=:e AND deleted_at IS NULL")
            .bind("c", codigo).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<Boolean> existsByCodigoExcludingId(String codigo, Long id, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM asignatura WHERE codigo=:c AND empresa_id=:e AND id<>:id AND deleted_at IS NULL")
            .bind("c", codigo).bind("e", empresaId).bind("id", id)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<AsignaturaResponseDto> findActiveById(Long id, Long empresaId) {
        return db.sql("""
                SELECT a.id AS "id", a.codigo AS "codigo", a.nombre AS "nombre", a.descripcion AS "descripcion",
                       a.centro_costo_departamento_id AS "centroCostoDepartamentoId",
                       a.activo AS "active", a.created_at AS "createdAt"
                FROM asignatura a WHERE a.id=:id AND a.empresa_id=:e AND a.deleted_at IS NULL LIMIT 1
                """)
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, AsignaturaResponseDto.class));
    }

    @SuppressWarnings("unchecked")
    public Mono<PageResponseDto<AsignaturaTableDto>> list(PageableDto<?> request, Long empresaId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String search = request.getSearch() != null ? request.getSearch().trim() : null;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "codigo";
        String order = "DESC".equalsIgnoreCase(request.getOrder()) ? "DESC" : "ASC";

        StringBuilder sql = new StringBuilder("""
                SELECT a.id AS "id", a.codigo AS "codigo", a.nombre AS "nombre", a.activo AS "active",
                       COUNT(*) OVER() AS total_rows
                FROM asignatura a WHERE a.empresa_id=:e AND a.deleted_at IS NULL
                """);
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (LOWER(a.nombre) LIKE LOWER(:search) OR LOWER(a.codigo) LIKE LOWER(:search)) ");
        }
        sql.append(" ORDER BY a.").append(orderBy).append(" ").append(order);
        sql.append(" OFFSET :offset LIMIT :limit");

        var spec = db.sql(sql.toString()).bind("e", empresaId);
        if (search != null && !search.isEmpty()) {
            spec = spec.bind("search", "%" + search + "%");
        }
        spec = spec.bind("offset", page * size).bind("limit", size);

        return spec.fetch().all().collectList().map(rows -> {
            long total = rows.isEmpty() ? 0 : ((Number) rows.get(0).get("total_rows")).longValue();
            List<AsignaturaTableDto> content = rows.stream()
                .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, AsignaturaTableDto.class))
                .toList();
            return new PageResponseDto<>(content, page, size, total);
        });
    }

    // ---------- Satélite: programas de la asignatura ----------
    public Mono<List<AsignaturaProgramaResponseDto>> listProgramas(Long asignaturaId) {
        return db.sql("""
                SELECT ap.id AS "id", ap.asignatura_id AS "asignaturaId", ap.programa_academico_id AS "programaAcademicoId",
                       p.nombre AS "programaNombre", ap.semestre AS "semestre", ap.creditos AS "creditos",
                       ap.activo AS "active"
                FROM asignatura_programa ap
                JOIN programa_academico p ON p.id = ap.programa_academico_id
                WHERE ap.asignatura_id=:a AND ap.deleted_at IS NULL
                ORDER BY ap.semestre NULLS LAST, ap.id
                """)
            .bind("a", asignaturaId).fetch().all()
            .map(r -> MapperRepository.mapResultSetToObject(r, AsignaturaProgramaResponseDto.class))
            .collectList();
    }

    public Mono<Boolean> existeEnlaceVigente(Long asignaturaId, Long programaId) {
        return db.sql("SELECT count(*) AS c FROM asignatura_programa WHERE asignatura_id=:a AND programa_academico_id=:p AND deleted_at IS NULL")
            .bind("a", asignaturaId).bind("p", programaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }
}
