package com.cloud_tecnoligical.nyxora_erp.mapper.nomina;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.nomina.CargoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.CreateCargoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.CargoEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CargoMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(source = "nivelCargo", target = "nivel_cargo"),
        @Mapping(source = "tipoRemuneracion", target = "tipo_remuneracion"),
        @Mapping(source = "sueldoBasico", target = "sueldo_basico"),
        @Mapping(source = "sueldoMaximo", target = "sueldo_maximo"),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "usuario_creacion", ignore = true),
        @Mapping(target = "usuario_modificacion", ignore = true)
    })
    CargoEntity toEntity(CreateCargoRequestDto dto);

    @Mappings({
        @Mapping(source = "nivel_cargo", target = "nivelCargo"),
        @Mapping(source = "tipo_remuneracion", target = "tipoRemuneracion"),
        @Mapping(source = "sueldo_basico", target = "sueldoBasico"),
        @Mapping(source = "sueldo_maximo", target = "sueldoMaximo"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    CargoResponseDto toResponseDto(CargoEntity entity);
}
