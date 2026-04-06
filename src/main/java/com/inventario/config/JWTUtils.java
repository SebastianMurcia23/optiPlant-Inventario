package com.inventario.config;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;

@Component
public class JWTUtils {

    private static final String SECRET = "inventario-sistema-clave-super-secreta-2024-jwt-token";
    private static final long EXPIRATION = 1000L * 60 * 60 * 8; // 8 horas

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String generarToken(Long usuarioId, String email,
                               String rol, String nombre, Long sucursalId) {
        return Jwts.builder()
                .setSubject(email)
                .addClaims(Map.of(
                        "usuarioId",  usuarioId,
                        "rol",        rol,
                        "nombre",     nombre,
                        "sucursalId", sucursalId != null ? sucursalId : ""
                ))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getKey(), SignatureAlgorithm.HS384)
                .compact();
    }

    public String obtenerEmail(String token) {
        return getClaims(token).getSubject();
    }

    public Long obtenerUsuarioId(String token) {
        Object id = getClaims(token).get("usuarioId");
        return id instanceof Integer ? ((Integer) id).longValue() : (Long) id;
    }

    public String obtenerRol(String token) {
        return (String) getClaims(token).get("rol");
    }

    public Long obtenerSucursalId(String token) {
        Object id = getClaims(token).get("sucursalId");
        if (id == null || id.toString().isEmpty()) return null;
        return id instanceof Integer ? ((Integer) id).longValue() : (Long) id;
    }

    public boolean validarToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}