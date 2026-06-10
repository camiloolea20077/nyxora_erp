package com.cloud_tecnoligical.nyxora_erp.mapper.producto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.cloud_tecnoligical.nyxora_erp.dto.producto.CreateProductoProveedorDto;
import com.cloud_tecnoligical.nyxora_erp.dto.producto.ProductoProveedorResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.producto.UpdateProductoProveedorDto;
import com.cloud_tecnoligical.nyxora_erp.entity.ProductoProveedorEntity;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductoProveedorMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "producto_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(source = "proveedorId", target = "proveedor_id"),
        @Mapping(source = "codigoProducto", target = "codigo_producto"),
        @Mapping(source = "cantidadMinima", target = "cantidad_minima"),
        @Mapping(source = "plazoEntrega", target = "plazo_entrega")
    })
    ProductoProveedorEntity toEntity(CreateProductoProveedorDto dto);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "producto_id", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(source = "proveedorId", target = "proveedor_id"),
        @Mapping(source = "codigoProducto", target = "codigo_producto"),
        @Mapping(source = "cantidadMinima", target = "cantidad_minima"),
        @Mapping(source = "plazoEntrega", target = "plazo_entrega")
    })
    void updateEntityFromDto(UpdateProductoProveedorDto dto, @MappingTarget ProductoProveedorEntity entity);

    @Mappings({
        @Mapping(source = "producto_id", target = "productoId"),
        @Mapping(source = "proveedor_id", target = "proveedorId"),
        @Mapping(source = "codigo_producto", target = "codigoProducto"),
        @Mapping(source = "cantidad_minima", target = "cantidadMinima"),
        @Mapping(source = "plazo_entrega", target = "plazoEntrega"),
        @Mapping(source = "activo", target = "active")
    })
    ProductoProveedorResponseDto toResponseDto(ProductoProveedorEntity entity);
}
