package com.inventario.controller;

import com.inventario.config.JWTUtils;
import com.inventario.dto.MensajeDTO;
import com.inventario.dto.request.CrearVentaDTO;
import com.inventario.dto.response.VentaDTO;
import com.inventario.service.interfaces.VentaServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Ventas", description = "Ciclo completo de ventas")
public class VentaControlador {

    private final VentaServicio ventaServicio;
    private final JWTUtils jwtUtils;

    @PostMapping("/api/operador/ventas")
    @Operation(summary = "Crear venta en estado PENDIENTE")
    public ResponseEntity<MensajeDTO<VentaDTO>> crear(
            @Valid @RequestBody CrearVentaDTO dto,
            HttpServletRequest request) throws Exception {
        return ResponseEntity.ok(new MensajeDTO<>(false,
                ventaServicio.crear(dto, obtenerUsuarioId(request))));
    }

    @PutMapping("/api/operador/ventas/{id}/confirmar")
    @Operation(summary = "Confirmar venta y descontar stock")
    public ResponseEntity<MensajeDTO<VentaDTO>> confirmar(
            @PathVariable Long id) throws Exception {
        return ResponseEntity.ok(new MensajeDTO<>(false,
                ventaServicio.confirmar(id)));
    }

    @PutMapping("/api/gerente/ventas/{id}/anular")
    @Operation(summary = "Anular venta (devuelve stock si estaba confirmada)")
    public ResponseEntity<MensajeDTO<VentaDTO>> anular(
            @PathVariable Long id) throws Exception {
        return ResponseEntity.ok(new MensajeDTO<>(false,
                ventaServicio.anular(id)));
    }

    @GetMapping("/api/operador/ventas/{id}")
    @Operation(summary = "Detalle de venta")
    public ResponseEntity<MensajeDTO<VentaDTO>> obtener(
            @PathVariable Long id) throws Exception {
        return ResponseEntity.ok(new MensajeDTO<>(false,
                ventaServicio.obtenerPorId(id)));
    }

    @GetMapping("/api/operador/ventas/sucursal/{sucursalId}")
    @Operation(summary = "Ventas de una sucursal (paginado)")
    public ResponseEntity<MensajeDTO<Page<VentaDTO>>> porSucursal(
            @PathVariable Long sucursalId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(new MensajeDTO<>(false,
                ventaServicio.listarPorSucursal(sucursalId, PageRequest.of(page, size))));
    }

    private Long obtenerUsuarioId(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return jwtUtils.obtenerUsuarioId(header.replace("Bearer ", ""));
        }
        return 1L;
    }
}