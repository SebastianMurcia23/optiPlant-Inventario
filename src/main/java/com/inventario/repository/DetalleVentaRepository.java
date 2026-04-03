package com.inventario.repository;

import com.inventario.model.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {

    List<DetalleVenta> findByVentaId(Long ventaId);

    List<DetalleVenta> findByProductoId(Long productoId);

    // Ventas de un producto específico en un rango de fechas
    @Query("""
        SELECT d FROM DetalleVenta d
        JOIN d.venta v
        WHERE d.producto.id = :productoId
        AND v.sucursal.id = :sucursalId
        AND v.fechaVenta BETWEEN :desde AND :hasta
        AND v.estado = 'CONFIRMADA'
    """)
    List<DetalleVenta> findByProductoEnSucursalYRango(
            Long productoId, Long sucursalId, LocalDateTime desde, LocalDateTime hasta);
}