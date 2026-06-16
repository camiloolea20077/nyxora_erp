package com.cloud_tecnoligical.nyxora_erp.repository.cuentaspagar;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar.ObligacionPagoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar.ObligacionPagoRetencionResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar.ObligacionPagoTableDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Repository
public class ObligacionPagoQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("fecha", "fecha_vencimiento", "saldo", "estado", "created_at");

    public ObligacionPagoQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<Boolean> terceroExisteEnEmpresa(Long terceroId, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM tercero WHERE id=:id AND empresa_id=:e AND deleted_at IS NULL")
            .bind("id", terceroId).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<ObligacionPagoResponseDto> findActiveById(Long id, Long empresaId) {
        return db.sql("""
                SELECT o.id AS "id", o.proveedor_id AS "proveedorId", o.factura_proveedor_id AS "facturaProveedorId",
                       o.cuenta_id AS "cuentaId", o.numero AS "numero", o.fecha AS "fecha",
                       o.fecha_vencimiento AS "fechaVencimiento", o.valor_total AS "valorTotal", o.saldo AS "saldo",
                       o.estado AS "estado", o.activo AS "active", o.created_at AS "createdAt"
                FROM obligacion_pago o WHERE o.id=:id AND o.empresa_id=:e AND o.deleted_at IS NULL LIMIT 1
                """)
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, ObligacionPagoResponseDto.class));
    }

    public Mono<List<ObligacionPagoRetencionResponseDto>> listRetenciones(Long obligacionPagoId) {
        return db.sql("""
                SELECT id AS "id", obligacion_pago_id AS "obligacionPagoId", impuesto_id AS "impuestoId",
                       base AS "base", limite AS "limite", valor AS "valor"
                FROM obligacion_pago_retencion WHERE obligacion_pago_id=:o AND deleted_at IS NULL ORDER BY id
                """)
            .bind("o", obligacionPagoId).fetch().all()
            .map(r -> MapperRepository.mapResultSetToObject(r, ObligacionPagoRetencionResponseDto.class))
            .collectList();
    }

    @SuppressWarnings("unchecked")
    public Mono<PageResponseDto<ObligacionPagoTableDto>> list(PageableDto<?> request, Long empresaId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "fecha";
        String order = "ASC".equalsIgnoreCase(request.getOrder()) ? "ASC" : "DESC";

        String sql = """
                SELECT o.id AS "id", o.proveedor_id AS "proveedorId", o.factura_proveedor_id AS "facturaProveedorId",
                       o.numero AS "numero", o.fecha AS "fecha", o.fecha_vencimiento AS "fechaVencimiento",
                       o.valor_total AS "valorTotal", o.saldo AS "saldo", o.estado AS "estado", o.activo AS "active",
                       COUNT(*) OVER() AS total_rows
                FROM obligacion_pago o WHERE o.empresa_id=:e AND o.deleted_at IS NULL
                ORDER BY o.%s %s OFFSET :offset LIMIT :limit
                """.formatted(orderBy, order);

        return db.sql(sql).bind("e", empresaId).bind("offset", page * size).bind("limit", size)
            .fetch().all().collectList().map(rows -> {
                long total = rows.isEmpty() ? 0 : ((Number) rows.get(0).get("total_rows")).longValue();
                List<ObligacionPagoTableDto> content = rows.stream()
                    .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, ObligacionPagoTableDto.class))
                    .toList();
                return new PageResponseDto<>(content, page, size, total);
            });
    }
}
