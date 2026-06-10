package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.producto.CreateProductoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.producto.ProductoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.producto.ProductoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.producto.UpdateProductoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.ProductoEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.producto.ProductoMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.producto.ProductoQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.producto.ProductoR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.ProductoService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoR2dbcRepository repository;
    private final ProductoQueryRepository queryRepository;
    private final ProductoMapper mapper;

    public ProductoServiceImpl(ProductoR2dbcRepository repository,
                               ProductoQueryRepository queryRepository, ProductoMapper mapper) {
        this.repository = repository;
        this.queryRepository = queryRepository;
        this.mapper = mapper;
    }

    @Override
    public Mono<ProductoResponseDto> create(CreateProductoRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            validarCategoria(dto.getCategoriaId(), t.getEmpresaId())
                .then(queryRepository.existsByCodigo(dto.getCodigo(), t.getEmpresaId()))
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe un producto con ese código"));
                    }
                    ProductoEntity entity = mapper.toEntity(dto);
                    entity.setEmpresa_id(t.getEmpresaId());
                    entity.setUsuario_creacion(t.getUsuarioId());
                    entity.setActivo(true);
                    entity.setCreated_at(LocalDateTime.now());
                    aplicarDefaults(entity);
                    return repository.save(entity)
                        .flatMap(saved -> queryRepository.setImpuestos(saved.getId(), dto.getImpuestoIds())
                            .then(Mono.fromCallable(() -> {
                                ProductoResponseDto resp = mapper.toResponseDto(saved);
                                resp.setImpuestoIds(dto.getImpuestoIds());
                                return resp;
                            })));
                }));
    }

    @Override
    public Mono<Boolean> update(UpdateProductoRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargar(dto.getId(), t.getEmpresaId())
                .flatMap(entity -> validarCategoria(dto.getCategoriaId(), t.getEmpresaId())
                    .then(queryRepository.existsByCodigoExcludingId(dto.getCodigo(), dto.getId(), t.getEmpresaId()))
                    .flatMap(dup -> {
                        if (Boolean.TRUE.equals(dup)) {
                            return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe otro producto con ese código"));
                        }
                        mapper.updateEntityFromDto(dto, entity);
                        if (dto.getActive() != null) {
                            entity.setActivo(dto.getActive());
                        }
                        aplicarDefaults(entity);
                        entity.setUsuario_modificacion(t.getUsuarioId());
                        entity.setUpdated_at(LocalDateTime.now());
                        Mono<Void> imp = dto.getImpuestoIds() != null
                            ? queryRepository.setImpuestos(dto.getId(), dto.getImpuestoIds())
                            : Mono.empty();
                        return repository.save(entity).then(imp).thenReturn(true);
                    })));
    }

    @Override
    public Mono<Boolean> delete(Long id) {
        return TenantContext.get().flatMap(t ->
            cargar(id, t.getEmpresaId()).flatMap(entity -> {
                entity.setDeleted_at(LocalDateTime.now());
                entity.setUsuario_modificacion(t.getUsuarioId());
                return repository.save(entity).thenReturn(true);
            }));
    }

    @Override
    public Mono<ProductoResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepository.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Producto no encontrado")))
                .flatMap(resp -> queryRepository.findImpuestoIds(resp.getId())
                    .map(ids -> { resp.setImpuestoIds(ids); return resp; })));
    }

    @Override
    public Mono<PageResponseDto<ProductoTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepository.list(request, t.getEmpresaId()));
    }

    private Mono<ProductoEntity> cargar(Long id, Long empresaId) {
        return repository.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Producto no encontrado")))
            .flatMap(entity -> {
                if (entity.getDeleted_at() != null || !entity.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
                }
                return Mono.just(entity);
            });
    }

    private Mono<Void> validarCategoria(Long categoriaId, Long empresaId) {
        if (categoriaId == null) {
            return Mono.empty();
        }
        return queryRepository.categoriaExisteEnEmpresa(categoriaId, empresaId)
            .flatMap(ok -> Boolean.TRUE.equals(ok)
                ? Mono.empty()
                : Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "La categoría no existe")));
    }

    /** Flags y tipo son NOT NULL en BD: aplicar defaults si vienen null. */
    private void aplicarDefaults(ProductoEntity e) {
        if (e.getTipo() == null) e.setTipo("bien");
        if (e.getEs_compuesto() == null) e.setEs_compuesto(false);
        if (e.getManeja_inventario() == null) e.setManeja_inventario(true);
        if (e.getManeja_lote() == null) e.setManeja_lote(false);
        if (e.getManeja_desperdicio() == null) e.setManeja_desperdicio(false);
        if (e.getEs_devolutivo() == null) e.setEs_devolutivo(false);
        if (e.getDiscrimina_iva() == null) e.setDiscrimina_iva(false);
        if (e.getAplica_impuesto_bolsa() == null) e.setAplica_impuesto_bolsa(false);
        if (e.getEs_pos() == null) e.setEs_pos(false);
    }
}
