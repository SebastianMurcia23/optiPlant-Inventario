package com.inventario.dto.response;

import java.time.LocalDateTime;

public record SucursalDTO(
        Long id,
        String nombre,
        String direccion,
        String telefono,
        String ciudad,
        String pais,
        boolean activa,
        LocalDateTime fechaCreacion
) {}