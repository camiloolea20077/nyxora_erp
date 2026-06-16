package com.cloud_tecnoligical.nyxora_erp.repository.cuentaspagar;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar.FacturaProveedorEventoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar.FacturaProveedorResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar.FacturaProveedorTableDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Repository
public class FacturaProveedorQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("fecha_recepcion", "numero_documento", "estado", "created_at");

    public FacturaProveedorQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<Boolean> terceroExisteEnEmpresa(Long terceroId, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM tercero WHERE id=:id AND empresa_id=:e AND deleted_at IS NULL")
            .bind("id", terceroId).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<FacturaProveedorResponseDto> findActiveById(Long id, Long empresaId) {
        return db.sql("""
                SELECT f.id AS "id", f.proveedor_id AS "proveedorId", f.receptor_id AS "receptorId",
                       f.numero_documento AS "numeroDocumento", f.cufe AS "cufe", f.fecha_recepcion AS "fechaRecepcion",
                       f.valor_factura AS "valorFactura", f.email_remitente AS "emailRemitente", f.pdf_url AS "pdfUrl",
                       f.estado AS "estado", f.activo AS "active", f.created_at AS "createdAt"
                FROM factura_proveedor f WHERE f.id=:id AND f.empresa_id=:e AND f.deleted_at IS NULL LIMIT 1
                """)
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, FacturaProveedorResponseDto.class));
    }

    public Mono<List<FacturaProveedorEventoResponseDto>> listEventos(Long facturaProveedorId) {
        return db.sql("""
                SELECT id AS "id", factura_proveedor_id AS "facturaProveedorId", evento AS "evento",
                       fecha_evento AS "fechaEvento", cude_evento AS "cudeEvento", concepto_reclamo AS "conceptoReclamo",
                       descripcion_reclamo AS "descripcionReclamo", estado AS "estado", error_evento AS "errorEvento"
                FROM factura_proveedor_evento WHERE factura_proveedor_id=:f AND deleted_at IS NULL ORDER BY id
                """)
            .bind("f", facturaProveedorId).fetch().all()
            .map(r -> MapperRepository.mapResultSetToObject(r, FacturaProveedorEventoResponseDto.class))
            .collectList();
    }

    @SuppressWarnings("unchecked")
    public Mono<PageResponseDto<FacturaProveedorTableDto>> list(PageableDto<?> request, Long empresaId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String search = request.getSearch() != null ? request.getSearch().trim() : null;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "fecha_recepcion";
        String order = "ASC".equalsIgnoreCase(request.getOrder()) ? "ASC" : "DESC";

        StringBuilder sql = new StringBuilder("""
                SELECT f.id AS "id", f.proveedor_id AS "proveedorId", f.numero_documento AS "numeroDocumento",
                       f.fecha_recepcion AS "fechaRecepcion", f.valor_factura AS "valorFactura", f.estado AS "estado",
                       f.activo AS "active", COUNT(*) OVER() AS total_rows
                FROM factura_proveedor f WHERE f.empresa_id=:e AND f.deleted_at IS NULL
                """);
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (LOWER(f.numero_documento) LIKE LOWER(:search) OR LOWER(f.cufe) LIKE LOWER(:search)) ");
        }
        sql.append(" ORDER BY f.").append(orderBy).append(" ").append(order);
        sql.append(" OFFSET :offset LIMIT :limit");

        var spec = db.sql(sql.toString()).bind("e", empresaId);
        if (search != null && !search.isEmpty()) {
            spec = spec.bind("search", "%" + search + "%");
        }
        spec = spec.bind("offset", page * size).bind("limit", size);

        return spec.fetch().all().collectList().map(rows -> {
            long total = rows.isEmpty() ? 0 : ((Number) rows.get(0).get("total_rows")).longValue();
            List<FacturaProveedorTableDto> content = rows.stream()
                .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, FacturaProveedorTableDto.class))
                .toList();
            return new PageResponseDto<>(content, page, size, total);
        });
    }
}
