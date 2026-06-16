package com.cloud_tecnoligical.nyxora_erp.repository.facturacion;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.ResolucionDianResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.ResolucionDianTableDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Repository
public class ResolucionDianQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("numero_resolucion", "fecha_final", "created_at");

    public ResolucionDianQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<ResolucionDianResponseDto> findActiveById(Long id, Long empresaId) {
        return db.sql("""
                SELECT r.id AS "id", r.numero_resolucion AS "numeroResolucion", r.prefijo AS "prefijo",
                       r.factura_inicial AS "facturaInicial", r.factura_final AS "facturaFinal",
                       r.fecha_inicial AS "fechaInicial", r.fecha_final AS "fechaFinal",
                       r.clave_tecnica AS "claveTecnica", r.descripcion AS "descripcion",
                       r.consecutivo_actual AS "consecutivoActual", r.activo AS "active", r.created_at AS "createdAt"
                FROM resolucion_dian r WHERE r.id=:id AND r.empresa_id=:e AND r.deleted_at IS NULL LIMIT 1
                """)
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, ResolucionDianResponseDto.class));
    }

    public Mono<Boolean> existsByNumero(String numero, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM resolucion_dian WHERE numero_resolucion=:n AND empresa_id=:e AND deleted_at IS NULL")
            .bind("n", numero).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<Boolean> existsByNumeroExcludingId(String numero, Long id, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM resolucion_dian WHERE numero_resolucion=:n AND empresa_id=:e AND id<>:id AND deleted_at IS NULL")
            .bind("n", numero).bind("e", empresaId).bind("id", id)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    /**
     * Bloquea la fila (FOR UPDATE), incrementa consecutivo_actual y devuelve el nuevo valor.
     * DEBE ejecutarse dentro de una transacción para que el bloqueo tenga efecto.
     */
    public Mono<Long> incrementarConsecutivo(Long resolucionId, Long empresaId) {
        return db.sql("""
                SELECT consecutivo_actual FROM resolucion_dian
                WHERE id=:id AND empresa_id=:e AND deleted_at IS NULL
                FOR UPDATE
                """)
            .bind("id", resolucionId).bind("e", empresaId)
            .map(row -> ((Number) row.get("consecutivo_actual")).longValue())
            .one()
            .flatMap(actual -> {
                long nuevo = actual + 1;
                return db.sql("UPDATE resolucion_dian SET consecutivo_actual=:n WHERE id=:id AND empresa_id=:e")
                    .bind("n", nuevo).bind("id", resolucionId).bind("e", empresaId)
                    .fetch().rowsUpdated()
                    .thenReturn(nuevo);
            });
    }

    @SuppressWarnings("unchecked")
    public Mono<PageResponseDto<ResolucionDianTableDto>> list(PageableDto<?> request, Long empresaId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String search = request.getSearch() != null ? request.getSearch().trim() : null;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "numero_resolucion";
        String order = "DESC".equalsIgnoreCase(request.getOrder()) ? "DESC" : "ASC";

        StringBuilder sql = new StringBuilder("""
                SELECT r.id AS "id", r.numero_resolucion AS "numeroResolucion", r.prefijo AS "prefijo",
                       r.factura_inicial AS "facturaInicial", r.factura_final AS "facturaFinal",
                       r.fecha_final AS "fechaFinal", r.consecutivo_actual AS "consecutivoActual",
                       r.activo AS "active", COUNT(*) OVER() AS total_rows
                FROM resolucion_dian r WHERE r.empresa_id=:e AND r.deleted_at IS NULL
                """);
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (LOWER(r.numero_resolucion) LIKE LOWER(:search) OR LOWER(r.prefijo) LIKE LOWER(:search)) ");
        }
        sql.append(" ORDER BY r.").append(orderBy).append(" ").append(order);
        sql.append(" OFFSET :offset LIMIT :limit");

        var spec = db.sql(sql.toString()).bind("e", empresaId);
        if (search != null && !search.isEmpty()) {
            spec = spec.bind("search", "%" + search + "%");
        }
        spec = spec.bind("offset", page * size).bind("limit", size);

        return spec.fetch().all().collectList().map(rows -> {
            long total = rows.isEmpty() ? 0 : ((Number) rows.get(0).get("total_rows")).longValue();
            List<ResolucionDianTableDto> content = rows.stream()
                .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, ResolucionDianTableDto.class))
                .toList();
            return new PageResponseDto<>(content, page, size, total);
        });
    }
}
