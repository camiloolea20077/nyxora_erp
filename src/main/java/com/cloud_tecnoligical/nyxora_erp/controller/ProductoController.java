package com.cloud_tecnoligical.nyxora_erp.controller;

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

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.producto.CreateProductoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.producto.ProductoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.producto.ProductoTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.producto.UpdateProductoRequestDto;
import com.cloud_tecnoligical.nyxora_erp.service.ProductoService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "Catálogo de productos (bienes/servicios) (Común)")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear producto (con impuestos adicionales)")
    public Mono<ResponseEntity<ApiResponse<ProductoResponseDto>>> create(@Valid @RequestBody CreateProductoRequestDto dto) {
        return service.create(dto)
            .map(p -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Producto creado", false, p)));
    }

    @PutMapping
    @Operation(summary = "Actualizar producto (reemplaza impuestos si se envían)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(@Valid @RequestBody UpdateProductoRequestDto dto) {
        return service.update(dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Producto actualizado", false, ok)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto (lógico)")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(@PathVariable Long id) {
        return service.delete(id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Producto eliminado", false, ok)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar producto por id (incluye impuestoIds)")
    public Mono<ResponseEntity<ApiResponse<ProductoResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }

    @PostMapping("/list")
    @Operation(summary = "Listar productos (paginado, búsqueda por nombre/código)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<ProductoTableDto>>>> list(@RequestBody PageableDto<Void> request) {
        return service.list(request)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }
}
