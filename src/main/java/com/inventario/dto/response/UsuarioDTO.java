package com.inventario.dto.response;

import com.inventario.model.enums.EstadoUsuario;
import com.inventario.model.enums.RolUsuario;

import java.time.LocalDateTime;

public record UsuarioDTO(
        Long id,
        String nombre,
        String email,
        String telefono,
        RolUsuario rol,
        EstadoUsuario estado,
        Long sucursalId,
        String nombreSucursal,
        LocalDateTime fechaRegistro
) {}