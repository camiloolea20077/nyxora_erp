package com.cloud_tecnoligical.nyxora_erp.mapper.compras;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.compras.OrdenCompraResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.OrdenCompraEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrdenCompraMapper {

    @Mappings({
        @Mapping(source = "sede_id", target = "sedeId"),
        @Mapping(source = "vigencia_id", target = "vigenciaId"),
        @Mapping(source = "tipo_documento_id", target = "tipoDocumentoId"),
        @Mapping(source = "proveedor_id", target = "proveedorId"),
        @Mapping(source = "bodega_id", target = "bodegaId"),
        @Mapping(source = "centro_costo_id", target = "centroCostoId"),
        @Mapping(source = "condicion_pago_id", target = "condicionPagoId"),
        @Mapping(source = "fecha_entrega", target = "fechaEntrega"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt"),
        @Mapping(target = "lineas", ignore = true)
    })
    OrdenCompraResponseDto toResponseDto(OrdenCompraEntity entity);
}
