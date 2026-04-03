package com.inventario.dto.response;

public record TokenDTO(
        String token,
        String tipo,
        String email,
        String rol,
        Long sucursalId,
        String nombreSucursal
) {}