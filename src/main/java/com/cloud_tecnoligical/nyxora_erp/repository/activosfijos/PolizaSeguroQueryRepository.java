package com.cloud_tecnoligical.nyxora_erp.repository.activosfijos;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.PolizaSeguroResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.PolizaSeguroTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Repository
public class PolizaSeguroQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("numero", "tipo", "fecha_fin", "created_at");

    public PolizaSeguroQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<Boolean> existsByNumero(String numero, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM poliza_seguro WHERE numero=:n AND empresa_id=:e AND deleted_at IS NULL")
            .bind("n", numero).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<Boolean> existsByNumeroExcludingId(String numero, Long id, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM poliza_seguro WHERE numero=:n AND empresa_id=:e AND id<>:id AND deleted_at IS NULL")
            .bind("n", numero).bind("e", empresaId).bind("id", id)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<PolizaSeguroResponseDto> findActiveById(Long id, Long empresaId) {
        return db.sql("""
                SELECT p.id AS "id", p.numero AS "numero", p.aseguradora_id AS "aseguradoraId",
                       COALESCE(a.razon_social, a.primer_nombre) AS "aseguradoraNombre", p.tipo AS "tipo",
                       p.fecha_inicio AS "fechaInicio", p.fecha_fin AS "fechaFin",
                       p.valor_asegurado AS "valorAsegurado", p.activo AS "active", p.created_at AS "createdAt"
                FROM poliza_seguro p
                LEFT JOIN tercero a ON a.id = p.aseguradora_id
                WHERE p.id=:id AND p.empresa_id=:e AND p.deleted_at IS NULL LIMIT 1
                """)
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, PolizaSeguroResponseDto.class));
    }

    @SuppressWarnings("unchecked")
    public Mono<PageResponseDto<PolizaSeguroTableDto>> list(PageableDto<?> request, Long empresaId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String search = request.getSearch() != null ? request.getSearch().trim() : null;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "numero";
        String order = "DESC".equalsIgnoreCase(request.getOrder()) ? "DESC" : "ASC";

        StringBuilder sql = new StringBuilder("""
                SELECT p.id AS "id", p.numero AS "numero", p.tipo AS "tipo", p.valor_asegurado AS "valorAsegurado",
                       p.fecha_fin AS "fechaFin", p.activo AS "active", COUNT(*) OVER() AS total_rows
                FROM poliza_seguro p WHERE p.empresa_id=:e AND p.deleted_at IS NULL
                """);
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (LOWER(p.numero) LIKE LOWER(:search) OR LOWER(p.tipo) LIKE LOWER(:search)) ");
        }
        sql.append(" ORDER BY p.").append(orderBy).append(" ").append(order);
        sql.append(" OFFSET :offset LIMIT :limit");

        var spec = db.sql(sql.toString()).bind("e", empresaId);
        if (search != null && !search.isEmpty()) {
            spec = spec.bind("search", "%" + search + "%");
        }
        spec = spec.bind("offset", page * size).bind("limit", size);

        return spec.fetch().all().collectList().map(rows -> {
            long total = rows.isEmpty() ? 0 : ((Number) rows.get(0).get("total_rows")).longValue();
            List<PolizaSeguroTableDto> content = rows.stream()
                .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, PolizaSeguroTableDto.class))
                .toList();
            return new PageResponseDto<>(content, page, size, total);
        });
    }
}
