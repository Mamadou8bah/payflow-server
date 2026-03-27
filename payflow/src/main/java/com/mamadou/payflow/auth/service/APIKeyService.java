package com.mamadou.payflow.auth.service;

import com.mamadou.payflow.auth.dto.APIKeyCreateRequest;
import com.mamadou.payflow.auth.dto.ApiKeyResponse;
import com.mamadou.payflow.auth.entity.APIKey;
import com.mamadou.payflow.auth.repository.APIKeyRepository;
import com.mamadou.payflow.common.Exception.APIKeyNotFoundException;
import com.mamadou.payflow.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;


@Service
@RequiredArgsConstructor
public class APIKeyService {

    private final SecureRandom secureRandom=new SecureRandom();
    private final APIKeyRepository apiKeyRepository;

    private final SCryptPasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    @Transactional
    public ApiKeyResponse createApiKey(APIKeyCreateRequest request) {
        String userName= SecurityContextHolder.getContext().getAuthentication().getName();

        var user= userRepository.findByEmail(userName).orElseThrow(
                () -> new UsernameNotFoundException("Username not found")
        );
        String apiToken = generatePublicId()+"."+generateSecret();
        var apiKey= APIKey.builder()
                .name(request.name())
                .expiresAt(LocalDateTime.now().plusDays(request.expiresInDays()))
                .publicId(generatePublicId())
                .secretHash(passwordEncoder.encode(apiToken))
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();
        apiKeyRepository.save(apiKey);
        return new ApiKeyResponse(
                request.name(),
                request.expiresInDays(),
                apiToken
        );
    }

    @Transactional
    public ApiKeyResponse updateApiKey(APIKeyCreateRequest request) {
        APIKey apiKey=apiKeyRepository.findByName(request.name()).orElseThrow(
                ()->new APIKeyNotFoundException("API key not found")
        );
        apiKey.setName(request.name());
        apiKeyRepository.save(apiKey);
        return new ApiKeyResponse(
                request.name(),
                apiKey.getExpiresAt().getDayOfYear(),
                apiKey.getPublicId()
        );
    }

    @Transactional
    public String revokeApiKey(String apiKey) {
        String[]apiKeys=apiKey.split(".");
        String publicId=apiKeys[0];
        APIKey apiKey1=apiKeyRepository.findByPublicId(publicId).orElseThrow(
                ()->new APIKeyNotFoundException("API key not found")
        );
        apiKey1.setRevoked(true);
        apiKeyRepository.save(apiKey1);
        return apiKey1.getName()+" is revoked";
    }

    @Transactional
    public String deleteApiKey(String apiKey) {
        String[]apiKeys=apiKey.split(".");
        String publicId=apiKeys[0];
        APIKey apiKey1=apiKeyRepository.findByPublicId(publicId).orElseThrow(
                ()->new APIKeyNotFoundException("API key not found")
        );
        apiKeyRepository.delete(apiKey1);
        return apiKey1.getName()+" is deleted";
    }

    private String generatePublicId() {
        byte[] bytes = new byte[6];
        secureRandom.nextBytes(bytes);
        return "pf_live_"+bytesToHex(bytes);
    }
    private String generateSecret() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    private String bytesToHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }
}
