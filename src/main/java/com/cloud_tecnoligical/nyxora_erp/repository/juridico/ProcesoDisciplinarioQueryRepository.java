package com.cloud_tecnoligical.nyxora_erp.repository.juridico;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.ProcesoDescargoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.ProcesoDisciplinarioResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.ProcesoDisciplinarioTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.ProcesoFaltaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.ProcesoNotificacionResponseDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Repository
public class ProcesoDisciplinarioQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("fecha", "created_at");

    public ProcesoDisciplinarioQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<ProcesoDisciplinarioResponseDto> findHeaderById(Long id, Long empresaId) {
        return db.sql("""
                SELECT p.id AS "id", p.fecha AS "fecha", p.vinculacion_id AS "vinculacionId",
                       inv.nombre AS "investigadoNombre", p.responsable_id AS "responsableId",
                       p.descripcion AS "descripcion", p.estado AS "estado", p.activo AS "active", p.created_at AS "createdAt"
                FROM proceso_disciplinario p
                LEFT JOIN vinculacion v ON v.id = p.vinculacion_id
                LEFT JOIN tercero inv ON inv.id = v.empleado_id
                WHERE p.id=:id AND p.empresa_id=:e AND p.deleted_at IS NULL LIMIT 1
                """)
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, ProcesoDisciplinarioResponseDto.class));
    }

    @SuppressWarnings("unchecked")
    public Mono<PageResponseDto<ProcesoDisciplinarioTableDto>> list(PageableDto<?> request, Long empresaId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String search = request.getSearch() != null ? request.getSearch().trim() : null;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "fecha";
        String order = "ASC".equalsIgnoreCase(request.getOrder()) ? "ASC" : "DESC";

        StringBuilder sql = new StringBuilder("""
                SELECT p.id AS "id", p.fecha AS "fecha", inv.nombre AS "investigadoNombre",
                       p.estado AS "estado", p.activo AS "active", COUNT(*) OVER() AS total_rows
                FROM proceso_disciplinario p
                LEFT JOIN vinculacion v ON v.id = p.vinculacion_id
                LEFT JOIN tercero inv ON inv.id = v.empleado_id
                WHERE p.empresa_id=:e AND p.deleted_at IS NULL
                """);
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (LOWER(inv.nombre) LIKE LOWER(:search) OR LOWER(p.descripcion) LIKE LOWER(:search)) ");
        }
        sql.append(" ORDER BY p.").append(orderBy).append(" ").append(order).append(" NULLS LAST");
        sql.append(" OFFSET :offset LIMIT :limit");

        var spec = db.sql(sql.toString()).bind("e", empresaId);
        if (search != null && !search.isEmpty()) {
            spec = spec.bind("search", "%" + search + "%");
        }
        spec = spec.bind("offset", page * size).bind("limit", size);

        return spec.fetch().all().collectList().map(rows -> {
            long total = rows.isEmpty() ? 0 : ((Number) rows.get(0).get("total_rows")).longValue();
            List<ProcesoDisciplinarioTableDto> content = rows.stream()
                .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, ProcesoDisciplinarioTableDto.class))
                .toList();
            return new PageResponseDto<>(content, page, size, total);
        });
    }

    // ---------- Satélites ----------
    public Mono<List<ProcesoFaltaResponseDto>> listFaltas(Long procesoId) {
        return db.sql("""
                SELECT pf.id AS "id", pf.falta_id AS "faltaId", f.codigo AS "faltaCodigo", f.nombre AS "faltaNombre"
                FROM proceso_falta pf
                JOIN falta f ON f.id = pf.falta_id
                WHERE pf.proceso_disciplinario_id=:p AND pf.deleted_at IS NULL
                ORDER BY pf.id
                """)
            .bind("p", procesoId).fetch().all()
            .map(r -> MapperRepository.mapResultSetToObject(r, ProcesoFaltaResponseDto.class))
            .collectList();
    }

    public Mono<Boolean> existeFaltaVigente(Long procesoId, Long faltaId) {
        return db.sql("SELECT count(*) AS c FROM proceso_falta WHERE proceso_disciplinario_id=:p AND falta_id=:f AND deleted_at IS NULL")
            .bind("p", procesoId).bind("f", faltaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<List<ProcesoDescargoResponseDto>> listDescargos(Long procesoId) {
        return db.sql("""
                SELECT d.id AS "id", d.fecha AS "fecha", d.texto AS "texto"
                FROM proceso_descargo d WHERE d.proceso_disciplinario_id=:p AND d.deleted_at IS NULL
                ORDER BY d.fecha NULLS LAST, d.id
                """)
            .bind("p", procesoId).fetch().all()
            .map(r -> MapperRepository.mapResultSetToObject(r, ProcesoDescargoResponseDto.class))
            .collectList();
    }

    public Mono<List<ProcesoNotificacionResponseDto>> listNotificaciones(Long procesoId) {
        return db.sql("""
                SELECT n.id AS "id", n.fecha AS "fecha", n.tipo AS "tipo", n.texto AS "texto"
                FROM proceso_notificacion n WHERE n.proceso_disciplinario_id=:p AND n.deleted_at IS NULL
                ORDER BY n.fecha NULLS LAST, n.id
                """)
            .bind("p", procesoId).fetch().all()
            .map(r -> MapperRepository.mapResultSetToObject(r, ProcesoNotificacionResponseDto.class))
            .collectList();
    }
}
