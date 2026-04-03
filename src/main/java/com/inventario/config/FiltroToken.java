package com.inventario.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventario.dto.MensajeDTO;
import com.inventario.model.enums.RolUsuario;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class FiltroToken extends OncePerRequestFilter {

    private final JWTUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Cabeceras CORS
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS, PATCH");
        response.addHeader("Access-Control-Allow-Headers",
                "Origin, Accept, Content-Type, Authorization");

        if (request.getMethod().equals("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String uri = request.getRequestURI();
        String token = extraerToken(request);
        boolean error = true;

        try {
            // Rutas públicas — sin token
            if (esRutaPublica(uri)) {
                error = false;

                // Rutas de administrador general
            } else if (uri.startsWith("/api/admin")) {
                error = validarRol(token, RolUsuario.ADMINISTRADOR_GENERAL);

                // Rutas de gerente de sucursal
            } else if (uri.startsWith("/api/gerente")) {
                error = validarRolMultiple(token,
                        RolUsuario.ADMINISTRADOR_GENERAL,
                        RolUsuario.GERENTE_SUCURSAL);

                // Rutas de operadores (cualquier usuario autenticado)
            } else if (uri.startsWith("/api/operador")) {
                error = validarRolMultiple(token,
                        RolUsuario.ADMINISTRADOR_GENERAL,
                        RolUsuario.GERENTE_SUCURSAL,
                        RolUsuario.OPERADOR_INVENTARIO);

                // Cualquier otra ruta /api → requiere token válido
            } else if (uri.startsWith("/api")) {
                error = (token == null || !jwtUtils.esTokenValido(token));

            } else {
                error = false;
            }

            if (error) {
                responderError("No tiene permisos para acceder a este recurso",
                        HttpServletResponse.SC_FORBIDDEN, response);
                return;
            }

        } catch (MalformedJwtException | SignatureException e) {
            responderError("El token es inválido",
                    HttpServletResponse.SC_UNAUTHORIZED, response);
            return;
        } catch (ExpiredJwtException e) {
            responderError("El token ha expirado",
                    HttpServletResponse.SC_UNAUTHORIZED, response);
            return;
        } catch (Exception e) {
            responderError(e.getMessage(),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    // ── Métodos de apoyo ──────────────────────────────────

    private boolean esRutaPublica(String uri) {
        return uri.startsWith("/api/auth") ||
                uri.startsWith("/swagger-ui") ||
                uri.startsWith("/v3/api-docs") ||
                uri.startsWith("/actuator");
    }

    private String extraerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.replace("Bearer ", "");
        }
        return null;
    }

    private boolean validarRol(String token, RolUsuario rolRequerido) {
        if (token == null) return true;
        try {
            String rol = jwtUtils.obtenerRol(token);
            return !RolUsuario.valueOf(rol).equals(rolRequerido);
        } catch (Exception e) {
            return true;
        }
    }

    private boolean validarRolMultiple(String token, RolUsuario... rolesPermitidos) {
        if (token == null) return true;
        try {
            RolUsuario rolToken = RolUsuario.valueOf(jwtUtils.obtenerRol(token));
            for (RolUsuario rol : rolesPermitidos) {
                if (rolToken.equals(rol)) return false;
            }
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    private void responderError(String mensaje, int codigo,
                                HttpServletResponse response) throws IOException {
        MensajeDTO<String> dto = new MensajeDTO<>(true, mensaje);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(codigo);
        response.getWriter().write(new ObjectMapper().writeValueAsString(dto));
        response.getWriter().flush();
    }
}