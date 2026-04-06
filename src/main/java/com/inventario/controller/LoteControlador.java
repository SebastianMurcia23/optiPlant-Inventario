package com.inventario.controller;

import com.inventario.dto.MensajeDTO;
import com.inventario.model.LoteProducto;
import com.inventario.service.interfaces.LoteProductoServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class LoteControlador {

    private final LoteProductoServicio loteServicio;

    // ── CREAR LOTE ─────────────────────────────────────────
    @PostMapping("/api/operador/lotes")
    public ResponseEntity<MensajeDTO<LoteProducto>> registrar(
            @RequestBody Map<String, Object> body) throws Exception {

        Long productoId       = Long.valueOf(body.get("productoId").toString());
        Long sucursalId       = Long.valueOf(body.get("sucursalId").toString());
        String numeroLote     = body.get("numeroLote").toString();
        int cantidad          = Integer.parseInt(body.get("cantidad").toString());

        LocalDate fechaFabricacion = body.get("fechaFabricacion") != null &&
            !body.get("fechaFabricacion").toString().isBlank()
            ? LocalDate.parse(body.get("fechaFabricacion").toString())
            : null;

        LocalDate fechaVencimiento = LocalDate.parse(
            body.get("fechaVencimiento").toString());

        LoteProducto lote = loteServicio.registrar(
            productoId, sucursalId, numeroLote,
            cantidad, fechaFabricacion, fechaVencimiento
        );

        return ResponseEntity.ok(new MensajeDTO<>(false, lote));
    }

    // ── LISTAR POR PRODUCTO Y SUCURSAL ────────────────────
    @GetMapping("/api/operador/lotes/producto/{productoId}/sucursal/{sucursalId}")
    public ResponseEntity<MensajeDTO<List<LoteProducto>>> porProductoYSucursal(
            @PathVariable Long productoId,
            @PathVariable Long sucursalId) {
        return ResponseEntity.ok(new MensajeDTO<>(false,
            loteServicio.listarPorProductoYSucursal(productoId, sucursalId)));
    }

    // ── LISTAR TODOS POR SUCURSAL ─────────────────────────
    @GetMapping("/api/operador/lotes/sucursal/{sucursalId}")
    public ResponseEntity<MensajeDTO<List<LoteProducto>>> porSucursal(
            @PathVariable Long sucursalId) {
        // Devolver todos los lotes activos de la sucursal
        // Usamos vencidos + vigentes combinados
        List<LoteProducto> vigentes = loteServicio.listarProximosAVencer(sucursalId, 3650);
        return ResponseEntity.ok(new MensajeDTO<>(false, vigentes));
    }

    // ── PRÓXIMOS A VENCER ─────────────────────────────────
    @GetMapping("/api/gerente/lotes/vencer/{sucursalId}")
    public ResponseEntity<MensajeDTO<List<LoteProducto>>> proximosAVencer(
            @PathVariable Long sucursalId,
            @RequestParam(defaultValue = "30") int dias) {
        return ResponseEntity.ok(new MensajeDTO<>(false,
            loteServicio.listarProximosAVencer(sucursalId, dias)));
    }

    // ── YA VENCIDOS ───────────────────────────────────────
    @GetMapping("/api/gerente/lotes/vencidos/{sucursalId}")
    public ResponseEntity<MensajeDTO<List<LoteProducto>>> vencidos(
            @PathVariable Long sucursalId) {
        return ResponseEntity.ok(new MensajeDTO<>(false,
            loteServicio.listarVencidos(sucursalId)));
    }

    // ── DESACTIVAR LOTE ───────────────────────────────────
    @PutMapping("/api/gerente/lotes/{id}/desactivar")
    public ResponseEntity<MensajeDTO<String>> desactivar(
            @PathVariable Long id) throws Exception {
        loteServicio.desactivarLote(id);
        return ResponseEntity.ok(
            new MensajeDTO<>(false, "Lote desactivado correctamente"));
    }

    // ── ACTUALIZAR CANTIDAD ───────────────────────────────
    @PatchMapping("/api/gerente/lotes/{id}/cantidad")
    public ResponseEntity<MensajeDTO<String>> actualizarCantidad(
            @PathVariable Long id,
            @RequestParam int cantidad) throws Exception {
        loteServicio.actualizarCantidad(id, cantidad);
        return ResponseEntity.ok(
            new MensajeDTO<>(false, "Cantidad actualizada correctamente"));
    }
}