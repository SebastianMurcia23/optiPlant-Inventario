package com.inventario.service.interfaces;

import com.inventario.dto.request.CrearOrdenCompraDTO;
import com.inventario.dto.request.RecibirOrdenCompraDTO;
import com.inventario.dto.response.OrdenCompraDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrdenCompraServicio {
    OrdenCompraDTO crear(CrearOrdenCompraDTO dto, Long solicitanteId) throws Exception;
    OrdenCompraDTO enviarAProveedor(Long ordenId) throws Exception;
    OrdenCompraDTO recibirMercancia(RecibirOrdenCompraDTO dto, Long responsableId) throws Exception;
    OrdenCompraDTO cancelar(Long ordenId) throws Exception;
    OrdenCompraDTO obtenerPorId(Long id) throws Exception;
    Page<OrdenCompraDTO> listarPorSucursal(Long sucursalId, Pageable pageable);
    Page<OrdenCompraDTO> listarPorProveedor(Long proveedorId, Pageable pageable);
}