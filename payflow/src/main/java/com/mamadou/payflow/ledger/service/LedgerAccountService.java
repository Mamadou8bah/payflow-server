package com.mamadou.payflow.ledger.service;

import com.mamadou.payflow.ledger.dto.LedgerAccountResponse;
import com.mamadou.payflow.ledger.entity.LedgerAccount;
import com.mamadou.payflow.ledger.enums.LedgerAccountType;
import com.mamadou.payflow.ledger.exception.LedgerException;
import com.mamadou.payflow.ledger.mapper.LedgerMapper;
import com.mamadou.payflow.ledger.repository.LedgerAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LedgerAccountService {

    private final LedgerAccountRepository ledgerAccountRepository;
    private final LedgerBalanceComputationService balanceComputationService;
    private final LedgerMapper ledgerMapper;

    @Transactional
    public LedgerAccountResponse createAccount(String code, String name, LedgerAccountType type, String currency) {
        LedgerAccount savedAccount = createAccountEntity(code, name, type, currency);
        return ledgerMapper.toAccountResponse(savedAccount, balanceComputationService.computeBalance(savedAccount));
    }

    @Transactional
    public LedgerAccount createAccountEntity(String code, String name, LedgerAccountType type, String currency) {
        String normalizedCode = normalizeCode(code);
        String normalizedCurrency = normalizeCurrency(currency);

        if (ledgerAccountRepository.existsByCode(normalizedCode)) {
            throw new LedgerException("Ledger account already exists: " + normalizedCode);
        }

        LedgerAccount account = LedgerAccount.builder()
                .code(normalizedCode)
                .name(name)
                .type(type)
                .currency(normalizedCurrency)
                .active(true)
                .build();

        return ledgerAccountRepository.save(account);
    }

    @Transactional
    public LedgerAccount getOrCreateAccountEntity(String code, String name, LedgerAccountType type, String currency) {
        String normalizedCode = normalizeCode(code);
        return ledgerAccountRepository.findByCode(normalizedCode)
                .orElseGet(() -> createAccountEntity(normalizedCode, name, type, currency));
    }

    @Transactional(readOnly = true)
    public LedgerAccountResponse getAccount(String code) {
        LedgerAccount account = getAccountEntity(code);
        return ledgerMapper.toAccountResponse(account, balanceComputationService.computeBalance(account));
    }

    @Transactional(readOnly = true)
    public List<LedgerAccountResponse> getAccounts() {
        return ledgerAccountRepository.findAll()
                .stream()
                .map(account -> ledgerMapper.toAccountResponse(account, balanceComputationService.computeBalance(account)))
                .toList();
    }

    @Transactional
    public LedgerAccountResponse activateAccount(String code) {
        LedgerAccount account = getAccountEntity(code);
        account.setActive(true);
        LedgerAccount savedAccount = ledgerAccountRepository.save(account);
        return ledgerMapper.toAccountResponse(savedAccount, balanceComputationService.computeBalance(savedAccount));
    }

    @Transactional
    public LedgerAccountResponse deactivateAccount(String code) {
        LedgerAccount account = getAccountEntity(code);
        account.setActive(false);
        LedgerAccount savedAccount = ledgerAccountRepository.save(account);
        return ledgerMapper.toAccountResponse(savedAccount, balanceComputationService.computeBalance(savedAccount));
    }

    public LedgerAccount getAccountEntity(String code) {
        return ledgerAccountRepository.findByCode(normalizeCode(code))
                .orElseThrow(() -> new LedgerException("Ledger account not found: " + code));
    }

    private String normalizeCode(String code) {
        if (code == null || code.isBlank()) {
            throw new LedgerException("Ledger account code is required");
        }
        return code.trim().toUpperCase();
    }

    private String normalizeCurrency(String currency) {
        if (currency == null || currency.isBlank()) {
            throw new LedgerException("Ledger account currency is required");
        }
        return currency.trim().toUpperCase();
    }
}
