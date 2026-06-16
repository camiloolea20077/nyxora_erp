package com.cloud_tecnoligical.nyxora_erp.mapper.contratacion;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.CreateModalidadContratoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.ModalidadContratoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.ModalidadContratoEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ModalidadContratoMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true)
    })
    ModalidadContratoEntity toEntity(CreateModalidadContratoRequestDto dto);

    @Mappings({
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    ModalidadContratoResponseDto toResponseDto(ModalidadContratoEntity entity);
}
