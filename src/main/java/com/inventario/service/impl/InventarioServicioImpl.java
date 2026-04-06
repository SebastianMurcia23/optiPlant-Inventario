package com.inventario.service.impl;

import com.inventario.config.JWTUtils;
import com.inventario.dto.MensajeDTO;
import com.inventario.dto.request.AjustarStockDTO;
import com.inventario.dto.response.InventarioItemDTO;
import com.inventario.model.*;
import com.inventario.model.enums.TipoAlerta;
import com.inventario.model.enums.EstadoAlerta;
import com.inventario.model.enums.TipoMovimiento;
import com.inventario.repository.*;
import com.inventario.service.interfaces.InventarioServicio;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ══════════════════════════════════════════════════════════════════
 * SERVICIO DE INVENTARIO — Con filtro RBAC automático por sucursal
 * ══════════════════════════════════════════════════════════════════
 *
 * REGLA DE NEGOCIO CRÍTICA (OptiPlant 6.2):
 *
 *   - ADMINISTRADOR_GENERAL → Ve TODAS las sucursales
 *   - GERENTE_SUCURSAL      → Solo ve SU sucursal (del JWT)
 *   - OPERADOR_INVENTARIO   → Solo ve SU sucursal (del JWT)
 *
 * El método auxiliar validarAccesoSucursal() se usa en TODOS los
 * endpoints para garantizar que un Gerente/Operador nunca pueda
 * consultar ni modificar inventario de otra sucursal.
 * ══════════════════════════════════════════════════════════════════
 */
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
    private final JWTUtils jwtUtils;
    private final HttpServletRequest request;

    // ════════════════════════════════════════════════════════════
    // MÉTODOS AUXILIARES RBAC
    // ════════════════════════════════════════════════════════════

    /**
     * Extrae el token JWT del header Authorization de la request actual.
     */
    private String obtenerToken() {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.replace("Bearer ", "");
        }
        throw new RuntimeException("Token no proporcionado");
    }

    /**
     * Obtiene el rol del usuario actual desde el JWT.
     */
    private String obtenerRolActual() {
        return jwtUtils.obtenerRol(obtenerToken());
    }

    /**
     * Obtiene el ID de sucursal del usuario actual desde el JWT.
     * Retorna null para ADMINISTRADOR_GENERAL (no tiene sucursal fija).
     */
    private Long obtenerSucursalIdActual() {
        return jwtUtils.obtenerSucursalId(obtenerToken());
    }

    /**
     * Obtiene el ID del usuario actual desde el JWT.
     */
    private Long obtenerUsuarioIdActual() {
        return jwtUtils.obtenerUsuarioId(obtenerToken());
    }

    /**
     * ══════════════════════════════════════════════════════════════
     * VALIDACIÓN RBAC CENTRAL
     * ══════════════════════════════════════════════════════════════
     *
     * Si el usuario NO es ADMINISTRADOR_GENERAL, verifica que el
     * sucursalId solicitado coincida con la sucursal asignada en
     * su JWT. Si no coincide, lanza excepción.
     *
     * Esto impide que un Gerente/Operador de la Sucursal Centro
     * consulte o modifique inventario de la Sucursal Norte.
     */
    private void validarAccesoSucursal(Long sucursalIdSolicitado) throws Exception {
        String rol = obtenerRolActual();

        // Admin tiene acceso total — sin restricciones
        if ("ADMINISTRADOR_GENERAL".equals(rol)) {
            return;
        }

        // Gerente y Operador: solo su propia sucursal
        Long sucursalIdUsuario = obtenerSucursalIdActual();
        if (sucursalIdUsuario == null) {
            throw new Exception("Tu usuario no tiene una sucursal asignada. Contacta al administrador.");
        }
        if (!sucursalIdUsuario.equals(sucursalIdSolicitado)) {
            throw new Exception(
                    "Acceso denegado: no tienes permiso para operar sobre el inventario de otra sucursal."
            );
        }
    }

    /**
     * Para Admin: retorna la sucursal solicitada o todas.
     * Para Gerente/Operador: siempre retorna SU sucursal, ignorando
     * cualquier parámetro que intente pasar.
     */
    private Long resolverSucursalId(Long sucursalIdParametro) {
        String rol = obtenerRolActual();

        if ("ADMINISTRADOR_GENERAL".equals(rol)) {
            // Admin puede consultar cualquier sucursal
            return sucursalIdParametro;
        }

        // Gerente/Operador: forzar su propia sucursal siempre
        Long sucursalIdUsuario = obtenerSucursalIdActual();
        return sucursalIdUsuario != null ? sucursalIdUsuario : sucursalIdParametro;
    }

    // ════════════════════════════════════════════════════════════
    // CONSULTAS DE INVENTARIO
    // ════════════════════════════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public List<InventarioItemDTO> listarPorSucursal(Long sucursalId) throws Exception {
        // RBAC: validar que puede ver esta sucursal
        Long sucursalReal = resolverSucursalId(sucursalId);
        validarAccesoSucursal(sucursalReal);

        return inventarioRepo.findBySucursalId(sucursalReal)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public InventarioItemDTO obtenerStock(Long productoId, Long sucursalId) throws Exception {
        // RBAC: validar acceso
        Long sucursalReal = resolverSucursalId(sucursalId);
        validarAccesoSucursal(sucursalReal);

        InventarioItem item = inventarioRepo
                .findByProductoIdAndSucursalId(productoId, sucursalReal)
                .orElseThrow(() -> new Exception("Producto no encontrado en esta sucursal"));
        return toDTO(item);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioItemDTO> stockEnRed(Long productoId) throws Exception {
        String rol = obtenerRolActual();

        if ("ADMINISTRADOR_GENERAL".equals(rol)) {
            // Admin ve stock en TODAS las sucursales
            return inventarioRepo.findByProductoId(productoId)
                    .stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
        }

        // Gerente/Operador: solo ven stock de su sucursal
        // Pero también ven las demás sucursales (solo consulta, no acción)
        // Esto cumple con "Comparte información de inventario con las demás
        // sucursales" (Sección 2.1 del PDF)
        return inventarioRepo.findByProductoId(productoId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioItemDTO> stockBajo(Long sucursalId) throws Exception {
        // RBAC: forzar sucursal del usuario
        Long sucursalReal = resolverSucursalId(sucursalId);
        validarAccesoSucursal(sucursalReal);

        return inventarioRepo.findStockBajoEnSucursal(sucursalReal)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ════════════════════════════════════════════════════════════
    // AJUSTE DE STOCK
    // ════════════════════════════════════════════════════════════

    @Override
    public String ajustarStock(AjustarStockDTO dto) throws Exception {
        // RBAC: validar que puede modificar esta sucursal
        validarAccesoSucursal(dto.sucursalId());

        Producto producto = productoRepo.findById(dto.productoId())
                .orElseThrow(() -> new Exception("Producto no encontrado"));
        Sucursal sucursal = sucursalRepo.findById(dto.sucursalId())
                .orElseThrow(() -> new Exception("Sucursal no encontrada"));

        // Obtener o crear el registro de inventario
        InventarioItem item = inventarioRepo
                .findByProductoIdAndSucursalId(dto.productoId(), dto.sucursalId())
                .orElseGet(() -> {
                    InventarioItem nuevo = new InventarioItem();
                    nuevo.setProducto(producto);
                    nuevo.setSucursal(sucursal);
                    nuevo.setCantidadDisponible(0);
                    nuevo.setCantidadReservada(0);
                    nuevo.setStockMinimo(producto.getStockMinimoGlobal() != null ? producto.getStockMinimoGlobal() : 0);
                    nuevo.setCostoPromedioLocal(BigDecimal.ZERO);
                    return nuevo;
                });

        int cantidadAnterior = item.getCantidadDisponible();
        int cantidadNueva;

        // Determinar si es ingreso o retiro
        TipoMovimiento tipo = dto.tipo();
        boolean esIngreso = tipo.name().startsWith("INGRESO");

        if (esIngreso) {
            cantidadNueva = cantidadAnterior + dto.cantidad();
        } else {
            if (cantidadAnterior < dto.cantidad()) {
                throw new Exception(
                        "Stock insuficiente. Disponible: " + cantidadAnterior +
                                ", solicitado: " + dto.cantidad()
                );
            }
            cantidadNueva = cantidadAnterior - dto.cantidad();
        }

        item.setCantidadDisponible(cantidadNueva);
        inventarioRepo.save(item);

        // Registrar movimiento de trazabilidad
        Usuario responsable = usuarioRepo.findById(obtenerUsuarioIdActual())
                .orElseThrow(() -> new Exception("Usuario responsable no encontrado"));

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setProducto(producto);
        movimiento.setSucursal(sucursal);
        movimiento.setTipo(tipo);
        movimiento.setCantidad(dto.cantidad());
        movimiento.setCantidadAnterior(cantidadAnterior);
        movimiento.setCantidadPosterior(cantidadNueva);
        movimiento.setMotivo(dto.motivo());
        movimiento.setDocumentoReferencia(dto.documentoReferencia());
        movimiento.setResponsable(responsable);
        movimientoRepo.save(movimiento);

        // Verificar si genera alerta de stock bajo
        verificarAlertasStock(item, producto, sucursal);

        return "Stock ajustado correctamente. " + producto.getNombre() +
                ": " + cantidadAnterior + " → " + cantidadNueva;
    }

    // ════════════════════════════════════════════════════════════
    // CONFIGURAR MÍNIMOS (solo Gerente/Admin)
    // ════════════════════════════════════════════════════════════

    @Override
    public InventarioItemDTO configurarMinimos(
            Long productoId, Long sucursalId,
            Integer stockMinimo, Integer stockMaximo) throws Exception {
        // RBAC: validar acceso
        validarAccesoSucursal(sucursalId);

        // Validar rol: solo Gerente o Admin puede configurar mínimos
        String rol = obtenerRolActual();
        if ("OPERADOR_INVENTARIO".equals(rol)) {
            throw new Exception("Solo Gerentes y Administradores pueden configurar stock mínimo/máximo");
        }

        InventarioItem item = inventarioRepo
                .findByProductoIdAndSucursalId(productoId, sucursalId)
                .orElseThrow(() -> new Exception("Producto no encontrado en esta sucursal"));

        item.setStockMinimo(stockMinimo);
        if (stockMaximo != null) {
            item.setStockMaximo(stockMaximo);
        }
        inventarioRepo.save(item);

        return toDTO(item);
    }

    // ════════════════════════════════════════════════════════════
    // MÉTODOS PRIVADOS
    // ════════════════════════════════════════════════════════════

    /**
     * Genera alertas automáticas cuando el stock cae por debajo
     * del mínimo o llega a cero. No crea duplicados.
     */
    private void verificarAlertasStock(
            InventarioItem item, Producto producto, Sucursal sucursal) {

        int stock = item.getCantidadDisponible();
        int minimo = item.getStockMinimo() != null ? item.getStockMinimo() : 0;

        if (stock == 0) {
            crearAlertaSiNoExiste(TipoAlerta.STOCK_AGOTADO, producto, sucursal,
                    "Stock agotado de " + producto.getNombre() + " en " + sucursal.getNombre());
        } else if (stock <= minimo) {
            crearAlertaSiNoExiste(TipoAlerta.STOCK_MINIMO, producto, sucursal,
                    "Stock bajo de " + producto.getNombre() + " en " + sucursal.getNombre() +
                            ": " + stock + " unidades (mínimo: " + minimo + ")");
        }
    }

    private void crearAlertaSiNoExiste(
            TipoAlerta tipo, Producto producto, Sucursal sucursal, String mensaje) {
        boolean yaExiste = alertaRepo
                .findBySucursalIdAndProductoIdAndTipoAndEstado(
                        sucursal.getId(), producto.getId(), tipo, EstadoAlerta.ACTIVA)
                .isPresent();

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

    /**
     * Convierte entidad a DTO de respuesta.
     */
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
                item.getCantidadDisponible() <= (item.getStockMinimo() != null ? item.getStockMinimo() : 0),
                item.getUltimaActualizacion() != null
                        ? item.getUltimaActualizacion().toString() : null
        );
    }
}