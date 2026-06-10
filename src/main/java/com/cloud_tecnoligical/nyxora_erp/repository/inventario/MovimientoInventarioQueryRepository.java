package com.cloud_tecnoligical.nyxora_erp.repository.inventario;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.inventario.KardexItemDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.MovimientoInventarioResponseDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;

import reactor.core.publisher.Mono;

@Repository
public class MovimientoInventarioQueryRepository {

    private final DatabaseClient db;

    public MovimientoInventarioQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<MovimientoInventarioResponseDto> findById(Long id, Long empresaId) {
        return db.sql("""
                SELECT m.id AS "id", m.bodega_id AS "bodegaId", m.ubicacion_id AS "ubicacionId",
                       m.producto_id AS "productoId", m.producto_variante_id AS "productoVarianteId",
                       m.lote_id AS "loteId", m.tipo AS "tipo", m.fecha AS "fecha", m.cantidad AS "cantidad",
                       m.costo_unitario AS "costoUnitario", m.descuento_porcentaje AS "descuentoPorcentaje",
                       m.descuento_valor AS "descuentoValor", m.impuesto_id AS "impuestoId",
                       m.impuesto_porcentaje AS "impuestoPorcentaje", m.impuesto_valor AS "impuestoValor",
                       m.subtotal AS "subtotal", m.total AS "total", m.centro_costo_id AS "centroCostoId",
                       m.tercero_id AS "terceroId", m.descripcion AS "descripcion",
                       m.origen_modulo AS "origenModulo", m.origen_id AS "origenId", m.created_at AS "createdAt"
                FROM movimiento_inventario m WHERE m.id=:id AND m.empresa_id=:e LIMIT 1
                """)
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, MovimientoInventarioResponseDto.class));
    }

    /** Kardex: movimientos de un producto (opcional por bodega) con saldo corriente acumulado. */
    public Mono<List<KardexItemDto>> kardex(Long productoId, Long bodegaId, Long empresaId) {
        StringBuilder sql = new StringBuilder("""
                SELECT m.id AS "id", m.bodega_id AS "bodegaId", m.producto_id AS "productoId",
                       m.producto_variante_id AS "productoVarianteId", m.lote_id AS "loteId",
                       m.tipo AS "tipo", m.fecha AS "fecha", m.cantidad AS "cantidad",
                       m.costo_unitario AS "costoUnitario", m.descripcion AS "descripcion"
                FROM movimiento_inventario m WHERE m.empresa_id=:e AND m.producto_id=:p
                """);
        if (bodegaId != null) {
            sql.append(" AND m.bodega_id=:b ");
        }
        sql.append(" ORDER BY m.fecha, m.id");

        var spec = db.sql(sql.toString()).bind("e", empresaId).bind("p", productoId);
        if (bodegaId != null) {
            spec = spec.bind("b", bodegaId);
        }
        return spec.fetch().all()
            .map(r -> MapperRepository.mapResultSetToObject(r, KardexItemDto.class))
            .collectList()
            .map(items -> {
                BigDecimal saldo = BigDecimal.ZERO;
                List<KardexItemDto> out = new ArrayList<>(items.size());
                for (KardexItemDto it : items) {
                    saldo = saldo.add(it.getCantidad() != null ? it.getCantidad() : BigDecimal.ZERO);
                    it.setSaldoCorriente(saldo);
                    out.add(it);
                }
                return out;
            });
    }

    /** El lote existe, es de la empresa y no está eliminado. */
    public Mono<Boolean> loteExisteEnEmpresa(Long loteId, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM lote WHERE id=:id AND empresa_id=:e AND deleted_at IS NULL")
            .bind("id", loteId).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    /** La variante existe y su producto es de la empresa. */
    public Mono<Boolean> varianteExisteEnEmpresa(Long varianteId, Long empresaId) {
        return db.sql("""
                SELECT count(*) AS c FROM producto_variante v
                JOIN producto p ON p.id = v.producto_id
                WHERE v.id=:id AND p.empresa_id=:e AND v.deleted_at IS NULL
                """)
            .bind("id", varianteId).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }
}
