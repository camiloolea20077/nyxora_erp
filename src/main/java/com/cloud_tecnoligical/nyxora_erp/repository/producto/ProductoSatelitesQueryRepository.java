package com.cloud_tecnoligical.nyxora_erp.repository.producto;

import java.util.List;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.producto.ProductoProveedorResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.producto.ProductoVarianteResponseDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;

import reactor.core.publisher.Mono;

/** Listados y validaciones de los satélites del producto (variantes, proveedores). */
@Repository
public class ProductoSatelitesQueryRepository {

    private final DatabaseClient db;

    public ProductoSatelitesQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    // ---------- Variantes ----------
    public Mono<List<ProductoVarianteResponseDto>> listVariantes(Long productoId) {
        return db.sql("""
                SELECT id AS "id", producto_id AS "productoId", sku_plu AS "skuPlu",
                       codigo_barra AS "codigoBarra", precio_adicional AS "precioAdicional",
                       costo AS "costo", activo AS "active"
                FROM producto_variante WHERE producto_id=:p AND deleted_at IS NULL ORDER BY id
                """)
            .bind("p", productoId).fetch().all()
            .map(r -> MapperRepository.mapResultSetToObject(r, ProductoVarianteResponseDto.class))
            .collectList();
    }

    // ---------- Proveedores ----------
    public Mono<List<ProductoProveedorResponseDto>> listProveedores(Long productoId) {
        return db.sql("""
                SELECT id AS "id", producto_id AS "productoId", proveedor_id AS "proveedorId",
                       codigo_producto AS "codigoProducto", cantidad_minima AS "cantidadMinima",
                       plazo_entrega AS "plazoEntrega", activo AS "active"
                FROM producto_proveedor WHERE producto_id=:p AND deleted_at IS NULL ORDER BY id
                """)
            .bind("p", productoId).fetch().all()
            .map(r -> MapperRepository.mapResultSetToObject(r, ProductoProveedorResponseDto.class))
            .collectList();
    }

    /** El tercero (proveedor) existe, es de la empresa y no está eliminado. */
    public Mono<Boolean> proveedorExisteEnEmpresa(Long proveedorId, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM tercero WHERE id=:id AND empresa_id=:e AND deleted_at IS NULL")
            .bind("id", proveedorId).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    /** Ya existe ese proveedor para el producto (UNIQUE producto_id, proveedor_id), excluyendo opcionalmente un id. */
    public Mono<Boolean> proveedorYaAsignado(Long productoId, Long proveedorId, Long excludeId) {
        String sql = "SELECT count(*) AS c FROM producto_proveedor WHERE producto_id=:p AND proveedor_id=:pr AND deleted_at IS NULL"
            + (excludeId != null ? " AND id<>:id" : "");
        var spec = db.sql(sql).bind("p", productoId).bind("pr", proveedorId);
        if (excludeId != null) {
            spec = spec.bind("id", excludeId);
        }
        return spec.map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }
}
