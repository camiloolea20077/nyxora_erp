package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.parametro.CreateParametroRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.parametro.ParametroResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.parametro.ParametroTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.parametro.UpdateParametroRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.ParametroEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.parametro.ParametroMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.parametro.ParametroQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.parametro.ParametroR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.ParametroService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class ParametroServiceImpl implements ParametroService {

    private final ParametroR2dbcRepository parametroR2dbcRepository;
    private final ParametroQueryRepository parametroQueryRepository;
    private final ParametroMapper parametroMapper;

    public ParametroServiceImpl(ParametroR2dbcRepository parametroR2dbcRepository,
                                ParametroQueryRepository parametroQueryRepository, ParametroMapper parametroMapper) {
        this.parametroR2dbcRepository = parametroR2dbcRepository;
        this.parametroQueryRepository = parametroQueryRepository;
        this.parametroMapper = parametroMapper;
    }

    @Override
    public Mono<ParametroResponseDto> create(CreateParametroRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            parametroQueryRepository.existsByClave(dto.getKey(), t.getEmpresaId())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe un parámetro con esa clave"));
                    }
                    ParametroEntity entity = parametroMapper.toEntity(dto);
                    entity.setEmpresa_id(t.getEmpresaId());
                    entity.setTipo_dato(dto.getDataType() != null ? dto.getDataType() : "string");
                    entity.setActivo(true);
                    entity.setUsuario_creacion(t.getUsuarioId());
                    entity.setCreated_at(LocalDateTime.now());
                    return parametroR2dbcRepository.save(entity).map(parametroMapper::toResponseDto);
                }));
    }

    @Override
    public Mono<Boolean> update(UpdateParametroRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargar(dto.getId(), t.getEmpresaId()).flatMap(entity -> {
                entity.setValor(dto.getValue());
                if (dto.getDataType() != null) {
                    entity.setTipo_dato(dto.getDataType());
                }
                entity.setUsuario_modificacion(t.getUsuarioId());
                entity.setUpdated_at(LocalDateTime.now());
                return parametroR2dbcRepository.save(entity).thenReturn(true);
            }));
    }

    @Override
    public Mono<Boolean> delete(Long id) {
        return TenantContext.get().flatMap(t ->
            cargar(id, t.getEmpresaId()).flatMap(entity -> {
                entity.setDeleted_at(LocalDateTime.now());
                entity.setUsuario_modificacion(t.getUsuarioId());
                return parametroR2dbcRepository.save(entity).thenReturn(true);
            }));
    }

    @Override
    public Mono<ParametroResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            parametroQueryRepository.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Parámetro no encontrado"))));
    }

    @Override
    public Mono<ParametroResponseDto> findByClave(String clave) {
        return TenantContext.get().flatMap(t ->
            parametroQueryRepository.findActiveByClave(clave, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Parámetro no encontrado"))));
    }

    @Override
    public Mono<PageResponseDto<ParametroTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> parametroQueryRepository.list(request, t.getEmpresaId()));
    }

    private Mono<ParametroEntity> cargar(Long id, Long empresaId) {
        return parametroR2dbcRepository.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Parámetro no encontrado")))
            .flatMap(entity -> {
                if (entity.getDeleted_at() != null || !entity.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Parámetro no encontrado"));
                }
                return Mono.just(entity);
            });
    }
}
