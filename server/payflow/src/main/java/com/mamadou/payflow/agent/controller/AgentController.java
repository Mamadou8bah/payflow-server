package com.mamadou.payflow.agent.controller;

import com.mamadou.payflow.agent.dto.AgentOperationResponse;
import com.mamadou.payflow.agent.service.AgentOperationService;
import com.mamadou.payflow.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/agent/operations")
@RequiredArgsConstructor
@PreAuthorize("hasRole('AGENT') or hasRole('ADMIN')")
public class AgentController {

    private final AgentOperationService agentOperationService;

    @GetMapping("/{reference}")
    public ResponseEntity<AgentOperationResponse> lookup(
            @PathVariable String reference,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(agentOperationService.lookup(reference, currentUser));
    }

    @PostMapping("/{reference}/complete")
    public ResponseEntity<AgentOperationResponse> complete(
            @PathVariable String reference,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(agentOperationService.complete(reference, currentUser));
    }
}
