package com.cloud_tecnoligical.nyxora_erp.mapper.tercero;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.tercero.CreateTerceroContactoDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.TerceroContactoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.UpdateTerceroContactoDto;
import com.cloud_tecnoligical.nyxora_erp.entity.TerceroContactoEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TerceroContactoMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "tercero_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true)
    })
    TerceroContactoEntity toEntity(CreateTerceroContactoDto dto);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "tercero_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true)
    })
    void updateEntityFromDto(UpdateTerceroContactoDto dto, @MappingTarget TerceroContactoEntity entity);

    @Mappings({
        @Mapping(source = "tercero_id", target = "terceroId"),
        @Mapping(source = "activo", target = "active")
    })
    TerceroContactoResponseDto toResponseDto(TerceroContactoEntity entity);
}
