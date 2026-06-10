package com.cloud_tecnoligical.nyxora_erp.dto.documento;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsecutivoResponseDto {
    private Long tipoDocumentoId;
    private Long sedeId;
    private Long vigenciaId;
    private Long numero;
    private String numeroFormateado;

    public ConsecutivoResponseDto() {
    }

    public ConsecutivoResponseDto(Long tipoDocumentoId, Long sedeId, Long vigenciaId, Long numero, String numeroFormateado) {
        this.tipoDocumentoId = tipoDocumentoId;
        this.sedeId = sedeId;
        this.vigenciaId = vigenciaId;
        this.numero = numero;
        this.numeroFormateado = numeroFormateado;
    }
}
