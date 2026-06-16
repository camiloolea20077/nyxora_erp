package com.cloud_tecnoligical.nyxora_erp.repository.caja;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.caja.ReciboCajaLineaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.caja.ReciboCajaPagoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.caja.ReciboCajaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.caja.ReciboCajaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Repository
public class ReciboCajaQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("fecha", "numero", "estado", "created_at");

    public ReciboCajaQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<ReciboCajaResponseDto> findActiveById(Long id, Long empresaId) {
        return db.sql("""
                SELECT r.id AS "id", r.caja_id AS "cajaId", r.tipo_documento_id AS "tipoDocumentoId",
                       r.numero AS "numero", r.cliente_id AS "clienteId", r.fecha AS "fecha", r.valor AS "valor",
                       r.estado AS "estado", r.observaciones AS "observaciones",
                       r.activo AS "active", r.created_at AS "createdAt"
                FROM recibo_caja r WHERE r.id=:id AND r.empresa_id=:e AND r.deleted_at IS NULL LIMIT 1
                """)
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, ReciboCajaResponseDto.class));
    }

    public Mono<List<ReciboCajaPagoResponseDto>> listPagos(Long reciboCajaId) {
        return db.sql("""
                SELECT id AS "id", recibo_caja_id AS "reciboCajaId", forma_pago_id AS "formaPagoId",
                       valor AS "valor", banco_id AS "bancoId", numero_cheque AS "numeroCheque",
                       numero_tarjeta AS "numeroTarjeta", cuenta_bancaria AS "cuentaBancaria"
                FROM recibo_caja_pago WHERE recibo_caja_id=:r AND deleted_at IS NULL ORDER BY id
                """)
            .bind("r", reciboCajaId).fetch().all()
            .map(row -> MapperRepository.mapResultSetToObject(row, ReciboCajaPagoResponseDto.class))
            .collectList();
    }

    public Mono<List<ReciboCajaLineaResponseDto>> listLineas(Long reciboCajaId) {
        return db.sql("""
                SELECT id AS "id", recibo_caja_id AS "reciboCajaId", cuenta_por_cobrar_id AS "cuentaPorCobrarId",
                       valor_aplicado AS "valorAplicado"
                FROM recibo_caja_linea WHERE recibo_caja_id=:r AND deleted_at IS NULL ORDER BY id
                """)
            .bind("r", reciboCajaId).fetch().all()
            .map(row -> MapperRepository.mapResultSetToObject(row, ReciboCajaLineaResponseDto.class))
            .collectList();
    }

    /** Total recaudado por una caja (recibos registrados, no anulados). Para el arqueo. */
    public Mono<BigDecimal> totalRecaudadoCaja(Long cajaId, Long empresaId) {
        return db.sql("""
                SELECT COALESCE(SUM(valor), 0) AS total FROM recibo_caja
                WHERE caja_id=:c AND empresa_id=:e AND estado='registrado' AND deleted_at IS NULL
                """)
            .bind("c", cajaId).bind("e", empresaId)
            .map(row -> {
                Object v = row.get("total");
                return v instanceof BigDecimal bd ? bd : new BigDecimal(v.toString());
            }).one();
    }

    @SuppressWarnings("unchecked")
    public Mono<PageResponseDto<ReciboCajaTableDto>> list(PageableDto<?> request, Long empresaId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "fecha";
        String order = "ASC".equalsIgnoreCase(request.getOrder()) ? "ASC" : "DESC";

        String sql = """
                SELECT r.id AS "id", r.caja_id AS "cajaId", r.numero AS "numero", r.cliente_id AS "clienteId",
                       r.fecha AS "fecha", r.valor AS "valor", r.estado AS "estado", r.activo AS "active",
                       COUNT(*) OVER() AS total_rows
                FROM recibo_caja r WHERE r.empresa_id=:e AND r.deleted_at IS NULL
                ORDER BY r.%s %s OFFSET :offset LIMIT :limit
                """.formatted(orderBy, order);

        return db.sql(sql).bind("e", empresaId).bind("offset", page * size).bind("limit", size)
            .fetch().all().collectList().map(rows -> {
                long total = rows.isEmpty() ? 0 : ((Number) rows.get(0).get("total_rows")).longValue();
                List<ReciboCajaTableDto> content = rows.stream()
                    .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, ReciboCajaTableDto.class))
                    .toList();
                return new PageResponseDto<>(content, page, size, total);
            });
    }
}
