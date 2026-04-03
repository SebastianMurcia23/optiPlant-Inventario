package com.inventario.dto.request;

import com.inventario.model.enums.PrioridadTransferencia;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

import java.util.List;

public record CrearTransferenciaDTO(
        @NotNull Long sucursalOrigenId,
        @NotNull Long sucursalDestinoId,
        PrioridadTransferencia prioridad,
        @Length(max = 500) String observaciones,
        @NotEmpty List<DetalleTransferenciaDTO> detalles
) {
    public record DetalleTransferenciaDTO(
            @NotNull Long productoId,
            @NotNull @Positive Integer cantidadSolicitada
    ) {}
}