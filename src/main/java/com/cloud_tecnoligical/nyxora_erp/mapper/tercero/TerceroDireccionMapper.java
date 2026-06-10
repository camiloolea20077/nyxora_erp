package com.cloud_tecnoligical.nyxora_erp.mapper.tercero;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.tercero.CreateTerceroDireccionDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.TerceroDireccionResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.UpdateTerceroDireccionDto;
import com.cloud_tecnoligical.nyxora_erp.entity.TerceroDireccionEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TerceroDireccionMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "tercero_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(source = "municipioId", target = "municipio_id"),
        @Mapping(source = "barrioId", target = "barrio_id"),
        @Mapping(source = "codigoPostal", target = "codigo_postal")
    })
    TerceroDireccionEntity toEntity(CreateTerceroDireccionDto dto);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "tercero_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(source = "municipioId", target = "municipio_id"),
        @Mapping(source = "barrioId", target = "barrio_id"),
        @Mapping(source = "codigoPostal", target = "codigo_postal")
    })
    void updateEntityFromDto(UpdateTerceroDireccionDto dto, @MappingTarget TerceroDireccionEntity entity);

    @Mappings({
        @Mapping(source = "tercero_id", target = "terceroId"),
        @Mapping(source = "municipio_id", target = "municipioId"),
        @Mapping(source = "barrio_id", target = "barrioId"),
        @Mapping(source = "codigo_postal", target = "codigoPostal"),
        @Mapping(source = "activo", target = "active")
    })
    TerceroDireccionResponseDto toResponseDto(TerceroDireccionEntity entity);
}
