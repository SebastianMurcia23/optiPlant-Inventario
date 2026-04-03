package com.inventario.service.impl;

import com.inventario.dto.response.AlertaDTO;
import com.inventario.model.Alerta;
import com.inventario.model.InventarioItem;
import com.inventario.model.LoteProducto;
import com.inventario.model.Sucursal;
import com.inventario.model.enums.EstadoAlerta;
import com.inventario.model.enums.TipoAlerta;
import com.inventario.repository.AlertaRepository;
import com.inventario.repository.InventarioItemRepository;
import com.inventario.repository.LoteProductoRepository;
import com.inventario.repository.SucursalRepository;
import com.inventario.service.interfaces.AlertaServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AlertaServicioImpl implements AlertaServicio {

    private final AlertaRepository alertaRepository;
    private final InventarioItemRepository inventarioItemRepository;
    private final LoteProductoRepository loteProductoRepository;
    private final SucursalRepository sucursalRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AlertaDTO> listarActivasPorSucursal(Long sucursalId) {
        return alertaRepository.findActivasBySucursal(sucursalId)
                .stream().map(this::mapearDTO).toList();
    }

    @Override
    public void marcarComoLeida(Long alertaId) throws Exception {
        Alerta alerta = obtenerEntidad(alertaId);
        alerta.setEstado(EstadoAlerta.LEIDA);
        alerta.setFechaLectura(LocalDateTime.now());
        alertaRepository.save(alerta);
    }

    @Override
    public void marcarComoResuelta(Long alertaId) throws Exception {
        Alerta alerta = obtenerEntidad(alertaId);
        alerta.setEstado(EstadoAlerta.RESUELTA);
        alerta.setFechaResolucion(LocalDateTime.now());
        alertaRepository.save(alerta);
    }

    @Override
    public void generarAlertasStockBajo(Long sucursalId) {
        List<InventarioItem> itemsBajos =
                inventarioItemRepository.findStockBajoBySucursal(sucursalId);

        for (InventarioItem item : itemsBajos) {
            TipoAlerta tipo = item.getCantidadDisponible() == 0
                    ? TipoAlerta.STOCK_AGOTADO
                    : TipoAlerta.STOCK_MINIMO;

            // No crear alerta duplicada
            if (alertaRepository.existeAlertaActivaParaProducto(
                    sucursalId, item.getProducto().getId(), tipo)) continue;

            Alerta alerta = new Alerta();
            alerta.setTipo(tipo);
            alerta.setProducto(item.getProducto());
            alerta.setSucursal(item.getSucursal());
            alerta.setMensaje(tipo == TipoAlerta.STOCK_AGOTADO
                    ? "Producto AGOTADO: " + item.getProducto().getNombre()
                    : "Stock bajo para: " + item.getProducto().getNombre() +
                    " (disponible: " + item.getCantidadDisponible() +
                    ", mínimo: " + item.getStockMinimo() + ")");
            alertaRepository.save(alerta);
        }
    }

    @Override
    public void generarAlertasVencimiento(Long sucursalId) {
        Sucursal sucursal = sucursalRepository.findById(sucursalId).orElse(null);
        if (sucursal == null) return;

        // Lotes que vencen en los próximos 30 días
        List<LoteProducto> proximosAVencer = loteProductoRepository
                .findProximosAVencer(sucursalId, LocalDate.now().plusDays(30));

        for (LoteProducto lote : proximosAVencer) {
            if (alertaRepository.existeAlertaActivaParaProducto(
                    sucursalId, lote.getProducto().getId(),
                    TipoAlerta.PRODUCTO_POR_VENCER)) continue;

            Alerta alerta = new Alerta();
            alerta.setTipo(TipoAlerta.PRODUCTO_POR_VENCER);
            alerta.setProducto(lote.getProducto());
            alerta.setSucursal(sucursal);
            alerta.setMensaje("Lote " + lote.getNumeroLote() + " de " +
                    lote.getProducto().getNombre() +
                    " vence el " + lote.getFechaVencimiento());
            alertaRepository.save(alerta);
        }

        // Lotes ya vencidos
        List<LoteProducto> vencidos = loteProductoRepository.findVencidosConStock(sucursalId);
        for (LoteProducto lote : vencidos) {
            if (alertaRepository.existeAlertaActivaParaProducto(
                    sucursalId, lote.getProducto().getId(),
                    TipoAlerta.PRODUCTO_VENCIDO)) continue;

            Alerta alerta = new Alerta();
            alerta.setTipo(TipoAlerta.PRODUCTO_VENCIDO);
            alerta.setProducto(lote.getProducto());
            alerta.setSucursal(sucursal);
            alerta.setMensaje("VENCIDO: Lote " + lote.getNumeroLote() + " de " +
                    lote.getProducto().getNombre() +
                    " venció el " + lote.getFechaVencimiento());
            alertaRepository.save(alerta);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long contarActivasPorSucursal(Long sucursalId) {
        return alertaRepository.countActivasBySucursal(sucursalId);
    }

    // ── Métodos de apoyo ──────────────────────────────────
    private Alerta obtenerEntidad(Long id) throws Exception {
        return alertaRepository.findById(id)
                .orElseThrow(() -> new Exception("Alerta no encontrada: " + id));
    }

    private AlertaDTO mapearDTO(Alerta a) {
        return new AlertaDTO(
                a.getId(), a.getTipo(), a.getEstado(),
                a.getProducto() != null ? a.getProducto().getNombre() : null,
                a.getSucursal().getNombre(),
                a.getMensaje(),
                a.getFechaGeneracion(),
                a.getFechaLectura()
        );
    }
}