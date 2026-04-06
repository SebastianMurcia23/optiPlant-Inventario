package com.inventario.service.interfaces;

import com.inventario.model.LoteProducto;

import java.time.LocalDate;
import java.util.List;

public interface LoteProductoServicio {
    LoteProducto registrar(Long productoId, Long sucursalId,
    String numeroLote, int cantidad,
    LocalDate fechaFabricacion, LocalDate fechaVencimiento) throws Exception;

List<LoteProducto> listarPorProductoYSucursal(Long productoId, Long sucursalId);

List<LoteProducto> listarProximosAVencer(Long sucursalId, int diasAnticipacion);

List<LoteProducto> listarVencidos(Long sucursalId);

void desactivarLote(Long loteId) throws Exception;

void actualizarCantidad(Long loteId, int nuevaCantidad) throws Exception;
}