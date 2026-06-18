package com.cloud_tecnoligical.nyxora_erp.repository.nomina;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.NovedadNominaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.NovedadNominaTableDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Repository
public class NovedadNominaQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("fecha_inicial", "created_at");

    public NovedadNominaQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<NovedadNominaResponseDto> findActiveById(Long id, Long empresaId) {
        return db.sql("""
                SELECT n.id AS "id", n.vinculacion_id AS "vinculacionId", n.concepto_nomina_id AS "conceptoNominaId",
                       con.nombre AS "conceptoNombre", n.tercero_id AS "terceroId", n.descripcion AS "descripcion",
                       n.cantidad_valor AS "cantidadValor", n.fecha_inicial AS "fechaInicial",
                       n.fecha_final AS "fechaFinal", n.fecha_aplicada AS "fechaAplicada",
                       n.numero_cuota AS "numeroCuota", n.dias AS "dias", n.tipo_ausentismo AS "tipoAusentismo",
                       n.tipo_embargo AS "tipoEmbargo", n.expediente AS "expediente", n.demandante AS "demandante",
                       n.banco_id AS "bancoId", n.numero_cuenta_bancaria AS "numeroCuentaBancaria",
                       n.estado_novedad AS "estadoNovedad", n.anulado AS "anulado", n.activo AS "active",
                       n.created_at AS "createdAt"
                FROM novedad_nomina n
                JOIN concepto_nomina con ON con.id = n.concepto_nomina_id
                WHERE n.id=:id AND n.empresa_id=:e AND n.deleted_at IS NULL LIMIT 1
                """)
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, NovedadNominaResponseDto.class));
    }

    @SuppressWarnings("unchecked")
    public Mono<PageResponseDto<NovedadNominaTableDto>> list(PageableDto<?> request, Long empresaId, Long vinculacionId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String search = request.getSearch() != null ? request.getSearch().trim() : null;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "created_at";
        String order = "ASC".equalsIgnoreCase(request.getOrder()) ? "ASC" : "DESC";

        StringBuilder sql = new StringBuilder("""
                SELECT n.id AS "id", n.vinculacion_id AS "vinculacionId", emp.nombre AS "empleadoNombre",
                       con.nombre AS "conceptoNombre", n.cantidad_valor AS "cantidadValor",
                       n.estado_novedad AS "estadoNovedad", n.anulado AS "anulado", COUNT(*) OVER() AS total_rows
                FROM novedad_nomina n
                JOIN concepto_nomina con ON con.id = n.concepto_nomina_id
                JOIN vinculacion v ON v.id = n.vinculacion_id
                JOIN tercero emp ON emp.id = v.empleado_id
                WHERE n.empresa_id=:e AND n.deleted_at IS NULL
                """);
        if (vinculacionId != null) {
            sql.append(" AND n.vinculacion_id = :vinculacionId ");
        }
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (LOWER(emp.nombre) LIKE LOWER(:search) OR LOWER(con.nombre) LIKE LOWER(:search)) ");
        }
        sql.append(" ORDER BY n.").append(orderBy).append(" ").append(order);
        sql.append(" OFFSET :offset LIMIT :limit");

        var spec = db.sql(sql.toString()).bind("e", empresaId);
        if (vinculacionId != null) {
            spec = spec.bind("vinculacionId", vinculacionId);
        }
        if (search != null && !search.isEmpty()) {
            spec = spec.bind("search", "%" + search + "%");
        }
        spec = spec.bind("offset", page * size).bind("limit", size);

        return spec.fetch().all().collectList().map(rows -> {
            long total = rows.isEmpty() ? 0 : ((Number) rows.get(0).get("total_rows")).longValue();
            List<NovedadNominaTableDto> content = rows.stream()
                .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, NovedadNominaTableDto.class))
                .toList();
            return new PageResponseDto<>(content, page, size, total);
        });
    }
}
