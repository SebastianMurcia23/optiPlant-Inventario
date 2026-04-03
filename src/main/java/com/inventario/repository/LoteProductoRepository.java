package com.inventario.repository;

import com.inventario.model.LoteProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoteProductoRepository extends JpaRepository<LoteProducto, Long> {

    List<LoteProducto> findByProductoIdAndSucursalId(Long productoId, Long sucursalId);

    List<LoteProducto> findByProductoIdAndSucursalIdAndActivoTrue(Long productoId, Long sucursalId);

    @Query("""
        SELECT l FROM LoteProducto l
        WHERE l.sucursal.id = :sucursalId
        AND l.activo = true
        AND l.fechaVencimiento IS NOT NULL
        AND l.fechaVencimiento <= :fechaLimite
        AND l.fechaVencimiento >= CURRENT_DATE
        AND l.cantidadActual > 0
        ORDER BY l.fechaVencimiento ASC
    """)
    List<LoteProducto> findProximosAVencer(Long sucursalId, LocalDate fechaLimite);

    @Query("""
        SELECT l FROM LoteProducto l
        WHERE l.sucursal.id = :sucursalId
        AND l.activo = true
        AND l.fechaVencimiento < CURRENT_DATE
        AND l.cantidadActual > 0
    """)
    List<LoteProducto> findVencidosConStock(Long sucursalId);

    @Query("""
        SELECT COALESCE(SUM(l.cantidadActual), 0)
        FROM LoteProducto l
        WHERE l.producto.id = :productoId
        AND l.sucursal.id = :sucursalId
        AND l.activo = true
    """)
    Integer findStockTotalByLotes(Long productoId, Long sucursalId);
}