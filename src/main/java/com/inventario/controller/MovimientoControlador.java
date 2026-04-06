package com.inventario.controller;

import com.inventario.dto.MensajeDTO;
import com.inventario.dto.response.MovimientoDTO;
import com.inventario.service.interfaces.MovimientoServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Movimientos", description = "Trazabilidad de inventario")
public class MovimientoControlador {

    private final MovimientoServicio movimientoServicio;

    @GetMapping("/api/operador/movimientos/sucursal/{sucursalId}")
    @Operation(summary = "Movimientos de una sucursal (paginado)")
    public ResponseEntity<MensajeDTO<Page<MovimientoDTO>>> porSucursal(
            @PathVariable Long sucursalId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(new MensajeDTO<>(false,
                movimientoServicio.listarPorSucursal(sucursalId,
                        PageRequest.of(page, size, Sort.by("fecha").descending()))));
    }

    @GetMapping("/api/operador/movimientos/producto/{productoId}/sucursal/{sucursalId}")
    @Operation(summary = "Movimientos de un producto en una sucursal")
    public ResponseEntity<MensajeDTO<Page<MovimientoDTO>>> porProductoYSucursal(
            @PathVariable Long productoId,
            @PathVariable Long sucursalId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(new MensajeDTO<>(false,
                movimientoServicio.listarPorProductoYSucursal(productoId, sucursalId,
                        PageRequest.of(page, size))));
    }

    @GetMapping("/api/operador/movimientos/producto/{productoId}/rango")
    @Operation(summary = "Historial de producto en rango de fechas")
    public ResponseEntity<MensajeDTO<List<MovimientoDTO>>> porProductoEnRango(
            @PathVariable Long productoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return ResponseEntity.ok(new MensajeDTO<>(false,
                movimientoServicio.listarPorProductoEnRango(productoId, desde, hasta)));
    }

    @GetMapping("/api/gerente/movimientos/sucursal/{sucursalId}/rango")
    @Operation(summary = "Movimientos de sucursal en rango de fechas (reporte)")
    public ResponseEntity<MensajeDTO<Page<MovimientoDTO>>> porSucursalEnRango(
            @PathVariable Long sucursalId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(new MensajeDTO<>(false,
                movimientoServicio.listarPorSucursalEnRango(sucursalId, desde, hasta,
                        PageRequest.of(page, size))));
    }

    @GetMapping("/api/gerente/movimientos/responsable/{usuarioId}")
    @Operation(summary = "Movimientos registrados por un usuario")
    public ResponseEntity<MensajeDTO<List<MovimientoDTO>>> porResponsable(
            @PathVariable Long usuarioId) {
        return ResponseEntity.ok(new MensajeDTO<>(false,
                movimientoServicio.listarPorResponsable(usuarioId)));
    }
}