package com.cloud_tecnoligical.nyxora_erp.mapper.cartera;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.cartera.AcuerdoPagoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.AcuerdoPagoEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AcuerdoPagoMapper {

    @Mappings({
        @Mapping(source = "cuenta_por_cobrar_id", target = "cuentaPorCobrarId"),
        @Mapping(source = "numero_cuotas", target = "numeroCuotas"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt"),
        @Mapping(target = "cuotas", ignore = true)
    })
    AcuerdoPagoResponseDto toResponseDto(AcuerdoPagoEntity entity);
}
