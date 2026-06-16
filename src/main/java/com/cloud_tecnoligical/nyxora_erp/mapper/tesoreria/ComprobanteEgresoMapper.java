package com.cloud_tecnoligical.nyxora_erp.mapper.tesoreria;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.ComprobanteEgresoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.CreateComprobanteEgresoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.ComprobanteEgresoEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ComprobanteEgresoMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "estado", ignore = true),
        @Mapping(target = "origen_modulo", ignore = true),
        @Mapping(target = "origen_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "usuario_creacion", ignore = true),
        @Mapping(target = "usuario_modificacion", ignore = true),
        @Mapping(source = "cuentaBancariaId", target = "cuenta_bancaria_id"),
        @Mapping(source = "beneficiarioId", target = "beneficiario_id"),
        @Mapping(source = "tipoDocumentoId", target = "tipo_documento_id"),
        @Mapping(source = "formaPagoId", target = "forma_pago_id"),
        @Mapping(source = "numeroCheque", target = "numero_cheque")
    })
    ComprobanteEgresoEntity toEntity(CreateComprobanteEgresoRequestDto dto);

    @Mappings({
        @Mapping(source = "cuenta_bancaria_id", target = "cuentaBancariaId"),
        @Mapping(source = "beneficiario_id", target = "beneficiarioId"),
        @Mapping(source = "tipo_documento_id", target = "tipoDocumentoId"),
        @Mapping(source = "forma_pago_id", target = "formaPagoId"),
        @Mapping(source = "numero_cheque", target = "numeroCheque"),
        @Mapping(source = "origen_modulo", target = "origenModulo"),
        @Mapping(source = "origen_id", target = "origenId"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    ComprobanteEgresoResponseDto toResponseDto(ComprobanteEgresoEntity entity);
}
