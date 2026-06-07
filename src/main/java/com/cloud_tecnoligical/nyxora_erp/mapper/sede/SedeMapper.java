package com.cloud_tecnoligical.nyxora_erp.mapper.sede;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

import com.cloud_tecnoligical.nyxora_erp.dto.sede.CreateSedeRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.sede.SedeResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.sede.UpdateSedeRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.SedeEntity;

@Mapper(componentModel = "spring")
public interface SedeMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "usuario_creacion", ignore = true),
        @Mapping(target = "usuario_modificacion", ignore = true),
        @Mapping(source = "code", target = "codigo"),
        @Mapping(source = "name", target = "nombre")
    })
    SedeEntity toEntity(CreateSedeRequestDto dto);

    @Mappings({
        @Mapping(source = "codigo", target = "code"),
        @Mapping(source = "nombre", target = "name"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    SedeResponseDto toResponseDto(SedeEntity entity);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "usuario_creacion", ignore = true),
        @Mapping(target = "usuario_modificacion", ignore = true),
        @Mapping(source = "code", target = "codigo"),
        @Mapping(source = "name", target = "nombre")
    })
    void updateEntityFromDto(UpdateSedeRequestDto dto, @MappingTarget SedeEntity entity);
}
