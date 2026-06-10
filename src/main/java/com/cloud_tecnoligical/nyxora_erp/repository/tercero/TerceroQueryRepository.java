package com.cloud_tecnoligical.nyxora_erp.repository.tercero;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.TerceroFilterDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.TerceroResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.TerceroTableDto;
import com.cloud_tecnoligical.nyxora_erp.util.MapperRepository;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class TerceroQueryRepository {

    private final DatabaseClient db;
    private static final Set<String> SORTABLE = Set.of("numero_documento", "nombre", "created_at");

    public TerceroQueryRepository(DatabaseClient db) {
        this.db = db;
    }

    /** El tercero existe, es de la empresa y no está eliminado (para validar satélites). */
    public Mono<Boolean> existsActivoEnEmpresa(Long terceroId, Long empresaId) {
        return db.sql("SELECT count(*) AS c FROM tercero WHERE id=:id AND empresa_id=:e AND deleted_at IS NULL")
            .bind("id", terceroId).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<Boolean> existsByDocumento(Long tipoIdentificacionId, String numeroDocumento, Long empresaId) {
        return db.sql("""
                SELECT count(*) AS c FROM tercero
                WHERE tipo_identificacion_id=:ti AND numero_documento=:nd AND empresa_id=:e AND deleted_at IS NULL
                """)
            .bind("ti", tipoIdentificacionId).bind("nd", numeroDocumento).bind("e", empresaId)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<Boolean> existsByDocumentoExcludingId(Long tipoIdentificacionId, String numeroDocumento, Long id, Long empresaId) {
        return db.sql("""
                SELECT count(*) AS c FROM tercero
                WHERE tipo_identificacion_id=:ti AND numero_documento=:nd AND empresa_id=:e AND id<>:id AND deleted_at IS NULL
                """)
            .bind("ti", tipoIdentificacionId).bind("nd", numeroDocumento).bind("e", empresaId).bind("id", id)
            .map(row -> ((Number) row.get("c")).longValue()).one().map(c -> c > 0);
    }

    public Mono<TerceroResponseDto> findActiveById(Long id, Long empresaId) {
        return db.sql("""
                SELECT t.id AS "id", t.tipo_identificacion_id AS "tipoIdentificacionId",
                       t.numero_documento AS "numeroDocumento", t.digito_verificacion AS "digitoVerificacion",
                       t.tipo_persona AS "tipoPersona", t.primer_nombre AS "primerNombre",
                       t.segundo_nombre AS "segundoNombre", t.primer_apellido AS "primerApellido",
                       t.segundo_apellido AS "segundoApellido", t.razon_social AS "razonSocial",
                       t.nombre_comercial AS "nombreComercial", t.nombre_representante_legal AS "nombreRepresentanteLegal",
                       t.documento_representante_legal AS "documentoRepresentanteLegal", t.nombre AS "nombre",
                       t.genero_id AS "generoId", t.estado_civil_id AS "estadoCivilId",
                       t.fecha_nacimiento AS "fechaNacimiento", t.municipio_id AS "municipioId",
                       t.barrio_id AS "barrioId", t.direccion AS "direccion", t.sitio_web AS "sitioWeb",
                       t.fecha_expedicion_documento AS "fechaExpedicionDocumento",
                       t.municipio_expedicion_id AS "municipioExpedicionId",
                       t.fecha_vencimiento_documento AS "fechaVencimientoDocumento",
                       t.actividad_economica_id AS "actividadEconomicaId", t.tipo_contribuyente_id AS "tipoContribuyenteId",
                       t.responsable_iva AS "responsableIva", t.es_autoretenedor_iva AS "esAutoretenedorIva",
                       t.es_autoretenedor_ica AS "esAutoretenedorIca", t.es_autoretenedor_fuente AS "esAutoretenedorFuente",
                       t.declarante AS "declarante", t.aplica_art_383 AS "aplicaArt383", t.tiene_rut AS "tieneRut",
                       t.condicion_pago_cliente_id AS "condicionPagoClienteId",
                       t.condicion_pago_proveedor_id AS "condicionPagoProveedorId",
                       t.forma_pago_cliente_id AS "formaPagoClienteId", t.forma_pago_proveedor_id AS "formaPagoProveedorId",
                       t.interes_efectivo_mensual AS "interesEfectivoMensual",
                       t.cuenta_contable_proveedor_id AS "cuentaContableProveedorId", t.recurso_id AS "recursoId",
                       t.es_reciproco AS "esReciproco", t.codigo_reciproco AS "codigoReciproco",
                       t.observaciones AS "observaciones", t.activo AS "active", t.created_at AS "createdAt"
                FROM tercero t WHERE t.id=:id AND t.empresa_id=:e AND t.deleted_at IS NULL LIMIT 1
                """)
            .bind("id", id).bind("e", empresaId)
            .fetch().one()
            .map(row -> MapperRepository.mapResultSetToObject(row, TerceroResponseDto.class));
    }

    public Mono<List<Long>> findClasificacionIds(Long terceroId) {
        return db.sql("SELECT tipo_tercero_id FROM tercero_clasificacion WHERE tercero_id=:t AND deleted_at IS NULL")
            .bind("t", terceroId)
            .map(row -> ((Number) row.get("tipo_tercero_id")).longValue())
            .all().collectList();
    }

    public Mono<PageResponseDto<TerceroTableDto>> list(PageableDto<?> request, Long empresaId) {
        long page = request.getPage() != null ? request.getPage() : 0;
        long size = request.getRows() != null ? request.getRows() : 10;
        String search = request.getSearch() != null ? request.getSearch().trim() : null;
        String reqOrderBy = request.getOrder_by();
        String orderBy = (reqOrderBy != null && SORTABLE.contains(reqOrderBy)) ? reqOrderBy : "nombre";
        String order = "DESC".equalsIgnoreCase(request.getOrder()) ? "DESC" : "ASC";

        // Filtros avanzados (params del PageableDto)
        Object rawParams = request.getParams();
        TerceroFilterDto f = (rawParams instanceof TerceroFilterDto) ? (TerceroFilterDto) rawParams : null;
        String tipoPersona = (f != null && f.getTipoPersona() != null && !f.getTipoPersona().isBlank())
            ? f.getTipoPersona().trim() : null;
        String numeroDocumento = (f != null && f.getNumeroDocumento() != null && !f.getNumeroDocumento().isBlank())
            ? f.getNumeroDocumento().trim() : null;
        Long tipoTerceroId = (f != null) ? f.getTipoTerceroId() : null;

        StringBuilder sql = new StringBuilder("""
                SELECT t.id AS "id", ti.nombre AS "tipoDocumento", t.numero_documento AS "numeroDocumento",
                       t.nombre AS "nombre", t.tipo_persona AS "tipoPersona", t.activo AS "active",
                       COUNT(*) OVER() AS total_rows
                FROM tercero t
                LEFT JOIN tipo_identificacion ti ON ti.id = t.tipo_identificacion_id
                WHERE t.empresa_id=:e AND t.deleted_at IS NULL
                """);
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (LOWER(t.nombre) LIKE LOWER(:search) OR t.numero_documento LIKE :search) ");
        }
        if (tipoPersona != null) {
            sql.append(" AND t.tipo_persona = :tipoPersona ");
        }
        if (numeroDocumento != null) {
            sql.append(" AND t.numero_documento LIKE :numeroDocumento ");
        }
        if (tipoTerceroId != null) {
            sql.append(" AND EXISTS (SELECT 1 FROM tercero_clasificacion tc "
                + "WHERE tc.tercero_id = t.id AND tc.tipo_tercero_id = :tipoTerceroId AND tc.deleted_at IS NULL) ");
        }
        sql.append(" ORDER BY t.").append(orderBy).append(" ").append(order);
        sql.append(" OFFSET :offset LIMIT :limit");

        var spec = db.sql(sql.toString()).bind("e", empresaId);
        if (search != null && !search.isEmpty()) {
            spec = spec.bind("search", "%" + search + "%");
        }
        if (tipoPersona != null) {
            spec = spec.bind("tipoPersona", tipoPersona);
        }
        if (numeroDocumento != null) {
            spec = spec.bind("numeroDocumento", numeroDocumento + "%");
        }
        if (tipoTerceroId != null) {
            spec = spec.bind("tipoTerceroId", tipoTerceroId);
        }
        spec = spec.bind("offset", page * size).bind("limit", size);

        return spec.fetch().all().collectList().map(rows -> {
            long total = rows.isEmpty() ? 0 : ((Number) rows.get(0).get("total_rows")).longValue();
            List<TerceroTableDto> content = rows.stream()
                .map(r -> MapperRepository.mapResultSetToObject((Map<String, Object>) r, TerceroTableDto.class))
                .toList();
            return new PageResponseDto<>(content, page, size, total);
        });
    }

    /** Reemplaza la clasificación (tipo_tercero) del tercero. */
    public Mono<Void> setClasificacion(Long terceroId, List<Long> tipoTerceroIds) {
        Mono<Long> del = db.sql("DELETE FROM tercero_clasificacion WHERE tercero_id=:t").bind("t", terceroId).fetch().rowsUpdated();
        if (tipoTerceroIds == null || tipoTerceroIds.isEmpty()) {
            return del.then();
        }
        return del.thenMany(Flux.fromIterable(tipoTerceroIds)
                .flatMap(ttId -> db.sql("""
                        INSERT INTO tercero_clasificacion (tercero_id, tipo_tercero_id) VALUES (:t,:tt)
                        ON CONFLICT (tercero_id, tipo_tercero_id) DO NOTHING
                        """)
                    .bind("t", terceroId).bind("tt", ttId).fetch().rowsUpdated()))
            .then();
    }
}
