package com.inventario.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

public record CrearProductoDTO(
        @NotBlank @Length(max = 50) String codigo,
        @NotBlank @Length(max = 150) String nombre,
        @Length(max = 500) String descripcion,
        Long categoriaId,
        @NotNull Long unidadMedidaId,
        @Positive BigDecimal precioVentaReferencia,
        Integer stockMinimoGlobal,
        boolean tieneFechaVencimiento,
        Integer diasAlertaVencimiento
) {}