package com.inventario.service.impl;

import com.inventario.dto.request.CrearOrdenCompraDTO;
import com.inventario.dto.request.RecibirOrdenCompraDTO;
import com.inventario.dto.response.OrdenCompraDTO;
import com.inventario.model.*;
import com.inventario.model.enums.EstadoOrdenCompra;
import com.inventario.model.enums.TipoMovimiento;
import com.inventario.repository.*;
import com.inventario.service.interfaces.OrdenCompraServicio;
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
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Transactional
@RequiredArgsConstructor
public class OrdenCompraServicioImpl implements OrdenCompraServicio {

    private final OrdenCompraRepository ordenCompraRepository;
    private final DetalleOrdenCompraRepository detalleOrdenCompraRepository;
    private final ProveedorRepository proveedorRepository;
    private final ProductoRepository productoRepository;
    private final SucursalServicioImpl sucursalServicio;
    private final UsuarioServicioImpl usuarioServicio;
    private final InventarioServicioImpl inventarioServicio;
    private final MovimientoInventarioRepository movimientoRepository;

    @Override
    public OrdenCompraDTO crear(CrearOrdenCompraDTO dto, Long solicitanteId) throws Exception {
        Proveedor proveedor = proveedorRepository.findById(dto.proveedorId())
                .orElseThrow(() -> new Exception("Proveedor no encontrado: " + dto.proveedorId()));
        Sucursal sucursal = sucursalServicio.obtenerEntidad(dto.sucursalId());
        Usuario solicitante = usuarioServicio.obtenerEntidad(solicitanteId);

        OrdenCompra orden = new OrdenCompra();
        orden.setNumero(generarNumero());
        orden.setProveedor(proveedor);
        orden.setSucursal(sucursal);
        orden.setSolicitante(solicitante);
        orden.setFechaEsperadaEntrega(dto.fechaEsperadaEntrega());
        orden.setPlazoPagoDias(dto.plazoPagoDias());
        orden.setObservaciones(dto.observaciones());
        orden.setEstado(EstadoOrdenCompra.BORRADOR);

        // Construir detalles
        List<DetalleOrdenCompra> detalles = new ArrayList<>();
        BigDecimal subtotalOrden = BigDecimal.ZERO;

        for (CrearOrdenCompraDTO.DetalleOrdenCompraDTO d : dto.detalles()) {
            Producto producto = productoRepository.findById(d.productoId())
                    .orElseThrow(() -> new Exception("Producto no encontrado: " + d.productoId()));

            DetalleOrdenCompra detalle = new DetalleOrdenCompra();
            detalle.setOrdenCompra(orden);
            detalle.setProducto(producto);
            detalle.setCantidadSolicitada(d.cantidadSolicitada());
            detalle.setPrecioUnitario(d.precioUnitario());
            detalle.setPorcentajeDescuento(
                    d.porcentajeDescuento() != null ? d.porcentajeDescuento() : BigDecimal.ZERO);
            detalle.calcularSubtotal();
            detalles.add(detalle);
            subtotalOrden = subtotalOrden.add(detalle.getSubtotal());
        }

        orden.setDetalles(detalles);
        orden.setSubtotal(subtotalOrden);
        orden.setTotal(subtotalOrden);

        return mapearDTO(ordenCompraRepository.save(orden));
    }

    @Override
    public OrdenCompraDTO enviarAProveedor(Long ordenId) throws Exception {
        OrdenCompra orden = obtenerEntidad(ordenId);
        if (orden.getEstado() != EstadoOrdenCompra.BORRADOR) {
            throw new Exception("Solo se pueden enviar órdenes en estado BORRADOR");
        }
        orden.setEstado(EstadoOrdenCompra.ENVIADA);
        return mapearDTO(ordenCompraRepository.save(orden));
    }

