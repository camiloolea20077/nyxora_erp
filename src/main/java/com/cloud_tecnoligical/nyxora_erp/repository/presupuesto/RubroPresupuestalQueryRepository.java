package com.cloud_tecnoligical.nyxora_erp.repository.presupuesto;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.RubroPresupuestalResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.RubroPresupuestalTableDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Repository
public class RubroPresupuestalQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("codigo_rubro", "tipo_rubro", "created_at");

    public RubroPresupuestalQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<Boolean> existsByCodigo(String codigo, Long vigenciaId, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM rubro_presupuestal WHERE codigo_rubro=:c AND vigencia_id=:v AND empresa_id=:e AND deleted_at IS NULL")
            .bind("c", codigo).bind("v", vigenciaId).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<Boolean> existsByCodigoExcludingId(String codigo, Long vigenciaId, Long id, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM rubro_presupuestal WHERE codigo_rubro=:c AND vigencia_id=:v AND empresa_id=:e AND id<>:id AND deleted_at IS NULL")
            .bind("c", codigo).bind("v", vigenciaId).bind("e", empresaId).bind("id", id)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    /** Nivel del rubro padre (para derivar el nivel del hijo). Devuelve 0 si no hay padre. */
    public Mono<Integer> nivelDePadre(Long padreId, Long empresaId) {
        return db.sql("SELECT COALESCE(nivel, 0) AS n FROM rubro_presupuestal WHERE id=:id AND empresa_id=:e AND deleted_at IS NULL")
            .bind("id", padreId).bind("e", empresaId)
            .map(row -> ((Number) row.get("n")).intValue()).one();
    }

    public Mono<RubroPresupuestalResponseDto> findActiveById(Long id, Long empresaId) {
        return db.sql("""
                SELECT r.id AS "id", r.vigencia_id AS "vigenciaId", r.rubro_padre_id AS "rubroPadreId",
                       r.tipo_rubro AS "tipoRubro", r.codigo_rubro AS "codigoRubro", r.nombre_rubro AS "nombreRubro",
                       r.maneja_movimiento AS "manejaMovimiento", r.homologacion_circular_unica AS "homologacionCircularUnica",
                       r.nivel AS "nivel", r.activo AS "active", r.created_at AS "createdAt"
                FROM rubro_presupuestal r WHERE r.id=:id AND r.empresa_id=:e AND r.deleted_at IS NULL LIMIT 1
                """)
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, RubroPresupuestalResponseDto.class));
    }

    @SuppressWarnings("unchecked")
    public Mono<PageResponseDto<RubroPresupuestalTableDto>> list(PageableDto<?> request, Long empresaId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String search = request.getSearch() != null ? request.getSearch().trim() : null;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "codigo_rubro";
        String order = "DESC".equalsIgnoreCase(request.getOrder()) ? "DESC" : "ASC";

        StringBuilder sql = new StringBuilder("""
                SELECT r.id AS "id", r.vigencia_id AS "vigenciaId", r.rubro_padre_id AS "rubroPadreId",
                       r.tipo_rubro AS "tipoRubro", r.codigo_rubro AS "codigoRubro", r.nombre_rubro AS "nombreRubro",
                       r.maneja_movimiento AS "manejaMovimiento", r.activo AS "active", COUNT(*) OVER() AS total_rows
                FROM rubro_presupuestal r WHERE r.empresa_id=:e AND r.deleted_at IS NULL
                """);
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (LOWER(r.nombre_rubro) LIKE LOWER(:search) OR LOWER(r.codigo_rubro) LIKE LOWER(:search)) ");
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
            List<RubroPresupuestalTableDto> content = rows.stream()
                .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, RubroPresupuestalTableDto.class))
                .toList();
            return new PageResponseDto<>(content, page, size, total);
        });
    }
}
