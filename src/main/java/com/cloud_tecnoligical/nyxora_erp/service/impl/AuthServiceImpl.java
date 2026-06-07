package com.cloud_tecnoligical.nyxora_erp.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.auth.LoginRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.auth.RefreshRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.auth.TokenResponseDto;
import com.cloud_tecnoligical.nyxora_erp.repository.auth.AuthQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.auth.UsuarioR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.JwtService;
import com.cloud_tecnoligical.nyxora_erp.security.TenantInfo;
import com.cloud_tecnoligical.nyxora_erp.service.AuthService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;

import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthQueryRepository authQueryRepository;
    private final UsuarioR2dbcRepository usuarioR2dbcRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(AuthQueryRepository authQueryRepository,
                           UsuarioR2dbcRepository usuarioR2dbcRepository,
                           JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.authQueryRepository = authQueryRepository;
        this.usuarioR2dbcRepository = usuarioR2dbcRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Mono<TokenResponseDto> login(LoginRequestDto dto) {
        return authQueryRepository.findActiveByUsername(dto.getUsername())
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas")))
            .flatMap(u -> Mono.fromCallable(() -> passwordEncoder.matches(dto.getPassword(), u.getHashPassword()))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(ok -> {
                    if (Boolean.FALSE.equals(ok)) {
                        return Mono.error(new GlobalException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));
                    }
                    return authQueryRepository.findPermisosByUsuario(u.getId()).collectList()
                        .map(permisos -> {
                            TenantInfo info = new TenantInfo(u.getEmpresaId(), u.getId(), null, false);
                            String access = jwtService.generateAccess(info, dto.getUsername(), permisos);
                            String refresh = jwtService.generateRefresh(u.getId());
                            return new TokenResponseDto(access, refresh, jwtService.getAccessExpSeconds(),
                                u.getId(), dto.getUsername(), u.getEmpresaId(), permisos);
                        });
                }));
    }

    @Override
    public Mono<TokenResponseDto> refresh(RefreshRequestDto dto) {
        return Mono.fromCallable(() -> jwtService.parse(dto.getRefreshToken()))
            .onErrorMap(e -> new GlobalException(HttpStatus.UNAUTHORIZED, "Refresh token inválido"))
            .flatMap(claims -> {
                if (!"refresh".equals(claims.get("type"))) {
                    return Mono.<TokenResponseDto>error(new GlobalException(HttpStatus.UNAUTHORIZED, "Token no es de refresco"));
                }
                Long usuarioId = Long.valueOf(((Claims) claims).getSubject());
                return usuarioR2dbcRepository.findById(usuarioId)
                    .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.UNAUTHORIZED, "Usuario no válido")))
                    .flatMap(usuario -> authQueryRepository.findPermisosByUsuario(usuarioId).collectList()
                        .map(permisos -> {
                            TenantInfo info = new TenantInfo(usuario.getEmpresa_id(), usuario.getId(), null, false);
                            String access = jwtService.generateAccess(info, usuario.getUsername(), permisos);
                            String refresh = jwtService.generateRefresh(usuario.getId());
                            return new TokenResponseDto(access, refresh, jwtService.getAccessExpSeconds(),
                                usuario.getId(), usuario.getUsername(), usuario.getEmpresa_id(), permisos);
                        }));
            });
    }
}
