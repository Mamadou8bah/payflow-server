package com.mamadou.payflow.transaction.service;

import com.mamadou.payflow.transaction.entity.IdempotencyKey;
import com.mamadou.payflow.transaction.entity.Transaction;
import com.mamadou.payflow.transaction.exception.TransactionException;
import com.mamadou.payflow.transaction.repository.IdempotencyKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final IdempotencyKeyRepository idempotencyKeyRepository;

    public Optional<Transaction> findExistingTransaction(String key, String requestFingerprint) {
        if (key == null || key.isBlank()) {
            return Optional.empty();
        }
        String normalizedKey = key.trim();
        String requestHash = hash(requestFingerprint);
        return idempotencyKeyRepository.findByKey(normalizedKey)
                .map(existingKey -> {
                    if (!existingKey.getRequestHash().equals(requestHash)) {
                        throw new TransactionException("Idempotency key was already used with a different request");
                    }
                    return existingKey.getTransaction();
                });
    }

    public void saveKey(String key, String requestFingerprint, Transaction transaction) {
        if (key == null || key.isBlank()) {
            return;
        }
        idempotencyKeyRepository.save(IdempotencyKey.builder()
                .key(key.trim())
                .requestHash(hash(requestFingerprint))
                .transaction(transaction)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .build());
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new TransactionException("Unable to hash idempotency request");
        }
    }
}
