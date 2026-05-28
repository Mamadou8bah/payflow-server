package com.mamadou.payflow.ledger.dto;

import com.mamadou.payflow.ledger.enums.LedgerPostingSide;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class LedgerPostingRequest {
    @Size(max = 80, message = "Trace ID cannot exceed 80 characters")
    private String traceId;

    @Size(max = 120, message = "External reference cannot exceed 120 characters")
    private String externalReference;

    @Pattern(regexp = "^[A-Za-z]{3}$", message = "Currency must be a 3-letter ISO code")
    private String currency;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @Valid
    @NotEmpty(message = "At least two posting lines are required")
    @Size(min = 2, message = "At least two posting lines are required")
    private List<Entry> postings;

    @Data
    public static class Entry {
        @NotBlank(message = "Account code is required")
        @Size(max = 80, message = "Account code cannot exceed 80 characters")
        private String accountCode;

        @NotNull(message = "Posting side is required")
        private LedgerPostingSide side;

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.0001", message = "Amount must be greater than zero")
        private BigDecimal amount;

        @Size(max = 255, message = "Description cannot exceed 255 characters")
        private String description;
    }
}
