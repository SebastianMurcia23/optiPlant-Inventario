package com.inventario.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.List;

public record CrearVentaDTO(
        @NotNull Long sucursalId,
        @Length(max = 150) String clienteNombre,
        @Length(max = 30) String clienteDocumento,
        @Length(max = 300) String observaciones,
        @NotEmpty List<DetalleVentaDTO> detalles
) {
    public record DetalleVentaDTO(
            @NotNull Long productoId,
            @NotNull @Positive Integer cantidad,
            @NotNull @Positive BigDecimal precioUnitario,
            BigDecimal porcentajeDescuento
    ) {}
}