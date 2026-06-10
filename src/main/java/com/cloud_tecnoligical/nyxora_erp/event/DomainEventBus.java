package com.cloud_tecnoligical.nyxora_erp.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * Bus de eventos de dominio in-process (reactivo), basado en Reactor Sinks.
 * Multicast: cada listener se suscribe vía {@link #on(Class)} a su tipo de evento.
 * No bloqueante; best-effort en memoria (ver ADR AD-R7).
 */
@Component
public class DomainEventBus {

    private static final Logger log = LoggerFactory.getLogger(DomainEventBus.class);

    private final Sinks.Many<DomainEvent> sink = Sinks.many().multicast().onBackpressureBuffer();

    /** Publica un evento (no bloqueante). Reintenta si hay acceso concurrente no serializado. */
    public void publish(DomainEvent event) {
        Sinks.EmitResult result = sink.tryEmitNext(event);
        if (result.isFailure() && result != Sinks.EmitResult.FAIL_ZERO_SUBSCRIBER) {
            sink.emitNext(event, (signalType, emitResult) -> emitResult == Sinks.EmitResult.FAIL_NON_SERIALIZED);
        }
        log.debug("Evento publicado: {}", event.getClass().getSimpleName());
    }

    /** Flujo filtrado por tipo de evento, para que cada listener consuma solo lo suyo. */
    public <E extends DomainEvent> Flux<E> on(Class<E> type) {
        return sink.asFlux().filter(type::isInstance).cast(type);
    }
}
