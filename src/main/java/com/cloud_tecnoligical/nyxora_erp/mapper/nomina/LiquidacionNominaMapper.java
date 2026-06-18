package com.cloud_tecnoligical.nyxora_erp.mapper.nomina;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.nomina.CreateLiquidacionNominaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.LiquidacionNominaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.LiquidacionNominaEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LiquidacionNominaMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(source = "grupoNominaId", target = "grupo_nomina_id"),
        @Mapping(target = "estado", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "usuario_creacion", ignore = true),
        @Mapping(target = "usuario_modificacion", ignore = true)
    })
    LiquidacionNominaEntity toEntity(CreateLiquidacionNominaRequestDto dto);

    @Mappings({
        @Mapping(source = "grupo_nomina_id", target = "grupoNominaId"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    LiquidacionNominaResponseDto toResponseDto(LiquidacionNominaEntity entity);
}
