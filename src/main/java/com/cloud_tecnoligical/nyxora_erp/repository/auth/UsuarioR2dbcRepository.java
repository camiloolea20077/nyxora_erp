package com.cloud_tecnoligical.nyxora_erp.repository.auth;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.cloud_tecnoligical.nyxora_erp.entity.UsuarioEntity;

/**
 * Repositorio reactivo MÍNIMO (save/findById heredados). Todo filtro va en AuthQueryRepository.
 */
public interface UsuarioR2dbcRepository extends ReactiveCrudRepository<UsuarioEntity, Long> {
    // Intencionalmente vacío.
}
