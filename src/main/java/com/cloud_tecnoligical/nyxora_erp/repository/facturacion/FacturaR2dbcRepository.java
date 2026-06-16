package com.cloud_tecnoligical.nyxora_erp.repository.facturacion;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.cloud_tecnoligical.nyxora_erp.entity.FacturaEntity;

public interface FacturaR2dbcRepository extends ReactiveCrudRepository<FacturaEntity, Long> {
}
