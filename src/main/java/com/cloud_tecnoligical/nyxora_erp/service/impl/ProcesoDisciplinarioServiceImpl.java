package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.AddProcesoFaltaDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.CambiarEstadoProcesoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.CreateProcesoDescargoDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.CreateProcesoDisciplinarioRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.CreateProcesoNotificacionDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.ProcesoDescargoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.ProcesoDisciplinarioResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.ProcesoDisciplinarioTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.ProcesoFaltaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.ProcesoNotificacionResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.juridico.UpdateProcesoDisciplinarioRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.ProcesoDescargoEntity;
import com.cloud_tecnoligical.nyxora_erp.entity.ProcesoDisciplinarioEntity;
import com.cloud_tecnoligical.nyxora_erp.entity.ProcesoFaltaEntity;
import com.cloud_tecnoligical.nyxora_erp.entity.ProcesoNotificacionEntity;
import com.cloud_tecnoligical.nyxora_erp.repository.juridico.FaltaQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.juridico.ProcesoDescargoR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.juridico.ProcesoDisciplinarioQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.juridico.ProcesoDisciplinarioR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.juridico.ProcesoFaltaR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.juridico.ProcesoNotificacionR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.ProcesoDisciplinarioService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class ProcesoDisciplinarioServiceImpl implements ProcesoDisciplinarioService {

    private final ProcesoDisciplinarioR2dbcRepository repo;
    private final ProcesoFaltaR2dbcRepository faltaRepo;
    private final ProcesoDescargoR2dbcRepository descargoRepo;
    private final ProcesoNotificacionR2dbcRepository notificacionRepo;
    private final ProcesoDisciplinarioQueryRepository queryRepo;
    private final FaltaQueryRepository faltaQueryRepository;

    public ProcesoDisciplinarioServiceImpl(ProcesoDisciplinarioR2dbcRepository repo,
            ProcesoFaltaR2dbcRepository faltaRepo, ProcesoDescargoR2dbcRepository descargoRepo,
            ProcesoNotificacionR2dbcRepository notificacionRepo, ProcesoDisciplinarioQueryRepository queryRepo,
            FaltaQueryRepository faltaQueryRepository) {
        this.repo = repo;
        this.faltaRepo = faltaRepo;
        this.descargoRepo = descargoRepo;
        this.notificacionRepo = notificacionRepo;
        this.queryRepo = queryRepo;
        this.faltaQueryRepository = faltaQueryRepository;
    }

    @Override
    public Mono<ProcesoDisciplinarioResponseDto> create(CreateProcesoDisciplinarioRequestDto dto) {
        return TenantContext.get().flatMap(t -> {
            ProcesoDisciplinarioEntity e = new ProcesoDisciplinarioEntity();
            e.setEmpresa_id(t.getEmpresaId());
            e.setFecha(dto.getFecha());
            e.setVinculacion_id(dto.getVinculacionId());
            e.setResponsable_id(dto.getResponsableId());
            e.setDescripcion(dto.getDescripcion());
            e.setEstado("abierto");
            e.setActivo(true);
            e.setCreated_at(LocalDateTime.now());
            e.setUsuario_creacion(t.getUsuarioId());
            return repo.save(e).flatMap(saved -> findById(saved.getId()));
        });
    }

    @Override
    public Mono<Boolean> update(UpdateProcesoDisciplinarioRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(dto.getId(), t.getEmpresaId()).flatMap(e -> {
                e.setFecha(dto.getFecha());
                e.setVinculacion_id(dto.getVinculacionId());
                e.setResponsable_id(dto.getResponsableId());
                e.setDescripcion(dto.getDescripcion());
                if (dto.getActive() != null) e.setActivo(dto.getActive());
                e.setUpdated_at(LocalDateTime.now());
                e.setUsuario_modificacion(t.getUsuarioId());
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
    public Mono<ProcesoDisciplinarioResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepo.findHeaderById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Proceso no encontrado"))));
    }

    @Override
    public Mono<PageResponseDto<ProcesoDisciplinarioTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepo.list(request, t.getEmpresaId()));
    }

    @Override
    public Mono<ProcesoDisciplinarioResponseDto> cambiarEstado(Long id, CambiarEstadoProcesoRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(id, t.getEmpresaId()).flatMap(e -> {
                e.setEstado(dto.getEstado().trim());
                e.setUpdated_at(LocalDateTime.now());
                e.setUsuario_modificacion(t.getUsuarioId());
                return repo.save(e).then(findById(id));
            }));
    }

    // ---------- Faltas ----------
    @Override
    public Mono<List<ProcesoFaltaResponseDto>> listFaltas(Long procesoId) {
        return validarProceso(procesoId).then(queryRepo.listFaltas(procesoId));
    }

    @Override
    public Mono<ProcesoFaltaResponseDto> addFalta(Long procesoId, AddProcesoFaltaDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(procesoId, t.getEmpresaId())
                .then(faltaQueryRepository.existsActivoEnEmpresa(dto.getFaltaId(), t.getEmpresaId()))
                .flatMap(ok -> {
                    if (!Boolean.TRUE.equals(ok)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La falta no existe"));
                    }
                    return queryRepo.existeFaltaVigente(procesoId, dto.getFaltaId()).flatMap(dup -> {
                        if (Boolean.TRUE.equals(dup)) {
                            return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La falta ya está imputada"));
                        }
                        ProcesoFaltaEntity e = new ProcesoFaltaEntity();
                        e.setProceso_disciplinario_id(procesoId);
                        e.setFalta_id(dto.getFaltaId());
                        e.setCreated_at(LocalDateTime.now());
                        return faltaRepo.save(e).then(queryRepo.listFaltas(procesoId))
                            .map(list -> list.stream().filter(x -> x.getFaltaId().equals(dto.getFaltaId()))
                                .findFirst().orElse(null));
                    });
                }));
    }

    @Override
    public Mono<Boolean> removeFalta(Long procesoId, Long id) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(procesoId, t.getEmpresaId()).then(faltaRepo.findById(id)
                .switchIfEmpty(noEncontrado())
                .flatMap(e -> {
                    if (e.getDeleted_at() != null || !e.getProceso_disciplinario_id().equals(procesoId)) return noEncontrado();
                    e.setDeleted_at(LocalDateTime.now());
                    return faltaRepo.save(e).thenReturn(true);
                })));
    }

    // ---------- Descargos ----------
    @Override
    public Mono<List<ProcesoDescargoResponseDto>> listDescargos(Long procesoId) {
        return validarProceso(procesoId).then(queryRepo.listDescargos(procesoId));
    }

    @Override
    public Mono<ProcesoDescargoResponseDto> addDescargo(Long procesoId, CreateProcesoDescargoDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(procesoId, t.getEmpresaId()).then(Mono.defer(() -> {
                ProcesoDescargoEntity e = new ProcesoDescargoEntity();
                e.setProceso_disciplinario_id(procesoId);
                e.setFecha(dto.getFecha());
                e.setTexto(dto.getTexto());
                e.setCreated_at(LocalDateTime.now());
                return descargoRepo.save(e).map(saved -> {
                    ProcesoDescargoResponseDto r = new ProcesoDescargoResponseDto();
                    r.setId(saved.getId());
                    r.setFecha(saved.getFecha());
                    r.setTexto(saved.getTexto());
                    return r;
                });
            })));
    }

    @Override
    public Mono<Boolean> removeDescargo(Long procesoId, Long id) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(procesoId, t.getEmpresaId()).then(descargoRepo.findById(id)
                .switchIfEmpty(noEncontrado())
                .flatMap(e -> {
                    if (e.getDeleted_at() != null || !e.getProceso_disciplinario_id().equals(procesoId)) return noEncontrado();
                    e.setDeleted_at(LocalDateTime.now());
                    return descargoRepo.save(e).thenReturn(true);
                })));
    }

    // ---------- Notificaciones ----------
    @Override
    public Mono<List<ProcesoNotificacionResponseDto>> listNotificaciones(Long procesoId) {
        return validarProceso(procesoId).then(queryRepo.listNotificaciones(procesoId));
    }

    @Override
    public Mono<ProcesoNotificacionResponseDto> addNotificacion(Long procesoId, CreateProcesoNotificacionDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(procesoId, t.getEmpresaId()).then(Mono.defer(() -> {
                ProcesoNotificacionEntity e = new ProcesoNotificacionEntity();
                e.setProceso_disciplinario_id(procesoId);
                e.setFecha(dto.getFecha());
                e.setTipo(dto.getTipo());
                e.setTexto(dto.getTexto());
                e.setCreated_at(LocalDateTime.now());
                return notificacionRepo.save(e).map(saved -> {
                    ProcesoNotificacionResponseDto r = new ProcesoNotificacionResponseDto();
                    r.setId(saved.getId());
                    r.setFecha(saved.getFecha());
                    r.setTipo(saved.getTipo());
                    r.setTexto(saved.getTexto());
                    return r;
                });
            })));
    }

    @Override
    public Mono<Boolean> removeNotificacion(Long procesoId, Long id) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(procesoId, t.getEmpresaId()).then(notificacionRepo.findById(id)
                .switchIfEmpty(noEncontrado())
                .flatMap(e -> {
                    if (e.getDeleted_at() != null || !e.getProceso_disciplinario_id().equals(procesoId)) return noEncontrado();
                    e.setDeleted_at(LocalDateTime.now());
                    return notificacionRepo.save(e).thenReturn(true);
                })));
    }

    // ---------- helpers ----------
    private Mono<Void> validarProceso(Long procesoId) {
        return TenantContext.get().flatMap(t -> cargarEntidad(procesoId, t.getEmpresaId()).then());
    }

    private <T> Mono<T> noEncontrado() {
        return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Registro no encontrado"));
    }

    private Mono<ProcesoDisciplinarioEntity> cargarEntidad(Long id, Long empresaId) {
        return repo.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Proceso no encontrado")))
            .flatMap(e -> {
                if (e.getDeleted_at() != null || !e.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Proceso no encontrado"));
                }
                return Mono.just(e);
            });
    }
}
