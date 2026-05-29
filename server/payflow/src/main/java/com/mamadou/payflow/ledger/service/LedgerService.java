package com.mamadou.payflow.ledger.service;

import com.mamadou.payflow.ledger.dto.LedgerPostingRequest;
import com.mamadou.payflow.ledger.dto.LedgerTraceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LedgerService {

    private final LedgerPostingService ledgerPostingService;

    @Transactional
    public LedgerTraceResponse recordFinancialTransaction(LedgerPostingRequest request) {
        return ledgerPostingService.recordPostings(request);
    }

    @Transactional(readOnly = true)
    public LedgerTraceResponse trace(String traceId) {
        return ledgerPostingService.getTrace(traceId);
    }
}
