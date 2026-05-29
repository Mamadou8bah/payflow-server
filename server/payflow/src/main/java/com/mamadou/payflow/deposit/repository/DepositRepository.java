package com.mamadou.payflow.deposit.repository;

import com.mamadou.payflow.deposit.entity.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepositRepository extends JpaRepository<Deposit, Long> {
    Optional<Deposit> findByReferenceIgnoreCase(String reference);
    Optional<Deposit> findByIdempotencyKey(String key);
}
