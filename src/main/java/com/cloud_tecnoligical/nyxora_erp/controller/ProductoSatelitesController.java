package com.cloud_tecnoligical.nyxora_erp.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloud_tecnoligical.nyxora_erp.dto.producto.CreateProductoProveedorDto;
import com.cloud_tecnoligical.nyxora_erp.dto.producto.CreateProductoVarianteDto;
import com.cloud_tecnoligical.nyxora_erp.dto.producto.ProductoProveedorResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.producto.ProductoVarianteResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.producto.UpdateProductoProveedorDto;
import com.cloud_tecnoligical.nyxora_erp.dto.producto.UpdateProductoVarianteDto;
import com.cloud_tecnoligical.nyxora_erp.service.ProductoSatelitesService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/productos/{productoId}")
@Tag(name = "Productos - Satélites", description = "Variantes y proveedores del producto")
public class ProductoSatelitesController {

    private final ProductoSatelitesService service;

    public ProductoSatelitesController(ProductoSatelitesService service) {
        this.service = service;
    }

    // ---------- Variantes ----------
    @GetMapping("/variantes")
    @Operation(summary = "Listar variantes del producto")
    public Mono<ResponseEntity<ApiResponse<List<ProductoVarianteResponseDto>>>> listVariantes(@PathVariable Long productoId) {
        return service.listVariantes(productoId).map(l -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, l)));
    }

    @PostMapping("/variantes")
    @Operation(summary = "Agregar variante al producto")
    public Mono<ResponseEntity<ApiResponse<ProductoVarianteResponseDto>>> createVariante(
            @PathVariable Long productoId, @Valid @RequestBody CreateProductoVarianteDto dto) {
        return service.createVariante(productoId, dto)
            .map(v -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Variante creada", false, v)));
    }

    @PutMapping("/variantes")
    @Operation(summary = "Actualizar variante del producto")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> updateVariante(
            @PathVariable Long productoId, @Valid @RequestBody UpdateProductoVarianteDto dto) {
        return service.updateVariante(productoId, dto).map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Variante actualizada", false, ok)));
    }

    @DeleteMapping("/variantes/{id}")
    @Operation(summary = "Eliminar variante del producto")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> deleteVariante(@PathVariable Long productoId, @PathVariable Long id) {
        return service.deleteVariante(productoId, id).map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Variante eliminada", false, ok)));
    }

    // ---------- Proveedores ----------
    @GetMapping("/proveedores")
    @Operation(summary = "Listar proveedores del producto")
    public Mono<ResponseEntity<ApiResponse<List<ProductoProveedorResponseDto>>>> listProveedores(@PathVariable Long productoId) {
        return service.listProveedores(productoId).map(l -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, l)));
    }

    @PostMapping("/proveedores")
    @Operation(summary = "Agregar proveedor al producto")
    public Mono<ResponseEntity<ApiResponse<ProductoProveedorResponseDto>>> createProveedor(
            @PathVariable Long productoId, @Valid @RequestBody CreateProductoProveedorDto dto) {
        return service.createProveedor(productoId, dto)
            .map(p -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Proveedor agregado", false, p)));
    }

    @PutMapping("/proveedores")
    @Operation(summary = "Actualizar proveedor del producto")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> updateProveedor(
            @PathVariable Long productoId, @Valid @RequestBody UpdateProductoProveedorDto dto) {
        return service.updateProveedor(productoId, dto).map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Proveedor actualizado", false, ok)));
    }

    @DeleteMapping("/proveedores/{id}")
    @Operation(summary = "Eliminar proveedor del producto")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> deleteProveedor(@PathVariable Long productoId, @PathVariable Long id) {
        return service.deleteProveedor(productoId, id).map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Proveedor eliminado", false, ok)));
    }
}
