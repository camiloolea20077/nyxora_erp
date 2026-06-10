package com.cloud_tecnoligical.nyxora_erp.repository.contabilidad;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.ComprobanteResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.ComprobanteTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.MovimientoContableResponseDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Repository
public class ComprobanteQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("fecha", "numero", "estado", "created_at");

    public ComprobanteQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<ComprobanteResponseDto> findActiveById(Long id, Long empresaId) {
        return db.sql("""
                SELECT c.id AS "id", c.periodo_contable_id AS "periodoContableId", c.tipo_documento_id AS "tipoDocumentoId",
                       c.numero AS "numero", c.fecha AS "fecha", c.descripcion AS "descripcion", c.estado AS "estado",
                       c.total_debito AS "totalDebito", c.total_credito AS "totalCredito",
                       c.origen_modulo AS "origenModulo", c.origen_id AS "origenId",
                       c.activo AS "active", c.created_at AS "createdAt"
                FROM comprobante c WHERE c.id=:id AND c.empresa_id=:e AND c.deleted_at IS NULL LIMIT 1
                """)
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, ComprobanteResponseDto.class));
    }

    public Mono<List<MovimientoContableResponseDto>> listMovimientos(Long comprobanteId) {
        return db.sql("""
                SELECT id AS "id", comprobante_id AS "comprobanteId", cuenta_id AS "cuentaId",
                       tercero_id AS "terceroId", centro_costo_id AS "centroCostoId", proyecto_id AS "proyectoId",
                       recurso_id AS "recursoId", descripcion AS "descripcion", debito AS "debito", credito AS "credito",
                       valor_base AS "valorBase", impuesto_id AS "impuestoId", porcentaje_impuesto AS "porcentajeImpuesto",
                       valor_trm AS "valorTrm", valor_dolar AS "valorDolar"
                FROM movimiento_contable WHERE comprobante_id=:c ORDER BY id
                """)
            .bind("c", comprobanteId).fetch().all()
            .map(r -> MapperRepository.mapResultSetToObject(r, MovimientoContableResponseDto.class))
            .collectList();
    }

    @SuppressWarnings("unchecked")
    public Mono<PageResponseDto<ComprobanteTableDto>> list(PageableDto<?> request, Long empresaId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String search = request.getSearch() != null ? request.getSearch().trim() : null;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "fecha";
        String order = "ASC".equalsIgnoreCase(request.getOrder()) ? "ASC" : "DESC";

        StringBuilder sql = new StringBuilder("""
                SELECT c.id AS "id", c.numero AS "numero", c.fecha AS "fecha", c.descripcion AS "descripcion",
                       c.estado AS "estado", c.total_debito AS "totalDebito", c.total_credito AS "totalCredito",
                       c.activo AS "active", COUNT(*) OVER() AS total_rows
                FROM comprobante c WHERE c.empresa_id=:e AND c.deleted_at IS NULL
                """);
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (LOWER(c.descripcion) LIKE LOWER(:search) OR c.numero LIKE :search) ");
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
            List<ComprobanteTableDto> content = rows.stream()
                .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, ComprobanteTableDto.class))
                .toList();
            return new PageResponseDto<>(content, page, size, total);
        });
    }

    // ---------- validaciones de las líneas ----------

    /** Cuántas de esas cuentas son imputables (maneja_movimiento) y de la empresa. */
    public Mono<Long> countCuentasImputables(Collection<Long> cuentaIds, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM cuenta WHERE id IN (:ids) AND empresa_id=:e AND deleted_at IS NULL AND maneja_movimiento = TRUE")
            .bind("ids", cuentaIds).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one();
    }

    public Mono<Long> countTerceros(Collection<Long> ids, Long empresaId) {
        return countEnEmpresa("tercero", ids, empresaId);
    }

    public Mono<Long> countCentrosCosto(Collection<Long> ids, Long empresaId) {
        return countEnEmpresa("centro_costo", ids, empresaId);
    }

    public Mono<Long> countProyectos(Collection<Long> ids, Long empresaId) {
        return countEnEmpresa("proyecto", ids, empresaId);
    }

    public Mono<Long> countImpuestos(Collection<Long> ids, Long empresaId) {
        return countEnEmpresa("impuesto", ids, empresaId);
    }

    /** Cuenta cuántos de esos ids existen en la empresa y no están eliminados. tabla es constante interna (no input del usuario). */
    private Mono<Long> countEnEmpresa(String tabla, Collection<Long> ids, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM " + tabla + " WHERE id IN (:ids) AND empresa_id=:e AND deleted_at IS NULL")
            .bind("ids", ids).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one();
    }
}
