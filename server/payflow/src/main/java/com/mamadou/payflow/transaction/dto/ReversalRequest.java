package com.mamadou.payflow.transaction.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReversalRequest {
    @Size(max = 255, message = "Reason cannot exceed 255 characters")
    private String reason;

    @Size(max = 120, message = "Idempotency key cannot exceed 120 characters")
    private String idempotencyKey;
}
