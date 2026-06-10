package com.cloud_tecnoligical.nyxora_erp.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cloud_tecnoligical.nyxora_erp.dto.contabilidad.CreateComprobanteRequestDto;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.security.TenantInfo;
import com.cloud_tecnoligical.nyxora_erp.service.ComprobanteService;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;

/**
 * Interfaz contable: consume {@link AsientoContableSolicitado} del bus de dominio y genera
 * (crea + confirma) un comprobante. Corre FUERA del Reactor Context del request original, por eso
 * inyecta el TenantInfo al contexto desde el propio evento. Errores se registran sin tumbar el bus.
 */
@Component
public class InterfazContableListener {

    private static final Logger log = LoggerFactory.getLogger(InterfazContableListener.class);

    private final DomainEventBus bus;
    private final ComprobanteService comprobanteService;

    public InterfazContableListener(DomainEventBus bus, ComprobanteService comprobanteService) {
        this.bus = bus;
        this.comprobanteService = comprobanteService;
    }

    @PostConstruct
    public void suscribir() {
        bus.on(AsientoContableSolicitado.class)
            .flatMap(ev -> procesar(ev)
                .doOnError(e -> log.error("Interfaz contable falló para origen {}#{}: {}",
                    ev.getOrigenModulo(), ev.getOrigenId(), e.getMessage()))
                .onErrorResume(e -> Mono.empty()))
            .subscribe();
        log.info("InterfazContableListener suscrito al bus de eventos de dominio");
    }

    private Mono<Void> procesar(AsientoContableSolicitado ev) {
        CreateComprobanteRequestDto dto = new CreateComprobanteRequestDto();
        dto.setPeriodoContableId(ev.getPeriodoContableId());
        dto.setFecha(ev.getFecha());
        dto.setDescripcion(ev.getDescripcion());
        dto.setOrigenModulo(ev.getOrigenModulo());
        dto.setOrigenId(ev.getOrigenId());
        dto.setMovimientos(ev.getMovimientos());

        TenantInfo tenant = new TenantInfo(ev.getEmpresaId(), ev.getUsuarioId(), ev.getSedeId(), false);

        return comprobanteService.crearYConfirmar(dto)
            .doOnSuccess(c -> log.info("Comprobante {} generado por interfaz contable desde {}#{}",
                c.getId(), ev.getOrigenModulo(), ev.getOrigenId()))
            .contextWrite(TenantContext.write(tenant))
            .then();
    }
}
