package com.cloud_tecnoligical.nyxora_erp.mapper.rol;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.cloud_tecnoligical.nyxora_erp.dto.rol.CreateRolRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.rol.RolResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.RolEntity;

@Mapper(componentModel = "spring")
public interface RolMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "usuario_creacion", ignore = true),
        @Mapping(target = "usuario_modificacion", ignore = true),
        @Mapping(source = "name", target = "nombre")
    })
    RolEntity toEntity(CreateRolRequestDto dto);

    @Mappings({
        @Mapping(source = "nombre", target = "name"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt"),
        @Mapping(target = "permisoIds", ignore = true)
    })
    RolResponseDto toResponseDto(RolEntity entity);
}
