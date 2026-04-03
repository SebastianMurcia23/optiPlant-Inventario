package com.inventario.dto.response;

import com.inventario.model.enums.EstadoTransferencia;
import com.inventario.model.enums.PrioridadTransferencia;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record TransferenciaDTO(
        Long id,
        String numero,
        String sucursalOrigenNombre,
        String sucursalDestinoNombre,
        String solicitanteNombre,
        String despachadorNombre,
        EstadoTransferencia estado,
        PrioridadTransferencia prioridad,
        String transportista,
        LocalDateTime fechaSolicitud,
        LocalDateTime fechaDespacho,
        LocalDate fechaEstimadaLlegada,
        LocalDateTime fechaRecepcion,
        Integer tiempoEstimadoHoras,
        Integer tiempoRealHoras,
        String observaciones,
        String observacionesRecepcion,
        List<DetalleTransferenciaRespDTO> detalles
) {
    public record DetalleTransferenciaRespDTO(
            Long id,
            String productoNombre,
            String productoCodigo,
            Integer cantidadSolicitada,
            Integer cantidadEnviada,
            Integer cantidadRecibida,
            Integer cantidadFaltante,
            String tratamientoFaltante
    ) {}
}