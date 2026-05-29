package com.mamadou.payflow.idempotency.repository;

import com.mamadou.payflow.idempotency.entity.IdempotencyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IdempotencyRepository extends JpaRepository<IdempotencyRecord, Long> {
    Optional<IdempotencyRecord> findByIdempotencyKey(String key);
}
