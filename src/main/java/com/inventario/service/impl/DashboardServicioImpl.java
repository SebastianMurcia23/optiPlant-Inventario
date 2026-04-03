package com.inventario.service.impl;

import com.inventario.dto.response.DashboardDTO;
import com.inventario.dto.response.PrediccionDemandaDTO;
import com.inventario.model.InventarioItem;
import com.inventario.repository.*;
import com.inventario.service.interfaces.DashboardServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DashboardServicioImpl implements DashboardServicio {

    private final VentaRepository ventaRepository;
    private final InventarioItemRepository inventarioItemRepository;
    private final TransferenciaRepository transferenciaRepository;
    private final AlertaRepository alertaRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final LoteProductoRepository loteProductoRepository;
    private final SucursalRepository sucursalRepository;

    @Override
    public DashboardDTO obtenerDashboardSucursal(Long sucursalId) {
        LocalDateTime inicioMesActual = LocalDateTime.now()
                .withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime inicioMesAnterior = inicioMesActual.minusMonths(1);
        LocalDateTime finMesAnterior = inicioMesActual.minusSeconds(1);

        // Ventas
        BigDecimal ventasMesActual = ventaRepository
                .findTotalVentasBySucursalEnRango(sucursalId, inicioMesActual, LocalDateTime.now());
        BigDecimal ventasMesAnterior = ventaRepository
                .findTotalVentasBySucursalEnRango(sucursalId, inicioMesAnterior, finMesAnterior);

        double pctCambio = calcularPorcentajeCambio(ventasMesAnterior, ventasMesActual);

        // Inventario
        List<InventarioItem> items = inventarioItemRepository
                .findInventarioCompletoSucursal(sucursalId);
        long stockBajo = items.stream().filter(InventarioItem::tieneStockBajo).count();
        long agotados = items.stream()
                .filter(i -> i.getCantidadDisponible() == 0).count();

        // Vencimientos próximos
        long proximosAVencer = loteProductoRepository
                .findProximosAVencer(sucursalId, LocalDate.now().plusDays(30)).size();

        // Transferencias
        long transferenciasEnCurso = transferenciaRepository
                .findActivasBySucursal(sucursalId).size();
        long urgentes = transferenciaRepository
                .findUrgentesEnEspera(sucursalId).size();

        // Alertas
        long alertasActivas = alertaRepository.countActivasBySucursal(sucursalId);

        // Top productos
        List<DashboardDTO.TopProductoDTO> topProductos = ventaRepository
                .findTopProductosVendidos(sucursalId, inicioMesActual, LocalDateTime.now())
                .stream()
                .limit(5)
                .map(row -> new DashboardDTO.TopProductoDTO(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        ((Number) row[2]).longValue()))
                .toList();

        // Ventas mensuales
        List<DashboardDTO.VentaMensualDTO> ventasMensuales = ventaRepository
                .findVentasMensualesBySucursal(sucursalId, LocalDateTime.now().getYear())
                .stream()
                .map(row -> new DashboardDTO.VentaMensualDTO(
                        ((Number) row[0]).intValue(),
                        Month.of(((Number) row[0]).intValue())
                                .getDisplayName(TextStyle.FULL, new Locale("es")),
                        (BigDecimal) row[1]))
                .toList();

        return new DashboardDTO(
                ventasMesActual, ventasMesAnterior, pctCambio,
                items.size(), (int) stockBajo, (int) agotados, (int) proximosAVencer,
                transferenciasEnCurso, urgentes, alertasActivas,
                topProductos, ventasMensuales, List.of()
        );
    }

    @Override
    public DashboardDTO obtenerDashboardGeneral() {
        // Dashboard consolidado de todas las sucursales
        List<DashboardDTO.ComparativaSucursalDTO> comparativa = sucursalRepository
                .findByActivaTrue()
                .stream()
                .map(s -> {
                    LocalDateTime inicio = LocalDateTime.now()
                            .withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
                    BigDecimal total = ventaRepository.findTotalVentasBySucursalEnRango(
                            s.getId(), inicio, LocalDateTime.now());
                    return new DashboardDTO.ComparativaSucursalDTO(
                            s.getId(), s.getNombre(), total, 0);
                })
                .collect(Collectors.toList());

        return new DashboardDTO(
                BigDecimal.ZERO, BigDecimal.ZERO, 0.0,
                0, 0, 0, 0, 0L, 0L, 0L,
                List.of(), List.of(), comparativa
        );
    }

    @Override
    public PrediccionDemandaDTO predecirDemanda(Long productoId, Long sucursalId) {
        // Promedio móvil de los últimos 6 meses
        List<Double> historial = new ArrayList<>();
        LocalDateTime ahora = LocalDateTime.now();

        for (int i = 5; i >= 0; i--) {
            LocalDateTime inicio = ahora.minusMonths(i + 1)
                    .withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime fin = ahora.minusMonths(i)
                    .withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).minusSeconds(1);

            Integer vendido = movimientoRepository
                    .findTotalVendidoByProductoSucursalYRango(productoId, sucursalId, inicio, fin);
            historial.add(vendido != null ? vendido.doubleValue() : 0.0);
        }

        double promedio = historial.stream()
                .mapToDouble(Double::doubleValue).average().orElse(0.0);

        // Stock actual
        int stockActual = inventarioItemRepository
                .findByProductoIdAndSucursalId(productoId, sucursalId)
                .map(InventarioItem::getCantidadDisponible)
                .orElse(0);

        int prediccion = (int) Math.ceil(promedio * 1.1); // +10% de margen
        boolean necesitaReabastecimiento = stockActual < prediccion;

        // Nombre del producto y sucursal
        String nombreProducto = inventarioItemRepository
                .findByProductoIdAndSucursalId(productoId, sucursalId)
                .map(i -> i.getProducto().getNombre())
                .orElse("Desconocido");

        String nombreSucursal = sucursalRepository.findById(sucursalId)
                .map(s -> s.getNombre()).orElse("Desconocida");

        return new PrediccionDemandaDTO(
                productoId, nombreProducto,
                sucursalId, nombreSucursal,
                BigDecimal.valueOf(promedio)
                        .setScale(2, RoundingMode.HALF_UP).doubleValue(),
                prediccion, stockActual,
                Math.max(prediccion, stockActual),
                necesitaReabastecimiento, historial
        );
    }

    @Override
    public List<PrediccionDemandaDTO> predecirDemandaSucursal(Long sucursalId) {
        return inventarioItemRepository.findBySucursalId(sucursalId)
                .stream()
                .map(item -> predecirDemanda(
                        item.getProducto().getId(), sucursalId))
                .collect(Collectors.toList());
    }

    // ── Métodos de apoyo ──────────────────────────────────
    private double calcularPorcentajeCambio(BigDecimal anterior, BigDecimal actual) {
        if (anterior == null || anterior.compareTo(BigDecimal.ZERO) == 0) return 0.0;
        return actual.subtract(anterior)
                .divide(anterior, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }
}