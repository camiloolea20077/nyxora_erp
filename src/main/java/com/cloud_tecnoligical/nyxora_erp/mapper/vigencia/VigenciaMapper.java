package com.cloud_tecnoligical.nyxora_erp.mapper.vigencia;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.cloud_tecnoligical.nyxora_erp.dto.vigencia.CreateVigenciaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.vigencia.VigenciaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.VigenciaEntity;

@Mapper(componentModel = "spring")
public interface VigenciaMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "estado", ignore = true),
        @Mapping(target = "fecha_apertura", ignore = true),
        @Mapping(target = "fecha_cierre", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "usuario_creacion", ignore = true),
        @Mapping(target = "usuario_modificacion", ignore = true),
        @Mapping(source = "year", target = "anio")
    })
    VigenciaEntity toEntity(CreateVigenciaRequestDto dto);

    @Mappings({
        @Mapping(source = "anio", target = "year"),
        @Mapping(source = "estado", target = "status"),
        @Mapping(source = "fecha_apertura", target = "openDate"),
        @Mapping(source = "fecha_cierre", target = "closeDate"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    VigenciaResponseDto toResponseDto(VigenciaEntity entity);
}
