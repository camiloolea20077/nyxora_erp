package com.cloud_tecnoligical.nyxora_erp.security;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

/**
 * Valida el JWT del header Authorization, construye la Authentication reactiva y escribe el
 * TenantInfo en el Reactor Context (NO ThreadLocal). Si el token es inválido/ausente, continúa
 * sin autenticación (las rutas protegidas devolverán 401 vía SecurityConfig).
 */
@Component
public class JwtAuthenticationWebFilter implements WebFilter {

    private final JwtService jwtService;

    public JwtAuthenticationWebFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String header = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }
        String token = header.substring(7);
        try {
            Claims claims = jwtService.parse(token);
            TenantInfo info = jwtService.toTenantInfo(claims);
            List<SimpleGrantedAuthority> authorities = jwtService.permisos(claims).stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
            var authentication = new UsernamePasswordAuthenticationToken(
                info.getUsuarioId(), null, authorities);

            return chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication))
                .contextWrite(TenantContext.write(info));
        } catch (Exception e) {
            // token inválido/expirado → sigue sin autenticación
            return chain.filter(exchange);
        }
    }
}
