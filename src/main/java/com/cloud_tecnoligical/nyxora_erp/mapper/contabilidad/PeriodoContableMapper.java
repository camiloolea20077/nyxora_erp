package com.cloud_tecnoligical.nyxora_erp.mapper.contabilidad;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.CreatePeriodoContableRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.PeriodoContableResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.PeriodoContableEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PeriodoContableMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "estado", ignore = true),
        @Mapping(target = "fecha_cierre", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "usuario_creacion", ignore = true),
        @Mapping(target = "usuario_modificacion", ignore = true),
        @Mapping(source = "vigenciaId", target = "vigencia_id")
    })
    PeriodoContableEntity toEntity(CreatePeriodoContableRequestDto dto);

    @Mappings({
        @Mapping(source = "vigencia_id", target = "vigenciaId"),
        @Mapping(source = "fecha_cierre", target = "fechaCierre"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    PeriodoContableResponseDto toResponseDto(PeriodoContableEntity entity);
}
