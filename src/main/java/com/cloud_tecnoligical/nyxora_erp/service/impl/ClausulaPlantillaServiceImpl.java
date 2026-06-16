package com.cloud_tecnoligical.nyxora_erp.service.impl;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cloud_tecnoligical.nyxora_erp.dto.common.PageResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.ClausulaPlantillaResponseDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.ClausulaPlantillaTableDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.CreateClausulaPlantillaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.dto.contratacion.UpdateClausulaPlantillaRequestDto;
import com.cloud_tecnoligical.nyxora_erp.entity.ClausulaPlantillaEntity;
import com.cloud_tecnoligical.nyxora_erp.mapper.contratacion.ClausulaPlantillaMapper;
import com.cloud_tecnoligical.nyxora_erp.repository.contratacion.ClausulaPlantillaQueryRepository;
import com.cloud_tecnoligical.nyxora_erp.repository.contratacion.ClausulaPlantillaR2dbcRepository;
import com.cloud_tecnoligical.nyxora_erp.security.TenantContext;
import com.cloud_tecnoligical.nyxora_erp.service.ClausulaPlantillaService;
import com.cloud_tecnoligical.nyxora_erp.util.GlobalException;
import com.cloud_tecnoligical.nyxora_erp.util.PageableDto;

import reactor.core.publisher.Mono;

@Service
public class ClausulaPlantillaServiceImpl implements ClausulaPlantillaService {

    private final ClausulaPlantillaR2dbcRepository repo;
    private final ClausulaPlantillaQueryRepository queryRepo;
    private final ClausulaPlantillaMapper mapper;

    public ClausulaPlantillaServiceImpl(ClausulaPlantillaR2dbcRepository repo,
                                        ClausulaPlantillaQueryRepository queryRepo,
                                        ClausulaPlantillaMapper mapper) {
        this.repo = repo;
        this.queryRepo = queryRepo;
        this.mapper = mapper;
    }

    @Override
    public Mono<ClausulaPlantillaResponseDto> create(CreateClausulaPlantillaRequestDto dto) {
        return TenantContext.get().flatMap(t -> {
            ClausulaPlantillaEntity e = mapper.toEntity(dto);
            e.setEmpresa_id(t.getEmpresaId());
            e.setActivo(true);
            e.setCreated_at(LocalDateTime.now());
            return repo.save(e).flatMap(saved -> findById(saved.getId()));
        });
    }

    @Override
    public Mono<Boolean> update(UpdateClausulaPlantillaRequestDto dto) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(dto.getId(), t.getEmpresaId()).flatMap(e -> {
                e.setTipo_clausula(dto.getTipoClausula());
                e.setNumero(dto.getNumero());
                e.setOrden(dto.getOrden());
                e.setNombre(dto.getNombre());
                e.setTexto(dto.getTexto());
                e.setUpdated_at(LocalDateTime.now());
                return repo.save(e).thenReturn(true);
            }));
    }

    @Override
    public Mono<Boolean> delete(Long id) {
        return TenantContext.get().flatMap(t ->
            cargarEntidad(id, t.getEmpresaId()).flatMap(e -> {
                e.setDeleted_at(LocalDateTime.now());
                return repo.save(e).thenReturn(true);
            }));
    }

    @Override
    public Mono<ClausulaPlantillaResponseDto> findById(Long id) {
        return TenantContext.get().flatMap(t ->
            queryRepo.findActiveById(id, t.getEmpresaId())
                .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Cláusula no encontrada"))));
    }

    @Override
    public Mono<PageResponseDto<ClausulaPlantillaTableDto>> list(PageableDto<?> request) {
        return TenantContext.get().flatMap(t -> queryRepo.list(request, t.getEmpresaId()));
    }

    private Mono<ClausulaPlantillaEntity> cargarEntidad(Long id, Long empresaId) {
        return repo.findById(id)
            .switchIfEmpty(Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Cláusula no encontrada")))
            .flatMap(e -> {
                if (e.getDeleted_at() != null || !e.getEmpresa_id().equals(empresaId)) {
                    return Mono.error(new GlobalException(HttpStatus.NOT_FOUND, "Cláusula no encontrada"));
                }
                return Mono.just(e);
            });
    }
}
