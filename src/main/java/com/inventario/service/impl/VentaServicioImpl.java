package com.inventario.service.impl;

import com.inventario.dto.request.CrearVentaDTO;
import com.inventario.dto.response.VentaDTO;
import com.inventario.model.*;
import com.inventario.model.enums.EstadoVenta;
import com.inventario.model.enums.TipoMovimiento;
import com.inventario.repository.*;
import com.inventario.service.interfaces.VentaServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class VentaServicioImpl implements VentaServicio {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ProductoRepository productoRepository;
    private final InventarioItemRepository inventarioItemRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final SucursalServicioImpl sucursalServicio;
    private final UsuarioServicioImpl usuarioServicio;

    @Override
    public VentaDTO crear(CrearVentaDTO dto, Long vendedorId) throws Exception {
        Sucursal sucursal = sucursalServicio.obtenerEntidad(dto.sucursalId());
        Usuario vendedor = usuarioServicio.obtenerEntidad(vendedorId);

        // Validar stock antes de crear
        for (CrearVentaDTO.DetalleVentaDTO d : dto.detalles()) {
            InventarioItem item = inventarioItemRepository
                    .findByProductoIdAndSucursalId(d.productoId(), dto.sucursalId())
                    .orElseThrow(() -> new Exception(
                            "Producto " + d.productoId() + " sin inventario en esta sucursal"));
            if (item.getCantidadDisponible() < d.cantidad()) {
                throw new Exception("Stock insuficiente para producto " +
                        item.getProducto().getNombre() +
                        ". Disponible: " + item.getCantidadDisponible());
            }
        }

        Venta venta = new Venta();
        venta.setNumero(generarNumero());
        venta.setSucursal(sucursal);
        venta.setVendedor(vendedor);
        venta.setEstado(EstadoVenta.PENDIENTE);
        venta.setClienteNombre(dto.clienteNombre());
        venta.setClienteDocumento(dto.clienteDocumento());
        venta.setObservaciones(dto.observaciones());

        List<DetalleVenta> detalles = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (CrearVentaDTO.DetalleVentaDTO d : dto.detalles()) {
            Producto producto = productoRepository.findById(d.productoId())
                    .orElseThrow(() -> new Exception("Producto no encontrado: " + d.productoId()));

            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);
            detalle.setProducto(producto);
            detalle.setCantidad(d.cantidad());
            detalle.setPrecioUnitario(d.precioUnitario());
            detalle.setPorcentajeDescuento(
                    d.porcentajeDescuento() != null ? d.porcentajeDescuento() : BigDecimal.ZERO);
            detalle.calcularSubtotal();
            detalles.add(detalle);
            subtotal = subtotal.add(detalle.getSubtotal());
        }

        venta.setDetalles(detalles);
        venta.setSubtotal(subtotal);
        venta.setTotal(subtotal);

        return mapearDTO(ventaRepository.save(venta));
    }

    @Override
    public VentaDTO confirmar(Long ventaId) throws Exception {
        Venta venta = obtenerEntidad(ventaId);
        if (venta.getEstado() != EstadoVenta.PENDIENTE) {
            throw new Exception("Solo se pueden confirmar ventas PENDIENTES");
        }

        // Descontar stock y registrar movimientos
        for (DetalleVenta detalle : venta.getDetalles()) {
            InventarioItem item = inventarioItemRepository
                    .findByProductoIdAndSucursalId(
                            detalle.getProducto().getId(), venta.getSucursal().getId())
                    .orElseThrow(() -> new Exception("Inventario no encontrado"));

            int cantidadAnterior = item.getCantidadDisponible();
            item.setCantidadDisponible(cantidadAnterior - detalle.getCantidad());
            inventarioItemRepository.save(item);

            MovimientoInventario mov = new MovimientoInventario();
            mov.setProducto(detalle.getProducto());
            mov.setSucursal(venta.getSucursal());
            mov.setTipo(TipoMovimiento.RETIRO_VENTA);
            mov.setCantidad(detalle.getCantidad());
            mov.setCantidadAnterior(cantidadAnterior);
            mov.setCantidadPosterior(item.getCantidadDisponible());
            mov.setCostoUnitario(detalle.getPrecioUnitario());
            mov.setMotivo("Venta " + venta.getNumero());
            mov.setDocumentoReferencia(venta.getNumero());
            mov.setResponsable(venta.getVendedor());
            movimientoRepository.save(mov);
        }

        venta.setEstado(EstadoVenta.CONFIRMADA);
        return mapearDTO(ventaRepository.save(venta));
    }

    @Override
    public VentaDTO anular(Long ventaId) throws Exception {
        Venta venta = obtenerEntidad(ventaId);
        if (venta.getEstado() == EstadoVenta.ANULADA) {
            throw new Exception("La venta ya está anulada");
        }

        // Si estaba confirmada, devolver stock
        if (venta.getEstado() == EstadoVenta.CONFIRMADA) {
            for (DetalleVenta detalle : venta.getDetalles()) {
                InventarioItem item = inventarioItemRepository
                        .findByProductoIdAndSucursalId(
                                detalle.getProducto().getId(), venta.getSucursal().getId())
                        .orElseThrow(() -> new Exception("Inventario no encontrado"));

                int cantidadAnterior = item.getCantidadDisponible();
                item.setCantidadDisponible(cantidadAnterior + detalle.getCantidad());
                inventarioItemRepository.save(item);

                MovimientoInventario mov = new MovimientoInventario();
                mov.setProducto(detalle.getProducto());
                mov.setSucursal(venta.getSucursal());
                mov.setTipo(TipoMovimiento.INGRESO_DEVOLUCION);
                mov.setCantidad(detalle.getCantidad());
                mov.setCantidadAnterior(cantidadAnterior);
                mov.setCantidadPosterior(item.getCantidadDisponible());
                mov.setMotivo("Anulación venta " + venta.getNumero());
                mov.setDocumentoReferencia(venta.getNumero());
                mov.setResponsable(venta.getVendedor());
                movimientoRepository.save(mov);
            }
        }

        venta.setEstado(EstadoVenta.ANULADA);
        return mapearDTO(ventaRepository.save(venta));
    }

    @Override
    @Transactional(readOnly = true)
    public VentaDTO obtenerPorId(Long id) throws Exception {
        return mapearDTO(obtenerEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaDTO> listarPorSucursal(Long sucursalId, Pageable pageable) {
        return ventaRepository
                .findBySucursalIdOrderByFechaVentaDesc(sucursalId, pageable)
                .map(this::mapearDTO);
    }

    // ── Métodos de apoyo ──────────────────────────────────
    private Venta obtenerEntidad(Long id) throws Exception {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new Exception("Venta no encontrada: " + id));
    }

    private String generarNumero() {
        String prefijo = "VTA-" + DateTimeFormatter.ofPattern("yyyyMM")
                .format(LocalDateTime.now()) + "-";
        long count = ventaRepository.count() + 1;
        return prefijo + String.format("%04d", count);
    }

    private VentaDTO mapearDTO(Venta v) {
        List<VentaDTO.DetalleVentaRespDTO> detalles = v.getDetalles() == null
                ? List.of()
                : v.getDetalles().stream().map(d -> new VentaDTO.DetalleVentaRespDTO(
                d.getId(),
                d.getProducto().getNombre(),
                d.getProducto().getCodigo(),
                d.getCantidad(),
                d.getPrecioUnitario(),
                d.getPorcentajeDescuento(),
                d.getSubtotal()
        )).toList();

        return new VentaDTO(
                v.getId(), v.getNumero(),
                v.getSucursal().getNombre(),
                v.getVendedor().getNombre(),
                v.getEstado(),
                v.getClienteNombre(), v.getClienteDocumento(),
                v.getSubtotal(), v.getDescuentoTotal(), v.getTotal(),
                v.getObservaciones(), v.getFechaVenta(), detalles
        );
    }
}