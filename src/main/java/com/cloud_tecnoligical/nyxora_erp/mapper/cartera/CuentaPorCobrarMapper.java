package com.cloud_tecnoligical.nyxora_erp.mapper.cartera;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.cartera.CuentaPorCobrarResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.CuentaPorCobrarEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CuentaPorCobrarMapper {

    @Mappings({
        @Mapping(source = "cliente_id", target = "clienteId"),
        @Mapping(source = "factura_id", target = "facturaId"),
        @Mapping(source = "cuenta_id", target = "cuentaId"),
        @Mapping(source = "fecha_emision", target = "fechaEmision"),
        @Mapping(source = "fecha_vencimiento", target = "fechaVencimiento"),
        @Mapping(source = "valor_total", target = "valorTotal"),
        @Mapping(source = "valor_interes", target = "valorInteres"),
        @Mapping(source = "fecha_ultima_liquidacion", target = "fechaUltimaLiquidacion"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    CuentaPorCobrarResponseDto toResponseDto(CuentaPorCobrarEntity entity);
}
