package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.CpcResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.CpcTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.CreateCpcRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.UpdateCpcRequestDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface CpcService {
    Mono<CpcResponseDto> create(CreateCpcRequestDto dto);
    Mono<Boolean> update(UpdateCpcRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<CpcResponseDto> findById(Long id);
    Mono<PageResponseDto<CpcTableDto>> list(PageableDto<?> request);
}
