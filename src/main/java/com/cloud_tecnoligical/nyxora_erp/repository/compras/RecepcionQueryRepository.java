package com.cloud_tecnoligical.nyxora_erp.repository.compras;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.compras.RecepcionLineaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.compras.RecepcionResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.compras.RecepcionTableDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Repository
public class RecepcionQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("fecha", "numero", "estado", "created_at");

    public RecepcionQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<RecepcionResponseDto> findActiveById(Long id, Long empresaId) {
        return db.sql("""
                SELECT r.id AS "id", r.orden_compra_id AS "ordenCompraId", r.bodega_id AS "bodegaId",
                       r.tipo_documento_id AS "tipoDocumentoId", r.numero AS "numero", r.fecha AS "fecha",
                       r.estado AS "estado", r.observaciones AS "observaciones",
                       r.activo AS "active", r.created_at AS "createdAt"
                FROM recepcion r WHERE r.id=:id AND r.empresa_id=:e AND r.deleted_at IS NULL LIMIT 1
                """)
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, RecepcionResponseDto.class));
    }

    public Mono<List<RecepcionLineaResponseDto>> listLineas(Long recepcionId) {
        return db.sql("""
                SELECT id AS "id", recepcion_id AS "recepcionId", orden_compra_linea_id AS "ordenCompraLineaId",
                       producto_id AS "productoId", producto_variante_id AS "productoVarianteId",
                       lote_id AS "loteId", ubicacion_id AS "ubicacionId",
                       cantidad_recibida AS "cantidadRecibida", costo_unitario AS "costoUnitario"
                FROM recepcion_linea WHERE recepcion_id=:r AND deleted_at IS NULL ORDER BY id
                """)
            .bind("r", recepcionId).fetch().all()
            .map(row -> MapperRepository.mapResultSetToObject(row, RecepcionLineaResponseDto.class))
            .collectList();
    }

    @SuppressWarnings("unchecked")
    public Mono<PageResponseDto<RecepcionTableDto>> list(PageableDto<?> request, Long empresaId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "fecha";
        String order = "ASC".equalsIgnoreCase(request.getOrder()) ? "ASC" : "DESC";

        String sql = """
                SELECT r.id AS "id", r.orden_compra_id AS "ordenCompraId", r.numero AS "numero",
                       r.fecha AS "fecha", r.estado AS "estado", r.activo AS "active", COUNT(*) OVER() AS total_rows
                FROM recepcion r WHERE r.empresa_id=:e AND r.deleted_at IS NULL
                ORDER BY r.%s %s OFFSET :offset LIMIT :limit
                """.formatted(orderBy, order);

        return db.sql(sql).bind("e", empresaId).bind("offset", page * size).bind("limit", size)
            .fetch().all().collectList().map(rows -> {
                long total = rows.isEmpty() ? 0 : ((Number) rows.get(0).get("total_rows")).longValue();
                List<RecepcionTableDto> content = rows.stream()
                    .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, RecepcionTableDto.class))
                    .toList();
                return new PageResponseDto<>(content, page, size, total);
            });
    }
}
