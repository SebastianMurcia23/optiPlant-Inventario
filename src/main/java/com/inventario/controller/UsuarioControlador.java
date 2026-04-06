package com.inventario.controller;

import com.inventario.dto.MensajeDTO;
import com.inventario.dto.request.CrearUsuarioDTO;
import com.inventario.dto.request.EditarUsuarioDTO;
import com.inventario.dto.response.UsuarioDTO;
import com.inventario.service.interfaces.UsuarioServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/usuarios")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Usuarios", description = "Gestión de usuarios — solo ADMIN")
public class UsuarioControlador {

    private final UsuarioServicio usuarioServicio;

    @PostMapping
    @Operation(summary = "Crear nuevo usuario")
    public ResponseEntity<MensajeDTO<UsuarioDTO>> crear(
            @Valid @RequestBody CrearUsuarioDTO dto) throws Exception {
        return ResponseEntity.ok(
                new MensajeDTO<>(false, usuarioServicio.crear(dto)));
    }

    @PutMapping
    @Operation(summary = "Editar usuario")
    public ResponseEntity<MensajeDTO<UsuarioDTO>> editar(
            @Valid @RequestBody EditarUsuarioDTO dto) throws Exception {
        return ResponseEntity.ok(
                new MensajeDTO<>(false, usuarioServicio.editar(dto)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario (lógico)")
    public ResponseEntity<MensajeDTO<String>> eliminar(
            @PathVariable Long id) throws Exception {
        usuarioServicio.eliminar(id);
        return ResponseEntity.ok(
                new MensajeDTO<>(false, "Usuario eliminado correctamente"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID")
    public ResponseEntity<MensajeDTO<UsuarioDTO>> obtenerPorId(
            @PathVariable Long id) throws Exception {
        return ResponseEntity.ok(
                new MensajeDTO<>(false, usuarioServicio.obtenerPorId(id)));
    }

    @GetMapping
    @Operation(summary = "Listar todos los usuarios")
    public ResponseEntity<MensajeDTO<List<UsuarioDTO>>> listarTodos() {
        return ResponseEntity.ok(
                new MensajeDTO<>(false, usuarioServicio.listarTodos()));
    }

    @GetMapping("/sucursal/{sucursalId}")
    @Operation(summary = "Listar usuarios por sucursal")
    public ResponseEntity<MensajeDTO<List<UsuarioDTO>>> listarPorSucursal(
            @PathVariable Long sucursalId) {
        return ResponseEntity.ok(
                new MensajeDTO<>(false, usuarioServicio.listarPorSucursal(sucursalId)));
    }
}