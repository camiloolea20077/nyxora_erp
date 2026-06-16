package com.cloud_tecnoligical.nyxora_erp.repository.tesoreria;

import java.util.List;
import java.util.Map;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.ChequeraResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.ChequeraTableDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Repository
public class ChequeraQueryRepository {

    private final DatabaseClient db;

    public ChequeraQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<Boolean> cuentaBancariaExisteEnEmpresa(Long cuentaBancariaId, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM cuenta_bancaria WHERE id=:id AND empresa_id=:e AND deleted_at IS NULL")
            .bind("id", cuentaBancariaId).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<ChequeraResponseDto> findActiveById(Long id, Long empresaId) {
        return db.sql("""
                SELECT c.id AS "id", c.cuenta_bancaria_id AS "cuentaBancariaId", c.fecha_expedicion AS "fechaExpedicion",
                       c.numero_inicial AS "numeroInicial", c.numero_final AS "numeroFinal",
                       c.consecutivo_actual AS "consecutivoActual", c.activo AS "active", c.created_at AS "createdAt"
                FROM chequera c WHERE c.id=:id AND c.empresa_id=:e AND c.deleted_at IS NULL LIMIT 1
                """)
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, ChequeraResponseDto.class));
    }

    @SuppressWarnings("unchecked")
    public Mono<PageResponseDto<ChequeraTableDto>> list(PageableDto<?> request, Long empresaId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String order = "DESC".equalsIgnoreCase(request.getOrder()) ? "DESC" : "ASC";

        String sql = """
                SELECT c.id AS "id", c.cuenta_bancaria_id AS "cuentaBancariaId", c.fecha_expedicion AS "fechaExpedicion",
                       c.numero_inicial AS "numeroInicial", c.numero_final AS "numeroFinal",
                       c.consecutivo_actual AS "consecutivoActual", c.activo AS "active", COUNT(*) OVER() AS total_rows
                FROM chequera c WHERE c.empresa_id=:e AND c.deleted_at IS NULL
                ORDER BY c.created_at %s OFFSET :offset LIMIT :limit
                """.formatted(order);

        return db.sql(sql).bind("e", empresaId).bind("offset", page * size).bind("limit", size)
            .fetch().all().collectList().map(rows -> {
                long total = rows.isEmpty() ? 0 : ((Number) rows.get(0).get("total_rows")).longValue();
                List<ChequeraTableDto> content = rows.stream()
                    .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, ChequeraTableDto.class))
                    .toList();
                return new PageResponseDto<>(content, page, size, total);
            });
    }
}
