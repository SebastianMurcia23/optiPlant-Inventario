package com.inventario.repository;

import com.inventario.model.DetalleTransferencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleTransferenciaRepository extends JpaRepository<DetalleTransferencia, Long> {

    List<DetalleTransferencia> findByTransferenciaId(Long transferenciaId);

    List<DetalleTransferencia> findByProductoId(Long productoId);

    // Transferencias con faltantes para seguimiento
    @Query("""
        SELECT d FROM DetalleTransferencia d
        WHERE d.transferencia.id = :transferenciaId
        AND d.cantidadFaltante > 0
    """)
    List<DetalleTransferencia> findConFaltantesByTransferencia(Long transferenciaId);
}