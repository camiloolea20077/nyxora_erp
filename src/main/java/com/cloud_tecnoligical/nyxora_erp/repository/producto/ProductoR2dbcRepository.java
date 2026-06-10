package com.cloud_tecnoligical.nyxora_erp.repository.producto;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.cloud_tecnoligical.nyxora_erp.entity.ProductoEntity;

public interface ProductoR2dbcRepository extends ReactiveCrudRepository<ProductoEntity, Long> {
}
