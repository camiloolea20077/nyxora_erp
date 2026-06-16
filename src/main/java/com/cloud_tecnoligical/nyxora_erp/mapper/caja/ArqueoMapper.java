package com.cloud_tecnoligical.nyxora_erp.mapper.caja;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.caja.ArqueoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.ArqueoEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ArqueoMapper {

    @Mappings({
        @Mapping(source = "caja_id", target = "cajaId"),
        @Mapping(source = "valor_declarado", target = "valorDeclarado"),
        @Mapping(source = "valor_sistema", target = "valorSistema"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    ArqueoResponseDto toResponseDto(ArqueoEntity entity);
}
