package com.cloud_tecnoligical.nyxora_erp.repository.presupuesto;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.cloud_tecnoligical.nyxora_erp.entity.FuenteFinanciamientoEntity;

public interface FuenteFinanciamientoR2dbcRepository
        extends ReactiveCrudRepository<FuenteFinanciamientoEntity, Long> {
}
