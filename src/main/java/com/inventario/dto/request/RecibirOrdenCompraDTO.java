package com.inventario.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RecibirOrdenCompraDTO(
        @NotNull Long ordenCompraId,
        @NotEmpty List<DetalleRecepcionDTO> detalles,
        String observaciones
) {
    public record DetalleRecepcionDTO(
            @NotNull Long detalleOrdenCompraId,
            @NotNull Integer cantidadRecibida
    ) {}
}