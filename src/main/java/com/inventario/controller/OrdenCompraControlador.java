package com.inventario.controller;

import com.inventario.config.JWTUtils;
import com.inventario.dto.MensajeDTO;
import com.inventario.dto.request.CrearOrdenCompraDTO;
import com.inventario.dto.request.RecibirOrdenCompraDTO;
import com.inventario.dto.response.OrdenCompraDTO;
import com.inventario.service.interfaces.OrdenCompraServicio;
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
@Tag(name = "Órdenes de Compra", description = "Ciclo completo de adquisición")
public class OrdenCompraControlador {

    private final OrdenCompraServicio ordenCompraServicio;
    private final JWTUtils jwtUtils;

    @PostMapping("/api/operador/compras")
    @Operation(summary = "Crear orden de compra")
    public ResponseEntity<MensajeDTO<OrdenCompraDTO>> crear(
            @Valid @RequestBody CrearOrdenCompraDTO dto,
            HttpServletRequest request) throws Exception {
        return ResponseEntity.ok(new MensajeDTO<>(false,
                ordenCompraServicio.crear(dto, obtenerUsuarioId(request))));
    }

    @PutMapping("/api/gerente/compras/{id}/enviar")
    @Operation(summary = "Enviar orden al proveedor")
    public ResponseEntity<MensajeDTO<OrdenCompraDTO>> enviar(
            @PathVariable Long id) throws Exception {
        return ResponseEntity.ok(new MensajeDTO<>(false,
                ordenCompraServicio.enviarAProveedor(id)));
    }

    @PostMapping("/api/operador/compras/recibir")
    @Operation(summary = "Registrar recepción de mercancía")
    public ResponseEntity<MensajeDTO<OrdenCompraDTO>> recibir(
            @Valid @RequestBody RecibirOrdenCompraDTO dto,
            HttpServletRequest request) throws Exception {
        return ResponseEntity.ok(new MensajeDTO<>(false,
                ordenCompraServicio.recibirMercancia(dto, obtenerUsuarioId(request))));
    }

    @DeleteMapping("/api/gerente/compras/{id}/cancelar")
    @Operation(summary = "Cancelar orden de compra")
    public ResponseEntity<MensajeDTO<OrdenCompraDTO>> cancelar(
            @PathVariable Long id) throws Exception {
        return ResponseEntity.ok(new MensajeDTO<>(false,
                ordenCompraServicio.cancelar(id)));
    }

    @GetMapping("/api/operador/compras/{id}")
    @Operation(summary = "Detalle de orden de compra")
    public ResponseEntity<MensajeDTO<OrdenCompraDTO>> obtener(
            @PathVariable Long id) throws Exception {
        return ResponseEntity.ok(new MensajeDTO<>(false,
                ordenCompraServicio.obtenerPorId(id)));
    }

    @GetMapping("/api/operador/compras/sucursal/{sucursalId}")
    @Operation(summary = "Órdenes de compra de una sucursal (paginado)")
    public ResponseEntity<MensajeDTO<Page<OrdenCompraDTO>>> porSucursal(
            @PathVariable Long sucursalId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(new MensajeDTO<>(false,
                ordenCompraServicio.listarPorSucursal(sucursalId, PageRequest.of(page, size))));
    }

    @GetMapping("/api/admin/compras/proveedor/{proveedorId}")
    @Operation(summary = "Historial de órdenes por proveedor")
    public ResponseEntity<MensajeDTO<Page<OrdenCompraDTO>>> porProveedor(
            @PathVariable Long proveedorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(new MensajeDTO<>(false,
                ordenCompraServicio.listarPorProveedor(proveedorId, PageRequest.of(page, size))));
    }

    private Long obtenerUsuarioId(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return jwtUtils.obtenerUsuarioId(header.replace("Bearer ", ""));
        }
        return 1L;
    }
}