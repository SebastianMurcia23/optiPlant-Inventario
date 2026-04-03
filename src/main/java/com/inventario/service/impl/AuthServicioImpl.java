package com.inventario.service.impl;

import com.inventario.config.JWTUtils;
import com.inventario.dto.request.LoginDTO;
import com.inventario.dto.response.TokenDTO;
import com.inventario.model.Usuario;
import com.inventario.repository.UsuarioRepository;
import com.inventario.service.interfaces.AuthServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServicioImpl implements AuthServicio {

    private final UsuarioRepository usuarioRepository;
    private final JWTUtils jwtUtils;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public TokenDTO iniciarSesion(LoginDTO loginDTO) throws Exception {
        // Buscar usuario
        Usuario usuario = usuarioRepository.findByEmail(loginDTO.email())
                .orElseThrow(() -> new Exception("Credenciales inválidas"));

        // Validar password
        if (!passwordEncoder.matches(loginDTO.password(), usuario.getPassword())) {
            throw new Exception("Credenciales inválidas");
        }

        // Validar estado
        if (!usuario.getEstado().name().equals("ACTIVO")) {
            throw new Exception("La cuenta no está activa");
        }

        // Actualizar último acceso
        usuario.setUltimoAcceso(LocalDateTime.now());
        usuarioRepository.save(usuario);

        // Construir claims del token
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", usuario.getRol().name());
        claims.put("nombre", usuario.getNombre());
        claims.put("id", usuario.getId());
        claims.put("sucursalId",
                usuario.getSucursal() != null ? usuario.getSucursal().getId() : null);

        String token = jwtUtils.generarToken(usuario.getEmail(), claims);

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