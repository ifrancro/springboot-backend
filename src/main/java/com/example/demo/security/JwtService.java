package com.example.demo.security;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {

    private final Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing() // Evita excepción si .env no existe
            .load();

    private Key key;

    @PostConstruct
    public void initKey() {
        String secret = null;

        try {
            secret = dotenv.get("JWT_SECRET");
        } catch (Exception ignored) {
        }

        if (secret == null || secret.isBlank()) {
            secret = System.getenv("JWT_SECRET");
        }

        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("❌ No se encontró JWT_SECRET ni en .env ni en variables del sistema");
        }

        // 4️⃣ Validar tamaño mínimo (256 bits = 32 bytes codificados Base64)
        byte[] keyBytes = Decoders.BASE64.decode(secret.trim());
        if (keyBytes.length < 32) {
            throw new IllegalStateException("❌ La clave JWT_SECRET es demasiado corta. Debe ser ≥ 256 bits (usa openssl rand -base64 64)");
        }

        this.key = Keys.hmacShaKeyFor(keyBytes);
        System.out.println("🔑 JWT_SECRET cargada correctamente (" + keyBytes.length * 8 + " bits)");
    }

    private Key getSignInKey() {
        if (key == null) {
            initKey(); // fallback si no fue inicializado
        }
        return key;
    }

    // ✅ Extrae el username (subject)
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // ✅ Extrae un claim genérico
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // ✅ Parse completo del token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ✅ Genera token con claims extra y roles
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)) // 24 horas
                .signWith(getSignInKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    // ✅ Valida token
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
