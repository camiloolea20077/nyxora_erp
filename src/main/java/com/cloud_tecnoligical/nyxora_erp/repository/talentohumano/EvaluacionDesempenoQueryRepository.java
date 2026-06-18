package com.cloud_tecnoligical.nyxora_erp.repository.talentohumano;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.EvaluacionDesempenoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.EvaluacionDesempenoTableDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Repository
public class EvaluacionDesempenoQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("fecha_inicial", "calificacion", "created_at");

    public EvaluacionDesempenoQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<EvaluacionDesempenoResponseDto> findActiveById(Long id, Long empresaId) {
        return db.sql("""
                SELECT d.id AS "id", d.evaluacion_programa_id AS "evaluacionProgramaId",
                       d.empleado_id AS "empleadoId", d.tipo_evaluacion AS "tipoEvaluacion",
                       d.fecha_inicial AS "fechaInicial", d.fecha_final AS "fechaFinal",
                       d.calificacion AS "calificacion", d.created_at AS "createdAt"
                FROM evaluacion_desempeno d WHERE d.id=:id AND d.empresa_id=:e AND d.deleted_at IS NULL LIMIT 1
                """)
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, EvaluacionDesempenoResponseDto.class));
    }

    @SuppressWarnings("unchecked")
    public Mono<PageResponseDto<EvaluacionDesempenoTableDto>> list(PageableDto<?> request, Long empresaId,
            Long empleadoId, Long programaId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "fecha_inicial";
        String order = "ASC".equalsIgnoreCase(request.getOrder()) ? "ASC" : "DESC";

        StringBuilder sql = new StringBuilder("""
                SELECT d.id AS "id", d.evaluacion_programa_id AS "evaluacionProgramaId",
                       d.empleado_id AS "empleadoId", d.tipo_evaluacion AS "tipoEvaluacion",
                       d.fecha_inicial AS "fechaInicial", d.calificacion AS "calificacion",
                       COUNT(*) OVER() AS total_rows
                FROM evaluacion_desempeno d WHERE d.empresa_id=:e AND d.deleted_at IS NULL
                """);
        if (empleadoId != null) sql.append(" AND d.empleado_id=:emp ");
        if (programaId != null) sql.append(" AND d.evaluacion_programa_id=:prog ");
        sql.append(" ORDER BY d.").append(orderBy).append(" ").append(order).append(" NULLS LAST");
        sql.append(" OFFSET :offset LIMIT :limit");

        var spec = db.sql(sql.toString()).bind("e", empresaId);
        if (empleadoId != null) spec = spec.bind("emp", empleadoId);
        if (programaId != null) spec = spec.bind("prog", programaId);
        spec = spec.bind("offset", page * size).bind("limit", size);

        return spec.fetch().all().collectList().map(rows -> {
            long total = rows.isEmpty() ? 0 : ((Number) rows.get(0).get("total_rows")).longValue();
            List<EvaluacionDesempenoTableDto> content = rows.stream()
                .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, EvaluacionDesempenoTableDto.class))
                .toList();
            return new PageResponseDto<>(content, page, size, total);
        });
    }
}
