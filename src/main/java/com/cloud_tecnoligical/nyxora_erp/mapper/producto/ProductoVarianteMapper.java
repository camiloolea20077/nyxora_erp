package com.cloud_tecnoligical.nyxora_erp.mapper.producto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.producto.CreateProductoVarianteDto;
import com.cloud_tecnoligical.nyxora_erp.dto.producto.ProductoVarianteResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.producto.UpdateProductoVarianteDto;
import com.cloud_tecnoligical.nyxora_erp.entity.ProductoVarianteEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductoVarianteMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "producto_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(source = "skuPlu", target = "sku_plu"),
        @Mapping(source = "codigoBarra", target = "codigo_barra"),
        @Mapping(source = "precioAdicional", target = "precio_adicional")
    })
    ProductoVarianteEntity toEntity(CreateProductoVarianteDto dto);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "producto_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(source = "skuPlu", target = "sku_plu"),
        @Mapping(source = "codigoBarra", target = "codigo_barra"),
        @Mapping(source = "precioAdicional", target = "precio_adicional")
    })
    void updateEntityFromDto(UpdateProductoVarianteDto dto, @MappingTarget ProductoVarianteEntity entity);

    @Mappings({
        @Mapping(source = "producto_id", target = "productoId"),
        @Mapping(source = "sku_plu", target = "skuPlu"),
        @Mapping(source = "codigo_barra", target = "codigoBarra"),
        @Mapping(source = "precio_adicional", target = "precioAdicional"),
        @Mapping(source = "activo", target = "active")
    })
    ProductoVarianteResponseDto toResponseDto(ProductoVarianteEntity entity);
}
