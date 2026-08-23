package com.mamadou.payflow.fraud.repository;

import com.mamadou.payflow.fraud.entity.FraudEvaluationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FraudEvaluationLogRepository extends JpaRepository<FraudEvaluationLog, Long> {

    Page<FraudEvaluationLog> findByWalletIdOrderByCreatedAtDesc(Long walletId, Pageable pageable);

    @Query("""
        SELECT COUNT(l) FROM FraudEvaluationLog l
        """)
    long countAll();

    @Query("""
        SELECT COUNT(l) FROM FraudEvaluationLog l WHERE l.confirmedFraud IS NOT NULL
        """)
    long countLabeled();

    @Query("""
        SELECT COUNT(l) FROM FraudEvaluationLog l WHERE l.confirmedFraud = true
        """)
    long countConfirmedFraud();
}
