package com.cloud_tecnoligical.nyxora_erp.repository.organizacion;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.DependenciaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.DependenciaTableDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Repository
public class DependenciaQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("codigo", "nombre", "nivel", "created_at");

    public DependenciaQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<Boolean> existsByCodigo(String codigo, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM dependencia WHERE codigo=:c AND empresa_id=:e AND deleted_at IS NULL")
            .bind("c", codigo).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<Boolean> existsByCodigoExcludingId(String codigo, Long id, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM dependencia WHERE codigo=:c AND empresa_id=:e AND id<>:id AND deleted_at IS NULL")
            .bind("c", codigo).bind("e", empresaId).bind("id", id)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    /** La dependencia existe, es de la empresa y no está eliminada (para validar padre). */
    public Mono<Boolean> existsActivaEnEmpresa(Long id, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM dependencia WHERE id=:id AND empresa_id=:e AND deleted_at IS NULL")
            .bind("id", id).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<DependenciaResponseDto> findActiveById(Long id, Long empresaId) {
        return db.sql("""
                SELECT d.id AS "id", d.centro_costo_id AS "centroCostoId", d.dependencia_padre_id AS "dependenciaPadreId",
                       d.codigo AS "codigo", d.nombre AS "nombre", d.ubicacion AS "ubicacion",
                       d.latitud AS "latitud", d.longitud AS "longitud",
                       d.izquierda AS "izquierda", d.derecha AS "derecha", d.nivel AS "nivel",
                       d.activo AS "active", d.created_at AS "createdAt"
                FROM dependencia d WHERE d.id=:id AND d.empresa_id=:e AND d.deleted_at IS NULL LIMIT 1
                """)
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, DependenciaResponseDto.class));
    }

    @SuppressWarnings("unchecked")
    public Mono<PageResponseDto<DependenciaTableDto>> list(PageableDto<?> request, Long empresaId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String search = request.getSearch() != null ? request.getSearch().trim() : null;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "codigo";
        String order = "DESC".equalsIgnoreCase(request.getOrder()) ? "DESC" : "ASC";

        StringBuilder sql = new StringBuilder("""
                SELECT d.id AS "id", d.centro_costo_id AS "centroCostoId", d.dependencia_padre_id AS "dependenciaPadreId",
                       d.codigo AS "codigo", d.nombre AS "nombre", d.activo AS "active", COUNT(*) OVER() AS total_rows
                FROM dependencia d WHERE d.empresa_id=:e AND d.deleted_at IS NULL
                """);
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (LOWER(d.nombre) LIKE LOWER(:search) OR LOWER(d.codigo) LIKE LOWER(:search)) ");
        }
        sql.append(" ORDER BY d.").append(orderBy).append(" ").append(order);
        sql.append(" OFFSET :offset LIMIT :limit");

        var spec = db.sql(sql.toString()).bind("e", empresaId);
        if (search != null && !search.isEmpty()) {
            spec = spec.bind("search", "%" + search + "%");
        }
        spec = spec.bind("offset", page * size).bind("limit", size);

        return spec.fetch().all().collectList().map(rows -> {
            long total = rows.isEmpty() ? 0 : ((Number) rows.get(0).get("total_rows")).longValue();
            List<DependenciaTableDto> content = rows.stream()
                .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, DependenciaTableDto.class))
                .toList();
            return new PageResponseDto<>(content, page, size, total);
        });
    }
}
