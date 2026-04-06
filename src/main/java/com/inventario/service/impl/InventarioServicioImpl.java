package com.inventario.service.impl;

import com.inventario.dto.request.AjustarStockDTO;
import com.inventario.dto.response.InventarioItemDTO;
import com.inventario.model.*;
import com.inventario.model.enums.EstadoAlerta;
import com.inventario.model.enums.TipoAlerta;
import com.inventario.model.enums.TipoMovimiento;
import com.inventario.repository.*;
import com.inventario.service.interfaces.InventarioServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InventarioServicioImpl implements InventarioServicio {

    private final InventarioItemRepository inventarioRepo;
    private final ProductoRepository productoRepo;
    private final SucursalRepository sucursalRepo;
    private final MovimientoInventarioRepository movimientoRepo;
    private final UsuarioRepository usuarioRepo;
    private final AlertaRepository alertaRepo;

    // ─── Interfaz: listarInventarioSucursal ──────────────────
    @Override
    @Transactional(readOnly = true)
    public List<InventarioItemDTO> listarInventarioSucursal(Long sucursalId) {
        return inventarioRepo.findBySucursalId(sucursalId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ─── Interfaz: obtenerStock ───────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public InventarioItemDTO obtenerStock(Long productoId, Long sucursalId) throws Exception {
        InventarioItem item = inventarioRepo
                .findByProductoIdAndSucursalId(productoId, sucursalId)
                .orElseThrow(() -> new Exception("Producto no encontrado en esta sucursal"));
        return toDTO(item);
    }

    // ─── Interfaz: listarStockPorProducto ────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<InventarioItemDTO> listarStockPorProducto(Long productoId) {
        return inventarioRepo.findByProductoId(productoId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ─── Interfaz: listarStockBajoEnSucursal ─────────────────
    @Override
    @Transactional(readOnly = true)
    public List<InventarioItemDTO> listarStockBajoEnSucursal(Long sucursalId) {
        return inventarioRepo.findStockBajoEnSucursal(sucursalId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ─── Interfaz: ajustarStock ───────────────────────────────
    @Override
    public void ajustarStock(AjustarStockDTO dto, Long responsableId) throws Exception {
        Producto producto = productoRepo.findById(dto.productoId())
                .orElseThrow(() -> new Exception("Producto no encontrado"));
        Sucursal sucursal = sucursalRepo.findById(dto.sucursalId())
                .orElseThrow(() -> new Exception("Sucursal no encontrada"));

        InventarioItem item = inventarioRepo
                .findByProductoIdAndSucursalId(dto.productoId(), dto.sucursalId())
                .orElseGet(() -> {
                    InventarioItem nuevo = new InventarioItem();
                    nuevo.setProducto(producto);
                    nuevo.setSucursal(sucursal);
                    nuevo.setCantidadDisponible(0);
                    nuevo.setCantidadReservada(0);
                    nuevo.setStockMinimo(
                            producto.getStockMinimoGlobal() != null
                                    ? producto.getStockMinimoGlobal() : 0);
                    nuevo.setCostoPromedioLocal(BigDecimal.ZERO);
                    return nuevo;
                });

        int cantidadAnterior = item.getCantidadDisponible();
        boolean esIngreso = dto.tipo().name().startsWith("INGRESO");

        int cantidadNueva;
        if (esIngreso) {
            cantidadNueva = cantidadAnterior + dto.cantidad();
        } else {
            if (cantidadAnterior < dto.cantidad()) {
                throw new Exception("Stock insuficiente. Disponible: " +
                        cantidadAnterior + ", solicitado: " + dto.cantidad());
            }
            cantidadNueva = cantidadAnterior - dto.cantidad();
        }

        item.setCantidadDisponible(cantidadNueva);
        inventarioRepo.save(item);

        // Trazabilidad
        Usuario responsable = usuarioRepo.findById(responsableId)
                .orElseThrow(() -> new Exception("Usuario responsable no encontrado"));

        MovimientoInventario mov = new MovimientoInventario();
        mov.setProducto(producto);
        mov.setSucursal(sucursal);
        mov.setTipo(dto.tipo());
        mov.setCantidad(dto.cantidad());
        mov.setCantidadAnterior(cantidadAnterior);
        mov.setCantidadPosterior(cantidadNueva);
        mov.setMotivo(dto.motivo());
        mov.setDocumentoReferencia(dto.documentoReferencia());
        mov.setResponsable(responsable);
        movimientoRepo.save(mov);

        verificarAlertasStock(item, producto, sucursal);
    }

    // ─── Interfaz: configurarStockMinimo ─────────────────────
    @Override
    public InventarioItemDTO configurarStockMinimo(
            Long productoId, Long sucursalId,
            Integer stockMinimo, Integer stockMaximo) throws Exception {

        InventarioItem item = inventarioRepo
                .findByProductoIdAndSucursalId(productoId, sucursalId)
                .orElseThrow(() -> new Exception(
                        "Producto no encontrado en esta sucursal"));

        item.setStockMinimo(stockMinimo);
        if (stockMaximo != null) item.setStockMaximo(stockMaximo);
        inventarioRepo.save(item);
        return toDTO(item);
    }

    // ─── Privados ─────────────────────────────────────────────
    private void verificarAlertasStock(
            InventarioItem item, Producto producto, Sucursal sucursal) {

        int stock   = item.getCantidadDisponible();
        int minimo  = item.getStockMinimo() != null ? item.getStockMinimo() : 0;

        if (stock == 0) {
            crearAlertaSiNoExiste(TipoAlerta.STOCK_AGOTADO, producto, sucursal,
                    "Stock agotado: " + producto.getNombre() +
                            " en " + sucursal.getNombre());
        } else if (stock <= minimo) {
            crearAlertaSiNoExiste(TipoAlerta.STOCK_MINIMO, producto, sucursal,
                    "Stock bajo: " + producto.getNombre() +
                            " en " + sucursal.getNombre() +
                            " (" + stock + " uds, mínimo: " + minimo + ")");
        }
    }

    private void crearAlertaSiNoExiste(
            TipoAlerta tipo, Producto producto,
            Sucursal sucursal, String mensaje) {

        boolean yaExiste = alertaRepo.existeAlertaActivaParaProducto(
                sucursal.getId(), producto.getId(), tipo);

        if (!yaExiste) {
            Alerta alerta = new Alerta();
            alerta.setTipo(tipo);
            alerta.setEstado(EstadoAlerta.ACTIVA);
            alerta.setProducto(producto);
            alerta.setSucursal(sucursal);
            alerta.setMensaje(mensaje);
            alertaRepo.save(alerta);
        }
    }

    private InventarioItemDTO toDTO(InventarioItem item) {
        return new InventarioItemDTO(
                item.getId(),
                item.getProducto().getId(),
                item.getProducto().getCodigo(),
                item.getProducto().getNombre(),
                item.getProducto().getUnidadMedida() != null
                        ? item.getProducto().getUnidadMedida().getAbreviatura() : "",
                item.getSucursal().getId(),
                item.getSucursal().getNombre(),
                item.getCantidadDisponible(),
                item.getCantidadReservada(),
                item.getCantidadDisponible() + item.getCantidadReservada(),
                item.getStockMinimo() != null ? item.getStockMinimo() : 0,
                item.getStockMaximo(),
                item.getCostoPromedioLocal(),
                item.getCantidadDisponible() <= (
                        item.getStockMinimo() != null ? item.getStockMinimo() : 0),
                item.getUltimaActualizacion()   // ← LocalDateTime directo, sin .toString()
        );
    }

    // ─── Métodos helper para otros servicios ─────────────────

    /**
     * Obtiene o crea un InventarioItem para producto+sucursal.
     * Usado por OrdenCompraServicio y TransferenciaServicio.
     */
    public InventarioItem obtenerOCrearItem(Long productoId, Long sucursalId) throws Exception {
        Producto producto = productoRepo.findById(productoId)
                .orElseThrow(() -> new Exception("Producto no encontrado: " + productoId));
        Sucursal sucursal = sucursalRepo.findById(sucursalId)
                .orElseThrow(() -> new Exception("Sucursal no encontrada: " + sucursalId));

        return inventarioRepo.findByProductoIdAndSucursalId(productoId, sucursalId)
                .orElseGet(() -> {
                    InventarioItem nuevo = new InventarioItem();
                    nuevo.setProducto(producto);
                    nuevo.setSucursal(sucursal);
                    nuevo.setCantidadDisponible(0);
                    nuevo.setCantidadReservada(0);
                    nuevo.setStockMinimo(
                            producto.getStockMinimoGlobal() != null
                                    ? producto.getStockMinimoGlobal() : 0);
                    nuevo.setCostoPromedioLocal(BigDecimal.ZERO);
                    return inventarioRepo.save(nuevo);
                });
    }

    /**
     * Actualiza el costo promedio ponderado al recibir mercancía.
     * Fórmula: (stockActual × costoActual + cantidadNueva × costoNuevo) / (stockActual + cantidadNueva)
     */
    public void actualizarCostoPromedio(InventarioItem item,
                                        int cantidadNueva,
                                        BigDecimal costoNuevo) {
        if (costoNuevo == null || costoNuevo.compareTo(BigDecimal.ZERO) == 0) return;

        int stockActual = item.getCantidadDisponible();
        BigDecimal costoActual = item.getCostoPromedioLocal() != null
                ? item.getCostoPromedioLocal() : BigDecimal.ZERO;

        if (stockActual == 0) {
            item.setCostoPromedioLocal(costoNuevo);
        } else {
            BigDecimal totalActual = costoActual.multiply(BigDecimal.valueOf(stockActual));
            BigDecimal totalNuevo  = costoNuevo.multiply(BigDecimal.valueOf(cantidadNueva));
            BigDecimal totalUnidades = BigDecimal.valueOf(stockActual + cantidadNueva);
            item.setCostoPromedioLocal(
                    totalActual.add(totalNuevo).divide(totalUnidades,
                            2, java.math.RoundingMode.HALF_UP));
        }
        inventarioRepo.save(item);
    }
}