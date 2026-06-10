package com.cloud_tecnoligical.nyxora_erp.dto.documento;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/** La sede se toma del JWT (TenantContext); aquí solo la vigencia. */
@Getter
@Setter
public class ConsecutivoRequestDto {

    @NotNull(message = "La vigencia es obligatoria")
    private Long vigenciaId;

    /** Sede opcional; si no se envía se usa la del JWT. */
    private Long sedeId;
}
