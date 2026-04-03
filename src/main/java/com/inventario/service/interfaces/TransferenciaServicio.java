package com.inventario.service.interfaces;

import com.inventario.dto.request.CrearTransferenciaDTO;
import com.inventario.dto.request.DespacharTransferenciaDTO;
import com.inventario.dto.request.RecibirTransferenciaDTO;
import com.inventario.dto.response.TransferenciaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TransferenciaServicio {
    TransferenciaDTO solicitar(CrearTransferenciaDTO dto, Long solicitanteId) throws Exception;
    TransferenciaDTO prepararEnvio(Long transferenciaId) throws Exception;
    TransferenciaDTO despachar(DespacharTransferenciaDTO dto, Long despachadorId) throws Exception;
    TransferenciaDTO recibirMercancia(RecibirTransferenciaDTO dto, Long receptorId) throws Exception;
    TransferenciaDTO cancelar(Long transferenciaId) throws Exception;
    TransferenciaDTO obtenerPorId(Long id) throws Exception;
    Page<TransferenciaDTO> listarPorSucursalOrigen(Long sucursalId, Pageable pageable);
    Page<TransferenciaDTO> listarPorSucursalDestino(Long sucursalId, Pageable pageable);
    List<TransferenciaDTO> listarActivasPorSucursal(Long sucursalId);
}