package com.inventario.dto.response;

import com.inventario.model.enums.TipoMovimiento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimientoDTO(
        Long id,
        String productoNombre,
        String productoCodigo,
        String sucursalNombre,
        TipoMovimiento tipo,
        Integer cantidad,
        Integer cantidadAnterior,
        Integer cantidadPosterior,
        BigDecimal costoUnitario,
        String motivo,
        String documentoReferencia,
        String responsableNombre,
        LocalDateTime fecha
) {}