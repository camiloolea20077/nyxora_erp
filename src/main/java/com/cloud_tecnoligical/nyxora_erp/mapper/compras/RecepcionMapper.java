package com.cloud_tecnoligical.nyxora_erp.mapper.compras;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.compras.RecepcionResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.RecepcionEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RecepcionMapper {

    @Mappings({
        @Mapping(source = "orden_compra_id", target = "ordenCompraId"),
        @Mapping(source = "bodega_id", target = "bodegaId"),
        @Mapping(source = "tipo_documento_id", target = "tipoDocumentoId"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt"),
        @Mapping(target = "lineas", ignore = true)
    })
    RecepcionResponseDto toResponseDto(RecepcionEntity entity);
}
