package com.inventario.service.impl;

import com.inventario.dto.request.AjustarStockDTO;
import com.inventario.dto.response.InventarioItemDTO;
import com.inventario.model.*;
import com.inventario.model.enums.TipoMovimiento;
import com.inventario.repository.*;
import com.inventario.service.interfaces.InventarioServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class InventarioServicioImpl implements InventarioServicio {

    private final InventarioItemRepository inventarioItemRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final ProductoRepository productoRepository;
    private final SucursalServicioImpl sucursalServicio;
    private final UsuarioServicioImpl usuarioServicio;

    @Override
    @Transactional(readOnly = true)
    public InventarioItemDTO obtenerStock(Long productoId, Long sucursalId) throws Exception {
        InventarioItem item = inventarioItemRepository
                .findByProductoIdAndSucursalId(productoId, sucursalId)
                .orElseThrow(() -> new Exception(
                        "No se encontró inventario para producto " + productoId +
                                " en sucursal " + sucursalId));
        return mapearDTO(item);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioItemDTO> listarInventarioSucursal(Long sucursalId) {
        return inventarioItemRepository.findInventarioCompletoSucursal(sucursalId)
                .stream().map(this::mapearDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioItemDTO> listarStockPorProducto(Long productoId) {
        return inventarioItemRepository.findStockPorSucursalParaProducto(productoId)
                .stream().map(this::mapearDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioItemDTO> listarStockBajoEnSucursal(Long sucursalId) {
        return inventarioItemRepository.findStockBajoBySucursal(sucursalId)
                .stream().map(this::mapearDTO).toList();
    }

    @Override
    public void ajustarStock(AjustarStockDTO dto, Long responsableId) throws Exception {
        Producto producto = productoRepository.findById(dto.productoId())
                .orElseThrow(() -> new Exception("Producto no encontrado: " + dto.productoId()));

        Sucursal sucursal = sucursalServicio.obtenerEntidad(dto.sucursalId());
        Usuario responsable = usuarioServicio.obtenerEntidad(responsableId);

        // Obtener o crear el InventarioItem
        InventarioItem item = inventarioItemRepository
                .findByProductoIdAndSucursalId(dto.productoId(), dto.sucursalId())
                .orElseGet(() -> {
                    InventarioItem nuevo = new InventarioItem();
                    nuevo.setProducto(producto);
                    nuevo.setSucursal(sucursal);
                    return nuevo;
                });

        int cantidadAnterior = item.getCantidadDisponible();

        // Aplicar movimiento según tipo
        if (esIngreso(dto.tipo())) {
            item.setCantidadDisponible(cantidadAnterior + dto.cantidad());
            // Recalcular costo promedio ponderado si viene con costo
        } else {
            if (item.getCantidadDisponible() < dto.cantidad()) {
                throw new Exception("Stock insuficiente. Disponible: " +
                        item.getCantidadDisponible() + ", solicitado: " + dto.cantidad());
            }
            item.setCantidadDisponible(cantidadAnterior - dto.cantidad());
        }

        inventarioItemRepository.save(item);

        // Registrar movimiento para trazabilidad
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setProducto(producto);
        movimiento.setSucursal(sucursal);
        movimiento.setTipo(dto.tipo());
        movimiento.setCantidad(dto.cantidad());
        movimiento.setCantidadAnterior(cantidadAnterior);
        movimiento.setCantidadPosterior(item.getCantidadDisponible());
        movimiento.setMotivo(dto.motivo());
        movimiento.setDocumentoReferencia(dto.documentoReferencia());
        movimiento.setResponsable(responsable);
        movimientoRepository.save(movimiento);
    }

    @Override
    public InventarioItemDTO configurarStockMinimo(Long productoId, Long sucursalId,
                                                   Integer stockMinimo, Integer stockMaximo) throws Exception {
        InventarioItem item = inventarioItemRepository
                .findByProductoIdAndSucursalId(productoId, sucursalId)
                .orElseThrow(() -> new Exception("Inventario no encontrado"));
        item.setStockMinimo(stockMinimo);
        if (stockMaximo != null) item.setStockMaximo(stockMaximo);
        return mapearDTO(inventarioItemRepository.save(item));
    }

    // ── Métodos de apoyo ──────────────────────────────────

    public void actualizarCostoPromedio(InventarioItem item, int cantidadNueva,
                                        BigDecimal costoNuevo) {
        int cantidadActual = item.getCantidadDisponible();
        BigDecimal costoActual = item.getCostoPromedioLocal();

        if (cantidadActual == 0) {
            item.setCostoPromedioLocal(costoNuevo);
        } else {
            BigDecimal totalActual = costoActual.multiply(BigDecimal.valueOf(cantidadActual));
            BigDecimal totalNuevo = costoNuevo.multiply(BigDecimal.valueOf(cantidadNueva));
            BigDecimal totalCantidad = BigDecimal.valueOf(cantidadActual + cantidadNueva);
            item.setCostoPromedioLocal(
                    totalActual.add(totalNuevo).divide(totalCantidad, 4, RoundingMode.HALF_UP));
        }
    }

    private boolean esIngreso(TipoMovimiento tipo) {
        return tipo == TipoMovimiento.INGRESO_COMPRA ||
                tipo == TipoMovimiento.INGRESO_DEVOLUCION ||
                tipo == TipoMovimiento.INGRESO_AJUSTE ||
                tipo == TipoMovimiento.INGRESO_TRANSFERENCIA;
    }

    public InventarioItem obtenerOCrearItem(Long productoId, Long sucursalId) throws Exception {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new Exception("Producto no encontrado: " + productoId));
        Sucursal sucursal = sucursalServicio.obtenerEntidad(sucursalId);

        return inventarioItemRepository
                .findByProductoIdAndSucursalId(productoId, sucursalId)
                .orElseGet(() -> {
                    InventarioItem item = new InventarioItem();
                    item.setProducto(producto);
                    item.setSucursal(sucursal);
                    return inventarioItemRepository.save(item);
                });
    }

    private InventarioItemDTO mapearDTO(InventarioItem i) {
        return new InventarioItemDTO(
                i.getId(),
                i.getProducto().getId(),
                i.getProducto().getCodigo(),
                i.getProducto().getNombre(),
                i.getProducto().getUnidadMedida().getAbreviatura(),
                i.getSucursal().getId(),
                i.getSucursal().getNombre(),
                i.getCantidadDisponible(),
                i.getCantidadReservada(),
                i.getCantidadReal(),
                i.getStockMinimo(),
                i.getStockMaximo(),
                i.getCostoPromedioLocal(),
                i.tieneStockBajo(),
                i.getUltimaActualizacion()
        );
    }
}