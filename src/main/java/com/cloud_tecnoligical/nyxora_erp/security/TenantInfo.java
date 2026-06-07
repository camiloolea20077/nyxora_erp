package com.cloud_tecnoligical.nyxora_erp.security;

import lombok.Getter;
import lombok.Setter;

/**
 * Datos del tenant/usuario extraídos del JWT en cada request.
 * Se transporta por el Reactor Context (ver TenantContext), NO por ThreadLocal.
 * PLACEHOLDER: el filtro JWT que lo puebla se implementa en el agente jwt-multitenant.
 */
@Getter
@Setter
public class TenantInfo {

    private Long empresaId;
    private Long usuarioId;
    private Long sedeId;
    private boolean superAdmin;

    public TenantInfo() {
    }

    public TenantInfo(Long empresaId, Long usuarioId, Long sedeId, boolean superAdmin) {
        this.empresaId = empresaId;
        this.usuarioId = usuarioId;
        this.sedeId = sedeId;
        this.superAdmin = superAdmin;
    }
}
