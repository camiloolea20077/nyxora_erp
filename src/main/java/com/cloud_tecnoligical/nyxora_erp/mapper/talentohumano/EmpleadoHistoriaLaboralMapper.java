package com.cloud_tecnoligical.nyxora_erp.mapper.talentohumano;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.CreateEmpleadoHistoriaLaboralDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.EmpleadoHistoriaLaboralResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.UpdateEmpleadoHistoriaLaboralDto;
import com.cloud_tecnoligical.nyxora_erp.entity.EmpleadoHistoriaLaboralEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EmpleadoHistoriaLaboralMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "empleado_id", ignore = true),
        @Mapping(source = "nombreEmpresa", target = "nombre_empresa"),
        @Mapping(source = "tipoContrato", target = "tipo_contrato"),
        @Mapping(source = "fechaInicio", target = "fecha_inicio"),
        @Mapping(source = "fechaFinal", target = "fecha_final"),
        @Mapping(source = "jefeInmediato", target = "jefe_inmediato"),
        @Mapping(source = "municipioId", target = "municipio_id"),
        @Mapping(source = "esPublico", target = "es_publico"),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true)
    })
    EmpleadoHistoriaLaboralEntity toEntity(CreateEmpleadoHistoriaLaboralDto dto);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "empleado_id", ignore = true),
        @Mapping(source = "nombreEmpresa", target = "nombre_empresa"),
        @Mapping(source = "tipoContrato", target = "tipo_contrato"),
        @Mapping(source = "fechaInicio", target = "fecha_inicio"),
        @Mapping(source = "fechaFinal", target = "fecha_final"),
        @Mapping(source = "jefeInmediato", target = "jefe_inmediato"),
        @Mapping(source = "municipioId", target = "municipio_id"),
        @Mapping(source = "esPublico", target = "es_publico"),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true)
    })
    void updateEntityFromDto(UpdateEmpleadoHistoriaLaboralDto dto, @MappingTarget EmpleadoHistoriaLaboralEntity entity);

    @Mappings({
        @Mapping(source = "empleado_id", target = "empleadoId"),
        @Mapping(source = "nombre_empresa", target = "nombreEmpresa"),
        @Mapping(source = "tipo_contrato", target = "tipoContrato"),
        @Mapping(source = "fecha_inicio", target = "fechaInicio"),
        @Mapping(source = "fecha_final", target = "fechaFinal"),
        @Mapping(source = "jefe_inmediato", target = "jefeInmediato"),
        @Mapping(source = "municipio_id", target = "municipioId"),
        @Mapping(source = "es_publico", target = "esPublico")
    })
    EmpleadoHistoriaLaboralResponseDto toResponseDto(EmpleadoHistoriaLaboralEntity entity);
}
