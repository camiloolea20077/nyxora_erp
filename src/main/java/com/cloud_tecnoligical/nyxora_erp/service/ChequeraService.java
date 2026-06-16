package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.ChequeraResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.ChequeraTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.CreateChequeraRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.UpdateChequeraRequestDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface ChequeraService {
    Mono<ChequeraResponseDto> create(CreateChequeraRequestDto dto);
    Mono<Boolean> update(UpdateChequeraRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<ChequeraResponseDto> findById(Long id);
    Mono<PageResponseDto<ChequeraTableDto>> list(PageableDto<?> request);
}
