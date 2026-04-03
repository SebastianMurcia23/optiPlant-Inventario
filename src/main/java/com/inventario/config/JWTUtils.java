package com.inventario.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Component
public class JWTUtils {

    private static final String CLAVE_SECRETA =
            "inventario-multi-sucursal-secret-key-2024-segura";

    public String generarToken(String email, Map<String, Object> claims) {
        Instant now = Instant.now();
        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(8L, ChronoUnit.HOURS)))
                .signWith(getKey())
                .compact();
    }

    public Jws<Claims> parseJwt(String jwtString)
            throws ExpiredJwtException, UnsupportedJwtException,
            MalformedJwtException, IllegalArgumentException {
        JwtParser parser = Jwts.parser()
                .verifyWith(getKey())
                .build();
        return parser.parseSignedClaims(jwtString);
    }

    public String obtenerEmail(String token) {
        return parseJwt(token).getPayload().getSubject();
    }

    public String obtenerRol(String token) {
        return parseJwt(token).getPayload().get("rol", String.class);
    }

    public Long obtenerUsuarioId(String token) {
        Object id = parseJwt(token).getPayload().get("id");
        return id != null ? ((Number) id).longValue() : null;
    }

    public Long obtenerSucursalId(String token) {
        Object sucursalId = parseJwt(token).getPayload().get("sucursalId");
        return sucursalId != null ? ((Number) sucursalId).longValue() : null;
    }

    public boolean esTokenValido(String token) {
        try {
            parseJwt(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(CLAVE_SECRETA.getBytes());
    }
}