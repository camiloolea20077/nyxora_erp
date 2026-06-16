package com.cloud_tecnoligical.nyxora_erp.repository.cuentaspagar;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.cloud_tecnoligical.nyxora_erp.entity.ObligacionPagoRetencionEntity;

public interface ObligacionPagoRetencionR2dbcRepository
        extends ReactiveCrudRepository<ObligacionPagoRetencionEntity, Long> {
}
