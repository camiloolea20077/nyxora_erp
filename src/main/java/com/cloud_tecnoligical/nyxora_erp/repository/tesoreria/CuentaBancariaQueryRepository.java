package com.cloud_tecnoligical.nyxora_erp.repository.tesoreria;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.CuentaBancariaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.CuentaBancariaTableDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Repository
public class CuentaBancariaQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("numero_cuenta", "created_at");

    public CuentaBancariaQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<Boolean> existsByNumero(Long bancoId, String numero, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM cuenta_bancaria WHERE banco_id=:b AND numero_cuenta=:n AND empresa_id=:e AND deleted_at IS NULL")
            .bind("b", bancoId).bind("n", numero).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<Boolean> existsByNumeroExcludingId(Long bancoId, String numero, Long id, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM cuenta_bancaria WHERE banco_id=:b AND numero_cuenta=:n AND empresa_id=:e AND id<>:id AND deleted_at IS NULL")
            .bind("b", bancoId).bind("n", numero).bind("e", empresaId).bind("id", id)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<CuentaBancariaResponseDto> findActiveById(Long id, Long empresaId) {
        return db.sql("""
                SELECT c.id AS "id", c.banco_id AS "bancoId", b.nombre AS "bancoNombre",
                       c.tipo_cuenta_bancaria_id AS "tipoCuentaBancariaId",
                       c.numero_cuenta AS "numeroCuenta", c.cuenta_contable_id AS "cuentaContableId",
                       c.maneja_sobregiro AS "manejaSobregiro", c.acepta_transferencias AS "aceptaTransferencias",
                       c.fecha_expiracion AS "fechaExpiracion", c.activo AS "active", c.created_at AS "createdAt"
                FROM cuenta_bancaria c
                LEFT JOIN tercero b ON b.id = c.banco_id
                WHERE c.id=:id AND c.empresa_id=:e AND c.deleted_at IS NULL LIMIT 1
                """)
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, CuentaBancariaResponseDto.class));
    }

    @SuppressWarnings("unchecked")
    public Mono<PageResponseDto<CuentaBancariaTableDto>> list(PageableDto<?> request, Long empresaId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String search = request.getSearch() != null ? request.getSearch().trim() : null;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "numero_cuenta";
        String order = "DESC".equalsIgnoreCase(request.getOrder()) ? "DESC" : "ASC";

        StringBuilder sql = new StringBuilder("""
                SELECT c.id AS "id", c.banco_id AS "bancoId", b.nombre AS "bancoNombre",
                       c.tipo_cuenta_bancaria_id AS "tipoCuentaBancariaId",
                       c.numero_cuenta AS "numeroCuenta", c.maneja_sobregiro AS "manejaSobregiro",
                       c.acepta_transferencias AS "aceptaTransferencias", c.activo AS "active", COUNT(*) OVER() AS total_rows
                FROM cuenta_bancaria c
                LEFT JOIN tercero b ON b.id = c.banco_id
                WHERE c.empresa_id=:e AND c.deleted_at IS NULL
                """);
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (c.numero_cuenta LIKE :search OR LOWER(b.nombre) LIKE LOWER(:search)) ");
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
            List<CuentaBancariaTableDto> content = rows.stream()
                .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, CuentaBancariaTableDto.class))
                .toList();
            return new PageResponseDto<>(content, page, size, total);
        });
    }
}
