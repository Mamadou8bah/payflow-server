package com.mamadou.payflow.wallet.repository;

import com.mamadou.payflow.user.entity.User;
import com.mamadou.payflow.wallet.entity.Wallet;
import com.mamadou.payflow.wallet.enums.KycLevel;
import com.mamadou.payflow.wallet.enums.WalletStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    List<Wallet> findByUser(User user);
    List<Wallet> findByUserId(Long userId);
    Optional<Wallet> findByIdAndUserId(Long id, Long userId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Wallet> findWithLockById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Wallet> findWithLockByIdAndUserId(Long id, Long userId);

    boolean existsByUserIdAndCurrencyIgnoreCase(Long userId, String currency);
    int countByUser(User user);
    int countByStatus(WalletStatus status);
}
