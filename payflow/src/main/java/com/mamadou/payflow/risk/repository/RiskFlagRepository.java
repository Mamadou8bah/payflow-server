package com.mamadou.payflow.risk.repository;

import com.mamadou.payflow.risk.entity.RiskFlag;
import com.mamadou.payflow.risk.enums.RiskLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RiskFlagRepository extends JpaRepository<RiskFlag, Long> {

    List<RiskFlag> findByWalletIdAndResolvedFalse(Long walletId);

    Page<RiskFlag> findByWalletId(Long walletId, Pageable pageable);

    Page<RiskFlag> findByWalletIdAndRiskLevel(Long walletId, RiskLevel riskLevel, Pageable pageable);

    List<RiskFlag> findByWalletIdAndFlaggedAtAfter(Long walletId, LocalDateTime dateTime);

    long countByWalletIdAndResolvedFalse(Long walletId);

    long countByWalletIdAndRiskLevelAndResolvedFalse(Long walletId, RiskLevel riskLevel);

    Optional<RiskFlag> findByTransactionId(Long transactionId);

    List<RiskFlag> findByResolvedFalseAndFlaggedAtBefore(LocalDateTime dateTime);
}
