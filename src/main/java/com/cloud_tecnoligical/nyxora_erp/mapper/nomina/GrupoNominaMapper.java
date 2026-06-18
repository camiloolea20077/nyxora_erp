package com.cloud_tecnoligical.nyxora_erp.mapper.nomina;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.nomina.CreateGrupoNominaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.GrupoNominaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.GrupoNominaEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface GrupoNominaMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(source = "frecuenciaPago", target = "frecuencia_pago"),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true)
    })
    GrupoNominaEntity toEntity(CreateGrupoNominaRequestDto dto);

    @Mappings({
        @Mapping(source = "frecuencia_pago", target = "frecuenciaPago"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    GrupoNominaResponseDto toResponseDto(GrupoNominaEntity entity);
}
