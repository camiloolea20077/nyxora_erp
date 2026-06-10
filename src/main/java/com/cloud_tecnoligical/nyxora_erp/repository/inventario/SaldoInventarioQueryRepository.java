package com.cloud_tecnoligical.nyxora_erp.repository.inventario;

import java.util.List;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.inventario.SaldoInventarioResponseDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;

import reactor.core.publisher.Mono;

/**
 * Saldos de inventario = PROYECCIÓN recalculable desde movimiento_inventario.
 * El recálculo borra y reconstruye los saldos de la bodega en una transacción.
 */
@Repository
public class SaldoInventarioQueryRepository {

    private final DatabaseClient db;

    public SaldoInventarioQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<Long> borrarSaldosBodega(Long bodegaId, Long empresaId) {
        return db.sql("DELETE FROM saldo_inventario WHERE bodega_id=:b AND empresa_id=:e")
            .bind("b", bodegaId).bind("e", empresaId).fetch().rowsUpdated();
    }

    /**
     * Reconstruye existencias por (producto/variante, ubicación, lote) de la bodega.
     * cantidad = SUM(cantidad con signo); costo_promedio = promedio ponderado de entradas (cantidad>0).
     */
    public Mono<Long> reconstruirSaldosBodega(Long bodegaId, Long empresaId) {
        return db.sql("""
                INSERT INTO saldo_inventario (empresa_id, bodega_id, ubicacion_id, lote_id, producto_id,
                    producto_variante_id, cantidad, costo_promedio, valor_total, fecha_recalculo)
                SELECT agg.empresa_id, agg.bodega_id, agg.ubicacion_id, agg.lote_id, agg.producto_id,
                       agg.producto_variante_id, agg.cantidad, agg.costo_promedio,
                       agg.cantidad * agg.costo_promedio, now()
                FROM (
                    SELECT m.empresa_id, m.bodega_id, m.ubicacion_id, m.lote_id, m.producto_id,
                           m.producto_variante_id,
                           SUM(m.cantidad) AS cantidad,
                           CASE WHEN SUM(CASE WHEN m.cantidad > 0 THEN m.cantidad ELSE 0 END) > 0
                                THEN SUM(CASE WHEN m.cantidad > 0 THEN m.cantidad * m.costo_unitario ELSE 0 END)
                                     / SUM(CASE WHEN m.cantidad > 0 THEN m.cantidad ELSE 0 END)
                                ELSE 0 END AS costo_promedio
                    FROM movimiento_inventario m
                    WHERE m.bodega_id = :b AND m.empresa_id = :e
                    GROUP BY m.empresa_id, m.bodega_id, m.ubicacion_id, m.lote_id, m.producto_id, m.producto_variante_id
                ) agg
                """)
            .bind("b", bodegaId).bind("e", empresaId).fetch().rowsUpdated();
    }

    public Mono<List<SaldoInventarioResponseDto>> listByBodega(Long bodegaId, Long productoId, Long empresaId) {
        StringBuilder sql = new StringBuilder("""
                SELECT s.id AS "id", s.bodega_id AS "bodegaId", s.ubicacion_id AS "ubicacionId",
                       s.lote_id AS "loteId", s.producto_id AS "productoId",
                       s.producto_variante_id AS "productoVarianteId", s.cantidad AS "cantidad",
                       s.costo_promedio AS "costoPromedio", s.valor_total AS "valorTotal"
                FROM saldo_inventario s WHERE s.bodega_id=:b AND s.empresa_id=:e
                """);
        if (productoId != null) {
            sql.append(" AND s.producto_id=:p ");
        }
        sql.append(" ORDER BY s.producto_id");

        var spec = db.sql(sql.toString()).bind("b", bodegaId).bind("e", empresaId);
        if (productoId != null) {
            spec = spec.bind("p", productoId);
        }
        return spec.fetch().all()
            .map(r -> MapperRepository.mapResultSetToObject(r, SaldoInventarioResponseDto.class))
            .collectList();
    }
}
