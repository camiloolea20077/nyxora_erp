package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.empresa.CreateEmpresaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.empresa.EmpresaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.empresa.EmpresaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.empresa.UpdateEmpresaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface EmpresaService {
    Mono<EmpresaResponseDto> create(CreateEmpresaRequestDto dto);   // super-admin
    Mono<Boolean> update(UpdateEmpresaRequestDto dto);              // propia o super-admin
    Mono<EmpresaResponseDto> findById(Long id);                    // propia o super-admin
    Mono<EmpresaResponseDto> findActual();                         // la empresa del usuario logueado
    Mono<PageResponseDto<EmpresaTableDto>> list(PageableDto<?> request); // super-admin
}
