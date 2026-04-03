package com.inventario.dto;

public record MensajeDTO<T>(
        boolean error,
        T respuesta
) {}