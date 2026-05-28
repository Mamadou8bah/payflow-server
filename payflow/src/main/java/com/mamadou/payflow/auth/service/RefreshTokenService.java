package com.mamadou.payflow.auth.service;

import com.mamadou.payflow.auth.entity.RefreshToken;
import com.mamadou.payflow.auth.repository.RefreshTokenRepository;
import com.mamadou.payflow.common.Exception.InvalidTokenException;
import com.mamadou.payflow.common.config.JWTConfig;
import com.mamadou.payflow.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JWTConfig jwtConfig;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public String generateRefreshToken(User user) {
        String tokenId = UUID.randomUUID().toString();

        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh_token");
        claims.put("role", user.getRole().name());
        claims.put("username", user.getUsername());
        claims.put("userId", user.getId());

        String rawRefreshToken = jwtConfig.generateRefreshToken(
                user.getUsername(),
                tokenId,
                claims
        );

        RefreshToken refreshToken = RefreshToken.builder()
                .tokenId(tokenId)
                .tokenHash(bCryptPasswordEncoder.encode(rawRefreshToken))
                .createdAt(LocalDateTime.now())
                .expiredAt(jwtConfig.getHelper().refreshExpiryDateTime())
                .revoked(false)
                .user(user)
                .build();

        refreshTokenRepository.save(refreshToken);

        return rawRefreshToken;
    }

    @Transactional(readOnly = true)
    public String refreshAccessToken(String rawRefreshToken) {
        if (!jwtConfig.validateToken(rawRefreshToken)) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        String tokenType = jwtConfig.extractAllClaims(rawRefreshToken).get("type", String.class);
        if (!"refresh_token".equals(tokenType)) {
            throw new InvalidTokenException("Invalid refresh token type");
        }

        String tokenId = jwtConfig.extractTokenId(rawRefreshToken);

        RefreshToken storedToken = refreshTokenRepository.findByTokenId(tokenId)
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        if (storedToken.isExpired() || storedToken.isRevoked() ) {
            throw new InvalidTokenException("Refresh token is revoked or expired");
        }

        if (!bCryptPasswordEncoder.matches(rawRefreshToken, storedToken.getTokenHash())) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        User user = storedToken.getUser();

        return generateAccessToken(user);
    }

    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "access_token");
        claims.put("role", user.getRole().name());
        claims.put("username", user.getUsername());
        claims.put("userId", user.getId());

        return jwtConfig.generateAccessToken(user.getUsername(), claims);
    }

    @Transactional
    public String revokeRefreshToken(String rawRefreshToken) {
        if (!jwtConfig.validateToken(rawRefreshToken)) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        String tokenId = jwtConfig.extractTokenId(rawRefreshToken);

        RefreshToken storedToken = refreshTokenRepository.findByTokenId(tokenId)
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        if (storedToken.isRevoked()) {
            throw new InvalidTokenException("Refresh token already revoked");
        }

        if (!bCryptPasswordEncoder.matches(rawRefreshToken, storedToken.getTokenHash())) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);

        return "Refresh token revoked successfully";
    }

    @Transactional
    public void revokeAllUserRefreshTokens(User user) {
        List<RefreshToken> activeTokens = refreshTokenRepository.findAllByUserAndRevokedFalse(user);
        for (RefreshToken token : activeTokens) {
            token.setRevoked(true);
        }
        refreshTokenRepository.saveAll(activeTokens);
    }

    @Transactional(readOnly = true)
    public User getUserForRefreshToken(String rawRefreshToken) {
        if (!jwtConfig.validateToken(rawRefreshToken)) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        String tokenId = jwtConfig.extractTokenId(rawRefreshToken);
        RefreshToken storedToken = refreshTokenRepository.findByTokenId(tokenId)
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        if (!bCryptPasswordEncoder.matches(rawRefreshToken, storedToken.getTokenHash())) {
            throw new InvalidTokenException("Invalid refresh token");
        }
        return storedToken.getUser();
    }
}
