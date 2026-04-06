package com.inventario.controller;

import com.inventario.dto.MensajeDTO;
import com.inventario.dto.request.CrearSucursalDTO;
import com.inventario.dto.response.SucursalDTO;
import com.inventario.service.interfaces.SucursalServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/sucursales")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Sucursales", description = "Gestión de sucursales — solo ADMIN")
public class SucursalControlador {

    private final SucursalServicio sucursalServicio;

    @PostMapping
    @Operation(summary = "Crear nueva sucursal")
    public ResponseEntity<MensajeDTO<SucursalDTO>> crear(
            @Valid @RequestBody CrearSucursalDTO dto) throws Exception {
        return ResponseEntity.ok(
                new MensajeDTO<>(false, sucursalServicio.crear(dto)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar sucursal existente")
    public ResponseEntity<MensajeDTO<SucursalDTO>> editar(
            @PathVariable Long id,
            @Valid @RequestBody CrearSucursalDTO dto) throws Exception {
        return ResponseEntity.ok(
                new MensajeDTO<>(false, sucursalServicio.editar(id, dto)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar sucursal")
    public ResponseEntity<MensajeDTO<String>> desactivar(
            @PathVariable Long id) throws Exception {
        sucursalServicio.desactivar(id);
        return ResponseEntity.ok(
                new MensajeDTO<>(false, "Sucursal desactivada correctamente"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener sucursal por ID")
    public ResponseEntity<MensajeDTO<SucursalDTO>> obtenerPorId(
            @PathVariable Long id) throws Exception {
        return ResponseEntity.ok(
                new MensajeDTO<>(false, sucursalServicio.obtenerPorId(id)));
    }

    @GetMapping
    @Operation(summary = "Listar todas las sucursales")
    public ResponseEntity<MensajeDTO<List<SucursalDTO>>> listarTodas() {
        return ResponseEntity.ok(
                new MensajeDTO<>(false, sucursalServicio.listarTodas()));
    }

    @GetMapping("/activas")
    @Operation(summary = "Listar sucursales activas")
    public ResponseEntity<MensajeDTO<List<SucursalDTO>>> listarActivas() {
        return ResponseEntity.ok(
                new MensajeDTO<>(false, sucursalServicio.listarActivas()));
    }
}