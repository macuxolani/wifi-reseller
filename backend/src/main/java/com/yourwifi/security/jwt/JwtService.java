package com.yourwifi.security.jwt;

import com.yourwifi.auth.dto.AuthResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Set;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    public AuthResponse buildAuthResponse(String username) {
        return buildAuthResponse(username, Set.of("CUSTOMER"));
    }

    public AuthResponse buildAuthResponse(String username, Set<String> roles) {
        String accessToken = createToken(username, roles, expirationMs);
        String refreshToken = createToken(username, roles, refreshExpirationMs);
        return new AuthResponse(accessToken, refreshToken, "Bearer", username, roles);
    }

    public String validateRefreshToken(String refreshToken) {
        return parseClaims(refreshToken).getSubject();
    }

    public String validateAccessToken(String accessToken) {
        return parseClaims(accessToken).getSubject();
    }

    public Set<String> roles(String accessToken) {
        Object value = parseClaims(accessToken).get("roles");
        if (value instanceof Iterable<?> iterable) {
            Set<String> roles = new java.util.HashSet<>();
            iterable.forEach(role -> roles.add(String.valueOf(role)));
            return roles;
        }
        return Set.of();
    }

    private String createToken(String username, Set<String> roles, long expirationMs) {
        Instant now = Instant.now();
        return Jwts.builder()
            .subject(username)
            .claim("roles", roles)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusMillis(expirationMs)))
            .signWith(getSigningKey())
            .compact();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
