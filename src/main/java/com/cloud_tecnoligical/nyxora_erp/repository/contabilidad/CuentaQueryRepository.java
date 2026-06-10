package com.cloud_tecnoligical.nyxora_erp.repository.contabilidad;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.CuentaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.CuentaTableDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Repository
public class CuentaQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("codigo_cuenta", "nombre_cuenta", "nivel", "created_at");

    public CuentaQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<Boolean> existsByCodigo(String codigo, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM cuenta WHERE codigo_cuenta=:c AND empresa_id=:e AND deleted_at IS NULL")
            .bind("c", codigo).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<Boolean> existsByCodigoExcludingId(String codigo, Long id, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM cuenta WHERE codigo_cuenta=:c AND empresa_id=:e AND id<>:id AND deleted_at IS NULL")
            .bind("c", codigo).bind("e", empresaId).bind("id", id)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    /** La cuenta existe, es de la empresa y no está eliminada (para validar padre). */
    public Mono<Boolean> existsActivaEnEmpresa(Long id, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM cuenta WHERE id=:id AND empresa_id=:e AND deleted_at IS NULL")
            .bind("id", id).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    /** Cuenta imputable (existe, de la empresa, no eliminada y maneja_movimiento=true). */
    public Mono<Boolean> esImputableEnEmpresa(Long id, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM cuenta WHERE id=:id AND empresa_id=:e AND deleted_at IS NULL AND maneja_movimiento = TRUE")
            .bind("id", id).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<CuentaResponseDto> findActiveById(Long id, Long empresaId) {
        return db.sql("""
                SELECT c.id AS "id", c.cuenta_padre_id AS "cuentaPadreId", c.codigo_cuenta AS "codigoCuenta",
                       c.nombre_cuenta AS "nombreCuenta", c.nivel AS "nivel", c.izquierda AS "izquierda",
                       c.derecha AS "derecha", c.naturaleza AS "naturaleza", c.tipo_cuenta AS "tipoCuenta",
                       c.maneja_movimiento AS "manejaMovimiento", c.maneja_movimiento_manual AS "manejaMovimientoManual",
                       c.maneja_tercero AS "manejaTercero", c.maneja_centro_costo AS "manejaCentroCosto",
                       c.maneja_impuesto AS "manejaImpuesto", c.maneja_proyecto AS "manejaProyecto",
                       c.maneja_recurso AS "manejaRecurso", c.maneja_saldo_contrario AS "manejaSaldoContrario",
                       c.es_corriente AS "esCorriente", c.activo AS "active", c.created_at AS "createdAt"
                FROM cuenta c WHERE c.id=:id AND c.empresa_id=:e AND c.deleted_at IS NULL LIMIT 1
                """)
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, CuentaResponseDto.class));
    }

    @SuppressWarnings("unchecked")
    public Mono<PageResponseDto<CuentaTableDto>> list(PageableDto<?> request, Long empresaId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String search = request.getSearch() != null ? request.getSearch().trim() : null;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "codigo_cuenta";
        String order = "DESC".equalsIgnoreCase(request.getOrder()) ? "DESC" : "ASC";

        StringBuilder sql = new StringBuilder("""
                SELECT c.id AS "id", c.cuenta_padre_id AS "cuentaPadreId", c.codigo_cuenta AS "codigoCuenta",
                       c.nombre_cuenta AS "nombreCuenta", c.naturaleza AS "naturaleza",
                       c.maneja_movimiento AS "manejaMovimiento", c.activo AS "active", COUNT(*) OVER() AS total_rows
                FROM cuenta c WHERE c.empresa_id=:e AND c.deleted_at IS NULL
                """);
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (LOWER(c.nombre_cuenta) LIKE LOWER(:search) OR c.codigo_cuenta LIKE :search) ");
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
            List<CuentaTableDto> content = rows.stream()
                .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, CuentaTableDto.class))
                .toList();
            return new PageResponseDto<>(content, page, size, total);
        });
    }
}
