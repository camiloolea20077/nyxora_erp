package com.cloud_tecnoligical.nyxora_erp.mapper.inventario;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.inventario.CreateUbicacionRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.UbicacionResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.UpdateUbicacionRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.UbicacionEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UbicacionMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(source = "bodegaId", target = "bodega_id"),
        @Mapping(source = "ubicacionPadreId", target = "ubicacion_padre_id")
    })
    UbicacionEntity toEntity(CreateUbicacionRequestDto dto);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(source = "bodegaId", target = "bodega_id"),
        @Mapping(source = "ubicacionPadreId", target = "ubicacion_padre_id")
    })
    void updateEntityFromDto(UpdateUbicacionRequestDto dto, @MappingTarget UbicacionEntity entity);

    @Mappings({
        @Mapping(source = "bodega_id", target = "bodegaId"),
        @Mapping(source = "ubicacion_padre_id", target = "ubicacionPadreId"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    UbicacionResponseDto toResponseDto(UbicacionEntity entity);
}
