package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

import com.cloud_tecnoligical.nyxora_erp.dto.academico.CargaAcademicaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.CargaAcademicaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.CargaDetalleLineaDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.CreateCargaAcademicaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.GenerarNovedadDocenteRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.academico.UpdateCargaAcademicaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.CargaAcademicaDetalleEntity;
import com.cloud_tecnoligical.nyxora_erp.entity.CargaAcademicaEntity;
import com.cloud_tecnoligical.nyxora_erp.event.CargaDocenteRegistrada;
import com.cloud_tecnoligical.nyxora_erp.event.DomainEventBus;
import com.cloud_tecnoligical.nyxora_erp.repository.academico.CargaAcademicaDetalleR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.academico.CargaAcademicaQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.academico.CargaAcademicaR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.nomina.ConceptoNominaQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.nomina.VinculacionQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.CargaAcademicaService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CargaAcademicaServiceImpl implements CargaAcademicaService {

    private final CargaAcademicaR2dbcRepository repo;
    private final CargaAcademicaDetalleR2dbcRepository detalleRepo;
    private final CargaAcademicaQueryRepository queryRepo;
    private final VinculacionQueryRepository vinculacionQueryRepository;
    private final ConceptoNominaQueryRepository conceptoQueryRepository;
    private final DomainEventBus eventBus;
    private final TransactionalOperator tx;

    public CargaAcademicaServiceImpl(CargaAcademicaR2dbcRepository repo,
            CargaAcademicaDetalleR2dbcRepository detalleRepo, CargaAcademicaQueryRepository queryRepo,
            VinculacionQueryRepository vinculacionQueryRepository, ConceptoNominaQueryRepository conceptoQueryRepository,
            DomainEventBus eventBus, ReactiveTransactionManager txManager) {
        this.repo = repo;
        this.detalleRepo = detalleRepo;
        this.queryRepo = queryRepo;
        this.vinculacionQueryRepository = vinculacionQueryRepository;
        this.conceptoQueryRepository = conceptoQueryRepository;
        this.eventBus = eventBus;
        this.tx = TransactionalOperator.create(txManager);
    }

    @Override
    public Mono<CargaAcademicaResponseDto> create(CreateCargaAcademicaRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            validarVinculacion(dto.getVinculacionId(), t.getEmpresaId()).then(Mono.defer(() -> {
                CargaAcademicaEntity e = new CargaAcademicaEntity();
                e.setEmpresa_id(t.getEmpresaId());
                aplicar(e, dto);
                e.setActivo(true);
                e.setCreated_at(LocalDateTime.now());
                return repo.save(e)
                    .flatMap(saved -> insertarDetalle(dto.getDetalle(), saved.getId()).then(findById(saved.getId())))
                    .as(tx::transactional);
            })));
    }

    @Override
    public Mono<Boolean> update(UpdateCargaAcademicaRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(dto.getId(), t.getEmpresaId()).flatMap(e ->
                validarVinculacion(dto.getVinculacionId(), t.getEmpresaId()).then(Mono.defer(() -> {
                    aplicar(e, dto);
                    if (dto.getActive() != null) e.setActivo(dto.getActive());
                    e.setUpdated_at(LocalDateTime.now());
                    return queryRepo.borrarDetalle(e.getId())
                        .then(insertarDetalle(dto.getDetalle(), e.getId()))
                        .then(repo.save(e))
                        .thenReturn(true)
                        .as(tx::transactional);
                }))));
    }

    @Override
    public Mono<Boolean> delete(Long id) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(id, t.getEmpresaId()).flatMap(e -> {
                e.setDeleted_at(LocalDateTime.now());
                return repo.save(e).thenReturn(true);
            }));
    }

    @Override
    public Mono<CargaAcademicaResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepo.findHeaderById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Carga académica no encontrada")))
                .flatMap(header -> queryRepo.listDetalle(id).map(det -> {
                    header.setDetalle(det);
                    return header;
                })));
    }

    @Override
    public Mono<PageResponseDto<CargaAcademicaTableDto>> list(PageableDto<?> request, Long vinculacionId) {
        return TenantContext.get().flatMap(t -> queryRepo.list(request, t.getEmpresaId(), vinculacionId));
    }

    @Override
    public Mono<CargaAcademicaResponseDto> generarNovedad(Long id, GenerarNovedadDocenteRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(id, t.getEmpresaId()).flatMap(carga -> {
                if (carga.getVinculacion_id() == null) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La carga no tiene docente (vinculación)"));
                }
                return conceptoQueryRepository.existsActivoEnEmpresa(dto.getConceptoNominaId(), t.getEmpresaId()).flatMap(ok -> {
                    if (!Boolean.TRUE.equals(ok)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "El concepto de nómina no existe"));
                    }
                    return queryRepo.sumHoras(id).flatMap(horas -> {
                        BigDecimal cantidad = horas.multiply(dto.getValorHora());
                        eventBus.publish(new CargaDocenteRegistrada(
                            t.getEmpresaId(), t.getUsuarioId(), t.getSedeId(),
                            carga.getVinculacion_id(), dto.getConceptoNominaId(), cantidad,
                            "Carga docente #" + id + " (" + horas + " horas)", id));
                        return findById(id);
                    });
                });
            }));
    }

    // ==================== helpers ====================
    private void aplicar(CargaAcademicaEntity e, CreateCargaAcademicaRequestDto dto) {
        e.setVinculacion_id(dto.getVinculacionId());
        e.setNivel_estudio_id(dto.getNivelEstudioId());
        e.setNumero_acto_administrativo(dto.getNumeroActoAdministrativo());
        e.setFecha_acto_administrativo(dto.getFechaActoAdministrativo());
    }

    private Mono<Void> insertarDetalle(List<CargaDetalleLineaDto> detalle, Long cargaId) {
        if (detalle == null || detalle.isEmpty()) {
            return Mono.empty();
        }
        return Flux.fromIterable(detalle).concatMap(l -> {
            CargaAcademicaDetalleEntity d = new CargaAcademicaDetalleEntity();
            d.setCarga_academica_id(cargaId);
            d.setAsignatura_programa_id(l.getAsignaturaProgramaId());
            d.setGrupo_academico_id(l.getGrupoAcademicoId());
            d.setHoras(l.getHoras());
            d.setCreated_at(LocalDateTime.now());
            return detalleRepo.save(d);
        }).then();
    }

    private Mono<Void> validarVinculacion(Long vinculacionId, Long empresaId) {
        if (vinculacionId == null) return Mono.empty();
        return vinculacionQueryRepository.existsActivoEnEmpresa(vinculacionId, empresaId).flatMap(ok ->
            Boolean.TRUE.equals(ok) ? Mono.<Void>empty()
                : Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La vinculación (docente) no existe")));
    }

    private Mono<CargaAcademicaEntity> cargarEntidad(Long id, Long empresaId) {
        return repo.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Carga académica no encontrada")))
            .flatMap(e -> {
                if (e.getDeleted_at() != null || !e.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Carga académica no encontrada"));
                }
                return Mono.just(e);
            });
    }
}
