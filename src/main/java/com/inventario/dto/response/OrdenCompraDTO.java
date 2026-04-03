package com.inventario.dto.response;

import com.inventario.model.enums.EstadoOrdenCompra;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record OrdenCompraDTO(
        Long id,
        String numero,
        String proveedorNombre,
        String sucursalNombre,
        String solicitanteNombre,
        EstadoOrdenCompra estado,
        LocalDateTime fechaCreacion,
        LocalDate fechaEsperadaEntrega,
        LocalDateTime fechaRecepcion,
        BigDecimal subtotal,
        BigDecimal descuentoTotal,
        BigDecimal total,
        Integer plazoPagoDias,
        String observaciones,
        List<DetalleOrdenCompraRespDTO> detalles
) {
    public record DetalleOrdenCompraRespDTO(
            Long id,
            String productoNombre,
            String productoCodigo,
            Integer cantidadSolicitada,
            Integer cantidadRecibida,
            BigDecimal precioUnitario,
            BigDecimal porcentajeDescuento,
            BigDecimal subtotal
    ) {}
}