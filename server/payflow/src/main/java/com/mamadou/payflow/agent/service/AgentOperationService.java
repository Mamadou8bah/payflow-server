package com.mamadou.payflow.agent.service;

import com.mamadou.payflow.agent.dto.AgentOperationResponse;
import com.mamadou.payflow.common.security.SecurityRoleUtils;
import com.mamadou.payflow.deposit.entity.Deposit;
import com.mamadou.payflow.deposit.repository.DepositRepository;
import com.mamadou.payflow.transaction.entity.Transaction;
import com.mamadou.payflow.transaction.enums.TransactionStatus;
import com.mamadou.payflow.transaction.repository.TransactionRepository;
import com.mamadou.payflow.user.entity.User;
import com.mamadou.payflow.user.repository.UserRepository;
import com.mamadou.payflow.wallet.entity.Wallet;
import com.mamadou.payflow.wallet.exception.WalletOperationException;
import com.mamadou.payflow.wallet.service.LedgerWalletMovementService;
import com.mamadou.payflow.withdrawal.entity.Withdrawal;
import com.mamadou.payflow.withdrawal.repository.WithdrawalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AgentOperationService {

    private final DepositRepository depositRepository;
    private final WithdrawalRepository withdrawalRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final LedgerWalletMovementService ledgerWalletMovementService;

    @Transactional(readOnly = true)
    public AgentOperationResponse lookup(String reference, User agent) {
        requireAgent(agent);
        Deposit deposit = depositRepository.findByReferenceIgnoreCase(reference).orElse(null);
        if (deposit != null) {
            return toResponse("DEPOSIT", deposit.getReference(), deposit.getWallet(), deposit.getAmount(), deposit.getStatus().name(), deposit.getUser());
        }
        Withdrawal withdrawal = withdrawalRepository.findByReferenceIgnoreCase(reference)
                .orElseThrow(() -> new IllegalArgumentException("Operation not found for reference " + reference));
        return toResponse("WITHDRAWAL", withdrawal.getReference(), withdrawal.getWallet(), withdrawal.getAmount(), withdrawal.getStatus().name(), withdrawal.getUser());
    }

    @Transactional
    public AgentOperationResponse complete(String reference, User agent) {
        requireAgent(agent);
        Deposit deposit = depositRepository.findByReferenceIgnoreCase(reference).orElse(null);
        if (deposit != null) {
            return completeDeposit(deposit, agent);
        }
        Withdrawal withdrawal = withdrawalRepository.findByReferenceIgnoreCase(reference)
                .orElseThrow(() -> new IllegalArgumentException("Operation not found for reference " + reference));
        return completeWithdrawal(withdrawal, agent);
    }

    private AgentOperationResponse completeDeposit(Deposit deposit, User agent) {
        if (deposit.getStatus() == Deposit.DepositStatus.COMPLETED) {
            return toResponse("DEPOSIT", deposit.getReference(), deposit.getWallet(), deposit.getAmount(), deposit.getStatus().name(), deposit.getUser());
        }
        if (deposit.getStatus() != Deposit.DepositStatus.AWAITING_AGENT && deposit.getStatus() != Deposit.DepositStatus.PENDING) {
            throw new WalletOperationException("Deposit cannot be completed in status " + deposit.getStatus());
        }

        ledgerWalletMovementService.creditWallet(
                deposit.getWallet(),
                deposit.getUser(),
                deposit.getAmount(),
                deposit.getReference(),
                "Agent-assisted deposit"
        );

        deposit.setAgent(agent);
        deposit.setDepositType(Deposit.DepositType.AGENT);
        deposit.setStatus(Deposit.DepositStatus.COMPLETED);
        deposit.setCompletedAt(LocalDateTime.now());
        depositRepository.save(deposit);

        transactionRepository.findByReference(deposit.getReference()).ifPresent(txn -> {
            txn.setStatus(TransactionStatus.COMPLETED);
            transactionRepository.save(txn);
        });

        return toResponse("DEPOSIT", deposit.getReference(), deposit.getWallet(), deposit.getAmount(), deposit.getStatus().name(), deposit.getUser());
    }

    private AgentOperationResponse completeWithdrawal(Withdrawal withdrawal, User agent) {
        if (withdrawal.getStatus() == Withdrawal.WithdrawalStatus.COMPLETED) {
            return toResponse("WITHDRAWAL", withdrawal.getReference(), withdrawal.getWallet(), withdrawal.getAmount(), withdrawal.getStatus().name(), withdrawal.getUser());
        }
        if (withdrawal.getStatus() != Withdrawal.WithdrawalStatus.AWAITING_AGENT
                && withdrawal.getStatus() != Withdrawal.WithdrawalStatus.PENDING
                && withdrawal.getStatus() != Withdrawal.WithdrawalStatus.APPROVED) {
            throw new WalletOperationException("Withdrawal cannot be completed in status " + withdrawal.getStatus());
        }

        ledgerWalletMovementService.debitWallet(
                withdrawal.getWallet(),
                withdrawal.getUser(),
                withdrawal.getAmount(),
                withdrawal.getReference(),
                "Agent-assisted withdrawal"
        );

        withdrawal.setAgent(agent);
        withdrawal.setWithdrawalType(Withdrawal.WithdrawalType.AGENT);
        withdrawal.setStatus(Withdrawal.WithdrawalStatus.COMPLETED);
        withdrawal.setApprovedAt(LocalDateTime.now());
        withdrawal.setCompletedAt(LocalDateTime.now());
        withdrawalRepository.save(withdrawal);

        transactionRepository.findByReference(withdrawal.getReference()).ifPresent(txn -> {
            txn.setStatus(TransactionStatus.COMPLETED);
            transactionRepository.save(txn);
        });

        return toResponse("WITHDRAWAL", withdrawal.getReference(), withdrawal.getWallet(), withdrawal.getAmount(), withdrawal.getStatus().name(), withdrawal.getUser());
    }

    private void requireAgent(User user) {
        if (!SecurityRoleUtils.isAgent(user) && !SecurityRoleUtils.isAdmin(user)) {
            throw new WalletOperationException("Only agents can perform this operation");
        }
    }

    private AgentOperationResponse toResponse(String operation, String reference, Wallet wallet, java.math.BigDecimal amount, String status, User user) {
        String merchantName = user.getFirstName() + " " + user.getLastName();
        return AgentOperationResponse.builder()
                .operation(operation)
                .reference(reference)
                .walletId(wallet.getId())
                .walletName(wallet.getName())
                .amount(amount)
                .currency(wallet.getCurrency())
                .status(status)
                .userId(user.getId())
                .merchantName(merchantName.trim())
                .build();
    }
}
