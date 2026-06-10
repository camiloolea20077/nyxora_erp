package com.cloud_tecnoligical.nyxora_erp.mapper.inventario;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.inventario.BodegaResponsableResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.CreateBodegaResponsableDto;
import com.cloud_tecnoligical.nyxora_erp.entity.BodegaResponsableEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BodegaResponsableMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "bodega_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(source = "terceroId", target = "tercero_id")
    })
    BodegaResponsableEntity toEntity(CreateBodegaResponsableDto dto);

    @Mappings({
        @Mapping(source = "bodega_id", target = "bodegaId"),
        @Mapping(source = "tercero_id", target = "terceroId"),
        @Mapping(source = "activo", target = "active")
    })
    BodegaResponsableResponseDto toResponseDto(BodegaResponsableEntity entity);
}
