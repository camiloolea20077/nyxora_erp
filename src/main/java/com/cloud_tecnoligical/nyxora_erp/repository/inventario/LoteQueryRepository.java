package com.cloud_tecnoligical.nyxora_erp.repository.inventario;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.LoteResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.LoteTableDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Repository
public class LoteQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("codigo", "nombre", "fecha_vencimiento", "created_at");

    public LoteQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<Boolean> existsByCodigo(String codigo, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM lote WHERE codigo=:c AND empresa_id=:e AND deleted_at IS NULL")
            .bind("c", codigo).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<Boolean> existsByCodigoExcludingId(String codigo, Long id, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM lote WHERE codigo=:c AND empresa_id=:e AND id<>:id AND deleted_at IS NULL")
            .bind("c", codigo).bind("e", empresaId).bind("id", id)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<LoteResponseDto> findActiveById(Long id, Long empresaId) {
        return db.sql("""
                SELECT l.id AS "id", l.producto_variante_id AS "productoVarianteId", l.codigo AS "codigo",
                       l.nombre AS "nombre", l.fecha_fabricado AS "fechaFabricado",
                       l.fecha_vencimiento AS "fechaVencimiento", l.activo AS "active", l.created_at AS "createdAt"
                FROM lote l WHERE l.id=:id AND l.empresa_id=:e AND l.deleted_at IS NULL LIMIT 1
                """)
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, LoteResponseDto.class));
    }

    @SuppressWarnings("unchecked")
    public Mono<PageResponseDto<LoteTableDto>> list(PageableDto<?> request, Long empresaId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String search = request.getSearch() != null ? request.getSearch().trim() : null;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "codigo";
        String order = "DESC".equalsIgnoreCase(request.getOrder()) ? "DESC" : "ASC";

        StringBuilder sql = new StringBuilder("""
                SELECT l.id AS "id", l.codigo AS "codigo", l.nombre AS "nombre",
                       l.fecha_vencimiento AS "fechaVencimiento", l.activo AS "active", COUNT(*) OVER() AS total_rows
                FROM lote l WHERE l.empresa_id=:e AND l.deleted_at IS NULL
                """);
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (LOWER(l.nombre) LIKE LOWER(:search) OR LOWER(l.codigo) LIKE LOWER(:search)) ");
        }
        sql.append(" ORDER BY l.").append(orderBy).append(" ").append(order);
        sql.append(" OFFSET :offset LIMIT :limit");

        var spec = db.sql(sql.toString()).bind("e", empresaId);
        if (search != null && !search.isEmpty()) {
            spec = spec.bind("search", "%" + search + "%");
        }
        spec = spec.bind("offset", page * size).bind("limit", size);

        return spec.fetch().all().collectList().map(rows -> {
            long total = rows.isEmpty() ? 0 : ((Number) rows.get(0).get("total_rows")).longValue();
            List<LoteTableDto> content = rows.stream()
                .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, LoteTableDto.class))
                .toList();
            return new PageResponseDto<>(content, page, size, total);
        });
    }
}
