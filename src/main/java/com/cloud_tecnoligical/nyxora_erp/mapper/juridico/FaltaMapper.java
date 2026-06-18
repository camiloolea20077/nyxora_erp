package com.cloud_tecnoligical.nyxora_erp.mapper.juridico;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.juridico.CreateFaltaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.FaltaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.FaltaEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FaltaMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(source = "clasificacionFaltaId", target = "clasificacion_falta_id"),
        @Mapping(source = "caducidadDias", target = "caducidad_dias"),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true)
    })
    FaltaEntity toEntity(CreateFaltaRequestDto dto);

    @Mappings({
        @Mapping(source = "clasificacion_falta_id", target = "clasificacionFaltaId"),
        @Mapping(source = "caducidad_dias", target = "caducidadDias"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    FaltaResponseDto toResponseDto(FaltaEntity entity);
}
