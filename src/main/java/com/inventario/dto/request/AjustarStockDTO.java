package com.inventario.dto.request;

import com.inventario.model.enums.TipoMovimiento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AjustarStockDTO(
        @NotNull Long productoId,
        @NotNull Long sucursalId,
        @NotNull TipoMovimiento tipo,
        @NotNull @Positive Integer cantidad,
        @NotBlank String motivo,
        String documentoReferencia
) {}