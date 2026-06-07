package com.cloud_tecnoligical.nyxora_erp.repository.empresa;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.empresa.EmpresaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.empresa.EmpresaTableDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Repository
public class EmpresaQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("nit", "razon_social", "created_at");

    public EmpresaQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    public Mono<Boolean> existsByNit(String nit) {
        return db.sql("SELECT count(*) AS c FROM empresa WHERE nit=:nit AND deleted_at IS NULL")
            .bind("nit", nit).map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<EmpresaResponseDto> findById(Long id) {
        return db.sql("""
                SELECT e.id AS "id", e.nit AS "nit", e.digito_verificacion AS "digitoVerificacion",
                       e.razon_social AS "razonSocial", e.nombre_comercial AS "nombreComercial",
                       e.codigo AS "codigo", e.tipo_persona AS "tipoPersona",
                       e.representante_legal AS "representanteLegal", e.regimen_tributario AS "regimenTributario",
                       e.tipo_contribuyente_id AS "tipoContribuyenteId", e.responsabilidad_fiscal AS "responsabilidadFiscal",
                       e.actividad_economica_id AS "actividadEconomicaId", e.sector AS "sector",
                       e.email AS "email", e.telefono AS "telefono", e.celular AS "celular", e.sitio_web AS "sitioWeb",
                       e.municipio_id AS "municipioId", e.direccion AS "direccion", e.codigo_postal AS "codigoPostal",
                       e.logo_url AS "logoUrl", e.activo AS "active", e.created_at AS "createdAt"
                FROM empresa e WHERE e.id = :id AND e.deleted_at IS NULL LIMIT 1
                """)
            .bind("id", id).fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, EmpresaResponseDto.class));
    }

    public Mono<PageResponseDto<EmpresaTableDto>> list(PageableDto<?> request) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String search = request.getSearch() != null ? request.getSearch().trim() : null;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "razon_social";
        String order = "DESC".equalsIgnoreCase(request.getOrder()) ? "DESC" : "ASC";

        StringBuilder sql = new StringBuilder("""
                SELECT e.id AS "id", e.nit AS "nit", e.razon_social AS "razonSocial",
                       e.nombre_comercial AS "nombreComercial", e.activo AS "active",
                       COUNT(*) OVER() AS total_rows
                FROM empresa e WHERE e.deleted_at IS NULL
                """);
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (LOWER(e.razon_social) LIKE LOWER(:search) OR e.nit LIKE :search) ");
        }
        sql.append(" ORDER BY e.").append(orderBy).append(" ").append(order);
        sql.append(" OFFSET :offset LIMIT :limit");

        var spec = db.sql(sql.toString());
        if (search != null && !search.isEmpty()) {
            spec = spec.bind("search", "%" + search + "%");
        }
        spec = spec.bind("offset", page * size).bind("limit", size);

        return spec.fetch().all().collectList().map(rows -> {
            long total = rows.isEmpty() ? 0 : ((Number) rows.get(0).get("total_rows")).longValue();
            List<EmpresaTableDto> content = rows.stream()
                .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, EmpresaTableDto.class))
                .toList();
            return new PageResponseDto<>(content, page, size, total);
        });
    }
}
