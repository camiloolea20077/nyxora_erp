package com.cloud_tecnoligical.nyxora_erp.service;

import java.util.List;

import com.cloud_tecnoligical.nyxora_erp.dto.reportes.BalanceGeneralDto;
import com.cloud_tecnoligical.nyxora_erp.dto.reportes.CarteraTerceroDto;
import com.cloud_tecnoligical.nyxora_erp.dto.reportes.EjecucionRubroDto;
import com.cloud_tecnoligical.nyxora_erp.dto.reportes.EstadoResultadosDto;

import reactor.core.publisher.Mono;

public interface ReporteService {
    Mono<BalanceGeneralDto> balanceGeneral(Long periodoContableId);
    Mono<EstadoResultadosDto> estadoResultados(Long periodoContableId);
    Mono<List<CarteraTerceroDto>> cartera();
    Mono<List<EjecucionRubroDto>> ejecucionPresupuestal(Long vigenciaId);
}
