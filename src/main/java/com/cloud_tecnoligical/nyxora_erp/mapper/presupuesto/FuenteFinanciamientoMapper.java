package com.cloud_tecnoligical.nyxora_erp.mapper.presupuesto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.CreateFuenteFinanciamientoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.FuenteFinanciamientoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.FuenteFinanciamientoEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FuenteFinanciamientoMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(source = "tipoRecurso", target = "tipo_recurso")
    })
    FuenteFinanciamientoEntity toEntity(CreateFuenteFinanciamientoRequestDto dto);

    @Mappings({
        @Mapping(source = "tipo_recurso", target = "tipoRecurso"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    FuenteFinanciamientoResponseDto toResponseDto(FuenteFinanciamientoEntity entity);
}
