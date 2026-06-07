package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.rol.CreateRolRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.rol.RolResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.rol.RolTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.rol.UpdateRolRequestDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface RolService {
    Mono<RolResponseDto> create(CreateRolRequestDto dto);
    Mono<Boolean> update(UpdateRolRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<RolResponseDto> findById(Long id);
    Mono<PageResponseDto<RolTableDto>> list(PageableDto<?> request);
}
