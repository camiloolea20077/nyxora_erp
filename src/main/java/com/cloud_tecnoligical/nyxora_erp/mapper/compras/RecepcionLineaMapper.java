package com.cloud_tecnoligical.nyxora_erp.mapper.compras;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.compras.CreateRecepcionLineaDto;
import com.cloud_tecnoligical.nyxora_erp.dto.compras.RecepcionLineaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.RecepcionLineaEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RecepcionLineaMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "recepcion_id", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(source = "ordenCompraLineaId", target = "orden_compra_linea_id"),
        @Mapping(source = "productoId", target = "producto_id"),
        @Mapping(source = "productoVarianteId", target = "producto_variante_id"),
        @Mapping(source = "loteId", target = "lote_id"),
        @Mapping(source = "ubicacionId", target = "ubicacion_id"),
        @Mapping(source = "cantidadRecibida", target = "cantidad_recibida"),
        @Mapping(source = "costoUnitario", target = "costo_unitario")
    })
    RecepcionLineaEntity toEntity(CreateRecepcionLineaDto dto);

    @Mappings({
        @Mapping(source = "recepcion_id", target = "recepcionId"),
        @Mapping(source = "orden_compra_linea_id", target = "ordenCompraLineaId"),
        @Mapping(source = "producto_id", target = "productoId"),
        @Mapping(source = "producto_variante_id", target = "productoVarianteId"),
        @Mapping(source = "lote_id", target = "loteId"),
        @Mapping(source = "ubicacion_id", target = "ubicacionId"),
        @Mapping(source = "cantidad_recibida", target = "cantidadRecibida"),
        @Mapping(source = "costo_unitario", target = "costoUnitario")
    })
    RecepcionLineaResponseDto toResponseDto(RecepcionLineaEntity entity);
}
