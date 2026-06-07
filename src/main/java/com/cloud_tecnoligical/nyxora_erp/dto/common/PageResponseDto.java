package com.cloud_tecnoligical.nyxora_erp.dto.common;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Respuesta de paginación para listados reactivos (reemplaza PageImpl en WebFlux).
 */
@Getter
@Setter
public class PageResponseDto<T> {

    private List<T> content;
    private long page;
    private long rows;
    private long total;

    public PageResponseDto(List<T> content, long page, long rows, long total) {
        this.content = content;
        this.page = page;
        this.rows = rows;
        this.total = total;
    }
}
