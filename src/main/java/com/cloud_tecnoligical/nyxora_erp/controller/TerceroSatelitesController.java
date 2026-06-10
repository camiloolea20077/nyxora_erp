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

import com.cloud_tecnoligical.nyxora_erp.dto.tercero.CreateTerceroContactoDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.CreateTerceroCuentaBancariaDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.CreateTerceroDireccionDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.TerceroContactoResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.TerceroCuentaBancariaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.TerceroDireccionResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.UpdateTerceroContactoDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.UpdateTerceroCuentaBancariaDto;
import com.cloud_tecnoligical.nyxora_erp.dto.tercero.UpdateTerceroDireccionDto;
import com.cloud_tecnoligical.nyxora_erp.service.TerceroSatelitesService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/terceros/{terceroId}")
@Tag(name = "Terceros - Satélites", description = "Contactos, direcciones y cuentas bancarias del tercero")
public class TerceroSatelitesController {

    private final TerceroSatelitesService service;

    public TerceroSatelitesController(TerceroSatelitesService service) {
        this.service = service;
    }

    // ---------- Contactos ----------
    @GetMapping("/contactos")
    @Operation(summary = "Listar contactos del tercero")
    public Mono<ResponseEntity<ApiResponse<List<TerceroContactoResponseDto>>>> listContactos(@PathVariable Long terceroId) {
        return service.listContactos(terceroId).map(l -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, l)));
    }

    @PostMapping("/contactos")
    @Operation(summary = "Agregar contacto al tercero")
    public Mono<ResponseEntity<ApiResponse<TerceroContactoResponseDto>>> createContacto(
            @PathVariable Long terceroId, @Valid @RequestBody CreateTerceroContactoDto dto) {
        return service.createContacto(terceroId, dto)
            .map(c -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Contacto creado", false, c)));
    }

    @PutMapping("/contactos")
    @Operation(summary = "Actualizar contacto del tercero")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> updateContacto(
            @PathVariable Long terceroId, @Valid @RequestBody UpdateTerceroContactoDto dto) {
        return service.updateContacto(terceroId, dto).map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Contacto actualizado", false, ok)));
    }

    @DeleteMapping("/contactos/{id}")
    @Operation(summary = "Eliminar contacto del tercero")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> deleteContacto(@PathVariable Long terceroId, @PathVariable Long id) {
        return service.deleteContacto(terceroId, id).map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Contacto eliminado", false, ok)));
    }

    // ---------- Direcciones ----------
    @GetMapping("/direcciones")
    @Operation(summary = "Listar direcciones del tercero")
    public Mono<ResponseEntity<ApiResponse<List<TerceroDireccionResponseDto>>>> listDirecciones(@PathVariable Long terceroId) {
        return service.listDirecciones(terceroId).map(l -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, l)));
    }

    @PostMapping("/direcciones")
    @Operation(summary = "Agregar dirección al tercero")
    public Mono<ResponseEntity<ApiResponse<TerceroDireccionResponseDto>>> createDireccion(
            @PathVariable Long terceroId, @Valid @RequestBody CreateTerceroDireccionDto dto) {
        return service.createDireccion(terceroId, dto)
            .map(d -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Dirección creada", false, d)));
    }

    @PutMapping("/direcciones")
    @Operation(summary = "Actualizar dirección del tercero")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> updateDireccion(
            @PathVariable Long terceroId, @Valid @RequestBody UpdateTerceroDireccionDto dto) {
        return service.updateDireccion(terceroId, dto).map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Dirección actualizada", false, ok)));
    }

    @DeleteMapping("/direcciones/{id}")
    @Operation(summary = "Eliminar dirección del tercero")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> deleteDireccion(@PathVariable Long terceroId, @PathVariable Long id) {
        return service.deleteDireccion(terceroId, id).map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Dirección eliminada", false, ok)));
    }

    // ---------- Cuentas bancarias ----------
    @GetMapping("/cuentas-bancarias")
    @Operation(summary = "Listar cuentas bancarias del tercero")
    public Mono<ResponseEntity<ApiResponse<List<TerceroCuentaBancariaResponseDto>>>> listCuentas(@PathVariable Long terceroId) {
        return service.listCuentas(terceroId).map(l -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, l)));
    }

    @PostMapping("/cuentas-bancarias")
    @Operation(summary = "Agregar cuenta bancaria al tercero")
    public Mono<ResponseEntity<ApiResponse<TerceroCuentaBancariaResponseDto>>> createCuenta(
            @PathVariable Long terceroId, @Valid @RequestBody CreateTerceroCuentaBancariaDto dto) {
        return service.createCuenta(terceroId, dto)
            .map(c -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Cuenta creada", false, c)));
    }

    @PutMapping("/cuentas-bancarias")
    @Operation(summary = "Actualizar cuenta bancaria del tercero")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> updateCuenta(
            @PathVariable Long terceroId, @Valid @RequestBody UpdateTerceroCuentaBancariaDto dto) {
        return service.updateCuenta(terceroId, dto).map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Cuenta actualizada", false, ok)));
    }

    @DeleteMapping("/cuentas-bancarias/{id}")
    @Operation(summary = "Eliminar cuenta bancaria del tercero")
    public Mono<ResponseEntity<ApiResponse<Boolean>>> deleteCuenta(@PathVariable Long terceroId, @PathVariable Long id) {
        return service.deleteCuenta(terceroId, id).map(ok -> ResponseEntity.ok(new ApiResponse<>(200, "Cuenta eliminada", false, ok)));
    }
}
