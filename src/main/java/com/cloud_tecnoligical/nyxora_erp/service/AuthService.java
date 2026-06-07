package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.auth.LoginRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.auth.RefreshRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.auth.TokenResponseDto;

import reactor.core.publisher.Mono;

public interface AuthService {
    Mono<TokenResponseDto> login(LoginRequestDto dto);
    Mono<TokenResponseDto> refresh(RefreshRequestDto dto);
}
