package com.cloud_tecnoligical.nyxora_erp.repository.catalogo;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.catalogo.CatalogoCrudDto;
import com.cloud_tecnoligical.nyxora_erp.dto.catalogo.CatalogoItemDto;
import com.cloud_tecnoligical.nyxora_erp.dto.catalogo.UbicacionMunicipioDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

/**
 * Catálogos globales con forma (id, codigo, nombre, activo). El slug se valida contra una whitelist
 * (anti-inyección). Soporta búsqueda, filtro por padre (cascade geográfico) y CRUD de los catálogos
 * planos marcados como editables (geografía es solo lectura).
 */
@Repository
public class CatalogoQueryRepository {

    private final DatabaseClient db;

    private record CatalogoDef(String tabla, String parentCol, boolean editable, boolean tieneActivo) {}

    private static final Map<String, CatalogoDef> DEFS = Map.ofEntries(
        Map.entry("tipo-contribuyente", new CatalogoDef("tipo_contribuyente", null, true, true)),
        Map.entry("actividad-economica", new CatalogoDef("actividad_economica", null, true, true)),
        Map.entry("pais", new CatalogoDef("pais", null, false, true)),
        Map.entry("departamento", new CatalogoDef("departamento", "pais_id", false, true)),
        Map.entry("municipio", new CatalogoDef("municipio", "departamento_id", false, true)),
        Map.entry("tipo-identificacion", new CatalogoDef("tipo_identificacion", null, true, true)),
        Map.entry("tipo-tercero", new CatalogoDef("tipo_tercero", null, true, true)),
        Map.entry("condicion-pago", new CatalogoDef("condicion_pago", null, true, true)),
        Map.entry("forma-pago", new CatalogoDef("forma_pago", null, true, true)),
        Map.entry("banco", new CatalogoDef("banco", null, true, true)),
        Map.entry("tipo-cuenta-bancaria", new CatalogoDef("tipo_cuenta_bancaria", null, true, true)),
        // unidad_medida es un catálogo simple sin columna 'activo'
        Map.entry("unidad-medida", new CatalogoDef("unidad_medida", null, true, false))
    );

