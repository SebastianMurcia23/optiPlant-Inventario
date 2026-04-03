package com.inventario.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.List;

public record DespacharTransferenciaDTO(
        @NotNull Long transferenciaId,
        @Length(max = 100) String transportista,
        LocalDate fechaEstimadaLlegada,
        Integer tiempoEstimadoHoras,
        @NotEmpty List<DetalleDespachoDTO> detalles,
        String observaciones
) {
    public record DetalleDespachoDTO(
            @NotNull Long detalleTransferenciaId,
            @NotNull Integer cantidadEnviada
    ) {}
}