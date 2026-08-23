package com.mamadou.payflow.withdrawal.repository;

import com.mamadou.payflow.withdrawal.entity.Withdrawal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WithdrawalRepository extends JpaRepository<Withdrawal, Long> {
    Optional<Withdrawal> findByReferenceIgnoreCase(String reference);
    Optional<Withdrawal> findByIdempotencyKey(String key);
    List<Withdrawal> findByUserIdOrderByCreatedAtDesc(Long userId);
}
