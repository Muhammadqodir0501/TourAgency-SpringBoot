package org.example.touragency.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

public class JwtUtil {

    private static final String SECRET = "very-very-secret-key-for-jwt-32bytes";
    private static final long EXPIRATION = 1000 * 60 * 60 * 24;

    private static final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    public static String generateToken(UUID userId, String role) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static boolean validate(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public static UUID getUserId(String token) {
        Claims claims = getClaims(token);
        return UUID.fromString(claims.getSubject());
    }

    public static String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    private static Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
