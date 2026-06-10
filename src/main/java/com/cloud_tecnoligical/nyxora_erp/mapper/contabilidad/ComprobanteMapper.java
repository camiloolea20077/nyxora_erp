package com.cloud_tecnoligical.nyxora_erp.mapper.contabilidad;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.ComprobanteResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.ComprobanteEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ComprobanteMapper {

    @Mappings({
        @Mapping(source = "periodo_contable_id", target = "periodoContableId"),
        @Mapping(source = "tipo_documento_id", target = "tipoDocumentoId"),
        @Mapping(source = "total_debito", target = "totalDebito"),
        @Mapping(source = "total_credito", target = "totalCredito"),
        @Mapping(source = "origen_modulo", target = "origenModulo"),
        @Mapping(source = "origen_id", target = "origenId"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt"),
        @Mapping(target = "movimientos", ignore = true)   // se llena en el service
    })
    ComprobanteResponseDto toResponseDto(ComprobanteEntity entity);
}
