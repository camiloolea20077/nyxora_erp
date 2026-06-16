package com.cloud_tecnoligical.nyxora_erp.service;

import java.util.List;

import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.ApropiarRubroRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.SaldoPresupuestalResponseDto;

import reactor.core.publisher.Mono;

public interface SaldoPresupuestalService {
    /** Define/ajusta la apropiación del rubro (mes 0 = anual). */
    Mono<SaldoPresupuestalResponseDto> apropiar(ApropiarRubroRequestDto dto);
    /** Reconstruye la ejecución (disponibilidad/compromiso/obligación/pagado/...) desde las afectaciones. */
    Mono<SaldoPresupuestalResponseDto> recalcular(Long rubroId, Integer anio);
    Mono<SaldoPresupuestalResponseDto> findByRubroAnio(Long rubroId, Integer anio);
    Mono<List<SaldoPresupuestalResponseDto>> listByRubro(Long rubroId);
}
