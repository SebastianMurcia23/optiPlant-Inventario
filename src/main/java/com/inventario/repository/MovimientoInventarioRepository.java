package com.inventario.repository;

import com.inventario.model.MovimientoInventario;
import com.inventario.model.enums.TipoMovimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    Page<MovimientoInventario> findBySucursalIdOrderByFechaDesc(Long sucursalId, Pageable pageable);

    Page<MovimientoInventario> findByProductoIdAndSucursalIdOrderByFechaDesc(
            Long productoId, Long sucursalId, Pageable pageable);

    List<MovimientoInventario> findByTipoAndSucursalId(TipoMovimiento tipo, Long sucursalId);

    // Historial de un producto en todas las sucursales
    @Query("""
        SELECT m FROM MovimientoInventario m
        WHERE m.producto.id = :productoId
        AND m.fecha BETWEEN :desde AND :hasta
        ORDER BY m.fecha DESC
    """)
    List<MovimientoInventario> findByProductoEnRangoFecha(
            Long productoId, LocalDateTime desde, LocalDateTime hasta);

    // Movimientos de una sucursal en rango de fechas (para reportes)
    @Query("""
        SELECT m FROM MovimientoInventario m
        WHERE m.sucursal.id = :sucursalId
        AND m.fecha BETWEEN :desde AND :hasta
        ORDER BY m.fecha DESC
    """)
    Page<MovimientoInventario> findBySucursalEnRangoFecha(
            Long sucursalId, LocalDateTime desde, LocalDateTime hasta, Pageable pageable);

    // Movimientos por responsable
    @Query("""
        SELECT m FROM MovimientoInventario m
        WHERE m.responsable.id = :usuarioId
        ORDER BY m.fecha DESC
    """)
    List<MovimientoInventario> findByResponsable(Long usuarioId);

    // Total de unidades vendidas por producto en un rango (para predicción de demanda)
    @Query("""
        SELECT COALESCE(SUM(m.cantidad), 0)
        FROM MovimientoInventario m
        WHERE m.producto.id = :productoId
        AND m.sucursal.id = :sucursalId
        AND m.tipo = 'RETIRO_VENTA'
        AND m.fecha BETWEEN :desde AND :hasta
    """)
    Integer findTotalVendidoByProductoSucursalYRango(
            Long productoId, Long sucursalId, LocalDateTime desde, LocalDateTime hasta);
}