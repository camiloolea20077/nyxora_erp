package com.cloud_tecnoligical.nyxora_erp.mapper.inventario;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.inventario.CreateMarcaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.MarcaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.UpdateMarcaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.MarcaEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MarcaMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true)
    })
    MarcaEntity toEntity(CreateMarcaRequestDto dto);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true)
    })
    void updateEntityFromDto(UpdateMarcaRequestDto dto, @MappingTarget MarcaEntity entity);

    @Mappings({
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    MarcaResponseDto toResponseDto(MarcaEntity entity);
}
