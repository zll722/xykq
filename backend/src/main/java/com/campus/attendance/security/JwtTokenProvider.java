package com.campus.attendance.security;

import com.campus.attendance.config.SecurityProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Component
public class JwtTokenProvider {
    private final SecurityProperties properties;

    public JwtTokenProvider(SecurityProperties properties) {
        this.properties = properties;
    }

    public String generateToken(Long userId, String username, String roleCode) {
        Instant now = Instant.now();
        Instant exp = now.plus(properties.getExpireHours(), ChronoUnit.HOURS);
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claims(Map.of("username", username, "role", roleCode))
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(signingKey())
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
    }
}
