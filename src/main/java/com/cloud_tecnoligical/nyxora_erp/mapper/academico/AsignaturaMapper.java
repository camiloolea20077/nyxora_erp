package com.cloud_tecnoligical.nyxora_erp.mapper.academico;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.academico.AsignaturaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.CreateAsignaturaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.AsignaturaEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AsignaturaMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(source = "centroCostoDepartamentoId", target = "centro_costo_departamento_id"),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true)
    })
    AsignaturaEntity toEntity(CreateAsignaturaRequestDto dto);

    @Mappings({
        @Mapping(source = "centro_costo_departamento_id", target = "centroCostoDepartamentoId"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    AsignaturaResponseDto toResponseDto(AsignaturaEntity entity);
}
