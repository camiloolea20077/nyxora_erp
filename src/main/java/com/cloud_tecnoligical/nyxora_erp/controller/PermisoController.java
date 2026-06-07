package com.cloud_tecnoligical.nyxora_erp.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloud_tecnoligical.nyxora_erp.dto.permiso.PermisoDto;
import com.cloud_tecnoligical.nyxora_erp.repository.permiso.PermisoQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.util.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/permisos")
@Tag(name = "Permisos", description = "Catálogo global de permisos (Administración)")
public class PermisoController {

    private final PermisoQueryRepository permisoQueryRepository;

    public PermisoController(PermisoQueryRepository permisoQueryRepository) {
        this.permisoQueryRepository = permisoQueryRepository;
    }

    @GetMapping
    @Operation(summary = "Listar todos los permisos")
    public Mono<ResponseEntity<ApiResponse<List<PermisoDto>>>> listAll() {
        return permisoQueryRepository.listAll()
            .map(list -> ResponseEntity.ok(new ApiResponse<>(200, "OK", false, list)));
    }
}
