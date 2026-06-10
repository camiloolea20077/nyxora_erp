package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.producto.CreateProductoProveedorDto;
import com.cloud_tecnoligical.nyxora_erp.dto.producto.CreateProductoVarianteDto;
import com.cloud_tecnoligical.nyxora_erp.dto.producto.ProductoProveedorResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.producto.ProductoVarianteResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.producto.UpdateProductoProveedorDto;
import com.cloud_tecnoligical.nyxora_erp.dto.producto.UpdateProductoVarianteDto;
import com.cloud_tecnoligical.nyxora_erp.entity.ProductoProveedorEntity;
import com.cloud_tecnoligical.nyxora_erp.entity.ProductoVarianteEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.producto.ProductoProveedorMapper;
import com.cloud_tecnoligical.nyxora_erp.mapper.producto.ProductoVarianteMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.producto.ProductoProveedorR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.producto.ProductoQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.producto.ProductoSatelitesQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.producto.ProductoVarianteR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.ProductoSatelitesService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;

import reactor.core.publisher.Mono;

@Service
public class ProductoSatelitesServiceImpl implements ProductoSatelitesService {

    private final ProductoVarianteR2dbcRepository varianteRepo;
    private final ProductoProveedorR2dbcRepository proveedorRepo;
    private final ProductoVarianteMapper varianteMapper;
    private final ProductoProveedorMapper proveedorMapper;
    private final ProductoQueryRepository productoQuery;
    private final ProductoSatelitesQueryRepository satQuery;

    public ProductoSatelitesServiceImpl(ProductoVarianteR2dbcRepository varianteRepo,
            ProductoProveedorR2dbcRepository proveedorRepo, ProductoVarianteMapper varianteMapper,
            ProductoProveedorMapper proveedorMapper, ProductoQueryRepository productoQuery,
            ProductoSatelitesQueryRepository satQuery) {
        this.varianteRepo = varianteRepo;
        this.proveedorRepo = proveedorRepo;
        this.varianteMapper = varianteMapper;
        this.proveedorMapper = proveedorMapper;
        this.productoQuery = productoQuery;
        this.satQuery = satQuery;
    }

    // ===================== Variantes =====================
    @Override
    public Mono<List<ProductoVarianteResponseDto>> listVariantes(Long productoId) {
        return validarProducto(productoId).then(satQuery.listVariantes(productoId));
    }

    @Override
    public Mono<ProductoVarianteResponseDto> createVariante(Long productoId, CreateProductoVarianteDto dto) {
        return validarProducto(productoId).then(Mono.defer(() -> {
            ProductoVarianteEntity e = varianteMapper.toEntity(dto);
            e.setProducto_id(productoId);
            e.setActivo(true);
            e.setCreated_at(LocalDateTime.now());
            return varianteRepo.save(e).map(varianteMapper::toResponseDto);
        }));
    }

    @Override
    public Mono<Boolean> updateVariante(Long productoId, UpdateProductoVarianteDto dto) {
        return validarProducto(productoId).then(varianteRepo.findById(dto.getId())
            .switchIfEmpty(noEncontrado())
            .flatMap(e -> {
                if (noPertenece(e.getDeleted_at(), e.getProducto_id(), productoId)) return noEncontrado();
                varianteMapper.updateEntityFromDto(dto, e);
                if (dto.getActive() != null) e.setActivo(dto.getActive());
                e.setUpdated_at(LocalDateTime.now());
                return varianteRepo.save(e).thenReturn(true);
            }));
    }

    @Override
    public Mono<Boolean> deleteVariante(Long productoId, Long id) {
        return validarProducto(productoId).then(varianteRepo.findById(id)
            .switchIfEmpty(noEncontrado())
            .flatMap(e -> {
                if (noPertenece(e.getDeleted_at(), e.getProducto_id(), productoId)) return noEncontrado();
                e.setDeleted_at(LocalDateTime.now());
                return varianteRepo.save(e).thenReturn(true);
            }));
    }

