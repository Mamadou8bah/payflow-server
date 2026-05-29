package com.mamadou.payflow.auth.service;

import com.mamadou.payflow.auth.dto.AuthResponse;
import com.mamadou.payflow.auth.dto.LoginRequest;
import com.mamadou.payflow.auth.dto.LogoutRequest;
import com.mamadou.payflow.auth.dto.RefreshTokenRequest;
import com.mamadou.payflow.auth.dto.RefreshTokenResponse;
import com.mamadou.payflow.auth.dto.RegisterRequest;
import com.mamadou.payflow.auth.mapper.AuthMapper;
import com.mamadou.payflow.common.Exception.AccountNotFoundException;
import com.mamadou.payflow.common.Exception.InvalidPasswordException;
import com.mamadou.payflow.common.Exception.InvalidTokenException;
import com.mamadou.payflow.common.Exception.ProfileAlreadyExistException;
import com.mamadou.payflow.common.metrics.PayFlowMetricsService;
import com.mamadou.payflow.notification.service.TwoFactorDeliveryService;
import com.mamadou.payflow.user.entity.User;
import com.mamadou.payflow.user.enums.UserStatus;
import com.mamadou.payflow.user.repository.UserRepository;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private static final long ACCESS_TOKEN_TTL_SECONDS = 15 * 60;
    private static final int MAX_FAILED_LOGIN_ATTEMPTS = 5;

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final AuthMapper authMapper;
    private final TwoFactorDeliveryService twoFactorDeliveryService;
    private final PayFlowMetricsService metricsService;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        metricsService.recordAuthRegisterAttempt();
        log.info("Registration attempt for phoneNumber={}", request.phoneNumber());
        
        if (userRepository.existsByPhoneNumber(request.phoneNumber())) {
            log.warn("Registration failed: phoneNumber already exists, phoneNumber={}", request.phoneNumber());
            throw new ProfileAlreadyExistException("Phone number already registered");
        }
        if (request.email() != null && !request.email().isBlank() && userRepository.existsByEmail(request.email())) {
            log.warn("Registration failed: email already exists, email={}", request.email());
            throw new ProfileAlreadyExistException("Email already registered");
        }

        User user = authMapper.toUser(request, passwordEncoder.encode(request.password()));
        userRepository.save(user);
        
        log.info("Registration successful, userId={}, phoneNumber={}, role={}", user.getId(), user.getPhoneNumber(), user.getRole());
        metricsService.recordAuthRegisterSuccess();

        String refreshToken = refreshTokenService.generateRefreshToken(user);
        String accessToken = refreshTokenService.generateAccessToken(user);
        return authMapper.authenticated(user, accessToken, refreshToken, ACCESS_TOKEN_TTL_SECONDS);
    }

    @Transactional(noRollbackFor = {InvalidPasswordException.class, InvalidTokenException.class})
    public AuthResponse login(LoginRequest request) {
        metricsService.recordAuthLoginAttempt();
        Timer.Sample sample = metricsService.startAuthLoginTimer();
        
        log.info("Login attempt for username={}", request.username());
        
        User user = userRepository.findByLoginIdentifier(request.username())
                .orElseThrow(() -> {
                    log.warn("Login failed: user not found, username={}", request.username());
                    metricsService.recordAuthLoginFailure();
                    return new AccountNotFoundException("Account not found");
                });

        if (user.getUserStatus() == UserStatus.SUSPENDED || !user.isAccountNonLocked()) {
            log.warn("Login failed: account locked or suspended, userId={}, username={}, status={}", 
                user.getId(), user.getPhoneNumber(), user.getUserStatus());
            metricsService.recordAuthLoginFailure();
            throw new InvalidPasswordException("Account is locked or suspended");
        }
        
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            log.warn("Login failed: invalid password, userId={}, username={}", user.getId(), user.getPhoneNumber());
            recordFailedLogin(user);
            metricsService.recordAuthLoginFailure();
            throw new InvalidPasswordException("Invalid username or password");
        } 

        if (user.isTwoFactorEnabled()) {
            if (request.twoFactorCode() == null || request.twoFactorCode().isBlank()) {
                String challengeId = startTwoFactorChallenge(user);
                userRepository.save(user);
                log.info("Login pending 2FA verification, userId={}, challengeId={}", user.getId(), challengeId);
                return authMapper.twoFactorRequired(user, challengeId);
            }
            verifyTwoFactorCode(user, request.twoFactorChallengeId(), request.twoFactorCode());
            log.info("2FA verification successful, userId={}", user.getId());
        }

        user.setLastLoginAt(LocalDateTime.now());
        user.setFailedLoginAttempts(0);
        userRepository.save(user);

        log.info("Login successful, userId={}, username={}, role={}", user.getId(), user.getPhoneNumber(), user.getRole());
        metricsService.recordAuthLoginSuccess();

        String refreshToken = refreshTokenService.generateRefreshToken(user);
        String accessToken = refreshTokenService.generateAccessToken(user);
        
        sample.stop(Timer.builder("auth.login.duration")
            .description("Login operation duration")
            .publishPercentiles(0.5, 0.95, 0.99)
            .register(new io.micrometer.core.instrument.simple.SimpleMeterRegistry())
        );
        
        return authMapper.authenticated(user, accessToken, refreshToken, ACCESS_TOKEN_TTL_SECONDS);
    }

    @Transactional
    public RefreshTokenResponse refresh(RefreshTokenRequest request) {
        String accessToken = refreshTokenService.refreshAccessToken(request.refreshToken());
        return new RefreshTokenResponse(accessToken, request.refreshToken(), "Bearer", ACCESS_TOKEN_TTL_SECONDS);
    }

    @Transactional
    public String logout(LogoutRequest request) {
        if (request.allDevices()) {
            User user = refreshTokenService.getUserForRefreshToken(request.refreshToken());
            refreshTokenService.revokeAllUserRefreshTokens(user);
            return "Logged out from all devices";
        }
        return refreshTokenService.revokeRefreshToken(request.refreshToken());
    }

    private String startTwoFactorChallenge(User user) {
        String challengeId = UUID.randomUUID().toString();
        String code = String.format("%06d", secureRandom.nextInt(1_000_000));

        user.setTwoFactorChallengeId(challengeId);
        user.setTwoFactorCodeHash(passwordEncoder.encode(code));
        user.setTwoFactorCodeExpiresAt(LocalDateTime.now().plusMinutes(5));

        twoFactorDeliveryService.sendCode(user, code);
        return challengeId;
    }

    private void recordFailedLogin(User user) {
        int failedAttempts = user.getFailedLoginAttempts() == null ? 0 : user.getFailedLoginAttempts();
        int updatedAttempts = failedAttempts + 1;

        user.setFailedLoginAttempts(updatedAttempts);
        if (updatedAttempts >= MAX_FAILED_LOGIN_ATTEMPTS) {
            user.setLockedAt(LocalDateTime.now());
        }
        userRepository.save(user);
    }

    private void verifyTwoFactorCode(User user, String challengeId, String code) {
        if (challengeId == null || !challengeId.equals(user.getTwoFactorChallengeId())) {
            throw new InvalidTokenException("Invalid 2FA challenge");
        }
        if (user.getTwoFactorCodeExpiresAt() == null || user.getTwoFactorCodeExpiresAt().isBefore(LocalDateTime.now())) {
            clearTwoFactorChallenge(user);
            throw new InvalidTokenException("2FA code expired");
        }
        if (!passwordEncoder.matches(code, user.getTwoFactorCodeHash())) {
            throw new InvalidTokenException("Invalid 2FA code");
        }
        clearTwoFactorChallenge(user);
    }

    private void clearTwoFactorChallenge(User user) {
        user.setTwoFactorChallengeId(null);
        user.setTwoFactorCodeHash(null);
        user.setTwoFactorCodeExpiresAt(null);
    }
}
