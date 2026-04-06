package com.inventario.repository;

import com.inventario.model.InventarioItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioItemRepository extends JpaRepository<InventarioItem, Long> {

    Optional<InventarioItem> findByProductoIdAndSucursalId(Long productoId, Long sucursalId);

    List<InventarioItem> findBySucursalId(Long sucursalId);

    List<InventarioItem> findByProductoId(Long productoId);

    // Productos con stock bajo en una sucursal
    @Query("""
        SELECT i FROM InventarioItem i
        WHERE i.sucursal.id = :sucursalId
        AND i.cantidadDisponible <= i.stockMinimo
    """)
    List<InventarioItem> findStockBajoBySucursal(Long sucursalId);

    // Productos agotados en una sucursal
    @Query("""
        SELECT i FROM InventarioItem i
        WHERE i.sucursal.id = :sucursalId
        AND i.cantidadDisponible = 0
    """)
    List<InventarioItem> findAgotadosBySucursal(Long sucursalId);

    // Stock total de un producto en toda la red
    @Query("""
        SELECT COALESCE(SUM(i.cantidadDisponible), 0)
        FROM InventarioItem i
        WHERE i.producto.id = :productoId
    """)
    Integer findStockTotalByProducto(Long productoId);

    // Inventario completo de una sucursal con info del producto
    @Query("""
        SELECT i FROM InventarioItem i
        JOIN FETCH i.producto p
        JOIN FETCH p.unidadMedida
        WHERE i.sucursal.id = :sucursalId
        AND p.activo = true
        ORDER BY p.nombre ASC
    """)
    List<InventarioItem> findInventarioCompletoSucursal(Long sucursalId);

    // Comparativa de stock entre sucursales para un producto
    @Query("""
        SELECT i FROM InventarioItem i
        JOIN FETCH i.sucursal s
        WHERE i.producto.id = :productoId
        AND s.activa = true
        ORDER BY i.cantidadDisponible DESC
    """)
    List<InventarioItem> findStockPorSucursalParaProducto(Long productoId);
    @Query("SELECT i FROM InventarioItem i WHERE i.sucursal.id = :sucursalId AND i.cantidadDisponible <= i.stockMinimo")
    List<InventarioItem> findStockBajoEnSucursal(Long sucursalId);
}