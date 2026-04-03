package com.inventario.dto.response;

public record ProveedorDTO(
        Long id,
        String nombre,
        String nitRuc,
        String email,
        String telefono,
        String ciudad,
        String personaContacto,
        Integer plazoPagoDias,
        Double calificacionPromedio,
        boolean activo
) {}