package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.CreateFacturaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.EmitirFacturaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.FacturaDianResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.FacturaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.FacturaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.RegistrarFacturaDianRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.facturacion.UpdateFacturaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface FacturaService {
    Mono<FacturaResponseDto> create(CreateFacturaRequestDto dto);
    Mono<Boolean> update(UpdateFacturaRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<FacturaResponseDto> findById(Long id);
    Mono<PageResponseDto<FacturaTableDto>> list(PageableDto<?> request);
    Mono<FacturaResponseDto> emitir(Long id, EmitirFacturaRequestDto params);
    Mono<Boolean> anular(Long id);
    Mono<FacturaDianResponseDto> registrarDian(Long id, RegistrarFacturaDianRequestDto dto);
}
