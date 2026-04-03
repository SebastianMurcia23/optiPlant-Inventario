package com.inventario.dto.response;

import com.inventario.model.enums.EstadoVenta;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record VentaDTO(
        Long id,
        String numero,
        String sucursalNombre,
        String vendedorNombre,
        EstadoVenta estado,
        String clienteNombre,
        String clienteDocumento,
        BigDecimal subtotal,
        BigDecimal descuentoTotal,
        BigDecimal total,
        String observaciones,
        LocalDateTime fechaVenta,
        List<DetalleVentaRespDTO> detalles
) {
    public record DetalleVentaRespDTO(
            Long id,
            String productoNombre,
            String productoCodigo,
            Integer cantidad,
            BigDecimal precioUnitario,
            BigDecimal porcentajeDescuento,
            BigDecimal subtotal
    ) {}
}