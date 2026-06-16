package com.cloud_tecnoligical.nyxora_erp.mapper.facturacion;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.FacturaDianResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.FacturaDianEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FacturaDianMapper {

    @Mappings({
        @Mapping(source = "factura_id", target = "facturaId"),
        @Mapping(source = "estado_dian", target = "estadoDian"),
        @Mapping(source = "fecha_acuse", target = "fechaAcuse"),
        @Mapping(source = "comentario_acuse", target = "comentarioAcuse"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    FacturaDianResponseDto toResponseDto(FacturaDianEntity entity);
}
