package com.mamadou.payflow.common.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Configuration
@Getter
public class JWTConfig {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration-minutes:15}")
    private long accessTokenExpirationMinutes;

    @Value("${jwt.refresh-token-expiration-days:7}")
    private long refreshTokenExpirationDays;

    private static final String LOCAL_DEV_SECRET =
            "ZGV2ZWxvcG1lbnQtc2VjcmV0LWtleS1mb3ItcGF5Zmxvdy1hcHAtMzItYnl0ZXM=";

    private SecretKey signingKey;

    @Value("${spring.profiles.active:}")
    private String activeProfiles;

    @PostConstruct
    public void init() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("jwt.secret / JWT_SECRET must be configured");
        }
        if (activeProfiles != null && activeProfiles.contains("prod") && LOCAL_DEV_SECRET.equals(secret)) {
            throw new IllegalStateException("JWT_SECRET must not use the local development default in production");
        }
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        if (keyBytes.length < 32) {
            throw new IllegalStateException("JWT_SECRET must decode to at least 32 bytes");
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTokenExpirationMinutes * 60)))
                .signWith(signingKey)
                .compact();
    }

    public String generateRefreshToken(String subject, String tokenId, Map<String, Object> claims) {
        Instant now = Instant.now();
        return Jwts.builder()
                .id(tokenId)
                .subject(subject)
                .claims(claims)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(refreshTokenExpirationDays * 24 * 60 * 60)))
                .signWith(signingKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractTokenId(String token) {
        return extractClaim(token, Claims::getId);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(token));
    }

    public LocalDateTimeHelper getHelper() {
        return new LocalDateTimeHelper();
    }

    public class LocalDateTimeHelper {
        public java.time.LocalDateTime refreshExpiryDateTime() {
            return java.time.LocalDateTime.now().plusDays(refreshTokenExpirationDays);
        }
    }
}