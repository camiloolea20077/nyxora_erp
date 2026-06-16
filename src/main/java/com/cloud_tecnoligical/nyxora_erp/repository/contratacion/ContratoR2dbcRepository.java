package com.cloud_tecnoligical.nyxora_erp.repository.contratacion;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.cloud_tecnoligical.nyxora_erp.entity.ContratoEntity;

public interface ContratoR2dbcRepository
        extends ReactiveCrudRepository<ContratoEntity, Long> {
}
