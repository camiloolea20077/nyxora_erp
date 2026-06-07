package com.cloud_tecnoligical.nyxora_erp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloud_tecnoligical.nyxora_erp.dto.auth.LoginRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.auth.RefreshRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.auth.TokenResponseDto;
import com.cloud_tecnoligical.nyxora_erp.service.AuthService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Login y refresco de JWT multi-tenant")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Inicia sesión y devuelve un JWT (access + refresh)")
    public Mono<ResponseEntity<ApiResponse<TokenResponseDto>>> login(@Valid @RequestBody LoginRequestDto dto) {
        return authService.login(dto)
            .map(token -> ResponseEntity.ok(new ApiResponse<>(200, "Autenticado", false, token)));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renueva el access token a partir del refresh token")
    public Mono<ResponseEntity<ApiResponse<TokenResponseDto>>> refresh(@Valid @RequestBody RefreshRequestDto dto) {
        return authService.refresh(dto)
            .map(token -> ResponseEntity.ok(new ApiResponse<>(200, "Token renovado", false, token)));
    }
}
