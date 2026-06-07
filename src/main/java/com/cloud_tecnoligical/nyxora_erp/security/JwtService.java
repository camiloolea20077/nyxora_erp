package com.cloud_tecnoligical.nyxora_erp.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Emisión y validación de JWT (HS256). Stateless. El tenant viaja en los claims.
 */
@Service
public class JwtService {

    private final SecretKey key;
    private final long accessExpMin;
    private final long refreshExpDays;

    public JwtService(
        @Value("${app.jwt.secret}") String secret,
        @Value("${app.jwt.access-exp-min}") long accessExpMin,
        @Value("${app.jwt.refresh-exp-days}") long refreshExpDays
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpMin = accessExpMin;
        this.refreshExpDays = refreshExpDays;
    }

    public long getAccessExpSeconds() {
        return accessExpMin * 60;
    }

    public String generateAccess(TenantInfo info, String username, List<String> permisos) {
        Instant now = Instant.now();
        return Jwts.builder()
            .subject(String.valueOf(info.getUsuarioId()))
            .claim("empresa_id", info.getEmpresaId())
            .claim("sede_id", info.getSedeId())
            .claim("super_admin", info.isSuperAdmin())
            .claim("username", username)
            .claim("permisos", permisos)
            .claim("type", "access")
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusSeconds(accessExpMin * 60)))
            .signWith(key)
            .compact();
    }

    public String generateRefresh(Long usuarioId) {
        Instant now = Instant.now();
        return Jwts.builder()
            .subject(String.valueOf(usuarioId))
            .claim("type", "refresh")
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusSeconds(refreshExpDays * 86400)))
            .signWith(key)
            .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build()
            .parseSignedClaims(token).getPayload();
    }

    @SuppressWarnings("unchecked")
    public List<String> permisos(Claims claims) {
        Object p = claims.get("permisos");
        return p instanceof List ? (List<String>) p : List.of();
    }

    public TenantInfo toTenantInfo(Claims claims) {
        TenantInfo info = new TenantInfo();
        info.setUsuarioId(Long.valueOf(claims.getSubject()));
        info.setEmpresaId(asLong(claims.get("empresa_id")));
        info.setSedeId(asLong(claims.get("sede_id")));
        Object sa = claims.get("super_admin");
        info.setSuperAdmin(sa instanceof Boolean b && b);
        return info;
    }

    private Long asLong(Object value) {
        return value == null ? null : ((Number) value).longValue();
    }
}
