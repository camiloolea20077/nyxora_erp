package com.cloud_tecnoligical.nyxora_erp.mapper.talentohumano;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.CreateEmpleadoEstudioDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.EmpleadoEstudioResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.UpdateEmpleadoEstudioDto;
import com.cloud_tecnoligical.nyxora_erp.entity.EmpleadoEstudioEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EmpleadoEstudioMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "empleado_id", ignore = true),
        @Mapping(source = "nivelEstudioId", target = "nivel_estudio_id"),
        @Mapping(source = "fechaInicial", target = "fecha_inicial"),
        @Mapping(source = "fechaFinal", target = "fecha_final"),
        @Mapping(source = "fechaGrado", target = "fecha_grado"),
        @Mapping(source = "numeroTarjetaProfesional", target = "numero_tarjeta_profesional"),
        @Mapping(source = "municipioEstudioId", target = "municipio_estudio_id"),
        @Mapping(source = "semestresAprobados", target = "semestres_aprobados"),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true)
    })
    EmpleadoEstudioEntity toEntity(CreateEmpleadoEstudioDto dto);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "empleado_id", ignore = true),
        @Mapping(source = "nivelEstudioId", target = "nivel_estudio_id"),
        @Mapping(source = "fechaInicial", target = "fecha_inicial"),
        @Mapping(source = "fechaFinal", target = "fecha_final"),
        @Mapping(source = "fechaGrado", target = "fecha_grado"),
        @Mapping(source = "numeroTarjetaProfesional", target = "numero_tarjeta_profesional"),
        @Mapping(source = "municipioEstudioId", target = "municipio_estudio_id"),
        @Mapping(source = "semestresAprobados", target = "semestres_aprobados"),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true)
    })
    void updateEntityFromDto(UpdateEmpleadoEstudioDto dto, @MappingTarget EmpleadoEstudioEntity entity);

    @Mappings({
        @Mapping(source = "empleado_id", target = "empleadoId"),
        @Mapping(source = "nivel_estudio_id", target = "nivelEstudioId"),
        @Mapping(source = "fecha_inicial", target = "fechaInicial"),
        @Mapping(source = "fecha_final", target = "fechaFinal"),
        @Mapping(source = "fecha_grado", target = "fechaGrado"),
        @Mapping(source = "numero_tarjeta_profesional", target = "numeroTarjetaProfesional"),
        @Mapping(source = "municipio_estudio_id", target = "municipioEstudioId"),
        @Mapping(source = "semestres_aprobados", target = "semestresAprobados"),
        @Mapping(source = "activo", target = "active")
    })
    EmpleadoEstudioResponseDto toResponseDto(EmpleadoEstudioEntity entity);
}
