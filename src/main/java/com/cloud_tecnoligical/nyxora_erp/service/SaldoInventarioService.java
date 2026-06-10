package com.cloud_tecnoligical.nyxora_erp.service;

import java.util.List;

import com.cloud_tecnoligical.nyxora_erp.dto.inventario.SaldoInventarioResponseDto;

import reactor.core.publisher.Mono;

public interface SaldoInventarioService {
    Mono<Long> recalcular(Long bodegaId);
    Mono<List<SaldoInventarioResponseDto>> consultar(Long bodegaId, Long productoId);
}
