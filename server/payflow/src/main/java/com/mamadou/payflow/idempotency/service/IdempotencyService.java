package com.mamadou.payflow.idempotency.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mamadou.payflow.idempotency.entity.IdempotencyRecord;
import com.mamadou.payflow.idempotency.repository.IdempotencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final IdempotencyRepository idempotencyRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public Optional<IdempotencyRecord> findByKey(String key) {
        if (key == null || key.isBlank()) return Optional.empty();
        return idempotencyRepository.findByIdempotencyKey(key);
    }

    @Transactional
    public IdempotencyRecord createProcessing(String key, String requestHash) {
        IdempotencyRecord r = IdempotencyRecord.builder()
                .idempotencyKey(key)
                .requestHash(requestHash)
                .status("PROCESSING")
                .build();
        try {
            return idempotencyRepository.save(r);
        } catch (DataIntegrityViolationException ex) {
            return idempotencyRepository.findByIdempotencyKey(key).orElseThrow(() -> ex);
        }
    }

    @Transactional
    public IdempotencyRecord complete(String key, Object response) {
        String payload;
        try {
            payload = objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            payload = null;
        }
        IdempotencyRecord r = idempotencyRepository.findByIdempotencyKey(key).orElseGet(() -> IdempotencyRecord.builder().idempotencyKey(key).build());
        r.setResponsePayload(payload);
        r.setStatus("COMPLETED");
        return idempotencyRepository.save(r);
    }
}
