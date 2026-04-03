package com.inventario.service.interfaces;

import com.inventario.dto.response.MovimientoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoServicio {
    Page<MovimientoDTO> listarPorSucursal(Long sucursalId, Pageable pageable);
    Page<MovimientoDTO> listarPorProductoYSucursal(Long productoId,
                                                   Long sucursalId,
                                                   Pageable pageable);
    List<MovimientoDTO> listarPorProductoEnRango(Long productoId,
                                                 LocalDateTime desde,
                                                 LocalDateTime hasta);
    Page<MovimientoDTO> listarPorSucursalEnRango(Long sucursalId,
                                                 LocalDateTime desde,
                                                 LocalDateTime hasta,
                                                 Pageable pageable);
    List<MovimientoDTO> listarPorResponsable(Long usuarioId);
}