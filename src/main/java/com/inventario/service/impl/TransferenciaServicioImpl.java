package com.inventario.service.impl;

import com.inventario.dto.request.CrearTransferenciaDTO;
import com.inventario.dto.request.DespacharTransferenciaDTO;
import com.inventario.dto.request.RecibirTransferenciaDTO;
import com.inventario.dto.response.TransferenciaDTO;
import com.inventario.model.*;
import com.inventario.model.enums.EstadoTransferencia;
import com.inventario.model.enums.PrioridadTransferencia;
import com.inventario.model.enums.TipoMovimiento;
import com.inventario.repository.*;
import com.inventario.service.interfaces.TransferenciaServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TransferenciaServicioImpl implements TransferenciaServicio {

    private final TransferenciaRepository transferenciaRepository;
    private final DetalleTransferenciaRepository detalleTransferenciaRepository;
    private final ProductoRepository productoRepository;
    private final InventarioItemRepository inventarioItemRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final SucursalServicioImpl sucursalServicio;
    private final UsuarioServicioImpl usuarioServicio;
    private final InventarioServicioImpl inventarioServicio;

    @Override
    public TransferenciaDTO solicitar(CrearTransferenciaDTO dto, Long solicitanteId) throws Exception {
        if (dto.sucursalOrigenId().equals(dto.sucursalDestinoId())) {
            throw new Exception("Origen y destino no pueden ser la misma sucursal");
        }
        Sucursal origen = sucursalServicio.obtenerEntidad(dto.sucursalOrigenId());
        Sucursal destino = sucursalServicio.obtenerEntidad(dto.sucursalDestinoId());
        Usuario solicitante = usuarioServicio.obtenerEntidad(solicitanteId);

        Transferencia transferencia = new Transferencia();
        transferencia.setNumero(generarNumero());
        transferencia.setSucursalOrigen(origen);
        transferencia.setSucursalDestino(destino);
        transferencia.setSolicitante(solicitante);
        transferencia.setPrioridad(dto.prioridad() != null
                ? dto.prioridad() : PrioridadTransferencia.NORMAL);
        transferencia.setObservaciones(dto.observaciones());
        transferencia.setEstado(EstadoTransferencia.SOLICITADA);

        List<DetalleTransferencia> detalles = new ArrayList<>();
        for (CrearTransferenciaDTO.DetalleTransferenciaDTO d : dto.detalles()) {
            Producto producto = productoRepository.findById(d.productoId())
                    .orElseThrow(() -> new Exception("Producto no encontrado: " + d.productoId()));

            DetalleTransferencia detalle = new DetalleTransferencia();
            detalle.setTransferencia(transferencia);
            detalle.setProducto(producto);
            detalle.setCantidadSolicitada(d.cantidadSolicitada());
            detalles.add(detalle);
        }

        transferencia.setDetalles(detalles);
        return mapearDTO(transferenciaRepository.save(transferencia));
    }

    @Override
    public TransferenciaDTO prepararEnvio(Long transferenciaId) throws Exception {
        Transferencia transferencia = obtenerEntidad(transferenciaId);
        if (transferencia.getEstado() != EstadoTransferencia.SOLICITADA) {
            throw new Exception("Solo se pueden preparar transferencias en estado SOLICITADA");
        }
        transferencia.setEstado(EstadoTransferencia.EN_PREPARACION);
        return mapearDTO(transferenciaRepository.save(transferencia));
    }

    @Override
    public TransferenciaDTO despachar(DespacharTransferenciaDTO dto,
                                      Long despachadorId) throws Exception {
        Transferencia transferencia = obtenerEntidad(dto.transferenciaId());
        if (transferencia.getEstado() != EstadoTransferencia.EN_PREPARACION) {
            throw new Exception("Solo se pueden despachar transferencias EN_PREPARACION");
        }

        Usuario despachador = usuarioServicio.obtenerEntidad(despachadorId);

        for (DespacharTransferenciaDTO.DetalleDespachoDTO d : dto.detalles()) {
            DetalleTransferencia detalle = detalleTransferenciaRepository.findById(d.detalleTransferenciaId())
                    .orElseThrow(() -> new Exception("Detalle no encontrado"));

            // Validar stock en origen
            InventarioItem itemOrigen = inventarioItemRepository
                    .findByProductoIdAndSucursalId(
                            detalle.getProducto().getId(),
                            transferencia.getSucursalOrigen().getId())
                    .orElseThrow(() -> new Exception(
                            "Sin inventario para " + detalle.getProducto().getNombre()));

            if (itemOrigen.getCantidadDisponible() < d.cantidadEnviada()) {
                throw new Exception("Stock insuficiente en origen para: " +
                        detalle.getProducto().getNombre());
            }

            // Descontar del origen
            int cantidadAnterior = itemOrigen.getCantidadDisponible();
            itemOrigen.setCantidadDisponible(cantidadAnterior - d.cantidadEnviada());
            inventarioItemRepository.save(itemOrigen);

            // Registrar movimiento de salida en origen
            MovimientoInventario movSalida = new MovimientoInventario();
            movSalida.setProducto(detalle.getProducto());
            movSalida.setSucursal(transferencia.getSucursalOrigen());
            movSalida.setTipo(TipoMovimiento.RETIRO_TRANSFERENCIA);
            movSalida.setCantidad(d.cantidadEnviada());
            movSalida.setCantidadAnterior(cantidadAnterior);
            movSalida.setCantidadPosterior(itemOrigen.getCantidadDisponible());
            movSalida.setMotivo("Despacho transferencia " + transferencia.getNumero());
            movSalida.setDocumentoReferencia(transferencia.getNumero());
            movSalida.setResponsable(despachador);
            movimientoRepository.save(movSalida);

            detalle.setCantidadEnviada(d.cantidadEnviada());
            detalleTransferenciaRepository.save(detalle);
        }

        transferencia.setDespachador(despachador);
        transferencia.setTransportista(dto.transportista());
        transferencia.setFechaEstimadaLlegada(dto.fechaEstimadaLlegada());
        transferencia.setTiempoEstimadoHoras(dto.tiempoEstimadoHoras());
        transferencia.setFechaDespacho(LocalDateTime.now());
        transferencia.setEstado(EstadoTransferencia.EN_TRANSITO);

        return mapearDTO(transferenciaRepository.save(transferencia));
    }

    @Override
    public TransferenciaDTO recibirMercancia(RecibirTransferenciaDTO dto,
                                             Long receptorId) throws Exception {
        Transferencia transferencia = obtenerEntidad(dto.transferenciaId());
        if (transferencia.getEstado() != EstadoTransferencia.EN_TRANSITO) {
            throw new Exception("Solo se pueden recibir transferencias EN_TRANSITO");
        }

        Usuario receptor = usuarioServicio.obtenerEntidad(receptorId);
        boolean hayFaltantes = false;

        for (RecibirTransferenciaDTO.DetalleRecepcionTransferenciaDTO rec : dto.detalles()) {
            DetalleTransferencia detalle = detalleTransferenciaRepository
                    .findById(rec.detalleTransferenciaId())
                    .orElseThrow(() -> new Exception("Detalle no encontrado"));

            detalle.setCantidadRecibida(rec.cantidadRecibida());
            int faltante = detalle.getCantidadEnviada() - rec.cantidadRecibida();
            detalle.setCantidadFaltante(faltante);

            if (faltante > 0) {
                hayFaltantes = true;
                detalle.setTratamientoFaltante(rec.tratamientoFaltante());
            }
            detalle.setObservaciones(rec.observaciones());
            detalleTransferenciaRepository.save(detalle);

            // Ingresar al inventario del destino
            InventarioItem itemDestino = inventarioServicio.obtenerOCrearItem(
                    detalle.getProducto().getId(),
                    transferencia.getSucursalDestino().getId());

            int cantidadAnterior = itemDestino.getCantidadDisponible();
            itemDestino.setCantidadDisponible(cantidadAnterior + rec.cantidadRecibida());
            inventarioItemRepository.save(itemDestino);

            MovimientoInventario movEntrada = new MovimientoInventario();
            movEntrada.setProducto(detalle.getProducto());
            movEntrada.setSucursal(transferencia.getSucursalDestino());
            movEntrada.setTipo(TipoMovimiento.INGRESO_TRANSFERENCIA);
            movEntrada.setCantidad(rec.cantidadRecibida());
            movEntrada.setCantidadAnterior(cantidadAnterior);
            movEntrada.setCantidadPosterior(itemDestino.getCantidadDisponible());
            movEntrada.setMotivo("Recepción transferencia " + transferencia.getNumero());
            movEntrada.setDocumentoReferencia(transferencia.getNumero());
            movEntrada.setResponsable(receptor);
            movimientoRepository.save(movEntrada);
        }

        // Calcular tiempo real
        if (transferencia.getFechaDespacho() != null) {
            long horas = Duration.between(transferencia.getFechaDespacho(),
                    LocalDateTime.now()).toHours();
            transferencia.setTiempoRealHoras((int) horas);
        }

        transferencia.setFechaRecepcion(LocalDateTime.now());
        transferencia.setObservacionesRecepcion(dto.observacionesRecepcion());
        transferencia.setEstado(hayFaltantes
                ? EstadoTransferencia.RECIBIDA_PARCIAL
                : EstadoTransferencia.RECIBIDA_COMPLETA);

        return mapearDTO(transferenciaRepository.save(transferencia));
    }

    @Override
    public TransferenciaDTO cancelar(Long transferenciaId) throws Exception {
        Transferencia transferencia = obtenerEntidad(transferenciaId);
        if (transferencia.getEstado() == EstadoTransferencia.EN_TRANSITO ||
                transferencia.getEstado() == EstadoTransferencia.RECIBIDA_COMPLETA) {
            throw new Exception("No se puede cancelar una transferencia en este estado");
        }
        transferencia.setEstado(EstadoTransferencia.CANCELADA);
        return mapearDTO(transferenciaRepository.save(transferencia));
    }

    @Override
    @Transactional(readOnly = true)
    public TransferenciaDTO obtenerPorId(Long id) throws Exception {
        return mapearDTO(obtenerEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransferenciaDTO> listarPorSucursalOrigen(Long sucursalId, Pageable pageable) {
        return transferenciaRepository
                .findBySucursalOrigenIdOrderByFechaSolicitudDesc(sucursalId, pageable)
                .map(this::mapearDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransferenciaDTO> listarPorSucursalDestino(Long sucursalId, Pageable pageable) {
        return transferenciaRepository
                .findBySucursalDestinoIdOrderByFechaSolicitudDesc(sucursalId, pageable)
                .map(this::mapearDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransferenciaDTO> listarActivasPorSucursal(Long sucursalId) {
        return transferenciaRepository.findActivasBySucursal(sucursalId)
                .stream().map(this::mapearDTO).toList();
    }

    // ── Métodos de apoyo ──────────────────────────────────
    private Transferencia obtenerEntidad(Long id) throws Exception {
        return transferenciaRepository.findById(id)
                .orElseThrow(() -> new Exception("Transferencia no encontrada: " + id));
    }

    private String generarNumero() {
        String prefijo = "TRF-" + DateTimeFormatter.ofPattern("yyyyMM")
                .format(LocalDateTime.now()) + "-";
        long count = transferenciaRepository.count() + 1;
        return prefijo + String.format("%04d", count);
    }

    private TransferenciaDTO mapearDTO(Transferencia t) {
        List<TransferenciaDTO.DetalleTransferenciaRespDTO> detalles = t.getDetalles() == null
                ? List.of()
                : t.getDetalles().stream().map(d ->
                new TransferenciaDTO.DetalleTransferenciaRespDTO(
                        d.getId(),
                        d.getProducto().getNombre(),
                        d.getProducto().getCodigo(),
                        d.getCantidadSolicitada(),
                        d.getCantidadEnviada(),
                        d.getCantidadRecibida(),
                        d.getCantidadFaltante(),
                        d.getTratamientoFaltante()
                )).toList();

        return new TransferenciaDTO(
                t.getId(), t.getNumero(),
                t.getSucursalOrigen().getNombre(),
                t.getSucursalDestino().getNombre(),
                t.getSolicitante().getNombre(),
                t.getDespachador() != null ? t.getDespachador().getNombre() : null,
                t.getEstado(), t.getPrioridad(),
                t.getTransportista(),
                t.getFechaSolicitud(), t.getFechaDespacho(),
                t.getFechaEstimadaLlegada(), t.getFechaRecepcion(),
                t.getTiempoEstimadoHoras(), t.getTiempoRealHoras(),
                t.getObservaciones(), t.getObservacionesRecepcion(),
                detalles
        );
    }
}