package com.cloud_tecnoligical.nyxora_erp.mapper.tercero;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.tercero.CreateTerceroRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.TerceroResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.UpdateTerceroRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.TerceroEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TerceroMapper {

    // ---- camelCase (DTO) -> snake_case (entity) ----
    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "nombre", ignore = true),       // se calcula en el service
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "usuario_creacion", ignore = true),
        @Mapping(target = "usuario_modificacion", ignore = true),
        @Mapping(source = "tipoIdentificacionId", target = "tipo_identificacion_id"),
        @Mapping(source = "numeroDocumento", target = "numero_documento"),
        @Mapping(source = "digitoVerificacion", target = "digito_verificacion"),
        @Mapping(source = "tipoPersona", target = "tipo_persona"),
        @Mapping(source = "primerNombre", target = "primer_nombre"),
        @Mapping(source = "segundoNombre", target = "segundo_nombre"),
        @Mapping(source = "primerApellido", target = "primer_apellido"),
        @Mapping(source = "segundoApellido", target = "segundo_apellido"),
        @Mapping(source = "razonSocial", target = "razon_social"),
        @Mapping(source = "nombreComercial", target = "nombre_comercial"),
        @Mapping(source = "nombreRepresentanteLegal", target = "nombre_representante_legal"),
        @Mapping(source = "documentoRepresentanteLegal", target = "documento_representante_legal"),
        @Mapping(source = "generoId", target = "genero_id"),
        @Mapping(source = "estadoCivilId", target = "estado_civil_id"),
        @Mapping(source = "fechaNacimiento", target = "fecha_nacimiento"),
        @Mapping(source = "municipioId", target = "municipio_id"),
        @Mapping(source = "barrioId", target = "barrio_id"),
        @Mapping(source = "sitioWeb", target = "sitio_web"),
        @Mapping(source = "fechaExpedicionDocumento", target = "fecha_expedicion_documento"),
        @Mapping(source = "municipioExpedicionId", target = "municipio_expedicion_id"),
        @Mapping(source = "fechaVencimientoDocumento", target = "fecha_vencimiento_documento"),
        @Mapping(source = "actividadEconomicaId", target = "actividad_economica_id"),
        @Mapping(source = "tipoContribuyenteId", target = "tipo_contribuyente_id"),
        @Mapping(source = "responsableIva", target = "responsable_iva"),
        @Mapping(source = "esAutoretenedorIva", target = "es_autoretenedor_iva"),
        @Mapping(source = "esAutoretenedorIca", target = "es_autoretenedor_ica"),
        @Mapping(source = "esAutoretenedorFuente", target = "es_autoretenedor_fuente"),
        @Mapping(source = "aplicaArt383", target = "aplica_art_383"),
        @Mapping(source = "tieneRut", target = "tiene_rut"),
        @Mapping(source = "condicionPagoClienteId", target = "condicion_pago_cliente_id"),
        @Mapping(source = "condicionPagoProveedorId", target = "condicion_pago_proveedor_id"),
        @Mapping(source = "formaPagoClienteId", target = "forma_pago_cliente_id"),
        @Mapping(source = "formaPagoProveedorId", target = "forma_pago_proveedor_id"),
        @Mapping(source = "interesEfectivoMensual", target = "interes_efectivo_mensual"),
        @Mapping(source = "cuentaContableProveedorId", target = "cuenta_contable_proveedor_id"),
        @Mapping(source = "recursoId", target = "recurso_id"),
        @Mapping(source = "esReciproco", target = "es_reciproco"),
        @Mapping(source = "codigoReciproco", target = "codigo_reciproco")
        // direccion, declarante, observaciones se mapean por nombre igual
    })
    TerceroEntity toEntity(CreateTerceroRequestDto dto);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "nombre", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "usuario_creacion", ignore = true),
        @Mapping(target = "usuario_modificacion", ignore = true),
        @Mapping(source = "tipoIdentificacionId", target = "tipo_identificacion_id"),
        @Mapping(source = "numeroDocumento", target = "numero_documento"),
        @Mapping(source = "digitoVerificacion", target = "digito_verificacion"),
        @Mapping(source = "tipoPersona", target = "tipo_persona"),
        @Mapping(source = "primerNombre", target = "primer_nombre"),
        @Mapping(source = "segundoNombre", target = "segundo_nombre"),
        @Mapping(source = "primerApellido", target = "primer_apellido"),
        @Mapping(source = "segundoApellido", target = "segundo_apellido"),
        @Mapping(source = "razonSocial", target = "razon_social"),
        @Mapping(source = "nombreComercial", target = "nombre_comercial"),
        @Mapping(source = "nombreRepresentanteLegal", target = "nombre_representante_legal"),
        @Mapping(source = "documentoRepresentanteLegal", target = "documento_representante_legal"),
        @Mapping(source = "generoId", target = "genero_id"),
        @Mapping(source = "estadoCivilId", target = "estado_civil_id"),
        @Mapping(source = "fechaNacimiento", target = "fecha_nacimiento"),
        @Mapping(source = "municipioId", target = "municipio_id"),
        @Mapping(source = "barrioId", target = "barrio_id"),
        @Mapping(source = "sitioWeb", target = "sitio_web"),
        @Mapping(source = "fechaExpedicionDocumento", target = "fecha_expedicion_documento"),
        @Mapping(source = "municipioExpedicionId", target = "municipio_expedicion_id"),
        @Mapping(source = "fechaVencimientoDocumento", target = "fecha_vencimiento_documento"),
        @Mapping(source = "actividadEconomicaId", target = "actividad_economica_id"),
        @Mapping(source = "tipoContribuyenteId", target = "tipo_contribuyente_id"),
        @Mapping(source = "responsableIva", target = "responsable_iva"),
        @Mapping(source = "esAutoretenedorIva", target = "es_autoretenedor_iva"),
        @Mapping(source = "esAutoretenedorIca", target = "es_autoretenedor_ica"),
        @Mapping(source = "esAutoretenedorFuente", target = "es_autoretenedor_fuente"),
        @Mapping(source = "aplicaArt383", target = "aplica_art_383"),
        @Mapping(source = "tieneRut", target = "tiene_rut"),
        @Mapping(source = "condicionPagoClienteId", target = "condicion_pago_cliente_id"),
        @Mapping(source = "condicionPagoProveedorId", target = "condicion_pago_proveedor_id"),
        @Mapping(source = "formaPagoClienteId", target = "forma_pago_cliente_id"),
        @Mapping(source = "formaPagoProveedorId", target = "forma_pago_proveedor_id"),
        @Mapping(source = "interesEfectivoMensual", target = "interes_efectivo_mensual"),
        @Mapping(source = "cuentaContableProveedorId", target = "cuenta_contable_proveedor_id"),
        @Mapping(source = "recursoId", target = "recurso_id"),
        @Mapping(source = "esReciproco", target = "es_reciproco"),
        @Mapping(source = "codigoReciproco", target = "codigo_reciproco")
    })
    void updateEntityFromDto(UpdateTerceroRequestDto dto, @MappingTarget TerceroEntity entity);

    // ---- snake_case (entity) -> camelCase (response) ----
    @Mappings({
        @Mapping(source = "tipo_identificacion_id", target = "tipoIdentificacionId"),
        @Mapping(source = "numero_documento", target = "numeroDocumento"),
        @Mapping(source = "digito_verificacion", target = "digitoVerificacion"),
        @Mapping(source = "tipo_persona", target = "tipoPersona"),
        @Mapping(source = "primer_nombre", target = "primerNombre"),
        @Mapping(source = "segundo_nombre", target = "segundoNombre"),
        @Mapping(source = "primer_apellido", target = "primerApellido"),
        @Mapping(source = "segundo_apellido", target = "segundoApellido"),
        @Mapping(source = "razon_social", target = "razonSocial"),
        @Mapping(source = "nombre_comercial", target = "nombreComercial"),
        @Mapping(source = "nombre_representante_legal", target = "nombreRepresentanteLegal"),
        @Mapping(source = "documento_representante_legal", target = "documentoRepresentanteLegal"),
        @Mapping(source = "genero_id", target = "generoId"),
        @Mapping(source = "estado_civil_id", target = "estadoCivilId"),
        @Mapping(source = "fecha_nacimiento", target = "fechaNacimiento"),
        @Mapping(source = "municipio_id", target = "municipioId"),
        @Mapping(source = "barrio_id", target = "barrioId"),
        @Mapping(source = "sitio_web", target = "sitioWeb"),
        @Mapping(source = "fecha_expedicion_documento", target = "fechaExpedicionDocumento"),
        @Mapping(source = "municipio_expedicion_id", target = "municipioExpedicionId"),
        @Mapping(source = "fecha_vencimiento_documento", target = "fechaVencimientoDocumento"),
        @Mapping(source = "actividad_economica_id", target = "actividadEconomicaId"),
        @Mapping(source = "tipo_contribuyente_id", target = "tipoContribuyenteId"),
        @Mapping(source = "responsable_iva", target = "responsableIva"),
        @Mapping(source = "es_autoretenedor_iva", target = "esAutoretenedorIva"),
        @Mapping(source = "es_autoretenedor_ica", target = "esAutoretenedorIca"),
        @Mapping(source = "es_autoretenedor_fuente", target = "esAutoretenedorFuente"),
        @Mapping(source = "aplica_art_383", target = "aplicaArt383"),
        @Mapping(source = "tiene_rut", target = "tieneRut"),
        @Mapping(source = "condicion_pago_cliente_id", target = "condicionPagoClienteId"),
        @Mapping(source = "condicion_pago_proveedor_id", target = "condicionPagoProveedorId"),
        @Mapping(source = "forma_pago_cliente_id", target = "formaPagoClienteId"),
        @Mapping(source = "forma_pago_proveedor_id", target = "formaPagoProveedorId"),
        @Mapping(source = "interes_efectivo_mensual", target = "interesEfectivoMensual"),
        @Mapping(source = "cuenta_contable_proveedor_id", target = "cuentaContableProveedorId"),
        @Mapping(source = "recurso_id", target = "recursoId"),
        @Mapping(source = "es_reciproco", target = "esReciproco"),
        @Mapping(source = "codigo_reciproco", target = "codigoReciproco"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt"),
        @Mapping(target = "tipoTerceroIds", ignore = true)  // se llena en el service
    })
    TerceroResponseDto toResponseDto(TerceroEntity entity);
}
