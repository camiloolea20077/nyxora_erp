package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.vigencia.CreateVigenciaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.vigencia.UpdateVigenciaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.vigencia.VigenciaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.vigencia.VigenciaTableDto;
import com.cloud_tecnoligical.nyxora_erp.entity.VigenciaEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.vigencia.VigenciaMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.vigencia.VigenciaQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.vigencia.VigenciaR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.VigenciaService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class VigenciaServiceImpl implements VigenciaService {

    private final VigenciaR2dbcRepository vigenciaR2dbcRepository;
    private final VigenciaQueryRepository vigenciaQueryRepository;
    private final VigenciaMapper vigenciaMapper;

    public VigenciaServiceImpl(VigenciaR2dbcRepository vigenciaR2dbcRepository,
                               VigenciaQueryRepository vigenciaQueryRepository, VigenciaMapper vigenciaMapper) {
        this.vigenciaR2dbcRepository = vigenciaR2dbcRepository;
        this.vigenciaQueryRepository = vigenciaQueryRepository;
        this.vigenciaMapper = vigenciaMapper;
    }

    @Override
    public Mono<VigenciaResponseDto> create(CreateVigenciaRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            vigenciaQueryRepository.existsByAnio(dto.getYear(), t.getEmpresaId())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe una vigencia para ese año"));
                    }
                    VigenciaEntity entity = vigenciaMapper.toEntity(dto);
                    entity.setEmpresa_id(t.getEmpresaId());
                    entity.setEstado("planeada");
                    entity.setActivo(true);
                    entity.setUsuario_creacion(t.getUsuarioId());
                    entity.setCreated_at(LocalDateTime.now());
                    return vigenciaR2dbcRepository.save(entity).map(vigenciaMapper::toResponseDto);
                }));
    }

    @Override
    public Mono<Boolean> update(UpdateVigenciaRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargar(dto.getId(), t.getEmpresaId())
                .flatMap(entity -> vigenciaQueryRepository.existsByAnioExcludingId(dto.getYear(), dto.getId(), t.getEmpresaId())
                    .flatMap(dup -> {
                        if (Boolean.TRUE.equals(dup)) {
                            return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe otra vigencia para ese año"));
                        }
                        entity.setAnio(dto.getYear());
                        entity.setUsuario_modificacion(t.getUsuarioId());
                        entity.setUpdated_at(LocalDateTime.now());
                        return vigenciaR2dbcRepository.save(entity).thenReturn(true);
                    })));
    }

    @Override
    public Mono<Boolean> delete(Long id) {
        return TenantContext.get().flatMap(t ->
            cargar(id, t.getEmpresaId()).flatMap(entity -> {
                entity.setDeleted_at(LocalDateTime.now());
                entity.setUsuario_modificacion(t.getUsuarioId());
                return vigenciaR2dbcRepository.save(entity).thenReturn(true);
            }));
    }

    @Override
    public Mono<VigenciaResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            vigenciaQueryRepository.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Vigencia no encontrada"))));
    }

    @Override
    public Mono<PageResponseDto<VigenciaTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> vigenciaQueryRepository.list(request, t.getEmpresaId()));
    }

    @Override
    public Mono<Boolean> abrir(Long id) {
        return TenantContext.get().flatMap(t ->
            cargar(id, t.getEmpresaId()).flatMap(entity -> {
                if (!"planeada".equals(entity.getEstado())) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST,
                        "Solo se puede abrir una vigencia en estado 'planeada' (actual: " + entity.getEstado() + ")"));
                }
                entity.setEstado("abierta");
                entity.setFecha_apertura(LocalDate.now());
                entity.setUsuario_modificacion(t.getUsuarioId());
                entity.setUpdated_at(LocalDateTime.now());
                return vigenciaR2dbcRepository.save(entity).thenReturn(true);
            }));
    }

    @Override
    public Mono<Boolean> cerrar(Long id) {
        return TenantContext.get().flatMap(t ->
            cargar(id, t.getEmpresaId()).flatMap(entity -> {
                if (!"abierta".equals(entity.getEstado()) && !"en_cierre".equals(entity.getEstado())) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST,
                        "Solo se puede cerrar una vigencia 'abierta' o 'en_cierre' (actual: " + entity.getEstado() + ")"));
                }
                entity.setEstado("cerrada");
                entity.setFecha_cierre(LocalDate.now());
                entity.setUsuario_modificacion(t.getUsuarioId());
                entity.setUpdated_at(LocalDateTime.now());
                return vigenciaR2dbcRepository.save(entity).thenReturn(true);
            }));
    }

    private Mono<VigenciaEntity> cargar(Long id, Long empresaId) {
        return vigenciaR2dbcRepository.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Vigencia no encontrada")))
            .flatMap(entity -> {
                if (entity.getDeleted_at() != null || !entity.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Vigencia no encontrada"));
                }
                return Mono.just(entity);
            });
    }
}
