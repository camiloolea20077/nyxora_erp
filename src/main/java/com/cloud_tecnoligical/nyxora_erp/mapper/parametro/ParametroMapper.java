package com.cloud_tecnoligical.nyxora_erp.mapper.parametro;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.cloud_tecnoligical.nyxora_erp.dto.parametro.CreateParametroRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.parametro.ParametroResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.ParametroEntity;

@Mapper(componentModel = "spring")
public interface ParametroMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "tipo_dato", ignore = true),   // se resuelve en el service (default 'string')
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "usuario_creacion", ignore = true),
        @Mapping(target = "usuario_modificacion", ignore = true),
        @Mapping(source = "key", target = "clave"),
        @Mapping(source = "value", target = "valor")
    })
    ParametroEntity toEntity(CreateParametroRequestDto dto);

    @Mappings({
        @Mapping(source = "clave", target = "key"),
        @Mapping(source = "valor", target = "value"),
        @Mapping(source = "tipo_dato", target = "dataType"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    ParametroResponseDto toResponseDto(ParametroEntity entity);
}
