package com.mamadou.payflow.ledger.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class LedgerTraceResponse {
    private String traceId;
    private String externalReference;
    private String currency;
    private BigDecimal totalDebits;
    private BigDecimal totalCredits;
    private boolean zeroSum;
    private List<LedgerPostingResponse> postings;
}
