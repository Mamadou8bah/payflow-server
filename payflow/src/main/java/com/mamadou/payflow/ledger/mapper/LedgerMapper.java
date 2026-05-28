package com.mamadou.payflow.ledger.mapper;

import com.mamadou.payflow.ledger.dto.LedgerAccountResponse;
import com.mamadou.payflow.ledger.dto.LedgerPostingResponse;
import com.mamadou.payflow.ledger.dto.LedgerTraceResponse;
import com.mamadou.payflow.ledger.entity.LedgerAccount;
import com.mamadou.payflow.ledger.entity.LedgerPosting;
import com.mamadou.payflow.ledger.enums.LedgerPostingSide;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class LedgerMapper {

    public LedgerAccountResponse toAccountResponse(LedgerAccount account, BigDecimal balance) {
        return LedgerAccountResponse.builder()
                .id(account.getId())
                .code(account.getCode())
                .name(account.getName())
                .type(account.getType())
                .currency(account.getCurrency())
                .active(account.isActive())
                .balance(balance)
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }

    public LedgerPostingResponse toPostingResponse(LedgerPosting posting) {
        return LedgerPostingResponse.builder()
                .id(posting.getId())
                .traceId(posting.getTraceId())
                .externalReference(posting.getExternalReference())
                .accountId(posting.getAccount().getId())
                .accountCode(posting.getAccount().getCode())
                .side(posting.getSide())
                .amount(posting.getAmount())
                .currency(posting.getCurrency())
                .description(posting.getDescription())
                .postedAt(posting.getPostedAt())
                .build();
    }

    public LedgerTraceResponse toTraceResponse(List<LedgerPosting> postings) {
        BigDecimal totalDebits = postings.stream()
                .filter(posting -> posting.getSide() == LedgerPostingSide.DEBIT)
                .map(LedgerPosting::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCredits = postings.stream()
                .filter(posting -> posting.getSide() == LedgerPostingSide.CREDIT)
                .map(LedgerPosting::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LedgerPosting firstPosting = postings.get(0);

        return LedgerTraceResponse.builder()
                .traceId(firstPosting.getTraceId())
                .externalReference(firstPosting.getExternalReference())
                .currency(firstPosting.getCurrency())
                .totalDebits(totalDebits)
                .totalCredits(totalCredits)
                .zeroSum(totalDebits.compareTo(totalCredits) == 0)
                .postings(postings.stream().map(this::toPostingResponse).toList())
                .build();
    }
}
