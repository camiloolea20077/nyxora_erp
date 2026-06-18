package com.cloud_tecnoligical.nyxora_erp.mapper.talentohumano;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.CreateEmpleadoFamiliarDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.EmpleadoFamiliarResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.talentohumano.UpdateEmpleadoFamiliarDto;
import com.cloud_tecnoligical.nyxora_erp.entity.EmpleadoFamiliarEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EmpleadoFamiliarMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "empleado_id", ignore = true),
        @Mapping(source = "nombreApellido", target = "nombre_apellido"),
        @Mapping(source = "fechaNacimiento", target = "fecha_nacimiento"),
        @Mapping(source = "ACargo", target = "a_cargo"),
        @Mapping(source = "dependienteRetencion", target = "dependiente_retencion"),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true)
    })
    EmpleadoFamiliarEntity toEntity(CreateEmpleadoFamiliarDto dto);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "empleado_id", ignore = true),
        @Mapping(source = "nombreApellido", target = "nombre_apellido"),
        @Mapping(source = "fechaNacimiento", target = "fecha_nacimiento"),
        @Mapping(source = "ACargo", target = "a_cargo"),
        @Mapping(source = "dependienteRetencion", target = "dependiente_retencion"),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true)
    })
    void updateEntityFromDto(UpdateEmpleadoFamiliarDto dto, @MappingTarget EmpleadoFamiliarEntity entity);

    @Mappings({
        @Mapping(source = "empleado_id", target = "empleadoId"),
        @Mapping(source = "nombre_apellido", target = "nombreApellido"),
        @Mapping(source = "fecha_nacimiento", target = "fechaNacimiento"),
        @Mapping(source = "a_cargo", target = "ACargo"),
        @Mapping(source = "dependiente_retencion", target = "dependienteRetencion")
    })
    EmpleadoFamiliarResponseDto toResponseDto(EmpleadoFamiliarEntity entity);
}
