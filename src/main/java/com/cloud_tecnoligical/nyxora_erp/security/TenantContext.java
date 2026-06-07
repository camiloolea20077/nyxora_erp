package com.cloud_tecnoligical.nyxora_erp.security;

import org.springframework.http.HttpStatus;

import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;

import reactor.core.publisher.Mono;
import reactor.util.context.Context;

/**
 * Contexto multi-tenant REACTIVO.
 *
 * IMPORTANTE: en WebFlux no se puede usar ThreadLocal (la ejecución salta de hilo
 * entre operadores). El tenant viaja en el Reactor Context. El filtro JWT
 * (agente jwt-multitenant) hace:  chain.filter(exchange).contextWrite(TenantContext.write(info))
 *
 * Los servicios obtienen el tenant con TenantContext.get() y componen con flatMap.
 */
public final class TenantContext {

    public static final String KEY = "TENANT_INFO";

    private TenantContext() {
    }

    /** Inserta el TenantInfo en el Reactor Context (lo usa el filtro JWT). */
    public static Context write(TenantInfo info) {
        return Context.of(KEY, info);
    }

    /** Obtiene el TenantInfo del Reactor Context o falla con 401 si no está presente. */
    public static Mono<TenantInfo> get() {
        return Mono.deferContextual(ctx -> ctx.hasKey(KEY)
            ? Mono.just(ctx.get(KEY))
            : Mono.error(new GlobalException(HttpStatus.UNAUTHORIZED, "Sesión no autenticada")));
    }

    public static Mono<Long> getEmpresaId() {
        return get().map(TenantInfo::getEmpresaId);
    }

    public static Mono<Long> getUsuarioId() {
        return get().map(TenantInfo::getUsuarioId);
    }

    public static Mono<Long> getSedeId() {
        return get().map(TenantInfo::getSedeId);
    }

    public static Mono<Boolean> isSuperAdmin() {
        return get().map(TenantInfo::isSuperAdmin);
    }
}
