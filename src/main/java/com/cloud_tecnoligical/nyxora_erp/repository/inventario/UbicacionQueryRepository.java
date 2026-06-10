package com.cloud_tecnoligical.nyxora_erp.repository.inventario;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.UbicacionResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.UbicacionTableDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Repository
public class UbicacionQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("codigo", "nombre", "nivel", "created_at");

    public UbicacionQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    /** Código único por bodega. */
    public Mono<Boolean> existsByCodigoEnBodega(String codigo, Long bodegaId) {
        return db.sql("SELECT count(*) AS c FROM ubicacion WHERE codigo=:c AND bodega_id=:b AND deleted_at IS NULL")
            .bind("c", codigo).bind("b", bodegaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<Boolean> existsByCodigoEnBodegaExcludingId(String codigo, Long bodegaId, Long id) {
        return db.sql("SELECT count(*) AS c FROM ubicacion WHERE codigo=:c AND bodega_id=:b AND id<>:id AND deleted_at IS NULL")
            .bind("c", codigo).bind("b", bodegaId).bind("id", id)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    /** La ubicación existe, es de la empresa y no está eliminada (para validar padre). */
    public Mono<Boolean> existsActivaEnEmpresa(Long id, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM ubicacion WHERE id=:id AND empresa_id=:e AND deleted_at IS NULL")
            .bind("id", id).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<UbicacionResponseDto> findActiveById(Long id, Long empresaId) {
        return db.sql("""
                SELECT u.id AS "id", u.bodega_id AS "bodegaId", u.ubicacion_padre_id AS "ubicacionPadreId",
                       u.codigo AS "codigo", u.nombre AS "nombre", u.pasillo AS "pasillo", u.altura AS "altura",
                       u.posicion AS "posicion", u.izquierda AS "izquierda", u.derecha AS "derecha", u.nivel AS "nivel",
                       u.activo AS "active", u.created_at AS "createdAt"
                FROM ubicacion u WHERE u.id=:id AND u.empresa_id=:e AND u.deleted_at IS NULL LIMIT 1
                """)
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, UbicacionResponseDto.class));
    }

    @SuppressWarnings("unchecked")
    public Mono<PageResponseDto<UbicacionTableDto>> list(PageableDto<?> request, Long empresaId, Long bodegaId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String search = request.getSearch() != null ? request.getSearch().trim() : null;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "codigo";
        String order = "DESC".equalsIgnoreCase(request.getOrder()) ? "DESC" : "ASC";

        StringBuilder sql = new StringBuilder("""
                SELECT u.id AS "id", u.bodega_id AS "bodegaId", u.ubicacion_padre_id AS "ubicacionPadreId",
                       u.codigo AS "codigo", u.nombre AS "nombre", u.activo AS "active", COUNT(*) OVER() AS total_rows
                FROM ubicacion u WHERE u.empresa_id=:e AND u.deleted_at IS NULL
                """);
        if (bodegaId != null) {
            sql.append(" AND u.bodega_id=:b ");
        }
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (LOWER(u.nombre) LIKE LOWER(:search) OR LOWER(u.codigo) LIKE LOWER(:search)) ");
        }
        sql.append(" ORDER BY u.").append(orderBy).append(" ").append(order);
        sql.append(" OFFSET :offset LIMIT :limit");

        var spec = db.sql(sql.toString()).bind("e", empresaId);
        if (bodegaId != null) {
            spec = spec.bind("b", bodegaId);
        }
        if (search != null && !search.isEmpty()) {
            spec = spec.bind("search", "%" + search + "%");
        }
        spec = spec.bind("offset", page * size).bind("limit", size);

        return spec.fetch().all().collectList().map(rows -> {
            long total = rows.isEmpty() ? 0 : ((Number) rows.get(0).get("total_rows")).longValue();
            List<UbicacionTableDto> content = rows.stream()
                .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, UbicacionTableDto.class))
                .toList();
            return new PageResponseDto<>(content, page, size, total);
        });
    }
}
