package com.inventario.controller;

import com.inventario.config.JWTUtils;
import com.inventario.dto.MensajeDTO;
import com.inventario.dto.request.AjustarStockDTO;
import com.inventario.dto.response.InventarioItemDTO;
import com.inventario.service.interfaces.InventarioServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Inventario", description = "Gestión de stock por sucursal")
public class InventarioControlador {

    private final InventarioServicio inventarioServicio;
    private final JWTUtils jwtUtils;

    @GetMapping("/api/operador/inventario/sucursal/{sucursalId}")
    @Operation(summary = "Inventario completo de una sucursal")
    public ResponseEntity<MensajeDTO<List<InventarioItemDTO>>> listarPorSucursal(
            @PathVariable Long sucursalId) {
        return ResponseEntity.ok(
                new MensajeDTO<>(false,
                        inventarioServicio.listarInventarioSucursal(sucursalId)));
    }

    @GetMapping("/api/operador/inventario/producto/{productoId}/sucursal/{sucursalId}")
    @Operation(summary = "Stock de un producto en una sucursal específica")
    public ResponseEntity<MensajeDTO<InventarioItemDTO>> obtenerStock(
            @PathVariable Long productoId,
            @PathVariable Long sucursalId) throws Exception {
        return ResponseEntity.ok(
                new MensajeDTO<>(false,
                        inventarioServicio.obtenerStock(productoId, sucursalId)));
    }

    @GetMapping("/api/operador/inventario/producto/{productoId}/red")
    @Operation(summary = "Stock de un producto en toda la red de sucursales")
    public ResponseEntity<MensajeDTO<List<InventarioItemDTO>>> stockEnRed(
            @PathVariable Long productoId) {
        return ResponseEntity.ok(
                new MensajeDTO<>(false,
                        inventarioServicio.listarStockPorProducto(productoId)));
    }

    @GetMapping("/api/operador/inventario/sucursal/{sucursalId}/stock-bajo")
    @Operation(summary = "Productos con stock bajo o agotado en una sucursal")
    public ResponseEntity<MensajeDTO<List<InventarioItemDTO>>> stockBajo(
            @PathVariable Long sucursalId) {
        return ResponseEntity.ok(
                new MensajeDTO<>(false,
                        inventarioServicio.listarStockBajoEnSucursal(sucursalId)));
    }

    @PostMapping("/api/operador/inventario/ajuste")
    @Operation(summary = "Ajustar stock manualmente (ingreso o retiro)")
    public ResponseEntity<MensajeDTO<String>> ajustarStock(
            @Valid @RequestBody AjustarStockDTO dto,
            HttpServletRequest request) throws Exception {
        Long responsableId = obtenerUsuarioId(request);
        inventarioServicio.ajustarStock(dto, responsableId);
        return ResponseEntity.ok(
                new MensajeDTO<>(false, "Stock ajustado correctamente"));
    }

    @PutMapping("/api/gerente/inventario/configurar-minimos")
    @Operation(summary = "Configurar stock mínimo y máximo por sucursal")
    public ResponseEntity<MensajeDTO<InventarioItemDTO>> configurarMinimos(
            @RequestParam Long productoId,
            @RequestParam Long sucursalId,
            @RequestParam Integer stockMinimo,
            @RequestParam(required = false) Integer stockMaximo) throws Exception {
        return ResponseEntity.ok(
                new MensajeDTO<>(false,
                        inventarioServicio.configurarStockMinimo(
                                productoId, sucursalId, stockMinimo, stockMaximo)));
    }

    // ── Método de apoyo ──────────────────────────────────
    private Long obtenerUsuarioId(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return jwtUtils.obtenerUsuarioId(header.replace("Bearer ", ""));
        }
        return 1L; // fallback para pruebas
    }
}