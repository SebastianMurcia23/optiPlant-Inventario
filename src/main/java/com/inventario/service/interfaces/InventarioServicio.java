package com.inventario.service.interfaces;

import com.inventario.dto.request.AjustarStockDTO;
import com.inventario.dto.response.InventarioItemDTO;

import java.util.List;

public interface InventarioServicio {
    InventarioItemDTO obtenerStock(Long productoId, Long sucursalId) throws Exception;
    List<InventarioItemDTO> listarInventarioSucursal(Long sucursalId);
    List<InventarioItemDTO> listarStockPorProducto(Long productoId);
    List<InventarioItemDTO> listarStockBajoEnSucursal(Long sucursalId);
    void ajustarStock(AjustarStockDTO dto, Long responsableId) throws Exception;
    InventarioItemDTO configurarStockMinimo(Long productoId, Long sucursalId,
                                            Integer stockMinimo, Integer stockMaximo) throws Exception;
}