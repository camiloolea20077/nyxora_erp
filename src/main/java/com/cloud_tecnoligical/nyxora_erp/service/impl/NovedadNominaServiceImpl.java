package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.CreateNovedadNominaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.NovedadNominaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.NovedadNominaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.nomina.UpdateNovedadNominaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.NovedadNominaEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.nomina.NovedadNominaMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.nomina.ConceptoNominaQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.nomina.NovedadNominaQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.nomina.NovedadNominaR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.nomina.VinculacionQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.NovedadNominaService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class NovedadNominaServiceImpl implements NovedadNominaService {

    private final NovedadNominaR2dbcRepository repo;
    private final NovedadNominaQueryRepository queryRepo;
    private final NovedadNominaMapper mapper;
    private final VinculacionQueryRepository vinculacionQueryRepository;
    private final ConceptoNominaQueryRepository conceptoQueryRepository;

    public NovedadNominaServiceImpl(NovedadNominaR2dbcRepository repo, NovedadNominaQueryRepository queryRepo,
            NovedadNominaMapper mapper, VinculacionQueryRepository vinculacionQueryRepository,
            ConceptoNominaQueryRepository conceptoQueryRepository) {
        this.repo = repo;
        this.queryRepo = queryRepo;
        this.mapper = mapper;
        this.vinculacionQueryRepository = vinculacionQueryRepository;
        this.conceptoQueryRepository = conceptoQueryRepository;
    }

    @Override
    public Mono<NovedadNominaResponseDto> create(CreateNovedadNominaRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            validarReferencias(dto, t.getEmpresaId()).then(Mono.defer(() -> {
                NovedadNominaEntity e = new NovedadNominaEntity();
                mapper.apply(dto, e);
                e.setEmpresa_id(t.getEmpresaId());
                e.setDescripcion(dto.getDescripcion());
                e.setDias(dto.getDias());
                e.setExpediente(dto.getExpediente());
                e.setDemandante(dto.getDemandante());
                if (e.getEstado_novedad() == null) e.setEstado_novedad("registrada");
                e.setAnulado(false);
                e.setActivo(true);
                e.setCreated_at(LocalDateTime.now());
                e.setUsuario_creacion(t.getUsuarioId());
                return repo.save(e).flatMap(saved -> findById(saved.getId()));
            })));
    }

    @Override
    public Mono<Boolean> update(UpdateNovedadNominaRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(dto.getId(), t.getEmpresaId()).flatMap(e -> {
                if (Boolean.TRUE.equals(e.getAnulado())) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "No se edita una novedad anulada"));
                }
                return validarReferencias(dto, t.getEmpresaId()).then(Mono.defer(() -> {
                    mapper.apply(dto, e);
                    e.setDescripcion(dto.getDescripcion());
                    e.setDias(dto.getDias());
                    e.setExpediente(dto.getExpediente());
                    e.setDemandante(dto.getDemandante());
                    if (dto.getActive() != null) e.setActivo(dto.getActive());
                    e.setUpdated_at(LocalDateTime.now());
                    e.setUsuario_modificacion(t.getUsuarioId());
                    return repo.save(e).thenReturn(true);
                }));
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
    public Mono<NovedadNominaResponseDto> anular(Long id) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(id, t.getEmpresaId()).flatMap(e -> {
                e.setAnulado(true);
                e.setEstado_novedad("anulada");
                e.setUpdated_at(LocalDateTime.now());
                e.setUsuario_modificacion(t.getUsuarioId());
                return repo.save(e).then(findById(id));
            }));
    }

    @Override
    public Mono<NovedadNominaResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepo.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Novedad no encontrada"))));
    }

    @Override
    public Mono<PageResponseDto<NovedadNominaTableDto>> list(PageableDto<?> request, Long vinculacionId) {
        return TenantContext.get().flatMap(t -> queryRepo.list(request, t.getEmpresaId(), vinculacionId));
    }

    private Mono<Void> validarReferencias(CreateNovedadNominaRequestDto dto, Long empresaId) {
        return vinculacionQueryRepository.existsActivoEnEmpresa(dto.getVinculacionId(), empresaId).flatMap(ok ->
                Boolean.TRUE.equals(ok) ? Mono.<Void>empty()
                    : Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Vinculación no encontrada")))
            .then(conceptoQueryRepository.existsActivoEnEmpresa(dto.getConceptoNominaId(), empresaId).flatMap(ok ->
                Boolean.TRUE.equals(ok) ? Mono.<Void>empty()
                    : Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "El concepto no existe"))));
    }

    private Mono<NovedadNominaEntity> cargarEntidad(Long id, Long empresaId) {
        return repo.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Novedad no encontrada")))
            .flatMap(e -> {
                if (e.getDeleted_at() != null || !e.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Novedad no encontrada"));
                }
                return Mono.just(e);
            });
    }
}
