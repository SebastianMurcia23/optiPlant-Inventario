package com.inventario.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record DashboardDTO(
        // Ventas
        BigDecimal totalVentasMesActual,
        BigDecimal totalVentasMesAnterior,
        Double porcentajeCambioVentas,

        // Inventario
        Integer totalProductos,
        Integer productosConStockBajo,
        Integer productosAgotados,
        Integer productosProximosAVencer,

        // Transferencias activas
        Long transferenciasEnCurso,
        Long transferenciasUrgentes,

        // Alertas
        Long alertasActivas,

        // Top productos
        List<TopProductoDTO> topProductosVendidos,

        // Ventas mensuales para gráfica
        List<VentaMensualDTO> ventasMensuales,

        // Comparativa entre sucursales (solo admin)
        List<ComparativaSucursalDTO> comparativaSucursales
) {
    public record TopProductoDTO(
            Long productoId,
            String productoNombre,
            Long totalVendido
    ) {}

    public record VentaMensualDTO(
            Integer mes,
            String nombreMes,
            BigDecimal total
    ) {}

    public record ComparativaSucursalDTO(
            Long sucursalId,
            String sucursalNombre,
            BigDecimal totalVentas,
            Integer totalMovimientos
    ) {}
}