    public CatalogoQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    private CatalogoDef def(String slug) {
        CatalogoDef def = DEFS.get(slug);
        if (def == null) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "Catálogo no válido: " + slug);
        }
        return def;
    }

    private CatalogoDef editable(String slug) {
        CatalogoDef def = def(slug);
        if (!def.editable()) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "Este catálogo no es editable: " + slug);
        }
        return def;
    }

    @SuppressWarnings("unchecked")
    public Mono<PageResponseDto<CatalogoItemDto>> list(String slug, PageableDto<?> request, Long parentId,
            boolean soloActivos) {
        final CatalogoDef def = def(slug);
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String search = request.getSearch() != null ? request.getSearch().trim() : null;
        boolean filtraPadre = parentId != null && def.parentCol() != null;

        String activoSel = def.tieneActivo() ? "activo AS \"active\"" : "TRUE AS \"active\"";
        StringBuilder sql = new StringBuilder("SELECT id AS \"id\", codigo AS \"codigo\", nombre AS \"nombre\", "
            + activoSel + ", COUNT(*) OVER() AS total_rows FROM " + def.tabla() + " WHERE 1=1");
        if (soloActivos && def.tieneActivo()) {
            sql.append(" AND activo = TRUE");
        }
        if (filtraPadre) {
            sql.append(" AND ").append(def.parentCol()).append(" = :parentId ");
        }
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (LOWER(nombre) LIKE LOWER(:search) OR codigo LIKE :search) ");
        }
        sql.append(" ORDER BY nombre ASC OFFSET :offset LIMIT :limit");

        var spec = db.sql(sql.toString());
        if (filtraPadre) {
            spec = spec.bind("parentId", parentId);
        }
        if (search != null && !search.isEmpty()) {
            spec = spec.bind("search", "%" + search + "%");
        }
        spec = spec.bind("offset", page * size).bind("limit", size);

        return spec.fetch().all().collectList().map(rows -> {
            long total = rows.isEmpty() ? 0 : ((Number) rows.get(0).get("total_rows")).longValue();
            List<CatalogoItemDto> content = rows.stream()
                .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, CatalogoItemDto.class))
                .toList();
            return new PageResponseDto<>(content, page, size, total);
        });
    }

    public Mono<CatalogoItemDto> byId(String slug, Long id) {
        final CatalogoDef def = def(slug);
        String activoSel = def.tieneActivo() ? "activo AS \"active\"" : "TRUE AS \"active\"";
        return db.sql("SELECT id AS \"id\", codigo AS \"codigo\", nombre AS \"nombre\", " + activoSel + " FROM "
                + def.tabla() + " WHERE id = :id LIMIT 1")
            .bind("id", id)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, CatalogoItemDto.class));
    }

    public Mono<CatalogoItemDto> create(String slug, CatalogoCrudDto dto) {
        final CatalogoDef def = editable(slug);
        boolean activo = dto.getActivo() == null || dto.getActivo();
        String sql = def.tieneActivo()
            ? "INSERT INTO " + def.tabla() + " (codigo, nombre, activo) VALUES (:codigo, :nombre, :activo) "
                + "RETURNING id AS \"id\", codigo AS \"codigo\", nombre AS \"nombre\", activo AS \"active\""
            : "INSERT INTO " + def.tabla() + " (codigo, nombre) VALUES (:codigo, :nombre) "
                + "RETURNING id AS \"id\", codigo AS \"codigo\", nombre AS \"nombre\", TRUE AS \"active\"";
        var spec = db.sql(sql).bind("codigo", dto.getCodigo().trim()).bind("nombre", dto.getNombre().trim());
        if (def.tieneActivo()) {
            spec = spec.bind("activo", activo);
        }
        return spec.fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, CatalogoItemDto.class))
            .onErrorMap(e -> !(e instanceof GlobalException),
                e -> new GlobalException(HttpStatus.CONFLICT, "No se pudo crear: el código ya existe"));
    }

    public Mono<Boolean> update(String slug, CatalogoCrudDto dto) {
        final CatalogoDef def = editable(slug);
        if (dto.getId() == null) {
            return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "El id es obligatorio"));
        }
        boolean activo = dto.getActivo() == null || dto.getActivo();
        String sql = def.tieneActivo()
            ? "UPDATE " + def.tabla() + " SET codigo = :codigo, nombre = :nombre, activo = :activo WHERE id = :id"
            : "UPDATE " + def.tabla() + " SET codigo = :codigo, nombre = :nombre WHERE id = :id";
        var spec = db.sql(sql)
            .bind("codigo", dto.getCodigo().trim())
            .bind("nombre", dto.getNombre().trim())
            .bind("id", dto.getId());
        if (def.tieneActivo()) {
            spec = spec.bind("activo", activo);
        }
        return spec.fetch().rowsUpdated()
            .map(n -> n > 0)
            .onErrorMap(e -> !(e instanceof GlobalException),
                e -> new GlobalException(HttpStatus.CONFLICT, "No se pudo actualizar: el código ya existe"));
    }

    public Mono<Boolean> delete(String slug, Long id) {
        final CatalogoDef def = editable(slug);
        return db.sql("DELETE FROM " + def.tabla() + " WHERE id = :id")
            .bind("id", id)
            .fetch().rowsUpdated()
            .map(n -> n > 0)
            .onErrorMap(e -> !(e instanceof GlobalException),
                e -> new GlobalException(HttpStatus.CONFLICT, "No se puede eliminar: el ítem está en uso"));
    }

    /** Dado un municipio, devuelve su departamento y país (para preseleccionar el cascade). */
    public Mono<UbicacionMunicipioDto> ubicacionMunicipio(Long municipioId) {
        return db.sql("""
                SELECT m.id AS "municipioId", m.departamento_id AS "departamentoId", d.pais_id AS "paisId"
                FROM municipio m JOIN departamento d ON d.id = m.departamento_id
                WHERE m.id = :id LIMIT 1
                """)
            .bind("id", municipioId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, UbicacionMunicipioDto.class));
    }
}
