package com.cloud_tecnoligical.nyxora_erp.repository.academico;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.academico.CargaAcademicaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.CargaAcademicaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.CargaDetalleDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Repository
public class CargaAcademicaQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("fecha_acto_administrativo", "created_at");

    public CargaAcademicaQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<CargaAcademicaResponseDto> findHeaderById(Long id, Long empresaId) {
        return db.sql("""
                SELECT c.id AS "id", c.vinculacion_id AS "vinculacionId", emp.nombre AS "docenteNombre",
                       c.nivel_estudio_id AS "nivelEstudioId", c.numero_acto_administrativo AS "numeroActoAdministrativo",
                       c.fecha_acto_administrativo AS "fechaActoAdministrativo", c.activo AS "active", c.created_at AS "createdAt"
                FROM carga_academica c
                LEFT JOIN vinculacion v ON v.id = c.vinculacion_id
                LEFT JOIN tercero emp ON emp.id = v.empleado_id
                WHERE c.id=:id AND c.empresa_id=:e AND c.deleted_at IS NULL LIMIT 1
                """)
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, CargaAcademicaResponseDto.class));
    }

    public Mono<List<CargaDetalleDto>> listDetalle(Long cargaId) {
        return db.sql("""
                SELECT d.id AS "id", d.asignatura_programa_id AS "asignaturaProgramaId",
                       d.grupo_academico_id AS "grupoAcademicoId", a.nombre AS "asignaturaNombre",
                       g.nombre AS "grupoNombre", d.horas AS "horas"
                FROM carga_academica_detalle d
                LEFT JOIN asignatura_programa ap ON ap.id = d.asignatura_programa_id
                LEFT JOIN asignatura a ON a.id = ap.asignatura_id
                LEFT JOIN grupo_academico g ON g.id = d.grupo_academico_id
                WHERE d.carga_academica_id=:c AND d.deleted_at IS NULL
                ORDER BY d.id
                """)
            .bind("c", cargaId).fetch().all()
            .map(r -> MapperRepository.mapResultSetToObject(r, CargaDetalleDto.class))
            .collectList();
    }

    public Mono<BigDecimal> sumHoras(Long cargaId) {
        return db.sql("SELECT COALESCE(SUM(horas),0) AS total FROM carga_academica_detalle WHERE carga_academica_id=:c AND deleted_at IS NULL")
            .bind("c", cargaId)
            .map(row -> (BigDecimal) row.get("total")).one();
    }

    public Mono<Long> borrarDetalle(Long cargaId) {
        return db.sql("DELETE FROM carga_academica_detalle WHERE carga_academica_id=:c")
            .bind("c", cargaId).fetch().rowsUpdated();
    }

    @SuppressWarnings("unchecked")
    public Mono<PageResponseDto<CargaAcademicaTableDto>> list(PageableDto<?> request, Long empresaId, Long vinculacionId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "created_at";
        String order = "ASC".equalsIgnoreCase(request.getOrder()) ? "ASC" : "DESC";

        StringBuilder sql = new StringBuilder("""
                SELECT c.id AS "id", c.vinculacion_id AS "vinculacionId", emp.nombre AS "docenteNombre",
                       c.numero_acto_administrativo AS "numeroActoAdministrativo",
                       c.fecha_acto_administrativo AS "fechaActoAdministrativo", c.activo AS "active",
                       COUNT(*) OVER() AS total_rows
                FROM carga_academica c
                LEFT JOIN vinculacion v ON v.id = c.vinculacion_id
                LEFT JOIN tercero emp ON emp.id = v.empleado_id
                WHERE c.empresa_id=:e AND c.deleted_at IS NULL
                """);
        if (vinculacionId != null) {
            sql.append(" AND c.vinculacion_id = :vinculacionId ");
        }
        sql.append(" ORDER BY c.").append(orderBy).append(" ").append(order).append(" NULLS LAST");
        sql.append(" OFFSET :offset LIMIT :limit");

        var spec = db.sql(sql.toString()).bind("e", empresaId);
        if (vinculacionId != null) {
            spec = spec.bind("vinculacionId", vinculacionId);
        }
        spec = spec.bind("offset", page * size).bind("limit", size);

        return spec.fetch().all().collectList().map(rows -> {
            long total = rows.isEmpty() ? 0 : ((Number) rows.get(0).get("total_rows")).longValue();
            List<CargaAcademicaTableDto> content = rows.stream()
                .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, CargaAcademicaTableDto.class))
                .toList();
            return new PageResponseDto<>(content, page, size, total);
        });
    }
}
