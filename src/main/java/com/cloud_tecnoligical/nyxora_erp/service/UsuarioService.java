package com.cloud_tecnoligical.nyxora_erp.service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.usuario.AsignarRolRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.usuario.CreateUsuarioRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.usuario.UpdateUsuarioRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.usuario.UsuarioResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.usuario.UsuarioTableDto;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

public interface UsuarioService {
    Mono<UsuarioResponseDto> create(CreateUsuarioRequestDto dto);
    Mono<Boolean> update(UpdateUsuarioRequestDto dto);
    Mono<Boolean> delete(Long id);
    Mono<UsuarioResponseDto> findById(Long id);
    Mono<PageResponseDto<UsuarioTableDto>> list(PageableDto<?> request);
    Mono<Boolean> asignarRol(Long usuarioId, AsignarRolRequestDto dto);
    Mono<Boolean> quitarRol(Long usuarioId, AsignarRolRequestDto dto);
}
