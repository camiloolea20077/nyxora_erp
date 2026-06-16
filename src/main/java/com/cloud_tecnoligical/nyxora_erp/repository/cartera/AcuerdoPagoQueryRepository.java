package com.cloud_tecnoligical.nyxora_erp.repository.cartera;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.cartera.AcuerdoPagoCuotaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cartera.AcuerdoPagoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cartera.AcuerdoPagoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Repository
public class AcuerdoPagoQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("fecha", "estado", "created_at");

    public AcuerdoPagoQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<AcuerdoPagoResponseDto> findActiveById(Long id, Long empresaId) {
        return db.sql("""
                SELECT a.id AS "id", a.cuenta_por_cobrar_id AS "cuentaPorCobrarId", a.fecha AS "fecha",
                       a.numero_cuotas AS "numeroCuotas", a.estado AS "estado",
                       a.activo AS "active", a.created_at AS "createdAt"
                FROM acuerdo_pago a WHERE a.id=:id AND a.empresa_id=:e AND a.deleted_at IS NULL LIMIT 1
                """)
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, AcuerdoPagoResponseDto.class));
    }

    public Mono<List<AcuerdoPagoCuotaResponseDto>> listCuotas(Long acuerdoPagoId) {
        return db.sql("""
                SELECT id AS "id", acuerdo_pago_id AS "acuerdoPagoId", numero_cuota AS "numeroCuota",
                       valor AS "valor", fecha_aplicacion AS "fechaAplicacion", estado AS "estado"
                FROM acuerdo_pago_cuota WHERE acuerdo_pago_id=:a AND deleted_at IS NULL ORDER BY numero_cuota
                """)
            .bind("a", acuerdoPagoId).fetch().all()
            .map(r -> MapperRepository.mapResultSetToObject(r, AcuerdoPagoCuotaResponseDto.class))
            .collectList();
    }

    @SuppressWarnings("unchecked")
    public Mono<PageResponseDto<AcuerdoPagoTableDto>> list(PageableDto<?> request, Long empresaId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "fecha";
        String order = "ASC".equalsIgnoreCase(request.getOrder()) ? "ASC" : "DESC";

        String sql = """
                SELECT a.id AS "id", a.cuenta_por_cobrar_id AS "cuentaPorCobrarId", a.fecha AS "fecha",
                       a.numero_cuotas AS "numeroCuotas", a.estado AS "estado", a.activo AS "active",
                       COUNT(*) OVER() AS total_rows
                FROM acuerdo_pago a WHERE a.empresa_id=:e AND a.deleted_at IS NULL
                ORDER BY a.%s %s OFFSET :offset LIMIT :limit
                """.formatted(orderBy, order);

        return db.sql(sql).bind("e", empresaId).bind("offset", page * size).bind("limit", size)
            .fetch().all().collectList().map(rows -> {
                long total = rows.isEmpty() ? 0 : ((Number) rows.get(0).get("total_rows")).longValue();
                List<AcuerdoPagoTableDto> content = rows.stream()
                    .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, AcuerdoPagoTableDto.class))
                    .toList();
                return new PageResponseDto<>(content, page, size, total);
            });
    }
}
