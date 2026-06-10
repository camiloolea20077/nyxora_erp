package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.documento.CreateTipoDocumentoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.documento.TipoDocumentoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.documento.TipoDocumentoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.documento.UpdateTipoDocumentoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface TipoDocumentoService {
    Mono<TipoDocumentoResponseDto> create(CreateTipoDocumentoRequestDto dto);
    Mono<Boolean> update(UpdateTipoDocumentoRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<TipoDocumentoResponseDto> findById(Long id);
    Mono<PageResponseDto<TipoDocumentoTableDto>> list(PageableDto<?> request);
}