    @Override
    public OrdenCompraDTO recibirMercancia(RecibirOrdenCompraDTO dto,
                                           Long responsableId) throws Exception {
        OrdenCompra orden = obtenerEntidad(dto.ordenCompraId());

        if (orden.getEstado() != EstadoOrdenCompra.ENVIADA &&
                orden.getEstado() != EstadoOrdenCompra.PARCIALMENTE_RECIBIDA) {
            throw new Exception("La orden no está en estado válido para recibir");
        }

        Usuario responsable = usuarioServicio.obtenerEntidad(responsableId);
        AtomicInteger detallesCompletos = new AtomicInteger(0);

        for (RecibirOrdenCompraDTO.DetalleRecepcionDTO rec : dto.detalles()) {
            DetalleOrdenCompra detalle = detalleOrdenCompraRepository.findById(rec.detalleOrdenCompraId())
                    .orElseThrow(() -> new Exception("Detalle no encontrado"));

            detalle.setCantidadRecibida(
                    (detalle.getCantidadRecibida() != null ? detalle.getCantidadRecibida() : 0)
                            + rec.cantidadRecibida());
            detalleOrdenCompraRepository.save(detalle);

            // Actualizar inventario
            InventarioItem item = inventarioServicio.obtenerOCrearItem(
                    detalle.getProducto().getId(), orden.getSucursal().getId());

            int cantidadAnterior = item.getCantidadDisponible();
            inventarioServicio.actualizarCostoPromedio(item, rec.cantidadRecibida(),
                    detalle.getPrecioUnitario());
            item.setCantidadDisponible(cantidadAnterior + rec.cantidadRecibida());

            // Registrar movimiento
            MovimientoInventario mov = new MovimientoInventario();
            mov.setProducto(detalle.getProducto());
            mov.setSucursal(orden.getSucursal());
            mov.setTipo(TipoMovimiento.INGRESO_COMPRA);
            mov.setCantidad(rec.cantidadRecibida());
            mov.setCantidadAnterior(cantidadAnterior);
            mov.setCantidadPosterior(item.getCantidadDisponible());
            mov.setCostoUnitario(detalle.getPrecioUnitario());
            mov.setMotivo("Recepción orden de compra " + orden.getNumero());
            mov.setDocumentoReferencia(orden.getNumero());
            mov.setResponsable(responsable);
            movimientoRepository.save(mov);

            if (detalle.getCantidadRecibida() >= detalle.getCantidadSolicitada()) {
                detallesCompletos.incrementAndGet();
            }
        }

        // Actualizar estado de la orden
        if (detallesCompletos.get() == orden.getDetalles().size()) {
            orden.setEstado(EstadoOrdenCompra.RECIBIDA);
        } else {
            orden.setEstado(EstadoOrdenCompra.PARCIALMENTE_RECIBIDA);
        }
        orden.setFechaRecepcion(LocalDateTime.now());

        return mapearDTO(ordenCompraRepository.save(orden));
    }

    @Override
    public OrdenCompraDTO cancelar(Long ordenId) throws Exception {
        OrdenCompra orden = obtenerEntidad(ordenId);
        if (orden.getEstado() == EstadoOrdenCompra.RECIBIDA) {
            throw new Exception("No se puede cancelar una orden ya recibida");
        }
        orden.setEstado(EstadoOrdenCompra.CANCELADA);
        return mapearDTO(ordenCompraRepository.save(orden));
    }

    @Override
    @Transactional(readOnly = true)
    public OrdenCompraDTO obtenerPorId(Long id) throws Exception {
        return mapearDTO(obtenerEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrdenCompraDTO> listarPorSucursal(Long sucursalId, Pageable pageable) {
        return ordenCompraRepository
                .findBySucursalIdOrderByFechaCreacionDesc(sucursalId, pageable)
                .map(this::mapearDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrdenCompraDTO> listarPorProveedor(Long proveedorId, Pageable pageable) {
        return ordenCompraRepository
                .findByProveedorIdOrderByFechaCreacionDesc(proveedorId, pageable)
                .map(this::mapearDTO);
    }

    // ── Métodos de apoyo ──────────────────────────────────
    private OrdenCompra obtenerEntidad(Long id) throws Exception {
        return ordenCompraRepository.findById(id)
                .orElseThrow(() -> new Exception("Orden de compra no encontrada: " + id));
    }

    private String generarNumero() {
        String prefijo = "OC-" + DateTimeFormatter.ofPattern("yyyyMM")
                .format(LocalDateTime.now()) + "-";
        long count = ordenCompraRepository.count() + 1;
        return prefijo + String.format("%04d", count);
    }

    private OrdenCompraDTO mapearDTO(OrdenCompra o) {
        List<OrdenCompraDTO.DetalleOrdenCompraRespDTO> detalles = o.getDetalles() == null
                ? List.of()
                : o.getDetalles().stream().map(d -> new OrdenCompraDTO.DetalleOrdenCompraRespDTO(
                d.getId(),
                d.getProducto().getNombre(),
                d.getProducto().getCodigo(),
                d.getCantidadSolicitada(),
                d.getCantidadRecibida(),
                d.getPrecioUnitario(),
                d.getPorcentajeDescuento(),
                d.getSubtotal()
        )).toList();

        return new OrdenCompraDTO(
                o.getId(), o.getNumero(),
                o.getProveedor().getNombre(),
                o.getSucursal().getNombre(),
                o.getSolicitante().getNombre(),
                o.getEstado(),
                o.getFechaCreacion(), o.getFechaEsperadaEntrega(),
                o.getFechaRecepcion(),
                o.getSubtotal(), o.getDescuentoTotal(), o.getTotal(),
                o.getPlazoPagoDias(), o.getObservaciones(),
                detalles
        );
    }
}