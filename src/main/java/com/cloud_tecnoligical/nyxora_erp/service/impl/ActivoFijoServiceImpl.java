package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.ActivoFijoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.ActivoFijoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.AsignarPolizaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.AsignarResponsableRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.CreateActivoFijoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.activosfijos.UpdateActivoFijoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.entity.ActivoFijoEntity;
import com.cloud_tecnoligical.nyxora_erp.entity.ActivoFijoPolizaEntity;
import com.cloud_tecnoligical.nyxora_erp.entity.ActivoFijoResponsableEntity;
import com.cloud_tecnoligical.nyxora_erp.repository.activosfijos.ActivoFijoPolizaR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.activosfijos.ActivoFijoQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.activosfijos.ActivoFijoR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.activosfijos.ActivoFijoResponsableR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.ActivoFijoService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class ActivoFijoServiceImpl implements ActivoFijoService {

    private final ActivoFijoR2dbcRepository repo;
    private final ActivoFijoResponsableR2dbcRepository responsableRepo;
    private final ActivoFijoPolizaR2dbcRepository polizaRepo;
    private final ActivoFijoQueryRepository queryRepo;

    public ActivoFijoServiceImpl(ActivoFijoR2dbcRepository repo,
                                 ActivoFijoResponsableR2dbcRepository responsableRepo,
                                 ActivoFijoPolizaR2dbcRepository polizaRepo,
                                 ActivoFijoQueryRepository queryRepo) {
        this.repo = repo;
        this.responsableRepo = responsableRepo;
        this.polizaRepo = polizaRepo;
        this.queryRepo = queryRepo;
    }

    @Override
    public Mono<ActivoFijoResponseDto> create(CreateActivoFijoRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            queryRepo.existsByCodigo(dto.getCodigo(), t.getEmpresaId()).flatMap(existe -> {
                if (Boolean.TRUE.equals(existe)) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe un activo fijo con ese código"));
                }
                ActivoFijoEntity e = new ActivoFijoEntity();
                e.setEmpresa_id(t.getEmpresaId());
                aplicarCabecera(e, dto);
                e.setValor_depreciacion(BigDecimal.ZERO);
                e.setMeses_depreciados(0);
                e.setValor_actual(valorActualInicial(dto));
                e.setEstado_activo(dto.getEstadoActivo() != null ? dto.getEstadoActivo() : "activo");
                e.setActivo(true);
                e.setCreated_at(LocalDateTime.now());
                e.setUsuario_creacion(t.getUsuarioId());
                return repo.save(e).flatMap(saved -> findById(saved.getId()));
            }));
    }

    @Override
    public Mono<Boolean> update(UpdateActivoFijoRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(dto.getId(), t.getEmpresaId()).flatMap(e ->
                queryRepo.existsByCodigoExcludingId(dto.getCodigo(), dto.getId(), t.getEmpresaId()).flatMap(dup -> {
                    if (Boolean.TRUE.equals(dup)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe un activo fijo con ese código"));
                    }
                    aplicarCabecera(e, dto);
                    if (dto.getEstadoActivo() != null) {
                        e.setEstado_activo(dto.getEstadoActivo());
                    }
                    // valor_actual = valor_compra - depreciación acumulada - deterioro
                    e.setValor_actual(nz(e.getValor_compra())
                        .subtract(nz(e.getValor_depreciacion()))
                        .subtract(nz(e.getDeterioro())));
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
    public Mono<ActivoFijoResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepo.findHeaderById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Activo fijo no encontrado")))
                .flatMap(header -> Mono.zip(
                        queryRepo.listResponsables(id),
                        queryRepo.listPolizas(id))
                    .map(tuple -> {
                        header.setResponsables(tuple.getT1());
                        header.setPolizas(tuple.getT2());
                        return header;
                    })));
    }

    @Override
    public Mono<PageResponseDto<ActivoFijoTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepo.list(request, t.getEmpresaId()));
    }

    @Override
    public Mono<ActivoFijoResponseDto> asignarResponsable(Long activoFijoId, AsignarResponsableRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(activoFijoId, t.getEmpresaId()).flatMap(af ->
                queryRepo.terceroExists(dto.getTerceroId(), t.getEmpresaId()).flatMap(ok -> {
                    if (!Boolean.TRUE.equals(ok)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "El tercero no existe"));
                    }
                    return queryRepo.responsableVigenteId(activoFijoId, dto.getTerceroId())
                        .hasElement()
                        .flatMap(yaAsignado -> {
                            if (Boolean.TRUE.equals(yaAsignado)) {
                                return Mono.<Void>empty();   // idempotente
                            }
                            ActivoFijoResponsableEntity r = new ActivoFijoResponsableEntity();
                            r.setActivo_fijo_id(activoFijoId);
                            r.setTercero_id(dto.getTerceroId());
                            r.setActivo(true);
                            r.setCreated_at(LocalDateTime.now());
                            return responsableRepo.save(r).then();
                        })
                        .then(findById(activoFijoId));
                })));
    }

    @Override
    public Mono<Boolean> removerResponsable(Long activoFijoId, Long terceroId) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(activoFijoId, t.getEmpresaId()).flatMap(af ->
                queryRepo.responsableVigenteId(activoFijoId, terceroId)
                    .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "El responsable no está asignado")))
                    .flatMap(responsableRepo::findById)
                    .flatMap(r -> {
                        r.setDeleted_at(LocalDateTime.now());
                        r.setActivo(false);
                        return responsableRepo.save(r).thenReturn(true);
                    })));
    }

    @Override
    public Mono<ActivoFijoResponseDto> asignarPoliza(Long activoFijoId, AsignarPolizaRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(activoFijoId, t.getEmpresaId()).flatMap(af ->
                queryRepo.polizaExistsInTenant(dto.getPolizaSeguroId(), t.getEmpresaId()).flatMap(ok -> {
                    if (!Boolean.TRUE.equals(ok)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La póliza no existe"));
                    }
                    return queryRepo.polizaVigenteId(activoFijoId, dto.getPolizaSeguroId())
                        .hasElement()
                        .flatMap(yaAsignada -> {
                            if (Boolean.TRUE.equals(yaAsignada)) {
                                return Mono.<Void>empty();   // idempotente
                            }
                            ActivoFijoPolizaEntity p = new ActivoFijoPolizaEntity();
                            p.setActivo_fijo_id(activoFijoId);
                            p.setPoliza_seguro_id(dto.getPolizaSeguroId());
                            p.setCreated_at(LocalDateTime.now());
                            return polizaRepo.save(p).then();
                        })
                        .then(findById(activoFijoId));
                })));
    }

    @Override
    public Mono<Boolean> removerPoliza(Long activoFijoId, Long polizaSeguroId) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(activoFijoId, t.getEmpresaId()).flatMap(af ->
                queryRepo.polizaVigenteId(activoFijoId, polizaSeguroId)
                    .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "La póliza no está asignada")))
                    .flatMap(polizaRepo::findById)
                    .flatMap(p -> {
                        p.setDeleted_at(LocalDateTime.now());
                        return polizaRepo.save(p).thenReturn(true);
                    })));
    }

    // ==================== helpers ====================

    private void aplicarCabecera(ActivoFijoEntity e, CreateActivoFijoRequestDto dto) {
        e.setCodigo(dto.getCodigo());
        e.setNombre(dto.getNombre());
        e.setProducto_id(dto.getProductoId());
        e.setCodigo_unspsc(dto.getCodigoUnspsc());
        e.setCodigo_barra(dto.getCodigoBarra());
        e.setDescripcion(dto.getDescripcion());
        e.setMarca_id(dto.getMarcaId());
        e.setUnidad_mayor_id(dto.getUnidadMayorId());
        e.setNumero_serie(dto.getNumeroSerie());
        e.setModelo(dto.getModelo());
        e.setBodega_id(dto.getBodegaId());
        e.setCentro_costo_id(dto.getCentroCostoId());
        e.setProveedor_id(dto.getProveedorId());
        e.setNumero_factura(dto.getNumeroFactura());
        e.setFecha_factura(dto.getFechaFactura());
        e.setValor_compra(dto.getValorCompra());
        e.setValor_salvamento(dto.getValorSalvamento());
        e.setPorcentaje_salvamento(dto.getPorcentajeSalvamento());
        e.setMetodo_depreciacion(dto.getMetodoDepreciacion());
        e.setTipo_depreciacion(dto.getTipoDepreciacion());
        e.setDeterioro(dto.getDeterioro());
        e.setAvaluo(dto.getAvaluo());
        e.setVida_util(dto.getVidaUtil());
        e.setCapitalizado(dto.getCapitalizado());
        e.setFecha_salida_servicio(dto.getFechaSalidaServicio());
    }

    private BigDecimal valorActualInicial(CreateActivoFijoRequestDto dto) {
        return nz(dto.getValorCompra()).subtract(nz(dto.getDeterioro()));
    }

    private BigDecimal nz(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }

    private Mono<ActivoFijoEntity> cargarEntidad(Long id, Long empresaId) {
        return repo.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Activo fijo no encontrado")))
            .flatMap(e -> {
                if (e.getDeleted_at() != null || !e.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Activo fijo no encontrado"));
                }
                return Mono.just(e);
            });
    }
}
