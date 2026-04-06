package com.inventario.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class FiltroToken extends OncePerRequestFilter {

    private final JWTUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Rutas públicas — pasar sin validar
        if (path.startsWith("/api/auth/") ||
                path.startsWith("/api/util/") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.equals("/swagger-ui.html")) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            enviarError(response, "Token no proporcionado");
            return;
        }

        String token = header.replace("Bearer ", "");

        if (!jwtUtils.validarToken(token)) {
            enviarError(response, "Token inválido o expirado");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void enviarError(HttpServletResponse response, String mensaje)
            throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(
                new ObjectMapper().writeValueAsString(
                        Map.of("error", true, "respuesta", mensaje)
                )
        );
    }
}