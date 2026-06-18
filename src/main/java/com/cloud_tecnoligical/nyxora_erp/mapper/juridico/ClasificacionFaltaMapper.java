package com.cloud_tecnoligical.nyxora_erp.mapper.juridico;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.juridico.ClasificacionFaltaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.CreateClasificacionFaltaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.ClasificacionFaltaEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ClasificacionFaltaMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true)
    })
    ClasificacionFaltaEntity toEntity(CreateClasificacionFaltaRequestDto dto);

    @Mappings({
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    ClasificacionFaltaResponseDto toResponseDto(ClasificacionFaltaEntity entity);
}
