package com.cloud_tecnoligical.nyxora_erp.mapper.caja;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.caja.ReciboCajaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.ReciboCajaEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ReciboCajaMapper {

    @Mappings({
        @Mapping(source = "caja_id", target = "cajaId"),
        @Mapping(source = "tipo_documento_id", target = "tipoDocumentoId"),
        @Mapping(source = "cliente_id", target = "clienteId"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt"),
        @Mapping(target = "pagos", ignore = true),
        @Mapping(target = "lineas", ignore = true)
    })
    ReciboCajaResponseDto toResponseDto(ReciboCajaEntity entity);
}
