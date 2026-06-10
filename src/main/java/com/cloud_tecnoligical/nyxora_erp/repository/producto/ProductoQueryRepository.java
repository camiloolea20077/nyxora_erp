package com.cloud_tecnoligical.nyxora_erp.repository.producto;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.producto.ProductoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.producto.ProductoTableDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class ProductoQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("codigo", "nombre", "tipo", "created_at");

    public ProductoQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    /** El producto existe, es de la empresa y no está eliminado (para validar satélites). */
    public Mono<Boolean> existsActivoEnEmpresa(Long productoId, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM producto WHERE id=:id AND empresa_id=:e AND deleted_at IS NULL")
            .bind("id", productoId).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<Boolean> existsByCodigo(String codigo, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM producto WHERE codigo=:c AND empresa_id=:e AND deleted_at IS NULL")
            .bind("c", codigo).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<Boolean> existsByCodigoExcludingId(String codigo, Long id, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM producto WHERE codigo=:c AND empresa_id=:e AND id<>:id AND deleted_at IS NULL")
            .bind("c", codigo).bind("e", empresaId).bind("id", id)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    /** La categoría existe, es de la empresa y no está eliminada. */
    public Mono<Boolean> categoriaExisteEnEmpresa(Long categoriaId, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM categoria WHERE id=:id AND empresa_id=:e AND deleted_at IS NULL")
            .bind("id", categoriaId).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<ProductoResponseDto> findActiveById(Long id, Long empresaId) {
        return db.sql("""
                SELECT p.id AS "id", p.categoria_id AS "categoriaId", p.codigo AS "codigo",
                       p.codigo_unspsc AS "codigoUnspsc", p.nombre AS "nombre", p.descripcion AS "descripcion",
                       p.tipo AS "tipo", p.es_compuesto AS "esCompuesto",
                       p.unidad_mayor_id AS "unidadMayorId", p.unidad_menor_id AS "unidadMenorId",
                       p.contenido AS "contenido", p.maneja_inventario AS "manejaInventario",
                       p.maneja_lote AS "manejaLote", p.maneja_desperdicio AS "manejaDesperdicio",
                       p.es_devolutivo AS "esDevolutivo", p.stock_minimo AS "stockMinimo",
                       p.stock_maximo AS "stockMaximo", p.tiempo_reabastecimiento AS "tiempoReabastecimiento",
                       p.impuesto_id AS "impuestoId", p.discrimina_iva AS "discriminaIva",
                       p.aplica_impuesto_bolsa AS "aplicaImpuestoBolsa", p.tarifa_maxima AS "tarifaMaxima",
                       p.es_pos AS "esPos", p.recurso_id AS "recursoId",
                       p.activo AS "active", p.created_at AS "createdAt"
                FROM producto p WHERE p.id=:id AND p.empresa_id=:e AND p.deleted_at IS NULL LIMIT 1
                """)
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, ProductoResponseDto.class));
    }

    @SuppressWarnings("unchecked")
    public Mono<PageResponseDto<ProductoTableDto>> list(PageableDto<?> request, Long empresaId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String search = request.getSearch() != null ? request.getSearch().trim() : null;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "codigo";
        String order = "DESC".equalsIgnoreCase(request.getOrder()) ? "DESC" : "ASC";

        StringBuilder sql = new StringBuilder("""
                SELECT p.id AS "id", p.codigo AS "codigo", p.nombre AS "nombre", p.tipo AS "tipo",
                       p.categoria_id AS "categoriaId", p.activo AS "active", COUNT(*) OVER() AS total_rows
                FROM producto p WHERE p.empresa_id=:e AND p.deleted_at IS NULL
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
            List<ProductoTableDto> content = rows.stream()
                .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, ProductoTableDto.class))
                .toList();
            return new PageResponseDto<>(content, page, size, total);
        });
    }

    // ---------- producto_impuesto (M2M) ----------
    public Mono<List<Long>> findImpuestoIds(Long productoId) {
        return db.sql("SELECT impuesto_id FROM producto_impuesto WHERE producto_id=:p")
            .bind("p", productoId)
            .map(row -> ((Number) row.get("impuesto_id")).longValue())
            .all().collectList();
    }

    /** Reemplaza los impuestos adicionales del producto. */
    public Mono<Void> setImpuestos(Long productoId, List<Long> impuestoIds) {
        Mono<Long> del = db.sql("DELETE FROM producto_impuesto WHERE producto_id=:p").bind("p", productoId).fetch().rowsUpdated();
        if (impuestoIds == null || impuestoIds.isEmpty()) {
            return del.then();
        }
        return del.thenMany(Flux.fromIterable(impuestoIds)
                .flatMap(impId -> db.sql("""
                        INSERT INTO producto_impuesto (producto_id, impuesto_id) VALUES (:p,:i)
                        ON CONFLICT (producto_id, impuesto_id) DO NOTHING
                        """)
                    .bind("p", productoId).bind("i", impId).fetch().rowsUpdated()))
            .then();
    }
}
