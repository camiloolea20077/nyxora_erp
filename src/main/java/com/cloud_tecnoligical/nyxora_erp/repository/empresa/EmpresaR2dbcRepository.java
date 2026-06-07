package com.cloud_tecnoligical.nyxora_erp.repository.empresa;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.cloud_tecnoligical.nyxora_erp.entity.EmpresaEntity;

public interface EmpresaR2dbcRepository extends ReactiveCrudRepository<EmpresaEntity, Long> {
}
