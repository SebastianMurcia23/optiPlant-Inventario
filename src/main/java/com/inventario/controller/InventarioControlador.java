package com.inventario.controller;

import com.inventario.dto.MensajeDTO;
import com.inventario.dto.request.AjustarStockDTO;
import com.inventario.dto.response.InventarioItemDTO;
import com.inventario.service.interfaces.InventarioServicio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InventarioControlador {

    private final InventarioServicio inventarioServicio;

    @GetMapping("/api/operador/inventario/sucursal/{sucursalId}")
    public ResponseEntity<MensajeDTO<List<InventarioItemDTO>>> listarPorSucursal(
            @PathVariable Long sucursalId) {
        return ResponseEntity.ok(new MensajeDTO<>(false,
                inventarioServicio.listarInventarioSucursal(sucursalId)));
    }

    @GetMapping("/api/operador/inventario/producto/{productoId}/sucursal/{sucursalId}")
    public ResponseEntity<MensajeDTO<InventarioItemDTO>> obtenerStock(
            @PathVariable Long productoId,
            @PathVariable Long sucursalId) throws Exception {
        return ResponseEntity.ok(new MensajeDTO<>(false,
                inventarioServicio.obtenerStock(productoId, sucursalId)));
    }

    @GetMapping("/api/operador/inventario/producto/{productoId}/red")
    public ResponseEntity<MensajeDTO<List<InventarioItemDTO>>> stockEnRed(
            @PathVariable Long productoId) {
        return ResponseEntity.ok(new MensajeDTO<>(false,
                inventarioServicio.listarStockPorProducto(productoId)));
    }

    @GetMapping("/api/operador/inventario/sucursal/{sucursalId}/stock-bajo")
    public ResponseEntity<MensajeDTO<List<InventarioItemDTO>>> stockBajo(
            @PathVariable Long sucursalId) {
        return ResponseEntity.ok(new MensajeDTO<>(false,
                inventarioServicio.listarStockBajoEnSucursal(sucursalId)));
    }

    @PostMapping("/api/operador/inventario/ajuste")
    public ResponseEntity<MensajeDTO<String>> ajustarStock(
            @Valid @RequestBody AjustarStockDTO dto) throws Exception {
        // responsableId = 1L como fallback — el servicio original lo resolvía del JWT
        inventarioServicio.ajustarStock(dto, 1L);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Stock ajustado correctamente"));
    }

    @PutMapping("/api/gerente/inventario/configurar-minimos")
    public ResponseEntity<MensajeDTO<InventarioItemDTO>> configurarMinimos(
            @RequestParam Long productoId,
            @RequestParam Long sucursalId,
            @RequestParam Integer stockMinimo,
            @RequestParam(required = false) Integer stockMaximo) throws Exception {
        return ResponseEntity.ok(new MensajeDTO<>(false,
                inventarioServicio.configurarStockMinimo(
                        productoId, sucursalId, stockMinimo, stockMaximo)));
    }

}