package com.inventario.service.impl;

import com.inventario.config.JWTUtils;
import com.inventario.dto.request.LoginDTO;
import com.inventario.dto.response.TokenDTO;
import com.inventario.model.Usuario;
import com.inventario.model.enums.EstadoUsuario;
import com.inventario.repository.UsuarioRepository;
import com.inventario.service.interfaces.AuthServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServicioImpl implements AuthServicio {

    private final UsuarioRepository usuarioRepositorio;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;

    @Override
    public TokenDTO iniciarSesion(LoginDTO loginDTO) throws Exception {

        // Records usan .campo() directamente, no getEmail()
        Usuario usuario = usuarioRepositorio
                .findByEmailAndEstadoNot(loginDTO.email(), EstadoUsuario.ELIMINADO)
                .orElseThrow(() -> new Exception("Credenciales inválidas"));

        if (!passwordEncoder.matches(loginDTO.password(), usuario.getPassword())) {
            throw new Exception("Credenciales inválidas");
        }

        String token = jwtUtils.generarToken(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getRol().name(),
                usuario.getNombre(),
                usuario.getSucursal() != null ? usuario.getSucursal().getId() : null
        );

        // TokenDTO es Record — se construye con el constructor directamente
        return new TokenDTO(
                token,
                "Bearer",
                usuario.getEmail(),
                usuario.getRol().name(),
                usuario.getSucursal() != null ? usuario.getSucursal().getId() : null,
                usuario.getSucursal() != null ? usuario.getSucursal().getNombre() : null
        );
    }
}