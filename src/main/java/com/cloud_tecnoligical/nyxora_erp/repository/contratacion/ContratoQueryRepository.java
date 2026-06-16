package com.cloud_tecnoligical.nyxora_erp.repository.contratacion;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.ContratoClausulaDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.ContratoPolizaDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.ContratoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.ContratoTableDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Repository
public class ContratoQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("numero", "nombre", "valor", "fecha_inicio", "created_at");

    public ContratoQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<Boolean> modalidadExists(Long modalidadId, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM modalidad_contrato WHERE id=:id AND empresa_id=:e AND deleted_at IS NULL")
            .bind("id", modalidadId).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<Boolean> contratistaExists(Long contratistaId, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM tercero WHERE id=:id AND empresa_id=:e AND deleted_at IS NULL")
            .bind("id", contratistaId).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<Boolean> polizaExistsInTenant(Long polizaId, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM poliza_seguro WHERE id=:id AND empresa_id=:e AND deleted_at IS NULL")
            .bind("id", polizaId).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<Long> polizaVigenteId(Long contratoId, Long polizaId) {
        return db.sql("SELECT id FROM contrato_poliza WHERE contrato_id=:c AND poliza_seguro_id=:p AND deleted_at IS NULL LIMIT 1")
            .bind("c", contratoId).bind("p", polizaId)
            .map(row -> ((Number) row.get("id")).longValue()).one();
    }

    /** Soft-delete de las cláusulas vigentes del contrato (para reemplazo en update). */
    public Mono<Long> borrarClausulas(Long contratoId) {
        return db.sql("UPDATE contrato_clausula SET deleted_at=now() WHERE contrato_id=:c AND deleted_at IS NULL")
            .bind("c", contratoId)
            .fetch().rowsUpdated();
    }

    public Mono<ContratoResponseDto> findHeaderById(Long id, Long empresaId) {
        return db.sql("""
                SELECT c.id AS "id", c.numero AS "numero", c.nombre AS "nombre", c.tipo_contrato AS "tipoContrato",
                       c.contratista_id AS "contratistaId",
                       COALESCE(te.razon_social, te.primer_nombre) AS "contratistaNombre",
                       c.modalidad_id AS "modalidadId", m.nombre AS "modalidadNombre",
                       c.objeto AS "objeto", c.fecha_inicio AS "fechaInicio", c.fecha_fin AS "fechaFin",
                       c.valor AS "valor", c.estado AS "estado", c.activo AS "active", c.created_at AS "createdAt"
                FROM contrato c
                LEFT JOIN tercero te ON te.id = c.contratista_id
                LEFT JOIN modalidad_contrato m ON m.id = c.modalidad_id
                WHERE c.id=:id AND c.empresa_id=:e AND c.deleted_at IS NULL LIMIT 1
                """)
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, ContratoResponseDto.class));
    }

    public Mono<List<ContratoClausulaDto>> listClausulas(Long contratoId) {
        return db.sql("""
                SELECT cc.id AS "id", cc.numero AS "numero", cc.orden AS "orden", cc.nombre AS "nombre", cc.texto AS "texto"
                FROM contrato_clausula cc WHERE cc.contrato_id=:c AND cc.deleted_at IS NULL ORDER BY cc.id
                """)
            .bind("c", contratoId)
            .fetch().all()
            .map(row -> MapperRepository.mapResultSetToObject(row, ContratoClausulaDto.class))
            .collectList();
    }

    public Mono<List<ContratoPolizaDto>> listPolizas(Long contratoId) {
        return db.sql("""
                SELECT cp.id AS "id", cp.poliza_seguro_id AS "polizaSeguroId", p.numero AS "numero",
                       p.tipo AS "tipo", p.valor_asegurado AS "valorAsegurado"
                FROM contrato_poliza cp
                JOIN poliza_seguro p ON p.id = cp.poliza_seguro_id
                WHERE cp.contrato_id=:c AND cp.deleted_at IS NULL ORDER BY cp.id
                """)
            .bind("c", contratoId)
            .fetch().all()
            .map(row -> MapperRepository.mapResultSetToObject(row, ContratoPolizaDto.class))
            .collectList();
    }

    @SuppressWarnings("unchecked")
    public Mono<PageResponseDto<ContratoTableDto>> list(PageableDto<?> request, Long empresaId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String search = request.getSearch() != null ? request.getSearch().trim() : null;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "created_at";
        String order = "ASC".equalsIgnoreCase(request.getOrder()) ? "ASC" : "DESC";

        StringBuilder sql = new StringBuilder("""
                SELECT c.id AS "id", c.numero AS "numero", c.nombre AS "nombre",
                       COALESCE(te.razon_social, te.primer_nombre) AS "contratistaNombre",
                       c.valor AS "valor", c.estado AS "estado", c.fecha_inicio AS "fechaInicio",
                       c.activo AS "active", COUNT(*) OVER() AS total_rows
                FROM contrato c
                LEFT JOIN tercero te ON te.id = c.contratista_id
                WHERE c.empresa_id=:e AND c.deleted_at IS NULL
                """);
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (LOWER(c.nombre) LIKE LOWER(:search) OR LOWER(c.numero) LIKE LOWER(:search)) ");
        }
        sql.append(" ORDER BY c.").append(orderBy).append(" ").append(order);
        sql.append(" OFFSET :offset LIMIT :limit");

        var spec = db.sql(sql.toString()).bind("e", empresaId);
        if (search != null && !search.isEmpty()) {
            spec = spec.bind("search", "%" + search + "%");
        }
        spec = spec.bind("offset", page * size).bind("limit", size);

        return spec.fetch().all().collectList().map(rows -> {
            long total = rows.isEmpty() ? 0 : ((Number) rows.get(0).get("total_rows")).longValue();
            List<ContratoTableDto> content = rows.stream()
                .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, ContratoTableDto.class))
                .toList();
            return new PageResponseDto<>(content, page, size, total);
        });
    }
}
