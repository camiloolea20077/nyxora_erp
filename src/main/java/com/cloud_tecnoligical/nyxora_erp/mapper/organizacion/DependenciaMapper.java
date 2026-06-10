package com.cloud_tecnoligical.nyxora_erp.mapper.organizacion;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.CreateDependenciaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.DependenciaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.organizacion.UpdateDependenciaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.DependenciaEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DependenciaMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "usuario_creacion", ignore = true),
        @Mapping(target = "usuario_modificacion", ignore = true),
        @Mapping(source = "centroCostoId", target = "centro_costo_id"),
        @Mapping(source = "dependenciaPadreId", target = "dependencia_padre_id")
    })
    DependenciaEntity toEntity(CreateDependenciaRequestDto dto);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "usuario_creacion", ignore = true),
        @Mapping(target = "usuario_modificacion", ignore = true),
        @Mapping(source = "centroCostoId", target = "centro_costo_id"),
        @Mapping(source = "dependenciaPadreId", target = "dependencia_padre_id")
    })
    void updateEntityFromDto(UpdateDependenciaRequestDto dto, @MappingTarget DependenciaEntity entity);

    @Mappings({
        @Mapping(source = "centro_costo_id", target = "centroCostoId"),
        @Mapping(source = "dependencia_padre_id", target = "dependenciaPadreId"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    DependenciaResponseDto toResponseDto(DependenciaEntity entity);
}
