package com.cloud_tecnoligical.nyxora_erp.mapper.facturacion;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.CreateFacturaLineaDto;
import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.FacturaLineaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.FacturaLineaEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FacturaLineaMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "factura_id", ignore = true),
        @Mapping(target = "subtotal", ignore = true),
        @Mapping(target = "total", ignore = true),
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
        @Mapping(source = "porcentajeImpuesto", target = "porcentaje_impuesto"),
        @Mapping(source = "valorImpuesto", target = "valor_impuesto"),
        @Mapping(source = "discriminaIva", target = "discrimina_iva"),
        @Mapping(source = "bodegaId", target = "bodega_id"),
        @Mapping(source = "loteId", target = "lote_id"),
        @Mapping(source = "centroCostoId", target = "centro_costo_id")
    })
    FacturaLineaEntity toEntity(CreateFacturaLineaDto dto);

    @Mappings({
        @Mapping(source = "factura_id", target = "facturaId"),
        @Mapping(source = "producto_id", target = "productoId"),
        @Mapping(source = "producto_variante_id", target = "productoVarianteId"),
        @Mapping(source = "unidad_medida_id", target = "unidadMedidaId"),
        @Mapping(source = "valor_unitario", target = "valorUnitario"),
        @Mapping(source = "descuento_porcentaje", target = "descuentoPorcentaje"),
        @Mapping(source = "descuento_valor", target = "descuentoValor"),
        @Mapping(source = "impuesto_id", target = "impuestoId"),
        @Mapping(source = "porcentaje_impuesto", target = "porcentajeImpuesto"),
        @Mapping(source = "valor_impuesto", target = "valorImpuesto"),
        @Mapping(source = "discrimina_iva", target = "discriminaIva"),
        @Mapping(source = "bodega_id", target = "bodegaId"),
        @Mapping(source = "lote_id", target = "loteId"),
        @Mapping(source = "centro_costo_id", target = "centroCostoId")
    })
    FacturaLineaResponseDto toResponseDto(FacturaLineaEntity entity);
}
