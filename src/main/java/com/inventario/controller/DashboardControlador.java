package com.inventario.controller;

import com.inventario.dto.MensajeDTO;
import com.inventario.dto.response.DashboardDTO;
import com.inventario.dto.response.PrediccionDemandaDTO;
import com.inventario.service.interfaces.DashboardServicio;
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
@Tag(name = "Dashboard", description = "KPIs, analytics y predicción de demanda")
public class DashboardControlador {

    private final DashboardServicio dashboardServicio;

    @GetMapping("/api/operador/dashboard/sucursal/{sucursalId}")
    @Operation(summary = "Dashboard completo de una sucursal")
    public ResponseEntity<MensajeDTO<DashboardDTO>> dashboardSucursal(
            @PathVariable Long sucursalId) {
        return ResponseEntity.ok(new MensajeDTO<>(false,
                dashboardServicio.obtenerDashboardSucursal(sucursalId)));
    }

    @GetMapping("/api/admin/dashboard/general")
    @Operation(summary = "Dashboard general con comparativa de toda la red")
    public ResponseEntity<MensajeDTO<DashboardDTO>> dashboardGeneral() {
        return ResponseEntity.ok(new MensajeDTO<>(false,
                dashboardServicio.obtenerDashboardGeneral()));
    }

    @GetMapping("/api/gerente/dashboard/prediccion/{productoId}/{sucursalId}")
    @Operation(summary = "Predicción de demanda de un producto en una sucursal")
    public ResponseEntity<MensajeDTO<PrediccionDemandaDTO>> predecir(
            @PathVariable Long productoId,
            @PathVariable Long sucursalId) {
        return ResponseEntity.ok(new MensajeDTO<>(false,
                dashboardServicio.predecirDemanda(productoId, sucursalId)));
    }

    @GetMapping("/api/gerente/dashboard/prediccion/sucursal/{sucursalId}")
    @Operation(summary = "Predicción de demanda para todos los productos de la sucursal")
    public ResponseEntity<MensajeDTO<List<PrediccionDemandaDTO>>> predecirSucursal(
            @PathVariable Long sucursalId) {
        return ResponseEntity.ok(new MensajeDTO<>(false,
                dashboardServicio.predecirDemandaSucursal(sucursalId)));
    }
}