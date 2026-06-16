package com.cloud_tecnoligical.nyxora_erp.repository.activosfijos;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.cloud_tecnoligical.nyxora_erp.entity.ActivoFijoEntity;

public interface ActivoFijoR2dbcRepository
        extends ReactiveCrudRepository<ActivoFijoEntity, Long> {
}
