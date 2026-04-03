package com.inventario.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record CrearCategoriaDTO(
        @NotBlank @Length(max = 100) String nombre,
        @Length(max = 255) String descripcion
) {}