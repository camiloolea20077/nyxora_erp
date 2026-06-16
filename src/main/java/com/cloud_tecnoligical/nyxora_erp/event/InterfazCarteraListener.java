package com.cloud_tecnoligical.nyxora_erp.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cloud_tecnoligical.nyxora_erp.service.CuentaPorCobrarService;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;

/**
 * Interfaz de Cartera: consume {@link CuentaPorCobrarSolicitada} del bus de dominio y registra la
 * cuenta por cobrar de la factura emitida. Corre FUERA del Reactor Context del request original,
 * por eso {@code crearDesdeEvento} no depende del TenantContext (toma el tenant del propio evento).
 * Errores se registran sin tumbar el bus.
 */
@Component
public class InterfazCarteraListener {

    private static final Logger log = LoggerFactory.getLogger(InterfazCarteraListener.class);

    private final DomainEventBus bus;
    private final CuentaPorCobrarService cuentaPorCobrarService;

    public InterfazCarteraListener(DomainEventBus bus, CuentaPorCobrarService cuentaPorCobrarService) {
        this.bus = bus;
        this.cuentaPorCobrarService = cuentaPorCobrarService;
    }

    @PostConstruct
    public void suscribir() {
        bus.on(CuentaPorCobrarSolicitada.class)
            .flatMap(ev -> cuentaPorCobrarService.crearDesdeEvento(ev)
                .doOnSuccess(cxc -> log.info("CxC {} registrada por interfaz de cartera desde factura #{}",
                    cxc.getId(), ev.getFacturaId()))
                .doOnError(e -> log.error("Interfaz de cartera falló para factura #{}: {}",
                    ev.getFacturaId(), e.getMessage()))
                .onErrorResume(e -> Mono.empty()))
            .subscribe();
        log.info("InterfazCarteraListener suscrito al bus de eventos de dominio");
    }
}
