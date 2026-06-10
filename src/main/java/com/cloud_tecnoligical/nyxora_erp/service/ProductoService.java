package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.producto.CreateProductoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.producto.ProductoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.producto.ProductoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.producto.UpdateProductoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface ProductoService {
    Mono<ProductoResponseDto> create(CreateProductoRequestDto dto);
    Mono<Boolean> update(UpdateProductoRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<ProductoResponseDto> findById(Long id);
    Mono<PageResponseDto<ProductoTableDto>> list(PageableDto<?> request);
}
