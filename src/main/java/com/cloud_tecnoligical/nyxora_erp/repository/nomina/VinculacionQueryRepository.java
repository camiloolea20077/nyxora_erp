package com.cloud_tecnoligical.nyxora_erp.repository.nomina;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.VinculacionResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.VinculacionTableDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Repository
public class VinculacionQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("codigo", "fecha", "created_at");

    public VinculacionQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<Boolean> existsActivoEnEmpresa(Long id, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM vinculacion WHERE id=:id AND empresa_id=:e AND deleted_at IS NULL")
            .bind("id", id).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<VinculacionResponseDto> findActiveById(Long id, Long empresaId) {
        return db.sql("""
                SELECT v.id AS "id", v.empleado_id AS "empleadoId", emp.nombre AS "empleadoNombre",
                       v.cargo_id AS "cargoId", car.nombre AS "cargoNombre", v.grupo_nomina_id AS "grupoNominaId",
                       v.codigo AS "codigo", v.fecha AS "fecha", v.fecha_fin AS "fechaFin",
                       v.tipo_vinculacion AS "tipoVinculacion", v.tipo_contrato AS "tipoContrato",
                       v.sueldo AS "sueldo", v.hora_trabajo AS "horaTrabajo", v.periodo_prueba AS "periodoPrueba",
                       v.fecha_fin_periodo_prueba AS "fechaFinPeriodoPrueba", v.frecuencia_pago AS "frecuenciaPago",
                       v.jefe_id AS "jefeId", v.sede_id AS "sedeId", v.dependencia_id AS "dependenciaId",
                       v.municipio_vinculacion_id AS "municipioVinculacionId", v.tipo_cotizante AS "tipoCotizante",
                       v.estado_vinculacion AS "estadoVinculacion", v.objeto AS "objeto", v.temporal AS "temporal",
                       v.activo AS "active", v.created_at AS "createdAt"
                FROM vinculacion v
                JOIN tercero emp ON emp.id = v.empleado_id
                LEFT JOIN cargo car ON car.id = v.cargo_id
                WHERE v.id=:id AND v.empresa_id=:e AND v.deleted_at IS NULL LIMIT 1
                """)
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, VinculacionResponseDto.class));
    }

    @SuppressWarnings("unchecked")
    public Mono<PageResponseDto<VinculacionTableDto>> list(PageableDto<?> request, Long empresaId, Long empleadoId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String search = request.getSearch() != null ? request.getSearch().trim() : null;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "fecha";
        String order = "ASC".equalsIgnoreCase(request.getOrder()) ? "ASC" : "DESC";

        StringBuilder sql = new StringBuilder("""
                SELECT v.id AS "id", v.codigo AS "codigo", v.empleado_id AS "empleadoId",
                       emp.nombre AS "empleadoNombre", car.nombre AS "cargoNombre", v.sueldo AS "sueldo",
                       v.fecha AS "fecha", v.estado_vinculacion AS "estadoVinculacion", v.activo AS "active",
                       COUNT(*) OVER() AS total_rows
                FROM vinculacion v
                JOIN tercero emp ON emp.id = v.empleado_id
                LEFT JOIN cargo car ON car.id = v.cargo_id
                WHERE v.empresa_id=:e AND v.deleted_at IS NULL
                """);
        if (empleadoId != null) {
            sql.append(" AND v.empleado_id = :empleadoId ");
        }
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (LOWER(emp.nombre) LIKE LOWER(:search) OR LOWER(v.codigo) LIKE LOWER(:search)) ");
        }
        sql.append(" ORDER BY v.").append(orderBy).append(" ").append(order).append(" NULLS LAST");
        sql.append(" OFFSET :offset LIMIT :limit");

        var spec = db.sql(sql.toString()).bind("e", empresaId);
        if (empleadoId != null) {
            spec = spec.bind("empleadoId", empleadoId);
        }
        if (search != null && !search.isEmpty()) {
            spec = spec.bind("search", "%" + search + "%");
        }
        spec = spec.bind("offset", page * size).bind("limit", size);

        return spec.fetch().all().collectList().map(rows -> {
            long total = rows.isEmpty() ? 0 : ((Number) rows.get(0).get("total_rows")).longValue();
            List<VinculacionTableDto> content = rows.stream()
                .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, VinculacionTableDto.class))
                .toList();
            return new PageResponseDto<>(content, page, size, total);
        });
    }
}
