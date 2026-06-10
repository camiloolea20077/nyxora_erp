package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.compras.CreateOrdenCompraRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.compras.OrdenCompraResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.compras.OrdenCompraTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.compras.UpdateOrdenCompraRequestDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface OrdenCompraService {
    Mono<OrdenCompraResponseDto> create(CreateOrdenCompraRequestDto dto);
    Mono<Boolean> update(UpdateOrdenCompraRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<OrdenCompraResponseDto> findById(Long id);
    Mono<PageResponseDto<OrdenCompraTableDto>> list(PageableDto<?> request);
    Mono<Boolean> aprobar(Long id);
    Mono<Boolean> anular(Long id);
}
