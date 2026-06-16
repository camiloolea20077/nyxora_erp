package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.caja.AbrirCajaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.caja.CajaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.caja.CajaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.caja.CreateCajaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.caja.UpdateCajaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.CajaEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.caja.CajaMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.caja.CajaQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.caja.CajaR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.CajaService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class CajaServiceImpl implements CajaService {

    private final CajaR2dbcRepository repo;
    private final CajaQueryRepository queryRepo;
    private final CajaMapper mapper;

    public CajaServiceImpl(CajaR2dbcRepository repo, CajaQueryRepository queryRepo, CajaMapper mapper) {
        this.repo = repo;
        this.queryRepo = queryRepo;
        this.mapper = mapper;
    }

    @Override
    public Mono<CajaResponseDto> create(CreateCajaRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            queryRepo.existsByCodigo(dto.getCodigo(), t.getEmpresaId()).flatMap(existe -> {
                if (Boolean.TRUE.equals(existe)) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe una caja con ese código"));
                }
                CajaEntity e = mapper.toEntity(dto);
                e.setEmpresa_id(t.getEmpresaId());
                e.setEstado("cerrada");
                e.setSaldo_inicial(BigDecimal.ZERO);
                e.setActivo(true);
                e.setCreated_at(LocalDateTime.now());
                e.setUsuario_creacion(t.getUsuarioId());
                return repo.save(e).flatMap(saved -> findById(saved.getId()));
            }));
    }

    @Override
    public Mono<Boolean> update(UpdateCajaRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(dto.getId(), t.getEmpresaId()).flatMap(e ->
                queryRepo.existsByCodigoExcludingId(dto.getCodigo(), dto.getId(), t.getEmpresaId()).flatMap(dup -> {
                    if (Boolean.TRUE.equals(dup)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe una caja con ese código"));
                    }
                    e.setCodigo(dto.getCodigo());
                    e.setNombre(dto.getNombre());
                    e.setSede_id(dto.getSedeId());
                    e.setUsuario_id(dto.getUsuarioId());
                    e.setUsuario_modificacion(t.getUsuarioId());
                    e.setUpdated_at(LocalDateTime.now());
                    return repo.save(e).thenReturn(true);
                })));
    }

    @Override
    public Mono<Boolean> delete(Long id) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(id, t.getEmpresaId()).flatMap(e -> {
                if ("abierta".equals(e.getEstado())) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "No se elimina una caja abierta"));
                }
                e.setDeleted_at(LocalDateTime.now());
                e.setUsuario_modificacion(t.getUsuarioId());
                return repo.save(e).thenReturn(true);
            }));
    }

    @Override
    public Mono<CajaResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepo.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Caja no encontrada"))));
    }

    @Override
    public Mono<PageResponseDto<CajaTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepo.list(request, t.getEmpresaId()));
    }

    @Override
    public Mono<CajaResponseDto> abrir(Long id, AbrirCajaRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(id, t.getEmpresaId()).flatMap(e -> {
                if ("abierta".equals(e.getEstado())) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La caja ya está abierta"));
                }
                e.setEstado("abierta");
                e.setSaldo_inicial(dto != null && dto.getSaldoInicial() != null ? dto.getSaldoInicial() : BigDecimal.ZERO);
                e.setFecha_apertura(LocalDateTime.now());
                e.setFecha_cierre(null);
                e.setUsuario_modificacion(t.getUsuarioId());
                e.setUpdated_at(LocalDateTime.now());
                return repo.save(e).then(findById(id));
            }));
    }

    @Override
    public Mono<CajaResponseDto> cerrar(Long id) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(id, t.getEmpresaId()).flatMap(e -> {
                if (!"abierta".equals(e.getEstado())) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La caja no está abierta"));
                }
                e.setEstado("cerrada");
                e.setFecha_cierre(LocalDateTime.now());
                e.setUsuario_modificacion(t.getUsuarioId());
                e.setUpdated_at(LocalDateTime.now());
                return repo.save(e).then(findById(id));
            }));
    }

    private Mono<CajaEntity> cargarEntidad(Long id, Long empresaId) {
        return repo.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Caja no encontrada")))
            .flatMap(e -> {
                if (e.getDeleted_at() != null || !e.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Caja no encontrada"));
                }
                return Mono.just(e);
            });
    }
}
