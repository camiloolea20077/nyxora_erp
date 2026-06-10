package com.cloud_tecnoligical.nyxora_erp.event;

import java.time.LocalDateTime;

/**
 * Evento de dominio in-process. Viaja con el tenant (empresa/usuario/sede) para poder
 * reconstruir el TenantInfo FUERA del Reactor Context del request original.
 * Ver docs/arquitectura/adr-bus-eventos-dominio.md
 */
public interface DomainEvent {
    Long getEmpresaId();
    Long getUsuarioId();
    Long getSedeId();
    LocalDateTime getOcurridoEn();
}
