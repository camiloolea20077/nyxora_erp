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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloud_tecnoligical.nyxora_erp.dto.catalogo.CatalogoCrudDto;
import com.cloud_tecnoligical.nyxora_erp.dto.catalogo.CatalogoItemDto;
import com.cloud_tecnoligical.nyxora_erp.dto.catalogo.UbicacionMunicipioDto;
import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.repository.catalogo.CatalogoQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/catalogos")
@Tag(name = "Catálogos", description = "Búsqueda y CRUD genérico de catálogos globales + cascade geográfico")
public class CatalogoController {

    private final CatalogoQueryRepository queryRepository;

    public CatalogoController(CatalogoQueryRepository queryRepository) {
        this.queryRepository = queryRepository;
    }

    @PostMapping("/{nombre}/list")
    @Operation(summary = "Buscar en un catálogo (parentId para cascade; soloActivos=false para administrar)")
    public Mono<ResponseEntity<ApiResponse<PageResponseDto<CatalogoItemDto>>>> list(
            @PathVariable String nombre,
            @RequestBody PageableDto<Void> request,
            @RequestParam(required = false) Long parentId,
            @RequestParam(required = false, defaultValue = "true") boolean soloActivos) {
        return queryRepository.list(nombre, request, parentId, soloActivos)
            .map(p -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, p)));
    }

    @GetMapping("/{nombre}/{id}")
    @Operation(summary = "Obtener un ítem de catálogo por id")
    public Mono<ResponseEntity<ApiResponse<CatalogoItemDto>>> byId(
            @PathVariable String nombre, @PathVariable Long id) {
        return queryRepository.byId(nombre, id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Ítem no encontrado")))
            .map(item -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, item)));
    }

    @PostMapping("/{nombre}")
    @Operation(summary = "Crear un ítem en un catálogo editable")
    public Mono<ResponseEntity<ApiResponse<CatalogoItemDto>>> create(
            @PathVariable String nombre, @Valid @RequestBody CatalogoCrudDto dto) {
        return queryRepository.create(nombre, dto)
            .map(item -> ResponseEntity.ok(new ApiResponse<>(200, "Creado", false, item)));
    }

    @PutMapping("/{nombre}")
    @Operation(summary = "Actualizar un ítem de un catálogo editable")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> update(
            @PathVariable String nombre, @Valid @RequestBody CatalogoCrudDto dto) {
        return queryRepository.update(nombre, dto)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Actualizado", false, ok)));
    }

    @DeleteMapping("/{nombre}/{id}")
    @Operation(summary = "Eliminar un ítem de un catálogo editable")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> delete(
            @PathVariable String nombre, @PathVariable Long id) {
        return queryRepository.delete(nombre, id)
            .map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Eliminado", false, ok)));
    }

    @GetMapping("/municipio/{id}/ubicacion")
    @Operation(summary = "Departamento y país de un municipio (para el cascade)")
    public Mono<ResponseEntity<ApiResponse<UbicacionMunicipioDto>>> ubicacionMunicipio(@PathVariable Long id) {
        return queryRepository.ubicacionMunicipio(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Municipio no encontrado")))
            .map(u -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, u)));
    }
}
