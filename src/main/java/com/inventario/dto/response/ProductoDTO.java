package com.inventario.dto.response;

import java.math.BigDecimal;

public record ProductoDTO(
        Long id,
        String codigo,
        String nombre,
        String descripcion,
        String categoriaNombre,
        String unidadMedidaNombre,
        String unidadMedidaAbreviatura,
        BigDecimal precioVentaReferencia,
        BigDecimal costoPromedio,
        Integer stockMinimoGlobal,
        boolean tieneFechaVencimiento,
        Integer diasAlertaVencimiento,
        boolean activo
) {}