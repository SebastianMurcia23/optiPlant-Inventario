package com.inventario.repository;

import com.inventario.model.Alerta;
import com.inventario.model.enums.EstadoAlerta;
import com.inventario.model.enums.TipoAlerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    List<Alerta> findBySucursalIdAndEstadoOrderByFechaGeneracionDesc(
            Long sucursalId, EstadoAlerta estado);

    List<Alerta> findBySucursalIdAndTipo(Long sucursalId, TipoAlerta tipo);

    // Alertas activas de una sucursal
    @Query("""
        SELECT a FROM Alerta a
        WHERE a.sucursal.id = :sucursalId
        AND a.estado = 'ACTIVA'
        ORDER BY a.fechaGeneracion DESC
    """)
    List<Alerta> findActivasBySucursal(Long sucursalId);

    // Verificar si ya existe alerta activa del mismo tipo para producto+sucursal
    @Query("""
        SELECT COUNT(a) > 0 FROM Alerta a
        WHERE a.sucursal.id = :sucursalId
        AND a.producto.id = :productoId
        AND a.tipo = :tipo
        AND a.estado = 'ACTIVA'
    """)
    boolean existeAlertaActivaParaProducto(Long sucursalId, Long productoId, TipoAlerta tipo);

    // Conteo de alertas activas por sucursal (para dashboard)
    @Query("""
        SELECT COUNT(a) FROM Alerta a
        WHERE a.sucursal.id = :sucursalId
        AND a.estado = 'ACTIVA'
    """)
    long countActivasBySucursal(Long sucursalId);
}