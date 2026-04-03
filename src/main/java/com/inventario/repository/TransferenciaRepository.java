package com.inventario.repository;

import com.inventario.model.Transferencia;
import com.inventario.model.enums.EstadoTransferencia;
import com.inventario.model.enums.PrioridadTransferencia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransferenciaRepository extends JpaRepository<Transferencia, Long> {

    Optional<Transferencia> findByNumero(String numero);

    boolean existsByNumero(String numero);

    // Transferencias donde la sucursal es origen
    Page<Transferencia> findBySucursalOrigenIdOrderByFechaSolicitudDesc(
            Long sucursalId, Pageable pageable);

    // Transferencias donde la sucursal es destino
    Page<Transferencia> findBySucursalDestinoIdOrderByFechaSolicitudDesc(
            Long sucursalId, Pageable pageable);

    // Transferencias activas (en curso) para una sucursal como origen o destino
    @Query("""
        SELECT t FROM Transferencia t
        WHERE (t.sucursalOrigen.id = :sucursalId OR t.sucursalDestino.id = :sucursalId)
        AND t.estado NOT IN ('RECIBIDA_COMPLETA', 'CANCELADA')
        ORDER BY t.prioridad DESC, t.fechaSolicitud ASC
    """)
    List<Transferencia> findActivasBySucursal(Long sucursalId);

    // Transferencias por estado para una sucursal origen
    List<Transferencia> findByEstadoAndSucursalOrigenId(
            EstadoTransferencia estado, Long sucursalId);

    // Transferencias urgentes pendientes
    @Query("""
        SELECT t FROM Transferencia t
        WHERE t.sucursalOrigen.id = :sucursalId
        AND t.estado = 'SOLICITADA'
        AND t.prioridad = 'URGENTE'
        ORDER BY t.fechaSolicitud ASC
    """)
    List<Transferencia> findUrgentesEnEspera(Long sucursalId);

    // Para reporte de cumplimiento logístico
    @Query("""
        SELECT t FROM Transferencia t
        WHERE t.estado IN ('RECIBIDA_COMPLETA', 'RECIBIDA_PARCIAL')
        AND t.sucursalOrigen.id = :sucursalId
        AND t.fechaRecepcion IS NOT NULL
        ORDER BY t.fechaRecepcion DESC
    """)
    List<Transferencia> findCompletadasBySucursalOrigen(Long sucursalId);

    // Último número para correlativo
    @Query("SELECT MAX(t.numero) FROM Transferencia t")
    Optional<String> findUltimoNumero();
}