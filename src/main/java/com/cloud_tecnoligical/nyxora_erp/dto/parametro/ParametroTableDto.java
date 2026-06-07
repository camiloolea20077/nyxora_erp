package com.cloud_tecnoligical.nyxora_erp.dto.parametro;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParametroTableDto {
    private Long id;
    private String key;
    private String value;
    private String dataType;
    private Boolean active;
}
