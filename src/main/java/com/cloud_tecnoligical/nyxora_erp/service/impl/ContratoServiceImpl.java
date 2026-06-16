package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.AsignarPolizaContratoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.CambiarEstadoContratoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.ContratoClausulaDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.ContratoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.ContratoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.CreateContratoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.UpdateContratoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.ContratoClausulaEntity;
import com.cloud_tecnoligical.nyxora_erp.entity.ContratoEntity;
import com.cloud_tecnoligical.nyxora_erp.entity.ContratoPolizaEntity;
import com.cloud_tecnoligical.nyxora_erp.repository.contratacion.ContratoClausulaR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.contratacion.ContratoPolizaR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.contratacion.ContratoQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.contratacion.ContratoR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.ContratoService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ContratoServiceImpl implements ContratoService {

    private static final Set<String> ESTADOS = Set.of(
        "planeado", "adjudicado", "suscrito", "en_ejecucion", "liquidado", "anulado");

    private final ContratoR2dbcRepository repo;
    private final ContratoClausulaR2dbcRepository clausulaRepo;
    private final ContratoPolizaR2dbcRepository polizaRepo;
    private final ContratoQueryRepository queryRepo;
    private final TransactionalOperator tx;

    public ContratoServiceImpl(ContratoR2dbcRepository repo,
                               ContratoClausulaR2dbcRepository clausulaRepo,
                               ContratoPolizaR2dbcRepository polizaRepo,
                               ContratoQueryRepository queryRepo,
                               ReactiveTransactionManager txManager) {
        this.repo = repo;
        this.clausulaRepo = clausulaRepo;
        this.polizaRepo = polizaRepo;
        this.queryRepo = queryRepo;
        this.tx = TransactionalOperator.create(txManager);
    }

    @Override
    public Mono<ContratoResponseDto> create(CreateContratoRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            validarReferencias(dto, t.getEmpresaId()).then(Mono.defer(() -> {
                ContratoEntity c = new ContratoEntity();
                c.setEmpresa_id(t.getEmpresaId());
                aplicarCabecera(c, dto);
                c.setEstado("planeado");
                c.setActivo(true);
                c.setCreated_at(LocalDateTime.now());
                c.setUsuario_creacion(t.getUsuarioId());
                return repo.save(c)
                    .flatMap(saved -> insertarClausulas(dto.getClausulas(), saved.getId())
                        .then(findById(saved.getId())))
                    .as(tx::transactional);
            })));
    }

    @Override
    public Mono<Boolean> update(UpdateContratoRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(dto.getId(), t.getEmpresaId()).flatMap(c -> {
                if ("liquidado".equals(c.getEstado()) || "anulado".equals(c.getEstado())) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST,
                        "No se edita un contrato liquidado o anulado"));
                }
                return validarReferencias(dto, t.getEmpresaId()).then(Mono.defer(() -> {
                    aplicarCabecera(c, dto);
                    c.setUsuario_modificacion(t.getUsuarioId());
                    c.setUpdated_at(LocalDateTime.now());
                    return queryRepo.borrarClausulas(c.getId())
                        .then(insertarClausulas(dto.getClausulas(), c.getId()))
                        .then(repo.save(c))
                        .thenReturn(true)
                        .as(tx::transactional);
                }));
            }));
    }

    @Override
    public Mono<Boolean> delete(Long id) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(id, t.getEmpresaId()).flatMap(c -> {
                c.setDeleted_at(LocalDateTime.now());
                c.setUsuario_modificacion(t.getUsuarioId());
                return repo.save(c).thenReturn(true);
            }));
    }

    @Override
    public Mono<ContratoResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepo.findHeaderById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Contrato no encontrado")))
                .flatMap(header -> Mono.zip(
                        queryRepo.listClausulas(id),
                        queryRepo.listPolizas(id))
                    .map(tuple -> {
                        header.setClausulas(tuple.getT1());
                        header.setPolizas(tuple.getT2());
                        return header;
                    })));
    }

    @Override
    public Mono<PageResponseDto<ContratoTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepo.list(request, t.getEmpresaId()));
    }

    @Override
    public Mono<ContratoResponseDto> cambiarEstado(Long id, CambiarEstadoContratoRequestDto dto) {
        String estado = dto.getEstado() != null ? dto.getEstado().trim() : null;
        if (estado == null || !ESTADOS.contains(estado)) {
            return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Estado de contrato inválido"));
        }
        return TenantContext.get().flatMap(t ->
            cargarEntidad(id, t.getEmpresaId()).flatMap(c -> {
                c.setEstado(estado);
                c.setUsuario_modificacion(t.getUsuarioId());
                c.setUpdated_at(LocalDateTime.now());
                return repo.save(c).then(findById(id));
            }));
    }

    @Override
    public Mono<ContratoResponseDto> asignarPoliza(Long id, AsignarPolizaContratoRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(id, t.getEmpresaId()).flatMap(c ->
                queryRepo.polizaExistsInTenant(dto.getPolizaSeguroId(), t.getEmpresaId()).flatMap(ok -> {
                    if (!Boolean.TRUE.equals(ok)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La póliza no existe"));
                    }
                    return queryRepo.polizaVigenteId(id, dto.getPolizaSeguroId())
                        .hasElement()
                        .flatMap(yaAsignada -> {
                            if (Boolean.TRUE.equals(yaAsignada)) {
                                return Mono.<Void>empty();   // idempotente
                            }
                            ContratoPolizaEntity p = new ContratoPolizaEntity();
                            p.setContrato_id(id);
                            p.setPoliza_seguro_id(dto.getPolizaSeguroId());
                            p.setCreated_at(LocalDateTime.now());
                            return polizaRepo.save(p).then();
                        })
                        .then(findById(id));
                })));
    }

    @Override
    public Mono<Boolean> removerPoliza(Long id, Long polizaSeguroId) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(id, t.getEmpresaId()).flatMap(c ->
                queryRepo.polizaVigenteId(id, polizaSeguroId)
                    .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "La póliza no está asignada")))
                    .flatMap(polizaRepo::findById)
                    .flatMap(p -> {
                        p.setDeleted_at(LocalDateTime.now());
                        return polizaRepo.save(p).thenReturn(true);
                    })));
    }

    // ==================== helpers ====================

    private void aplicarCabecera(ContratoEntity c, CreateContratoRequestDto dto) {
        c.setNumero(dto.getNumero());
        c.setNombre(dto.getNombre());
        c.setTipo_contrato(dto.getTipoContrato());
        c.setContratista_id(dto.getContratistaId());
        c.setModalidad_id(dto.getModalidadId());
        c.setObjeto(dto.getObjeto());
        c.setFecha_inicio(dto.getFechaInicio());
        c.setFecha_fin(dto.getFechaFin());
        c.setValor(dto.getValor());
    }

    private Mono<Void> validarReferencias(CreateContratoRequestDto dto, Long empresaId) {
        Mono<Void> chain = Mono.empty();
        if (dto.getModalidadId() != null) {
            chain = chain.then(queryRepo.modalidadExists(dto.getModalidadId(), empresaId).flatMap(ok ->
                Boolean.TRUE.equals(ok) ? Mono.empty()
                    : Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La modalidad no existe"))));
        }
        if (dto.getContratistaId() != null) {
            chain = chain.then(queryRepo.contratistaExists(dto.getContratistaId(), empresaId).flatMap(ok ->
                Boolean.TRUE.equals(ok) ? Mono.empty()
                    : Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "El contratista no existe"))));
        }
        return chain;
    }

    private Mono<Void> insertarClausulas(List<ContratoClausulaDto> clausulas, Long contratoId) {
        if (clausulas == null || clausulas.isEmpty()) {
            return Mono.empty();
        }
        return Flux.fromIterable(clausulas).concatMap(c -> {
            ContratoClausulaEntity e = new ContratoClausulaEntity();
            e.setContrato_id(contratoId);
            e.setNumero(c.getNumero());
            e.setOrden(c.getOrden());
            e.setNombre(c.getNombre());
            e.setTexto(c.getTexto());
            e.setCreated_at(LocalDateTime.now());
            return clausulaRepo.save(e);
        }).then();
    }

    private Mono<ContratoEntity> cargarEntidad(Long id, Long empresaId) {
        return repo.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Contrato no encontrado")))
            .flatMap(e -> {
                if (e.getDeleted_at() != null || !e.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Contrato no encontrado"));
                }
                return Mono.just(e);
            });
    }
}
