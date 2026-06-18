package com.cloud_tecnoligical.nyxora_erp.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cloud_tecnoligical.nyxora_erp.dto.nomina.CreateNovedadNominaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.security.TenantInfo;
import com.cloud_tecnoligical.nyxora_erp.service.NovedadNominaService;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;

/**
 * Interfaz de nómina docente: consume {@link CargaDocenteRegistrada} y registra una novedad_nomina
 * por las horas dictadas del catedrático. Corre fuera del Reactor Context del request original.
 */
@Component
public class InterfazNominaDocenteListener {

    private static final Logger log = LoggerFactory.getLogger(InterfazNominaDocenteListener.class);

    private final DomainEventBus bus;
    private final NovedadNominaService novedadNominaService;

    public InterfazNominaDocenteListener(DomainEventBus bus, NovedadNominaService novedadNominaService) {
        this.bus = bus;
        this.novedadNominaService = novedadNominaService;
    }

    @PostConstruct
    public void suscribir() {
        bus.on(CargaDocenteRegistrada.class)
            .flatMap(ev -> procesar(ev)
                .doOnError(e -> log.error("Interfaz nómina docente falló para carga {}: {}",
                    ev.getCargaAcademicaId(), e.getMessage()))
                .onErrorResume(e -> Mono.empty()))
            .subscribe();
        log.info("InterfazNominaDocenteListener suscrito al bus de eventos de dominio");
    }

    private Mono<Void> procesar(CargaDocenteRegistrada ev) {
        CreateNovedadNominaRequestDto dto = new CreateNovedadNominaRequestDto();
        dto.setVinculacionId(ev.getVinculacionId());
        dto.setConceptoNominaId(ev.getConceptoNominaId());
        dto.setCantidadValor(ev.getCantidadValor());
        dto.setDescripcion(ev.getDescripcion());

        TenantInfo tenant = new TenantInfo(ev.getEmpresaId(), ev.getUsuarioId(), ev.getSedeId(), false);

        return novedadNominaService.create(dto)
            .doOnSuccess(n -> log.info("Novedad {} generada por carga docente {}", n.getId(), ev.getCargaAcademicaId()))
            .contextWrite(TenantContext.write(tenant))
            .then();
    }
}
