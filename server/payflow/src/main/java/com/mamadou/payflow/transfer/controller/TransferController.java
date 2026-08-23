package com.mamadou.payflow.transfer.controller;

import com.mamadou.payflow.transaction.dto.ReversalRequest;
import com.mamadou.payflow.transaction.dto.TransactionResponse;
import com.mamadou.payflow.transfer.dto.TransferListItem;
import com.mamadou.payflow.transfer.dto.TransferRequest;
import com.mamadou.payflow.transfer.dto.TransferResponse;
import com.mamadou.payflow.transfer.service.TransferReversalService;
import com.mamadou.payflow.transfer.service.TransferService;
import com.mamadou.payflow.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;
    private final TransferReversalService transferReversalService;

    @GetMapping
    public ResponseEntity<List<TransferListItem>> listTransfers(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(transferService.listForCurrentUser());
    }

    @PostMapping
    public ResponseEntity<TransferResponse> transfer(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody TransferRequest request
    ) {
        return ResponseEntity.ok(transferService.transfer(request, idempotencyKey));
    }

    @PostMapping("/{transactionId}/reverse")
    public ResponseEntity<TransactionResponse> reverse(
            @PathVariable Long transactionId,
            @Valid @RequestBody(required = false) ReversalRequest request
    ) {
        return ResponseEntity.ok(transferReversalService.reverse(transactionId, request));
    }
}
