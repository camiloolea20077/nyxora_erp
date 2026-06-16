package com.cloud_tecnoligical.nyxora_erp.mapper.cuentaspagar;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar.ObligacionPagoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.ObligacionPagoEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ObligacionPagoMapper {

    @Mappings({
        @Mapping(source = "proveedor_id", target = "proveedorId"),
        @Mapping(source = "factura_proveedor_id", target = "facturaProveedorId"),
        @Mapping(source = "cuenta_id", target = "cuentaId"),
        @Mapping(source = "fecha_vencimiento", target = "fechaVencimiento"),
        @Mapping(source = "valor_total", target = "valorTotal"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt"),
        @Mapping(target = "retenciones", ignore = true)
    })
    ObligacionPagoResponseDto toResponseDto(ObligacionPagoEntity entity);
}
