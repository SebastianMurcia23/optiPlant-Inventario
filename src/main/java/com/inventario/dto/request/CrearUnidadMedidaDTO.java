package com.inventario.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record CrearUnidadMedidaDTO(
        @NotBlank @Length(max = 50) String nombre,
        @NotBlank @Length(max = 10) String abreviatura
) {}