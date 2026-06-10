package com.cloud_tecnoligical.nyxora_erp.service;

import java.util.List;

import com.cloud_tecnoligical.nyxora_erp.dto.inventario.CreateMovimientoInventarioDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.KardexItemDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.MovimientoInventarioResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.TrasladoInventarioDto;

import reactor.core.publisher.Mono;

public interface MovimientoInventarioService {
    Mono<MovimientoInventarioResponseDto> registrar(CreateMovimientoInventarioDto dto);
    Mono<List<MovimientoInventarioResponseDto>> traslado(TrasladoInventarioDto dto);
    Mono<MovimientoInventarioResponseDto> reversar(Long id);
    Mono<MovimientoInventarioResponseDto> findById(Long id);
    Mono<List<KardexItemDto>> kardex(Long productoId, Long bodegaId);
}
