package com.inventario.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CrearOrdenCompraDTO(
        @NotNull Long proveedorId,
        @NotNull Long sucursalId,
        LocalDate fechaEsperadaEntrega,
        Integer plazoPagoDias,
        @Length(max = 500) String observaciones,
        @NotEmpty List<DetalleOrdenCompraDTO> detalles
) {
    public record DetalleOrdenCompraDTO(
            @NotNull Long productoId,
            @NotNull @Positive Integer cantidadSolicitada,
            @NotNull @Positive BigDecimal precioUnitario,
            BigDecimal porcentajeDescuento
    ) {}
}