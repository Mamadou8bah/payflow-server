package com.mamadou.payflow.ledger.service;

import com.mamadou.payflow.ledger.entity.LedgerAccount;
import com.mamadou.payflow.ledger.enums.LedgerAccountType;
import com.mamadou.payflow.ledger.enums.LedgerPostingSide;
import com.mamadou.payflow.ledger.repository.LedgerPostingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class LedgerBalanceComputationService {

    private final LedgerPostingRepository ledgerPostingRepository;

    @Transactional(readOnly = true)
    public BigDecimal computeBalance(LedgerAccount account) {
        BigDecimal debits = ledgerPostingRepository.sumByAccountIdAndSide(account.getId(), LedgerPostingSide.DEBIT);
        BigDecimal credits = ledgerPostingRepository.sumByAccountIdAndSide(account.getId(), LedgerPostingSide.CREDIT);

        if (hasDebitNormalBalance(account)) {
            return debits.subtract(credits);
        }
        return credits.subtract(debits);
    }

    private boolean hasDebitNormalBalance(LedgerAccount account) {
        return account.getType() == LedgerAccountType.ASSET
                || account.getType() == LedgerAccountType.EXPENSE;
    }
}
