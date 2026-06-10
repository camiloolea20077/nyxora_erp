package com.cloud_tecnoligical.nyxora_erp.repository.inventario;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.cloud_tecnoligical.nyxora_erp.entity.MovimientoInventarioEntity;

public interface MovimientoInventarioR2dbcRepository extends ReactiveCrudRepository<MovimientoInventarioEntity, Long> {
}
