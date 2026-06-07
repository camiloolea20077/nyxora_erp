package com.cloud_tecnoligical.nyxora_erp.repository.sede;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.cloud_tecnoligical.nyxora_erp.entity.SedeEntity;

/** Repositorio reactivo MÍNIMO (save/findById). Todo filtro va en SedeQueryRepository. */
public interface SedeR2dbcRepository extends ReactiveCrudRepository<SedeEntity, Long> {
}
