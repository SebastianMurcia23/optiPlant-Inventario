package com.inventario.repository;

import com.inventario.model.OrdenCompra;
import com.inventario.model.enums.EstadoOrdenCompra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Long> {

    Optional<OrdenCompra> findByNumero(String numero);

    boolean existsByNumero(String numero);

    Page<OrdenCompra> findBySucursalIdOrderByFechaCreacionDesc(Long sucursalId, Pageable pageable);

    List<OrdenCompra> findByEstadoAndSucursalId(EstadoOrdenCompra estado, Long sucursalId);

    Page<OrdenCompra> findByProveedorIdOrderByFechaCreacionDesc(Long proveedorId, Pageable pageable);

    // Órdenes en un rango de fechas para reportes
    @Query("""
        SELECT o FROM OrdenCompra o
        WHERE o.sucursal.id = :sucursalId
        AND o.fechaCreacion BETWEEN :desde AND :hasta
        ORDER BY o.fechaCreacion DESC
    """)
    List<OrdenCompra> findBySucursalEnRangoFecha(
            Long sucursalId, LocalDateTime desde, LocalDateTime hasta);

    // Historial completo por proveedor
    @Query("""
        SELECT o FROM OrdenCompra o
        WHERE o.proveedor.id = :proveedorId
        AND o.estado = 'RECIBIDA'
        ORDER BY o.fechaRecepcion DESC
    """)
    List<OrdenCompra> findRecibidosByProveedor(Long proveedorId);

    // Último número de orden para generar correlativo
    @Query("SELECT MAX(o.numero) FROM OrdenCompra o")
    Optional<String> findUltimoNumero();
}