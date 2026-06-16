package com.cloud_tecnoligical.nyxora_erp.mapper.contratacion;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.CreateClausulaPlantillaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.ClausulaPlantillaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.ClausulaPlantillaEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ClausulaPlantillaMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(source = "tipoClausula", target = "tipo_clausula")
    })
    ClausulaPlantillaEntity toEntity(CreateClausulaPlantillaRequestDto dto);

    @Mappings({
        @Mapping(source = "tipo_clausula", target = "tipoClausula"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    ClausulaPlantillaResponseDto toResponseDto(ClausulaPlantillaEntity entity);
}
