package com.cloud_tecnoligical.nyxora_erp.mapper.organizacion;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.CreateProyectoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.ProyectoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.UpdateProyectoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.ProyectoEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProyectoMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "usuario_creacion", ignore = true),
        @Mapping(target = "usuario_modificacion", ignore = true),
        @Mapping(source = "programaId", target = "programa_id"),
        @Mapping(source = "fechaInicio", target = "fecha_inicio"),
        @Mapping(source = "fechaFinal", target = "fecha_final")
    })
    ProyectoEntity toEntity(CreateProyectoRequestDto dto);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "usuario_creacion", ignore = true),
        @Mapping(target = "usuario_modificacion", ignore = true),
        @Mapping(source = "programaId", target = "programa_id"),
        @Mapping(source = "fechaInicio", target = "fecha_inicio"),
        @Mapping(source = "fechaFinal", target = "fecha_final")
    })
    void updateEntityFromDto(UpdateProyectoRequestDto dto, @MappingTarget ProyectoEntity entity);

    @Mappings({
        @Mapping(source = "programa_id", target = "programaId"),
        @Mapping(source = "fecha_inicio", target = "fechaInicio"),
        @Mapping(source = "fecha_final", target = "fechaFinal"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    ProyectoResponseDto toResponseDto(ProyectoEntity entity);
}
