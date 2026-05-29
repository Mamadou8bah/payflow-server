package com.mamadou.payflow.ledger.dto;

import com.mamadou.payflow.ledger.enums.LedgerPostingSide;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class LedgerPostingResponse {
    private Long id;
    private String traceId;
    private String externalReference;
    private Long accountId;
    private String accountCode;
    private LedgerPostingSide side;
    private BigDecimal amount;
    private String currency;
    private String description;
    private LocalDateTime postedAt;
}
