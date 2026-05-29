package com.mamadou.payflow.subscription.controller;

import com.mamadou.payflow.subscription.dto.CreateSubscriptionRequest;
import com.mamadou.payflow.subscription.dto.SubscriptionResponse;
import com.mamadou.payflow.subscription.service.SubscriptionService;
import com.mamadou.payflow.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<SubscriptionResponse> create(@Valid @RequestBody CreateSubscriptionRequest request,
                                                       @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(subscriptionService.create(currentUser.getId(), request));
    }

    @GetMapping
    public ResponseEntity<List<SubscriptionResponse>> list(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(subscriptionService.listForUser(currentUser.getId()));
    }
}
