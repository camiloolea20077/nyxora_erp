package com.cloud_tecnoligical.nyxora_erp.mapper.producto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.producto.CreateProductoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.producto.ProductoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.producto.UpdateProductoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.ProductoEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductoMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "usuario_creacion", ignore = true),
        @Mapping(target = "usuario_modificacion", ignore = true),
        @Mapping(source = "categoriaId", target = "categoria_id"),
        @Mapping(source = "codigoUnspsc", target = "codigo_unspsc"),
        @Mapping(source = "esCompuesto", target = "es_compuesto"),
        @Mapping(source = "unidadMayorId", target = "unidad_mayor_id"),
        @Mapping(source = "unidadMenorId", target = "unidad_menor_id"),
        @Mapping(source = "manejaInventario", target = "maneja_inventario"),
        @Mapping(source = "manejaLote", target = "maneja_lote"),
        @Mapping(source = "manejaDesperdicio", target = "maneja_desperdicio"),
        @Mapping(source = "esDevolutivo", target = "es_devolutivo"),
        @Mapping(source = "stockMinimo", target = "stock_minimo"),
        @Mapping(source = "stockMaximo", target = "stock_maximo"),
        @Mapping(source = "tiempoReabastecimiento", target = "tiempo_reabastecimiento"),
        @Mapping(source = "impuestoId", target = "impuesto_id"),
        @Mapping(source = "discriminaIva", target = "discrimina_iva"),
        @Mapping(source = "aplicaImpuestoBolsa", target = "aplica_impuesto_bolsa"),
        @Mapping(source = "tarifaMaxima", target = "tarifa_maxima"),
        @Mapping(source = "esPos", target = "es_pos"),
        @Mapping(source = "recursoId", target = "recurso_id")
        // codigo, nombre, descripcion, tipo, contenido por nombre igual
    })
    ProductoEntity toEntity(CreateProductoRequestDto dto);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "empresa_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "usuario_creacion", ignore = true),
        @Mapping(target = "usuario_modificacion", ignore = true),
        @Mapping(source = "categoriaId", target = "categoria_id"),
        @Mapping(source = "codigoUnspsc", target = "codigo_unspsc"),
        @Mapping(source = "esCompuesto", target = "es_compuesto"),
        @Mapping(source = "unidadMayorId", target = "unidad_mayor_id"),
        @Mapping(source = "unidadMenorId", target = "unidad_menor_id"),
        @Mapping(source = "manejaInventario", target = "maneja_inventario"),
        @Mapping(source = "manejaLote", target = "maneja_lote"),
        @Mapping(source = "manejaDesperdicio", target = "maneja_desperdicio"),
        @Mapping(source = "esDevolutivo", target = "es_devolutivo"),
        @Mapping(source = "stockMinimo", target = "stock_minimo"),
        @Mapping(source = "stockMaximo", target = "stock_maximo"),
        @Mapping(source = "tiempoReabastecimiento", target = "tiempo_reabastecimiento"),
        @Mapping(source = "impuestoId", target = "impuesto_id"),
        @Mapping(source = "discriminaIva", target = "discrimina_iva"),
        @Mapping(source = "aplicaImpuestoBolsa", target = "aplica_impuesto_bolsa"),
        @Mapping(source = "tarifaMaxima", target = "tarifa_maxima"),
        @Mapping(source = "esPos", target = "es_pos"),
        @Mapping(source = "recursoId", target = "recurso_id")
    })
    void updateEntityFromDto(UpdateProductoRequestDto dto, @MappingTarget ProductoEntity entity);

    @Mappings({
        @Mapping(source = "categoria_id", target = "categoriaId"),
        @Mapping(source = "codigo_unspsc", target = "codigoUnspsc"),
        @Mapping(source = "es_compuesto", target = "esCompuesto"),
        @Mapping(source = "unidad_mayor_id", target = "unidadMayorId"),
        @Mapping(source = "unidad_menor_id", target = "unidadMenorId"),
        @Mapping(source = "maneja_inventario", target = "manejaInventario"),
        @Mapping(source = "maneja_lote", target = "manejaLote"),
        @Mapping(source = "maneja_desperdicio", target = "manejaDesperdicio"),
        @Mapping(source = "es_devolutivo", target = "esDevolutivo"),
        @Mapping(source = "stock_minimo", target = "stockMinimo"),
        @Mapping(source = "stock_maximo", target = "stockMaximo"),
        @Mapping(source = "tiempo_reabastecimiento", target = "tiempoReabastecimiento"),
        @Mapping(source = "impuesto_id", target = "impuestoId"),
        @Mapping(source = "discrimina_iva", target = "discriminaIva"),
        @Mapping(source = "aplica_impuesto_bolsa", target = "aplicaImpuestoBolsa"),
        @Mapping(source = "tarifa_maxima", target = "tarifaMaxima"),
        @Mapping(source = "es_pos", target = "esPos"),
        @Mapping(source = "recurso_id", target = "recursoId"),
        @Mapping(source = "activo", target = "active"),
        @Mapping(source = "created_at", target = "createdAt"),
        @Mapping(target = "impuestoIds", ignore = true)  // se llena en el service
    })
    ProductoResponseDto toResponseDto(ProductoEntity entity);
}
