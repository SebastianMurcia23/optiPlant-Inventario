package com.inventario.controller;

import com.inventario.dto.MensajeDTO;
import com.inventario.dto.request.LoginDTO;
import com.inventario.dto.response.TokenDTO;
import com.inventario.service.interfaces.AuthServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Login y registro")
public class AuthControlador {

    private final AuthServicio authServicio;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión y obtener token JWT")
    public ResponseEntity<MensajeDTO<TokenDTO>> login(
            @Valid @RequestBody LoginDTO loginDTO) throws Exception {
        return ResponseEntity.ok(
                new MensajeDTO<>(false, authServicio.iniciarSesion(loginDTO)));
    }
}