package com.inventario.controller;

import com.inventario.dto.MensajeDTO;
import com.inventario.dto.response.AlertaDTO;
import com.inventario.service.interfaces.AlertaServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Alertas", description = "Alertas inteligentes de stock y vencimiento")
public class AlertaControlador {

    private final AlertaServicio alertaServicio;

    @GetMapping("/api/operador/alertas/sucursal/{sucursalId}")
    @Operation(summary = "Alertas activas de una sucursal")
    public ResponseEntity<MensajeDTO<List<AlertaDTO>>> activas(
            @PathVariable Long sucursalId) {
        return ResponseEntity.ok(new MensajeDTO<>(false,
                alertaServicio.listarActivasPorSucursal(sucursalId)));
    }

    @PutMapping("/api/operador/alertas/{id}/leer")
    @Operation(summary = "Marcar alerta como leída")
    public ResponseEntity<MensajeDTO<String>> leer(
            @PathVariable Long id) throws Exception {
        alertaServicio.marcarComoLeida(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Alerta marcada como leída"));
    }

    @PutMapping("/api/gerente/alertas/{id}/resolver")
    @Operation(summary = "Marcar alerta como resuelta")
    public ResponseEntity<MensajeDTO<String>> resolver(
            @PathVariable Long id) throws Exception {
        alertaServicio.marcarComoResuelta(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Alerta marcada como resuelta"));
    }

    @PostMapping("/api/gerente/alertas/generar-stock/{sucursalId}")
    @Operation(summary = "Generar alertas de stock bajo para una sucursal")
    public ResponseEntity<MensajeDTO<String>> generarStock(
            @PathVariable Long sucursalId) {
        alertaServicio.generarAlertasStockBajo(sucursalId);
        return ResponseEntity.ok(
                new MensajeDTO<>(false, "Alertas de stock generadas correctamente"));
    }

    @PostMapping("/api/gerente/alertas/generar-vencimiento/{sucursalId}")
    @Operation(summary = "Generar alertas de vencimiento para una sucursal")
    public ResponseEntity<MensajeDTO<String>> generarVencimiento(
            @PathVariable Long sucursalId) {
        alertaServicio.generarAlertasVencimiento(sucursalId);
        return ResponseEntity.ok(
                new MensajeDTO<>(false, "Alertas de vencimiento generadas correctamente"));
    }

    @GetMapping("/api/operador/alertas/contar/sucursal/{sucursalId}")
    @Operation(summary = "Conteo de alertas activas de una sucursal")
    public ResponseEntity<MensajeDTO<Long>> contar(
            @PathVariable Long sucursalId) {
        return ResponseEntity.ok(new MensajeDTO<>(false,
                alertaServicio.contarActivasPorSucursal(sucursalId)));
    }
}