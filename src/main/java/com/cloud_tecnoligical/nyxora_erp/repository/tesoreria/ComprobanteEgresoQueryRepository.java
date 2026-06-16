package com.cloud_tecnoligical.nyxora_erp.repository.tesoreria;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.ComprobanteEgresoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.ComprobanteEgresoTableDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Repository
public class ComprobanteEgresoQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("fecha", "numero", "estado", "created_at");

    public ComprobanteEgresoQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<Boolean> terceroExisteEnEmpresa(Long terceroId, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM tercero WHERE id=:id AND empresa_id=:e AND deleted_at IS NULL")
            .bind("id", terceroId).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<Boolean> cuentaBancariaExisteEnEmpresa(Long id, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM cuenta_bancaria WHERE id=:id AND empresa_id=:e AND deleted_at IS NULL")
            .bind("id", id).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<ComprobanteEgresoResponseDto> findActiveById(Long id, Long empresaId) {
        return db.sql("""
                SELECT e.id AS "id", e.cuenta_bancaria_id AS "cuentaBancariaId", e.beneficiario_id AS "beneficiarioId",
                       e.tipo_documento_id AS "tipoDocumentoId", e.forma_pago_id AS "formaPagoId", e.numero AS "numero",
                       e.fecha AS "fecha", e.valor AS "valor", e.estado AS "estado", e.numero_cheque AS "numeroCheque",
                       e.descripcion AS "descripcion", e.origen_modulo AS "origenModulo", e.origen_id AS "origenId",
                       e.activo AS "active", e.created_at AS "createdAt"
                FROM comprobante_egreso e WHERE e.id=:id AND e.empresa_id=:em AND e.deleted_at IS NULL LIMIT 1
                """)
            .bind("id", id).bind("em", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, ComprobanteEgresoResponseDto.class));
    }

    @SuppressWarnings("unchecked")
    public Mono<PageResponseDto<ComprobanteEgresoTableDto>> list(PageableDto<?> request, Long empresaId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String search = request.getSearch() != null ? request.getSearch().trim() : null;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "fecha";
        String order = "ASC".equalsIgnoreCase(request.getOrder()) ? "ASC" : "DESC";

        StringBuilder sql = new StringBuilder("""
                SELECT e.id AS "id", e.cuenta_bancaria_id AS "cuentaBancariaId", e.beneficiario_id AS "beneficiarioId",
                       e.numero AS "numero", e.fecha AS "fecha", e.valor AS "valor", e.estado AS "estado",
                       e.activo AS "active", COUNT(*) OVER() AS total_rows
                FROM comprobante_egreso e WHERE e.empresa_id=:em AND e.deleted_at IS NULL
                """);
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (e.numero LIKE :search OR LOWER(e.descripcion) LIKE LOWER(:search)) ");
        }
        sql.append(" ORDER BY e.").append(orderBy).append(" ").append(order);
        sql.append(" OFFSET :offset LIMIT :limit");

        var spec = db.sql(sql.toString()).bind("em", empresaId);
        if (search != null && !search.isEmpty()) {
            spec = spec.bind("search", "%" + search + "%");
        }
        spec = spec.bind("offset", page * size).bind("limit", size);

        return spec.fetch().all().collectList().map(rows -> {
            long total = rows.isEmpty() ? 0 : ((Number) rows.get(0).get("total_rows")).longValue();
            List<ComprobanteEgresoTableDto> content = rows.stream()
                .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, ComprobanteEgresoTableDto.class))
                .toList();
            return new PageResponseDto<>(content, page, size, total);
        });
    }
}
