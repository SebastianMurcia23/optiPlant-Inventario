package com.inventario.repository;

import com.inventario.model.DetalleOrdenCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleOrdenCompraRepository extends JpaRepository<DetalleOrdenCompra, Long> {

    List<DetalleOrdenCompra> findByOrdenCompraId(Long ordenCompraId);

    List<DetalleOrdenCompra> findByProductoId(Long productoId);

    // Historial de compras de un producto específico con precio
    @Query("""
        SELECT d FROM DetalleOrdenCompra d
        JOIN d.ordenCompra o
        WHERE d.producto.id = :productoId
        AND o.estado = 'RECIBIDA'
        ORDER BY o.fechaRecepcion DESC
    """)
    List<DetalleOrdenCompra> findHistorialCompraByProducto(Long productoId);
}