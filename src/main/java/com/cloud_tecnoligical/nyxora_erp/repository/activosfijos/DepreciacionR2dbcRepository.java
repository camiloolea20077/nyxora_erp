package com.cloud_tecnoligical.nyxora_erp.repository.activosfijos;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.cloud_tecnoligical.nyxora_erp.entity.DepreciacionEntity;

public interface DepreciacionR2dbcRepository
        extends ReactiveCrudRepository<DepreciacionEntity, Long> {
}
