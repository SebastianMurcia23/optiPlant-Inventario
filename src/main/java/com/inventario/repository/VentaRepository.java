package com.inventario.repository;

import com.inventario.model.Venta;
import com.inventario.model.enums.EstadoVenta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    Optional<Venta> findByNumero(String numero);

    boolean existsByNumero(String numero);

    Page<Venta> findBySucursalIdOrderByFechaVentaDesc(Long sucursalId, Pageable pageable);

    List<Venta> findByEstadoAndSucursalId(EstadoVenta estado, Long sucursalId);

    Page<Venta> findByVendedorIdOrderByFechaVentaDesc(Long vendedorId, Pageable pageable);

    // Ventas en rango de fechas por sucursal
    @Query("""
        SELECT v FROM Venta v
        WHERE v.sucursal.id = :sucursalId
        AND v.fechaVenta BETWEEN :desde AND :hasta
        AND v.estado = 'CONFIRMADA'
        ORDER BY v.fechaVenta DESC
    """)
    List<Venta> findConfirmadasBySucursalEnRango(
            Long sucursalId, LocalDateTime desde, LocalDateTime hasta);

    // Total vendido por sucursal en un rango (dashboard)
    @Query("""
        SELECT COALESCE(SUM(v.total), 0)
        FROM Venta v
        WHERE v.sucursal.id = :sucursalId
        AND v.fechaVenta BETWEEN :desde AND :hasta
        AND v.estado = 'CONFIRMADA'
    """)
    BigDecimal findTotalVentasBySucursalEnRango(
            Long sucursalId, LocalDateTime desde, LocalDateTime hasta);

    // Comparativa mensual — ventas por mes en el año actual
    @Query("""
        SELECT EXTRACT(MONTH FROM v.fechaVenta) AS mes,
               COALESCE(SUM(v.total), 0) AS total
        FROM Venta v
        WHERE v.sucursal.id = :sucursalId
        AND EXTRACT(YEAR FROM v.fechaVenta) = :anio
        AND v.estado = 'CONFIRMADA'
        GROUP BY EXTRACT(MONTH FROM v.fechaVenta)
        ORDER BY EXTRACT(   MONTH FROM v.fechaVenta)
    """)

    List<Object[]> findVentasMensualesBySucursal(Long sucursalId, int anio);

    // Top productos más vendidos en una sucursal
    @Query("""
        SELECT dv.producto.id, dv.producto.nombre, SUM(dv.cantidad) AS totalVendido
        FROM DetalleVenta dv
        JOIN dv.venta v
        WHERE v.sucursal.id = :sucursalId
        AND v.fechaVenta BETWEEN :desde AND :hasta
        AND v.estado = 'CONFIRMADA'
        GROUP BY dv.producto.id, dv.producto.nombre
        ORDER BY totalVendido DESC
    """)
    List<Object[]> findTopProductosVendidos(Long sucursalId, LocalDateTime desde, LocalDateTime hasta);

    // Último número de venta para correlativo
    @Query("SELECT MAX(v.numero) FROM Venta v")
    Optional<String> findUltimoNumero();
}