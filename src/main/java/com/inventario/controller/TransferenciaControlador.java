package com.inventario.controller;

import com.inventario.config.JWTUtils;
import com.inventario.dto.MensajeDTO;
import com.inventario.dto.request.CrearTransferenciaDTO;
import com.inventario.dto.request.DespacharTransferenciaDTO;
import com.inventario.dto.request.RecibirTransferenciaDTO;
import com.inventario.dto.response.TransferenciaDTO;
import com.inventario.service.interfaces.TransferenciaServicio;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Transferencias", description = "Traslados de mercancía entre sucursales")
public class TransferenciaControlador {

    private final TransferenciaServicio transferenciaServicio;
    private final JWTUtils jwtUtils;

    @PostMapping("/api/operador/transferencias")
    @Operation(summary = "Solicitar transferencia entre sucursales")
    public ResponseEntity<MensajeDTO<TransferenciaDTO>> solicitar(
            @Valid @RequestBody CrearTransferenciaDTO dto,
            HttpServletRequest request) throws Exception {
        return ResponseEntity.ok(new MensajeDTO<>(false,
                transferenciaServicio.solicitar(dto, obtenerUsuarioId(request))));
    }

    @PutMapping("/api/gerente/transferencias/{id}/preparar")
    @Operation(summary = "Marcar transferencia en preparación")
    public ResponseEntity<MensajeDTO<TransferenciaDTO>> preparar(
            @PathVariable Long id) throws Exception {
        return ResponseEntity.ok(new MensajeDTO<>(false,
                transferenciaServicio.prepararEnvio(id)));
    }

    @PostMapping("/api/gerente/transferencias/despachar")
    @Operation(summary = "Despachar transferencia (descuenta stock origen)")
    public ResponseEntity<MensajeDTO<TransferenciaDTO>> despachar(
            @Valid @RequestBody DespacharTransferenciaDTO dto,
            HttpServletRequest request) throws Exception {
        return ResponseEntity.ok(new MensajeDTO<>(false,
                transferenciaServicio.despachar(dto, obtenerUsuarioId(request))));
    }

    @PostMapping("/api/operador/transferencias/recibir")
    @Operation(summary = "Recibir transferencia (suma stock destino)")
    public ResponseEntity<MensajeDTO<TransferenciaDTO>> recibir(
            @Valid @RequestBody RecibirTransferenciaDTO dto,
            HttpServletRequest request) throws Exception {
        return ResponseEntity.ok(new MensajeDTO<>(false,
                transferenciaServicio.recibirMercancia(dto, obtenerUsuarioId(request))));
    }

    @PutMapping("/api/gerente/transferencias/{id}/cancelar")
    @Operation(summary = "Cancelar transferencia")
    public ResponseEntity<MensajeDTO<TransferenciaDTO>> cancelar(
            @PathVariable Long id) throws Exception {
        return ResponseEntity.ok(new MensajeDTO<>(false,
                transferenciaServicio.cancelar(id)));
    }

    @GetMapping("/api/operador/transferencias/{id}")
    @Operation(summary = "Detalle de transferencia")
    public ResponseEntity<MensajeDTO<TransferenciaDTO>> obtener(
            @PathVariable Long id) throws Exception {
        return ResponseEntity.ok(new MensajeDTO<>(false,
                transferenciaServicio.obtenerPorId(id)));
    }

    @GetMapping("/api/operador/transferencias/activas/sucursal/{sucursalId}")
    @Operation(summary = "Transferencias activas de una sucursal")
    public ResponseEntity<MensajeDTO<List<TransferenciaDTO>>> activas(
            @PathVariable Long sucursalId) {
        return ResponseEntity.ok(new MensajeDTO<>(false,
                transferenciaServicio.listarActivasPorSucursal(sucursalId)));
    }

    @GetMapping("/api/operador/transferencias/origen/{sucursalId}")
    @Operation(summary = "Transferencias salientes (paginado)")
    public ResponseEntity<MensajeDTO<Page<TransferenciaDTO>>> porOrigen(
            @PathVariable Long sucursalId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(new MensajeDTO<>(false,
                transferenciaServicio.listarPorSucursalOrigen(
                        sucursalId, PageRequest.of(page, size))));
    }

    @GetMapping("/api/operador/transferencias/destino/{sucursalId}")
    @Operation(summary = "Transferencias entrantes (paginado)")
    public ResponseEntity<MensajeDTO<Page<TransferenciaDTO>>> porDestino(
            @PathVariable Long sucursalId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(new MensajeDTO<>(false,
                transferenciaServicio.listarPorSucursalDestino(
                        sucursalId, PageRequest.of(page, size))));
    }

    private Long obtenerUsuarioId(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return jwtUtils.obtenerUsuarioId(header.replace("Bearer ", ""));
        }
        return 1L;
    }
}