    // ===================== Proveedores =====================
    @Override
    public Mono<List<ProductoProveedorResponseDto>> listProveedores(Long productoId) {
        return validarProducto(productoId).then(satQuery.listProveedores(productoId));
    }

    @Override
    public Mono<ProductoProveedorResponseDto> createProveedor(Long productoId, CreateProductoProveedorDto dto) {
        return TenantContext.get().flatMap(t -> validarProductoEmpresa(productoId, t.getEmpresaId())
            .then(satQuery.proveedorExisteEnEmpresa(dto.getProveedorId(), t.getEmpresaId()))
            .flatMap(ok -> {
                if (!Boolean.TRUE.equals(ok)) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "El proveedor no existe"));
                }
                return satQuery.proveedorYaAsignado(productoId, dto.getProveedorId(), null);
            })
            .flatMap(dup -> {
                if (Boolean.TRUE.equals(dup)) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "El proveedor ya está asignado al producto"));
                }
                ProductoProveedorEntity e = proveedorMapper.toEntity(dto);
                e.setProducto_id(productoId);
                e.setActivo(true);
                e.setCreated_at(LocalDateTime.now());
                return proveedorRepo.save(e).map(proveedorMapper::toResponseDto);
            }));
    }

    @Override
    public Mono<Boolean> updateProveedor(Long productoId, UpdateProductoProveedorDto dto) {
        return TenantContext.get().flatMap(t -> validarProductoEmpresa(productoId, t.getEmpresaId())
            .then(proveedorRepo.findById(dto.getId()))
            .switchIfEmpty(noEncontrado())
            .flatMap(e -> {
                if (noPertenece(e.getDeleted_at(), e.getProducto_id(), productoId)) return noEncontrado();
                return satQuery.proveedorExisteEnEmpresa(dto.getProveedorId(), t.getEmpresaId())
                    .flatMap(ok -> {
                        if (!Boolean.TRUE.equals(ok)) {
                            return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "El proveedor no existe"));
                        }
                        return satQuery.proveedorYaAsignado(productoId, dto.getProveedorId(), dto.getId());
                    })
                    .flatMap(dup -> {
                        if (Boolean.TRUE.equals(dup)) {
                            return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "El proveedor ya está asignado al producto"));
                        }
                        proveedorMapper.updateEntityFromDto(dto, e);
                        if (dto.getActive() != null) e.setActivo(dto.getActive());
                        e.setUpdated_at(LocalDateTime.now());
                        return proveedorRepo.save(e).thenReturn(true);
                    });
            }));
    }

    @Override
    public Mono<Boolean> deleteProveedor(Long productoId, Long id) {
        return validarProducto(productoId).then(proveedorRepo.findById(id)
            .switchIfEmpty(noEncontrado())
            .flatMap(e -> {
                if (noPertenece(e.getDeleted_at(), e.getProducto_id(), productoId)) return noEncontrado();
                e.setDeleted_at(LocalDateTime.now());
                return proveedorRepo.save(e).thenReturn(true);
            }));
    }

    // ===================== helpers =====================
    private Mono<Void> validarProducto(Long productoId) {
        return TenantContext.get().flatMap(t -> validarProductoEmpresa(productoId, t.getEmpresaId()));
    }

    private Mono<Void> validarProductoEmpresa(Long productoId, Long empresaId) {
        return productoQuery.existsActivoEnEmpresa(productoId, empresaId)
            .flatMap(ok -> Boolean.TRUE.equals(ok) ? Mono.<Void>empty()
                : Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Producto no encontrado")));
    }

    private boolean noPertenece(LocalDateTime deletedAt, Long productoIdEntity, Long productoIdPath) {
        return deletedAt != null || !productoIdEntity.equals(productoIdPath);
    }

    private <T> Mono<T> noEncontrado() {
        return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Registro no encontrado"));
    }
}
