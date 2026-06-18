package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.CargoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.CargoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.CreateCargoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.UpdateCargoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface CargoService {
    Mono<CargoResponseDto> create(CreateCargoRequestDto dto);
    Mono<Boolean> update(UpdateCargoRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<CargoResponseDto> findById(Long id);
    Mono<PageResponseDto<CargoTableDto>> list(PageableDto<?> request);
}
