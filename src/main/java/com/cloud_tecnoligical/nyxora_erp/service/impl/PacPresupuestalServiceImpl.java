package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.PacPresupuestalResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.presupuesto.PacUpsertRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.PacPresupuestalEntity;
import com.cloud_tecnoligical.nyxora_erp.repository.presupuesto.PacPresupuestalQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.presupuesto.PacPresupuestalR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.presupuesto.RubroPresupuestalR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.PacPresupuestalService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;

import reactor.core.publisher.Mono;

@Service
public class PacPresupuestalServiceImpl implements PacPresupuestalService {

    private final PacPresupuestalR2dbcRepository repo;
    private final PacPresupuestalQueryRepository queryRepo;
    private final RubroPresupuestalR2dbcRepository rubroRepo;

    public PacPresupuestalServiceImpl(PacPresupuestalR2dbcRepository repo,
                                      PacPresupuestalQueryRepository queryRepo,
                                      RubroPresupuestalR2dbcRepository rubroRepo) {
        this.repo = repo;
        this.queryRepo = queryRepo;
        this.rubroRepo = rubroRepo;
    }

    @Override
    public Mono<PacPresupuestalResponseDto> upsert(PacUpsertRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            validarRubro(dto.getRubroPresupuestalId(), t.getEmpresaId())
                .then(queryRepo.findIdByRubroAnioMes(dto.getRubroPresupuestalId(), dto.getAnio(), dto.getMes(), t.getEmpresaId())
                    .flatMap(repo::findById)
                    .switchIfEmpty(Mono.defer(() -> {
                        PacPresupuestalEntity p = new PacPresupuestalEntity();
                        p.setEmpresa_id(t.getEmpresaId());
                        p.setRubro_presupuestal_id(dto.getRubroPresupuestalId());
                        p.setAnio(dto.getAnio());
                        p.setMes(dto.getMes());
                        return Mono.just(p);
                    }))
                    .flatMap(p -> {
                        p.setValor(nz(dto.getValor()));
                        return repo.save(p);
                    }))
                .flatMap(saved -> queryRepo.listByRubroAnio(saved.getRubro_presupuestal_id(), saved.getAnio(), t.getEmpresaId())
                    .map(list -> list.stream().filter(x -> x.getMes().equals(saved.getMes())).findFirst().orElse(null))));
    }

    @Override
    public Mono<List<PacPresupuestalResponseDto>> listByRubroAnio(Long rubroId, Integer anio) {
        return TenantContext.get().flatMap(t -> queryRepo.listByRubroAnio(rubroId, anio, t.getEmpresaId()));
    }

    private Mono<Void> validarRubro(Long rubroId, Long empresaId) {
        return rubroRepo.findById(rubroId)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "El rubro presupuestal no existe")))
            .flatMap(r -> {
                if (r.getDeleted_at() != null || !r.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.BAD_REQUEST, "El rubro presupuestal no existe"));
                }
                return Mono.<Void>empty();
            });
    }

    private static BigDecimal nz(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }
}
