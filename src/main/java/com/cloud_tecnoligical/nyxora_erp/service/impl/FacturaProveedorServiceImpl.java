package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar.CreateFacturaProveedorRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar.FacturaProveedorResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar.FacturaProveedorTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar.RegistrarEventoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.cuentaspagar.UpdateFacturaProveedorRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.FacturaProveedorEntity;
import com.cloud_tecnoligical.nyxora_erp.entity.FacturaProveedorEventoEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.cuentaspagar.FacturaProveedorMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.cuentaspagar.FacturaProveedorEventoR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.cuentaspagar.FacturaProveedorQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.cuentaspagar.FacturaProveedorR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.FacturaProveedorService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class FacturaProveedorServiceImpl implements FacturaProveedorService {

    private final FacturaProveedorR2dbcRepository repo;
    private final FacturaProveedorEventoR2dbcRepository eventoRepo;
    private final FacturaProveedorQueryRepository queryRepo;
    private final FacturaProveedorMapper mapper;

    public FacturaProveedorServiceImpl(FacturaProveedorR2dbcRepository repo,
                                       FacturaProveedorEventoR2dbcRepository eventoRepo,
                                       FacturaProveedorQueryRepository queryRepo, FacturaProveedorMapper mapper) {
        this.repo = repo;
        this.eventoRepo = eventoRepo;
        this.queryRepo = queryRepo;
        this.mapper = mapper;
    }

    @Override
    public Mono<FacturaProveedorResponseDto> create(CreateFacturaProveedorRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            queryRepo.terceroExisteEnEmpresa(dto.getProveedorId(), t.getEmpresaId()).flatMap(ok -> {
                if (!Boolean.TRUE.equals(ok)) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "El proveedor no existe"));
                }
                FacturaProveedorEntity e = mapper.toEntity(dto);
                e.setEmpresa_id(t.getEmpresaId());
                e.setEstado("recibida");
                e.setActivo(true);
                e.setCreated_at(LocalDateTime.now());
                e.setUsuario_creacion(t.getUsuarioId());
                return repo.save(e).flatMap(saved -> cargarRespuesta(saved.getId(), t.getEmpresaId()));
            }));
    }

    @Override
    public Mono<Boolean> update(UpdateFacturaProveedorRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(dto.getId(), t.getEmpresaId()).flatMap(e -> {
                if (!"recibida".equals(e.getEstado())) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Solo se edita una factura en estado 'recibida'"));
                }
                e.setProveedor_id(dto.getProveedorId());
                e.setReceptor_id(dto.getReceptorId());
                e.setNumero_documento(dto.getNumeroDocumento());
                e.setCufe(dto.getCufe());
                e.setFecha_recepcion(dto.getFechaRecepcion());
                e.setValor_factura(dto.getValorFactura());
                e.setEmail_remitente(dto.getEmailRemitente());
                e.setPdf_url(dto.getPdfUrl());
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
    public Mono<FacturaProveedorResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t -> cargarRespuesta(id, t.getEmpresaId()));
    }

    @Override
    public Mono<PageResponseDto<FacturaProveedorTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepo.list(request, t.getEmpresaId()));
    }

    @Override
    public Mono<FacturaProveedorResponseDto> registrarEvento(Long id, RegistrarEventoRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(id, t.getEmpresaId()).flatMap(f -> {
                FacturaProveedorEventoEntity ev = new FacturaProveedorEventoEntity();
                ev.setFactura_proveedor_id(f.getId());
                ev.setEvento(dto.getEvento());
                ev.setFecha_evento(dto.getFechaEvento());
                ev.setCude_evento(dto.getCudeEvento());
                ev.setConcepto_reclamo(dto.getConceptoReclamo());
                ev.setDescripcion_reclamo(dto.getDescripcionReclamo());
                ev.setEstado(dto.getEstado());
                ev.setCreated_at(LocalDateTime.now());
                Mono<Void> actualizarEstado = Mono.empty();
                if (dto.getEstado() != null && !dto.getEstado().isBlank()) {
                    f.setEstado(dto.getEstado());
                    f.setUsuario_modificacion(t.getUsuarioId());
                    f.setUpdated_at(LocalDateTime.now());
                    actualizarEstado = repo.save(f).then();
                }
                return eventoRepo.save(ev).then(actualizarEstado).then(cargarRespuesta(id, t.getEmpresaId()));
            }));
    }

    private Mono<FacturaProveedorEntity> cargarEntidad(Long id, Long empresaId) {
        return repo.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Factura de proveedor no encontrada")))
            .flatMap(e -> {
                if (e.getDeleted_at() != null || !e.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Factura de proveedor no encontrada"));
                }
                return Mono.just(e);
            });
    }

    private Mono<FacturaProveedorResponseDto> cargarRespuesta(Long id, Long empresaId) {
        return queryRepo.findActiveById(id, empresaId)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Factura de proveedor no encontrada")))
            .flatMap(resp -> queryRepo.listEventos(id).map(evs -> { resp.setEventos(evs); return resp; }));
    }
}
