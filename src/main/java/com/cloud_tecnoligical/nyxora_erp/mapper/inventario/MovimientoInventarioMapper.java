package com.cloud_tecnoligical.nyxora_erp.mapper.inventario;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.inventario.CreateMovimientoInventarioDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.MovimientoInventarioResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.MovimientoInventarioEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MovimientoInventarioMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "subtotal", ignore = true),
        @Mapping(target = "total", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "usuario_creacion", ignore = true),
        @Mapping(source = "bodegaId", target = "bodega_id"),
        @Mapping(source = "ubicacionId", target = "ubicacion_id"),
        @Mapping(source = "productoId", target = "producto_id"),
        @Mapping(source = "productoVarianteId", target = "producto_variante_id"),
        @Mapping(source = "loteId", target = "lote_id"),
        @Mapping(source = "costoUnitario", target = "costo_unitario"),
        @Mapping(source = "descuentoPorcentaje", target = "descuento_porcentaje"),
        @Mapping(source = "descuentoValor", target = "descuento_valor"),
        @Mapping(source = "impuestoId", target = "impuesto_id"),
        @Mapping(source = "impuestoPorcentaje", target = "impuesto_porcentaje"),
        @Mapping(source = "impuestoValor", target = "impuesto_valor"),
        @Mapping(source = "centroCostoId", target = "centro_costo_id"),
        @Mapping(source = "terceroId", target = "tercero_id"),
        @Mapping(source = "origenModulo", target = "origen_modulo"),
        @Mapping(source = "origenId", target = "origen_id")
    })
    MovimientoInventarioEntity toEntity(CreateMovimientoInventarioDto dto);

    @Mappings({
        @Mapping(source = "bodega_id", target = "bodegaId"),
        @Mapping(source = "ubicacion_id", target = "ubicacionId"),
        @Mapping(source = "producto_id", target = "productoId"),
        @Mapping(source = "producto_variante_id", target = "productoVarianteId"),
        @Mapping(source = "lote_id", target = "loteId"),
        @Mapping(source = "costo_unitario", target = "costoUnitario"),
        @Mapping(source = "descuento_porcentaje", target = "descuentoPorcentaje"),
        @Mapping(source = "descuento_valor", target = "descuentoValor"),
        @Mapping(source = "impuesto_id", target = "impuestoId"),
        @Mapping(source = "impuesto_porcentaje", target = "impuestoPorcentaje"),
        @Mapping(source = "impuesto_valor", target = "impuestoValor"),
        @Mapping(source = "centro_costo_id", target = "centroCostoId"),
        @Mapping(source = "tercero_id", target = "terceroId"),
        @Mapping(source = "origen_modulo", target = "origenModulo"),
        @Mapping(source = "origen_id", target = "origenId"),
        @Mapping(source = "created_at", target = "createdAt")
    })
    MovimientoInventarioResponseDto toResponseDto(MovimientoInventarioEntity entity);
}
