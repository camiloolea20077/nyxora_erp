package com.cloud_tecnoligical.nyxora_erp.mapper.compras;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.compras.CreateOrdenCompraLineaDto;
import com.cloud_tecnoligical.nyxora_erp.dto.compras.OrdenCompraLineaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.OrdenCompraLineaEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrdenCompraLineaMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "orden_compra_id", ignore = true),
        @Mapping(target = "subtotal", ignore = true),
        @Mapping(target = "total", ignore = true),
        @Mapping(target = "cantidad_recibida", ignore = true),
        @Mapping(target = "cantidad_pendiente", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(source = "productoId", target = "producto_id"),
        @Mapping(source = "productoVarianteId", target = "producto_variante_id"),
        @Mapping(source = "unidadMedidaId", target = "unidad_medida_id"),
        @Mapping(source = "valorUnitario", target = "valor_unitario"),
        @Mapping(source = "descuentoPorcentaje", target = "descuento_porcentaje"),
        @Mapping(source = "descuentoValor", target = "descuento_valor"),
        @Mapping(source = "impuestoId", target = "impuesto_id"),
        @Mapping(source = "impuestoPorcentaje", target = "impuesto_porcentaje"),
        @Mapping(source = "impuestoValor", target = "impuesto_valor"),
        @Mapping(source = "centroCostoId", target = "centro_costo_id")
    })
    OrdenCompraLineaEntity toEntity(CreateOrdenCompraLineaDto dto);

    @Mappings({
        @Mapping(source = "orden_compra_id", target = "ordenCompraId"),
        @Mapping(source = "producto_id", target = "productoId"),
        @Mapping(source = "producto_variante_id", target = "productoVarianteId"),
        @Mapping(source = "unidad_medida_id", target = "unidadMedidaId"),
        @Mapping(source = "valor_unitario", target = "valorUnitario"),
        @Mapping(source = "descuento_porcentaje", target = "descuentoPorcentaje"),
        @Mapping(source = "descuento_valor", target = "descuentoValor"),
        @Mapping(source = "impuesto_id", target = "impuestoId"),
        @Mapping(source = "impuesto_porcentaje", target = "impuestoPorcentaje"),
        @Mapping(source = "impuesto_valor", target = "impuestoValor"),
        @Mapping(source = "cantidad_recibida", target = "cantidadRecibida"),
        @Mapping(source = "cantidad_pendiente", target = "cantidadPendiente"),
        @Mapping(source = "centro_costo_id", target = "centroCostoId")
    })
    OrdenCompraLineaResponseDto toResponseDto(OrdenCompraLineaEntity entity);
}
