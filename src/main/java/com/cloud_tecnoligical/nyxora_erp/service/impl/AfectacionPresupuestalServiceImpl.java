package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.AfectacionPresupuestalResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.AfectacionPresupuestalTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.CreateAfectacionPresupuestalRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.AfectacionPresupuestalEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.presupuesto.AfectacionPresupuestalMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.presupuesto.AfectacionPresupuestalQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.presupuesto.AfectacionPresupuestalR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.AfectacionPresupuestalService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class AfectacionPresupuestalServiceImpl implements AfectacionPresupuestalService {

    private static final Set<String> TIPOS = Set.of(
        "disponibilidad", "compromiso", "obligacion", "pago", "reconocimiento", "recaudo");

    private final AfectacionPresupuestalR2dbcRepository repo;
    private final AfectacionPresupuestalQueryRepository queryRepo;
    private final AfectacionPresupuestalMapper mapper;

    public AfectacionPresupuestalServiceImpl(AfectacionPresupuestalR2dbcRepository repo,
                                             AfectacionPresupuestalQueryRepository queryRepo,
                                             AfectacionPresupuestalMapper mapper) {
        this.repo = repo;
        this.queryRepo = queryRepo;
        this.mapper = mapper;
    }

    @Override
    public Mono<AfectacionPresupuestalResponseDto> registrar(CreateAfectacionPresupuestalRequestDto dto) {
        if (!TIPOS.contains(dto.getTipoOperacion())) {
            return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "Tipo de operación inválido"));
        }
        return TenantContext.get().flatMap(t ->
            queryRepo.rubroManejaMovimiento(dto.getRubroPresupuestalId(), t.getEmpresaId()).flatMap(ok -> {
                if (!Boolean.TRUE.equals(ok)) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST,
                        "El rubro no existe o no maneja movimiento (debe ser un rubro imputable)"));
                }
                AfectacionPresupuestalEntity e = new AfectacionPresupuestalEntity();
                e.setEmpresa_id(t.getEmpresaId());
                e.setRubro_presupuestal_id(dto.getRubroPresupuestalId());
                e.setTipo_operacion(dto.getTipoOperacion());
                e.setTercero_id(dto.getTerceroId());
                e.setCentro_costo_id(dto.getCentroCostoId());
                e.setProyecto_id(dto.getProyectoId());
                e.setFuente_financiamiento_id(dto.getFuenteFinanciamientoId());
                e.setCpc_id(dto.getCpcId());
                e.setDescripcion(dto.getDescripcion());
                e.setValor(dto.getValor());
                e.setSubtotal(dto.getValor());
                e.setSaldo(dto.getValor());
                e.setOrigen_modulo("presupuesto");
                e.setCreated_at(LocalDateTime.now());
                e.setUsuario_creacion(t.getUsuarioId());
                return repo.save(e).flatMap(saved -> findById(saved.getId()));
            }));
    }

    @Override
    public Mono<AfectacionPresupuestalResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepo.findById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Afectación no encontrada"))));
    }

    @Override
    public Mono<PageResponseDto<AfectacionPresupuestalTableDto>> listByRubro(Long rubroId, PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepo.listByRubro(request, rubroId, t.getEmpresaId()));
    }
}
