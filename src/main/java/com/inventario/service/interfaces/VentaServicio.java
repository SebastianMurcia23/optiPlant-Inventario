package com.inventario.service.interfaces;

import com.inventario.dto.request.CrearVentaDTO;
import com.inventario.dto.response.VentaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VentaServicio {
    VentaDTO crear(CrearVentaDTO dto, Long vendedorId) throws Exception;
    VentaDTO confirmar(Long ventaId) throws Exception;
    VentaDTO anular(Long ventaId) throws Exception;
    VentaDTO obtenerPorId(Long id) throws Exception;
    Page<VentaDTO> listarPorSucursal(Long sucursalId, Pageable pageable);
}