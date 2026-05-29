package com.mamadou.payflow.ledger.repository;

import com.mamadou.payflow.ledger.entity.LedgerAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LedgerAccountRepository extends JpaRepository<LedgerAccount, Long> {
    Optional<LedgerAccount> findByCode(String code);
    boolean existsByCode(String code);
}
