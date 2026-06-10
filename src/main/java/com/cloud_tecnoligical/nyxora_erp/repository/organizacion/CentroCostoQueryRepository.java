package com.cloud_tecnoligical.nyxora_erp.repository.organizacion;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.CentroCostoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.CentroCostoTableDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Repository
public class CentroCostoQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("codigo", "nombre", "nivel", "created_at");

    public CentroCostoQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<Boolean> existsByCodigo(String codigo, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM centro_costo WHERE codigo=:c AND empresa_id=:e AND deleted_at IS NULL")
            .bind("c", codigo).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<Boolean> existsByCodigoExcludingId(String codigo, Long id, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM centro_costo WHERE codigo=:c AND empresa_id=:e AND id<>:id AND deleted_at IS NULL")
            .bind("c", codigo).bind("e", empresaId).bind("id", id)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    /** El centro de costo existe, es de la empresa y no está eliminado (para validar padre/FK). */
    public Mono<Boolean> existsActivoEnEmpresa(Long id, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM centro_costo WHERE id=:id AND empresa_id=:e AND deleted_at IS NULL")
            .bind("id", id).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<CentroCostoResponseDto> findActiveById(Long id, Long empresaId) {
        return db.sql("""
                SELECT cc.id AS "id", cc.sede_id AS "sedeId", cc.centro_costo_padre_id AS "centroCostoPadreId",
                       cc.codigo AS "codigo", cc.nombre AS "nombre", cc.tipo_centro_costo AS "tipoCentroCosto",
                       cc.clase_centro_costo AS "claseCentroCosto", cc.es_observacion AS "esObservacion",
                       cc.maneja_plan_financiero AS "manejaPlanFinanciero", cc.tercero_id AS "terceroId",
                       cc.direccion AS "direccion", cc.unidad_negocio_id AS "unidadNegocioId",
                       cc.izquierda AS "izquierda", cc.derecha AS "derecha", cc.nivel AS "nivel",
                       cc.activo AS "active", cc.created_at AS "createdAt"
                FROM centro_costo cc WHERE cc.id=:id AND cc.empresa_id=:e AND cc.deleted_at IS NULL LIMIT 1
                """)
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, CentroCostoResponseDto.class));
    }

    @SuppressWarnings("unchecked")
    public Mono<PageResponseDto<CentroCostoTableDto>> list(PageableDto<?> request, Long empresaId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String search = request.getSearch() != null ? request.getSearch().trim() : null;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "codigo";
        String order = "DESC".equalsIgnoreCase(request.getOrder()) ? "DESC" : "ASC";

        StringBuilder sql = new StringBuilder("""
                SELECT cc.id AS "id", cc.centro_costo_padre_id AS "centroCostoPadreId", cc.codigo AS "codigo",
                       cc.nombre AS "nombre", cc.es_observacion AS "esObservacion", cc.activo AS "active",
                       COUNT(*) OVER() AS total_rows
                FROM centro_costo cc WHERE cc.empresa_id=:e AND cc.deleted_at IS NULL
                """);
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (LOWER(cc.nombre) LIKE LOWER(:search) OR LOWER(cc.codigo) LIKE LOWER(:search)) ");
        }
        sql.append(" ORDER BY cc.").append(orderBy).append(" ").append(order);
        sql.append(" OFFSET :offset LIMIT :limit");

        var spec = db.sql(sql.toString()).bind("e", empresaId);
        if (search != null && !search.isEmpty()) {
            spec = spec.bind("search", "%" + search + "%");
        }
        spec = spec.bind("offset", page * size).bind("limit", size);

        return spec.fetch().all().collectList().map(rows -> {
            long total = rows.isEmpty() ? 0 : ((Number) rows.get(0).get("total_rows")).longValue();
            List<CentroCostoTableDto> content = rows.stream()
                .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, CentroCostoTableDto.class))
                .toList();
            return new PageResponseDto<>(content, page, size, total);
        });
    }
}
