package com.cloud_tecnoligical.nyxora_erp.service;

import java.util.List;

import com.cloud_tecnoligical.nyxora_erp.dto.producto.CreateProductoProveedorDto;
import com.cloud_tecnoligical.nyxora_erp.dto.producto.CreateProductoVarianteDto;
import com.cloud_tecnoligical.nyxora_erp.dto.producto.ProductoProveedorResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.producto.ProductoVarianteResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.producto.UpdateProductoProveedorDto;
import com.cloud_tecnoligical.nyxora_erp.dto.producto.UpdateProductoVarianteDto;

import reactor.core.publisher.Mono;

public interface ProductoSatelitesService {
    // Variantes
    Mono<List<ProductoVarianteResponseDto>> listVariantes(Long productoId);
    Mono<ProductoVarianteResponseDto> createVariante(Long productoId, CreateProductoVarianteDto dto);
    Mono<Boolean> updateVariante(Long productoId, UpdateProductoVarianteDto dto);
    Mono<Boolean> deleteVariante(Long productoId, Long id);

    // Proveedores
    Mono<List<ProductoProveedorResponseDto>> listProveedores(Long productoId);
    Mono<ProductoProveedorResponseDto> createProveedor(Long productoId, CreateProductoProveedorDto dto);
    Mono<Boolean> updateProveedor(Long productoId, UpdateProductoProveedorDto dto);
    Mono<Boolean> deleteProveedor(Long productoId, Long id);
}
