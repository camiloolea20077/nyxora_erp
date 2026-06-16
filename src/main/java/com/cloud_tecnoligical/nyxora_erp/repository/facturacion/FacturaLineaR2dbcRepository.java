package com.cloud_tecnoligical.nyxora_erp.repository.facturacion;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.cloud_tecnoligical.nyxora_erp.entity.FacturaLineaEntity;

public interface FacturaLineaR2dbcRepository extends ReactiveCrudRepository<FacturaLineaEntity, Long> {
}
