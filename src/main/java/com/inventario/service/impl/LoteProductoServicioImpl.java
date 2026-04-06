package com.inventario.service.impl;

import com.inventario.model.LoteProducto;
import com.inventario.model.Producto;
import com.inventario.model.Sucursal;
import com.inventario.repository.LoteProductoRepository;
import com.inventario.service.interfaces.LoteProductoServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class LoteProductoServicioImpl implements LoteProductoServicio {

    private final LoteProductoRepository loteProductoRepository;
    private final ProductoServicioImpl productoServicio;
    private final SucursalServicioImpl sucursalServicio;

    @Override
    public LoteProducto registrar(Long productoId, Long sucursalId,
        String numeroLote, int cantidad,
        LocalDate fechaFabricacion,
        LocalDate fechaVencimiento) throws Exception {
        Producto producto = productoServicio.obtenerEntidad(productoId);
        Sucursal sucursal = sucursalServicio.obtenerEntidad(sucursalId);

        LoteProducto lote = new LoteProducto();
        lote.setNumeroLote(numeroLote);
        lote.setProducto(producto);
        lote.setSucursal(sucursal);
        lote.setCantidadInicial(cantidad);
        lote.setCantidadActual(cantidad);
        lote.setFechaFabricacion(fechaFabricacion);
        lote.setFechaVencimiento(fechaVencimiento);
        return loteProductoRepository.save(lote);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoteProducto> listarPorProductoYSucursal(Long productoId, Long sucursalId) {
        return loteProductoRepository
                .findByProductoIdAndSucursalIdAndActivoTrue(productoId, sucursalId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoteProducto> listarProximosAVencer(Long sucursalId, int diasAnticipacion) {
        return loteProductoRepository.findProximosAVencer(
                sucursalId, LocalDate.now().plusDays(diasAnticipacion));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoteProducto> listarVencidos(Long sucursalId) {
        return loteProductoRepository.findVencidosConStock(sucursalId);
    }

    @Override
    public void desactivarLote(Long loteId) throws Exception {
        LoteProducto lote = loteProductoRepository.findById(loteId)
                .orElseThrow(() -> new Exception("Lote no encontrado: " + loteId));
        lote.setActivo(false);
        loteProductoRepository.save(lote);
    }

    @Override
    public void actualizarCantidad(Long loteId, int nuevaCantidad) throws Exception {
        LoteProducto lote = loteProductoRepository.findById(loteId)
                .orElseThrow(() -> new Exception("Lote no encontrado: " + loteId));
        if (nuevaCantidad < 0) throw new Exception("La cantidad no puede ser negativa");
        lote.setCantidadActual(nuevaCantidad);
        loteProductoRepository.save(lote);
    }
}