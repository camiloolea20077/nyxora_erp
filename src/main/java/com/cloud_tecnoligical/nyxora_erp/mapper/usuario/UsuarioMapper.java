package com.cloud_tecnoligical.nyxora_erp.mapper.usuario;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.cloud_tecnoligical.nyxora_erp.dto.usuario.CreateUsuarioRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.usuario.UsuarioResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.UsuarioEntity;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "hash_password", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "usuario_creacion", ignore = true),
        @Mapping(target = "usuario_modificacion", ignore = true),
        @Mapping(source = "terceroId", target = "tercero_id")
        // username y email se mapean por nombre (igual en DTO y entity)
    })
    UsuarioEntity toEntity(CreateUsuarioRequestDto dto);

    @Mappings({
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt"),
        @Mapping(source = "tercero_id", target = "terceroId"),
        @Mapping(target = "terceroNombre", ignore = true)  // se llena en consultas con JOIN
        // username y email por nombre
    })
    UsuarioResponseDto toResponseDto(UsuarioEntity entity);
}
