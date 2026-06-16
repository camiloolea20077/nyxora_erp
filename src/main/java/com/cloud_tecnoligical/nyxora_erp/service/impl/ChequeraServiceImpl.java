package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.ChequeraResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.ChequeraTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.CreateChequeraRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.UpdateChequeraRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.ChequeraEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.tesoreria.ChequeraMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.tesoreria.ChequeraQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.tesoreria.ChequeraR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.ChequeraService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class ChequeraServiceImpl implements ChequeraService {

    private final ChequeraR2dbcRepository repo;
    private final ChequeraQueryRepository queryRepo;
    private final ChequeraMapper mapper;

    public ChequeraServiceImpl(ChequeraR2dbcRepository repo, ChequeraQueryRepository queryRepo, ChequeraMapper mapper) {
        this.repo = repo;
        this.queryRepo = queryRepo;
        this.mapper = mapper;
    }

    @Override
    public Mono<ChequeraResponseDto> create(CreateChequeraRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            queryRepo.cuentaBancariaExisteEnEmpresa(dto.getCuentaBancariaId(), t.getEmpresaId()).flatMap(ok -> {
                if (!Boolean.TRUE.equals(ok)) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La cuenta bancaria no existe"));
                }
                ChequeraEntity e = mapper.toEntity(dto);
                e.setEmpresa_id(t.getEmpresaId());
                e.setConsecutivo_actual(dto.getNumeroInicial() != null ? dto.getNumeroInicial() : 0L);
                e.setActivo(true);
                e.setCreated_at(LocalDateTime.now());
                e.setUsuario_creacion(t.getUsuarioId());
                return repo.save(e).flatMap(saved -> findById(saved.getId()));
            }));
    }

    @Override
    public Mono<Boolean> update(UpdateChequeraRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(dto.getId(), t.getEmpresaId()).flatMap(e -> {
                e.setCuenta_bancaria_id(dto.getCuentaBancariaId());
                e.setFecha_expedicion(dto.getFechaExpedicion());
                e.setNumero_inicial(dto.getNumeroInicial());
                e.setNumero_final(dto.getNumeroFinal());
                e.setUsuario_modificacion(t.getUsuarioId());
                e.setUpdated_at(LocalDateTime.now());
                return repo.save(e).thenReturn(true);
            }));
    }

    @Override
    public Mono<Boolean> delete(Long id) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(id, t.getEmpresaId()).flatMap(e -> {
                e.setDeleted_at(LocalDateTime.now());
                e.setUsuario_modificacion(t.getUsuarioId());
                return repo.save(e).thenReturn(true);
            }));
    }

    @Override
    public Mono<ChequeraResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepo.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Chequera no encontrada"))));
    }

    @Override
    public Mono<PageResponseDto<ChequeraTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepo.list(request, t.getEmpresaId()));
    }

    private Mono<ChequeraEntity> cargarEntidad(Long id, Long empresaId) {
        return repo.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Chequera no encontrada")))
            .flatMap(e -> {
                if (e.getDeleted_at() != null || !e.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Chequera no encontrada"));
                }
                return Mono.just(e);
            });
    }
}
