package com.inventario.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record CrearProveedorDTO(
        @NotBlank @Length(max = 150) String nombre,
        @Length(max = 30) String nitRuc,
        @Email @Length(max = 150) String email,
        @Length(max = 20) String telefono,
        @Length(max = 255) String direccion,
        @Length(max = 100) String ciudad,
        @Length(max = 100) String personaContacto,
        Integer plazoPagoDias
) {}