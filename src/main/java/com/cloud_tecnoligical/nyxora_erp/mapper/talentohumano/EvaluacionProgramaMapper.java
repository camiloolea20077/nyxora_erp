package com.cloud_tecnoligical.nyxora_erp.mapper.talentohumano;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.CreateEvaluacionProgramaDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.EvaluacionProgramaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.EvaluacionProgramaEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EvaluacionProgramaMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(source = "fechaInicial", target = "fecha_inicial"),
        @Mapping(source = "fechaFinal", target = "fecha_final"),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true)
    })
    EvaluacionProgramaEntity toEntity(CreateEvaluacionProgramaDto dto);

    @Mappings({
        @Mapping(source = "fecha_inicial", target = "fechaInicial"),
        @Mapping(source = "fecha_final", target = "fechaFinal"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    EvaluacionProgramaResponseDto toResponseDto(EvaluacionProgramaEntity entity);
}
