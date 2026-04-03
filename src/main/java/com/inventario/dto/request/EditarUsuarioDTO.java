package com.inventario.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record EditarUsuarioDTO(
        @NotNull Long id,
        @NotBlank @Length(max = 100) String nombre,
        @Length(max = 20) String telefono,
        @Length(min = 8, max = 50) String nuevaPassword
) {}