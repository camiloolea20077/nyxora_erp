package com.cloud_tecnoligical.nyxora_erp.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloud_tecnoligical.nyxora_erp.dto.inventario.CreateMovimientoInventarioDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.KardexItemDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.MovimientoInventarioResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.inventario.TrasladoInventarioDto;
import com.cloud_tecnoligical.nyxora_erp.service.MovimientoInventarioService;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/movimientos-inventario")
@Tag(name = "Movimientos de inventario", description = "Entradas/salidas/ajustes/traslados (append-only) y kardex (Inventario)")
public class MovimientoInventarioController {

    private final MovimientoInventarioService service;

    public MovimientoInventarioController(MovimientoInventarioService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Registrar movimiento (entrada/salida/ajuste)")
    public Mono<ResponseEntity<ApiResponse<MovimientoInventarioResponseDto>>> registrar(@Valid @RequestBody CreateMovimientoInventarioDto dto) {
        return service.registrar(dto)
            .map(m -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Movimiento registrado", false, m)));
    }

    @PostMapping("/traslado")
    @Operation(summary = "Traslado entre bodegas (genera 2 movimientos)")
    public Mono<ResponseEntity<ApiResponse<List<MovimientoInventarioResponseDto>>>> traslado(@Valid @RequestBody TrasladoInventarioDto dto) {
        return service.traslado(dto)
            .map(l -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Traslado registrado", false, l)));
    }

    @PostMapping("/{id}/reversar")
    @Operation(summary = "Reversar un movimiento (genera movimiento inverso)")
    public Mono<ResponseEntity<ApiResponse<MovimientoInventarioResponseDto>>> reversar(@PathVariable Long id) {
        return service.reversar(id)
            .map(m -> ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Movimiento reversado", false, m)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar movimiento por id")
    public Mono<ResponseEntity<ApiResponse<MovimientoInventarioResponseDto>>> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(m -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, m)));
    }

    @GetMapping("/kardex")
    @Operation(summary = "Kardex de un producto (con saldo corriente)")
    public Mono<ResponseEntity<ApiResponse<List<KardexItemDto>>>> kardex(
            @RequestParam Long productoId, @RequestParam(required = false) Long bodegaId) {
        return service.kardex(productoId, bodegaId)
            .map(l -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, l)));
    }
}
