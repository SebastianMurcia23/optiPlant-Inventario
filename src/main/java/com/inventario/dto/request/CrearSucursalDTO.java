package com.inventario.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record CrearSucursalDTO(
        @NotBlank @Length(max = 100) String nombre,
        @Length(max = 200) String direccion,
        @Length(max = 20) String telefono,
        @Length(max = 100) String ciudad,
        @Length(max = 100) String pais
) {}