package com.inventario.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InventarioItemDTO(
        Long id,
        Long productoId,
        String productoCodigo,
        String productoNombre,
        String unidadMedidaAbreviatura,
        Long sucursalId,
        String sucursalNombre,
        Integer cantidadDisponible,
        Integer cantidadReservada,
        Integer cantidadReal,
        Integer stockMinimo,
        Integer stockMaximo,
        BigDecimal costoPromedioLocal,
        boolean stockBajo,
        LocalDateTime ultimaActualizacion
) {}