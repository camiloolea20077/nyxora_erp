package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.CreateCuentaBancariaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.CuentaBancariaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.CuentaBancariaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tesoreria.UpdateCuentaBancariaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.CuentaBancariaEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.tesoreria.CuentaBancariaMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.tesoreria.CuentaBancariaQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.tesoreria.CuentaBancariaR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.CuentaBancariaService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class CuentaBancariaServiceImpl implements CuentaBancariaService {

    private final CuentaBancariaR2dbcRepository repo;
    private final CuentaBancariaQueryRepository queryRepo;
    private final CuentaBancariaMapper mapper;

    public CuentaBancariaServiceImpl(CuentaBancariaR2dbcRepository repo, CuentaBancariaQueryRepository queryRepo,
                                     CuentaBancariaMapper mapper) {
        this.repo = repo;
        this.queryRepo = queryRepo;
        this.mapper = mapper;
    }

    @Override
    public Mono<CuentaBancariaResponseDto> create(CreateCuentaBancariaRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            queryRepo.existsByNumero(dto.getBancoId(), dto.getNumeroCuenta(), t.getEmpresaId()).flatMap(existe -> {
                if (Boolean.TRUE.equals(existe)) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe una cuenta con ese número en el banco"));
                }
                CuentaBancariaEntity e = mapper.toEntity(dto);
                e.setEmpresa_id(t.getEmpresaId());
                if (e.getManeja_sobregiro() == null) e.setManeja_sobregiro(false);
                if (e.getAcepta_transferencias() == null) e.setAcepta_transferencias(false);
                e.setActivo(true);
                e.setCreated_at(LocalDateTime.now());
                e.setUsuario_creacion(t.getUsuarioId());
                return repo.save(e).flatMap(saved -> findById(saved.getId()));
            }));
    }

    @Override
    public Mono<Boolean> update(UpdateCuentaBancariaRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(dto.getId(), t.getEmpresaId()).flatMap(e ->
                queryRepo.existsByNumeroExcludingId(dto.getBancoId(), dto.getNumeroCuenta(), dto.getId(), t.getEmpresaId()).flatMap(dup -> {
                    if (Boolean.TRUE.equals(dup)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe una cuenta con ese número en el banco"));
                    }
                    e.setBanco_id(dto.getBancoId());
                    e.setTipo_cuenta_bancaria_id(dto.getTipoCuentaBancariaId());
                    e.setNumero_cuenta(dto.getNumeroCuenta());
                    e.setCuenta_contable_id(dto.getCuentaContableId());
                    e.setManeja_sobregiro(dto.getManejaSobregiro() != null ? dto.getManejaSobregiro() : e.getManeja_sobregiro());
                    e.setAcepta_transferencias(dto.getAceptaTransferencias() != null ? dto.getAceptaTransferencias() : e.getAcepta_transferencias());
                    e.setFecha_expiracion(dto.getFechaExpiracion());
                    e.setUsuario_modificacion(t.getUsuarioId());
                    e.setUpdated_at(LocalDateTime.now());
                    return repo.save(e).thenReturn(true);
                })));
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
    public Mono<CuentaBancariaResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepo.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Cuenta bancaria no encontrada"))));
    }

    @Override
    public Mono<PageResponseDto<CuentaBancariaTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepo.list(request, t.getEmpresaId()));
    }

    private Mono<CuentaBancariaEntity> cargarEntidad(Long id, Long empresaId) {
        return repo.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Cuenta bancaria no encontrada")))
            .flatMap(e -> {
                if (e.getDeleted_at() != null || !e.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Cuenta bancaria no encontrada"));
                }
                return Mono.just(e);
            });
    }
}
