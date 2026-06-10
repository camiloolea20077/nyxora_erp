package com.cloud_tecnoligical.nyxora_erp.repository.inventario;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.BodegaResponsableResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.BodegaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.BodegaTableDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Repository
public class BodegaQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("codigo", "nombre", "created_at");

    public BodegaQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<Boolean> existsByCodigo(String codigo, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM bodega WHERE codigo=:c AND empresa_id=:e AND deleted_at IS NULL")
            .bind("c", codigo).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<Boolean> existsByCodigoExcludingId(String codigo, Long id, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM bodega WHERE codigo=:c AND empresa_id=:e AND id<>:id AND deleted_at IS NULL")
            .bind("c", codigo).bind("e", empresaId).bind("id", id)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    /** La bodega existe, es de la empresa y no está eliminada (para validar satélites/movimientos). */
    public Mono<Boolean> existsActivaEnEmpresa(Long id, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM bodega WHERE id=:id AND empresa_id=:e AND deleted_at IS NULL")
            .bind("id", id).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<BodegaResponseDto> findActiveById(Long id, Long empresaId) {
        return db.sql("""
                SELECT b.id AS "id", b.sede_id AS "sedeId", b.centro_costo_id AS "centroCostoId",
                       b.codigo AS "codigo", b.nombre AS "nombre", b.tipo_abastecimiento AS "tipoAbastecimiento",
                       b.direccion AS "direccion", b.latitud AS "latitud", b.longitud AS "longitud",
                       b.permite_compra AS "permiteCompra", b.activo AS "active", b.created_at AS "createdAt"
                FROM bodega b WHERE b.id=:id AND b.empresa_id=:e AND b.deleted_at IS NULL LIMIT 1
                """)
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, BodegaResponseDto.class));
    }

    @SuppressWarnings("unchecked")
    public Mono<PageResponseDto<BodegaTableDto>> list(PageableDto<?> request, Long empresaId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String search = request.getSearch() != null ? request.getSearch().trim() : null;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "codigo";
        String order = "DESC".equalsIgnoreCase(request.getOrder()) ? "DESC" : "ASC";

        StringBuilder sql = new StringBuilder("""
                SELECT b.id AS "id", b.codigo AS "codigo", b.nombre AS "nombre",
                       b.permite_compra AS "permiteCompra", b.activo AS "active", COUNT(*) OVER() AS total_rows
                FROM bodega b WHERE b.empresa_id=:e AND b.deleted_at IS NULL
                """);
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (LOWER(b.nombre) LIKE LOWER(:search) OR LOWER(b.codigo) LIKE LOWER(:search)) ");
        }
        sql.append(" ORDER BY b.").append(orderBy).append(" ").append(order);
        sql.append(" OFFSET :offset LIMIT :limit");

        var spec = db.sql(sql.toString()).bind("e", empresaId);
        if (search != null && !search.isEmpty()) {
            spec = spec.bind("search", "%" + search + "%");
        }
        spec = spec.bind("offset", page * size).bind("limit", size);

        return spec.fetch().all().collectList().map(rows -> {
            long total = rows.isEmpty() ? 0 : ((Number) rows.get(0).get("total_rows")).longValue();
            List<BodegaTableDto> content = rows.stream()
                .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, BodegaTableDto.class))
                .toList();
            return new PageResponseDto<>(content, page, size, total);
        });
    }

    // ---------- responsables ----------
    public Mono<List<BodegaResponsableResponseDto>> listResponsables(Long bodegaId) {
        return db.sql("""
                SELECT id AS "id", bodega_id AS "bodegaId", tercero_id AS "terceroId",
                       predeterminado AS "predeterminado", activo AS "active"
                FROM bodega_responsable WHERE bodega_id=:b AND deleted_at IS NULL
                ORDER BY predeterminado DESC, id
                """)
            .bind("b", bodegaId).fetch().all()
            .map(r -> MapperRepository.mapResultSetToObject(r, BodegaResponsableResponseDto.class))
            .collectList();
    }

    public Mono<Long> unsetPredeterminado(Long bodegaId) {
        return db.sql("UPDATE bodega_responsable SET predeterminado=false WHERE bodega_id=:b AND deleted_at IS NULL")
            .bind("b", bodegaId).fetch().rowsUpdated();
    }

    /** El tercero existe, es de la empresa y no está eliminado. */
    public Mono<Boolean> terceroExisteEnEmpresa(Long terceroId, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM tercero WHERE id=:id AND empresa_id=:e AND deleted_at IS NULL")
            .bind("id", terceroId).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }
}
