package com.inventario.dto.request;

import com.inventario.model.enums.RolUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record CrearUsuarioDTO(
        @NotBlank @Length(max = 100) String nombre,
        @NotBlank @Email @Length(max = 150) String email,
        @NotBlank @Length(min = 8, max = 50) String password,
        @Length(max = 20) String telefono,
        @NotNull RolUsuario rol,
        Long sucursalId
) {}