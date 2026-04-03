package com.inventario.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RecibirTransferenciaDTO(
        @NotNull Long transferenciaId,
        @NotEmpty List<DetalleRecepcionTransferenciaDTO> detalles,
        String observacionesRecepcion
) {
    public record DetalleRecepcionTransferenciaDTO(
            @NotNull Long detalleTransferenciaId,
            @NotNull Integer cantidadRecibida,
            String tratamientoFaltante,
            String observaciones
    ) {}
}