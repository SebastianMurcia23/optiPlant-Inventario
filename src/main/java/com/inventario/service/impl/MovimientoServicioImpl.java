package com.inventario.service.impl;

import com.inventario.dto.response.MovimientoDTO;
import com.inventario.model.MovimientoInventario;
import com.inventario.repository.MovimientoInventarioRepository;
import com.inventario.service.interfaces.MovimientoServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MovimientoServicioImpl implements MovimientoServicio {

    private final MovimientoInventarioRepository movimientoRepository;

    @Override
    public Page<MovimientoDTO> listarPorSucursal(Long sucursalId, Pageable pageable) {
        return movimientoRepository
                .findBySucursalIdOrderByFechaDesc(sucursalId, pageable)
                .map(this::mapearDTO);
    }

    @Override
    public Page<MovimientoDTO> listarPorProductoYSucursal(Long productoId,
                                                          Long sucursalId,
                                                          Pageable pageable) {
        return movimientoRepository
                .findByProductoIdAndSucursalIdOrderByFechaDesc(productoId, sucursalId, pageable)
                .map(this::mapearDTO);
    }

    @Override
    public List<MovimientoDTO> listarPorProductoEnRango(Long productoId,
                                                        LocalDateTime desde,
                                                        LocalDateTime hasta) {
        return movimientoRepository
                .findByProductoEnRangoFecha(productoId, desde, hasta)
                .stream().map(this::mapearDTO).toList();
    }

    @Override
    public Page<MovimientoDTO> listarPorSucursalEnRango(Long sucursalId,
                                                        LocalDateTime desde,
                                                        LocalDateTime hasta,
                                                        Pageable pageable) {
        return movimientoRepository
                .findBySucursalEnRangoFecha(sucursalId, desde, hasta, pageable)
                .map(this::mapearDTO);
    }

    @Override
    public List<MovimientoDTO> listarPorResponsable(Long usuarioId) {
        return movimientoRepository.findByResponsable(usuarioId)
                .stream().map(this::mapearDTO).toList();
    }

    // ── Método de apoyo ──────────────────────────────────
    private MovimientoDTO mapearDTO(MovimientoInventario m) {
        return new MovimientoDTO(
                m.getId(),
                m.getProducto().getNombre(),
                m.getProducto().getCodigo(),
                m.getSucursal().getNombre(),
                m.getTipo(),
                m.getCantidad(),
                m.getCantidadAnterior(),
                m.getCantidadPosterior(),
                m.getCostoUnitario(),
                m.getMotivo(),
                m.getDocumentoReferencia(),
                m.getResponsable().getNombre(),
                m.getFecha()
        );
    }
}