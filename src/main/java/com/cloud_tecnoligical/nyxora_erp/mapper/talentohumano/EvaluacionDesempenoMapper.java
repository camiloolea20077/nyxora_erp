package com.cloud_tecnoligical.nyxora_erp.mapper.talentohumano;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.CreateEvaluacionDesempenoDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.EvaluacionDesempenoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.UpdateEvaluacionDesempenoDto;
import com.cloud_tecnoligical.nyxora_erp.entity.EvaluacionDesempenoEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EvaluacionDesempenoMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(source = "evaluacionProgramaId", target = "evaluacion_programa_id"),
        @Mapping(source = "empleadoId", target = "empleado_id"),
        @Mapping(source = "tipoEvaluacion", target = "tipo_evaluacion"),
        @Mapping(source = "fechaInicial", target = "fecha_inicial"),
        @Mapping(source = "fechaFinal", target = "fecha_final"),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true)
    })
    EvaluacionDesempenoEntity toEntity(CreateEvaluacionDesempenoDto dto);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(source = "evaluacionProgramaId", target = "evaluacion_programa_id"),
        @Mapping(source = "empleadoId", target = "empleado_id"),
        @Mapping(source = "tipoEvaluacion", target = "tipo_evaluacion"),
        @Mapping(source = "fechaInicial", target = "fecha_inicial"),
        @Mapping(source = "fechaFinal", target = "fecha_final"),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true)
    })
    void updateEntityFromDto(UpdateEvaluacionDesempenoDto dto, @MappingTarget EvaluacionDesempenoEntity entity);

    @Mappings({
        @Mapping(source = "evaluacion_programa_id", target = "evaluacionProgramaId"),
        @Mapping(source = "empleado_id", target = "empleadoId"),
        @Mapping(source = "tipo_evaluacion", target = "tipoEvaluacion"),
        @Mapping(source = "fecha_inicial", target = "fechaInicial"),
        @Mapping(source = "fecha_final", target = "fechaFinal"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    EvaluacionDesempenoResponseDto toResponseDto(EvaluacionDesempenoEntity entity);
}
