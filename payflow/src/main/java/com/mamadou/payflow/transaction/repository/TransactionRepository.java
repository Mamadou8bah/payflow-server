package com.mamadou.payflow.transaction.repository;

import com.mamadou.payflow.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {
    Optional<Transaction> findByReference(String reference);
    boolean existsByReference(String reference);
    List<Transaction> findBySourceWalletIdOrDestinationWalletIdOrderByCreatedAtDesc(Long sourceWalletId, Long destinationWalletId);

    // Risk evaluation queries
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE " +
           "(t.sourceWallet.id = :walletId OR t.destinationWallet.id = :walletId) " +
           "AND t.createdAt BETWEEN :startDate AND :endDate")
    Optional<BigDecimal> sumTransactionAmountByWalletAndDateRange(
        @Param("walletId") Long walletId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT COUNT(t) FROM Transaction t WHERE " +
           "(t.sourceWallet.id = :walletId OR t.destinationWallet.id = :walletId) " +
           "AND t.createdAt BETWEEN :startDate AND :endDate")
    long countByWalletAndDateRange(
        @Param("walletId") Long walletId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT COUNT(t) FROM Transaction t WHERE " +
           "(t.sourceWallet.id = :walletId OR t.destinationWallet.id = :walletId)")
    long countByWallet(@Param("walletId") Long walletId);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.status = :status")
    long countByStatus(@Param("status") String status);
}
