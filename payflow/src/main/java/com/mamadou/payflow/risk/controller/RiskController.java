package com.mamadou.payflow.risk.controller;

import com.mamadou.payflow.common.response.ApiResponse;
import com.mamadou.payflow.risk.dto.RiskEvaluationResult;
import com.mamadou.payflow.risk.dto.RiskFlagResponse;
import com.mamadou.payflow.risk.enums.RiskLevel;
import com.mamadou.payflow.risk.service.RiskEngineService;
import com.mamadou.payflow.risk.service.RiskFlagService;
import com.mamadou.payflow.risk.service.RiskRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Risk assessment and monitoring API controller.
 * Provides endpoints for evaluating transaction risk, managing risk flags, and accessing risk analytics.
 */
@RestController
@RequestMapping("/api/v1/risk")
@RequiredArgsConstructor
@Slf4j
public class RiskController {

    private final RiskEngineService riskEngineService;
    private final RiskFlagService riskFlagService;
    private final RiskRuleService riskRuleService;

    /**
     * Evaluate risk for a transaction
     * POST /api/v1/risk/evaluate
     */
    @PostMapping("/evaluate")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RiskEvaluationResult>> evaluateTransactionRisk(
            @RequestParam Long walletId,
            @RequestParam BigDecimal amount,
            @RequestParam Long transactionId) {

        log.info("Evaluating risk for transaction: walletId={}, amount={}, transactionId={}",
            walletId, amount, transactionId);

        try {
            RiskEvaluationResult result = riskEngineService.evaluateTransactionRisk(walletId, amount, transactionId);
            return ResponseEntity.ok(ApiResponse.success("Risk evaluation completed", result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error evaluating risk", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error evaluating transaction risk"));
        }
    }

    /**
     * Get all unresolved risk flags for a wallet
     * GET /api/v1/risk/wallets/{walletId}/flags/unresolved
     */
    @GetMapping("/wallets/{walletId}/flags/unresolved")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<RiskFlagResponse>>> getUnresolvedFlags(
            @PathVariable Long walletId) {

        try {
            List<RiskFlagResponse> flags = riskFlagService.getUnresolvedFlagsForWallet(walletId);
            return ResponseEntity.ok(ApiResponse.success(
                String.format("Found %d unresolved risk flags", flags.size()), flags));
        } catch (Exception e) {
            log.error("Error fetching unresolved flags", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error fetching risk flags"));
        }
    }

    /**
     * Get paginated risk flags for a wallet
     * GET /api/v1/risk/wallets/{walletId}/flags
     */
    @GetMapping("/wallets/{walletId}/flags")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<RiskFlagResponse>>> getFlagsForWallet(
            @PathVariable Long walletId,
            Pageable pageable) {

        try {
            Page<RiskFlagResponse> flags = riskFlagService.getFlagsForWallet(walletId, pageable);
            return ResponseEntity.ok(ApiResponse.success("Risk flags retrieved", flags));
        } catch (Exception e) {
            log.error("Error fetching risk flags", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error fetching risk flags"));
        }
    }

    /**
     * Get risk flags by risk level
     * GET /api/v1/risk/wallets/{walletId}/flags/level/{level}
     */
    @GetMapping("/wallets/{walletId}/flags/level/{level}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<RiskFlagResponse>>> getFlagsByLevel(
            @PathVariable Long walletId,
            @PathVariable RiskLevel level,
            Pageable pageable) {

        try {
            Page<RiskFlagResponse> flags = riskFlagService.getFlagsByRiskLevel(walletId, level, pageable);
            return ResponseEntity.ok(ApiResponse.success(
                String.format("Risk flags with level %s retrieved", level), flags));
        } catch (Exception e) {
            log.error("Error fetching flags by level", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error fetching risk flags"));
        }
    }

    /**
     * Get risk flag by transaction ID
     * GET /api/v1/risk/transactions/{transactionId}/flag
     */
    @GetMapping("/transactions/{transactionId}/flag")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RiskFlagResponse>> getFlagByTransactionId(
            @PathVariable Long transactionId) {

        try {
            RiskFlagResponse flag = riskFlagService.getFlagByTransactionId(transactionId);
            if (flag != null) {
                return ResponseEntity.ok(ApiResponse.success("Risk flag retrieved", flag));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("No risk flag found for transaction"));
            }
        } catch (Exception e) {
            log.error("Error fetching flag by transaction", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error fetching risk flag"));
        }
    }

    /**
     * Get recent risk flags for a wallet
     * GET /api/v1/risk/wallets/{walletId}/flags/recent
     */
    @GetMapping("/wallets/{walletId}/flags/recent")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<RiskFlagResponse>>> getRecentFlags(
            @PathVariable Long walletId,
            @RequestParam(defaultValue = "24") int hoursBack) {

        try {
            List<RiskFlagResponse> flags = riskFlagService.getRecentFlags(walletId, hoursBack);
            return ResponseEntity.ok(ApiResponse.success(
                String.format("Found %d risk flags in last %d hours", flags.size(), hoursBack), flags));
        } catch (Exception e) {
            log.error("Error fetching recent flags", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error fetching recent flags"));
        }
    }

    /**
     * Resolve a risk flag
     * PUT /api/v1/risk/flags/{flagId}/resolve
     */
    @PutMapping("/flags/{flagId}/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RiskFlagResponse>> resolveFlag(
            @PathVariable Long flagId,
            @RequestParam String resolutionAction) {

        try {
            RiskFlagResponse resolved = riskFlagService.resolveFlag(flagId, resolutionAction);
            return ResponseEntity.ok(ApiResponse.success("Risk flag resolved", resolved));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error resolving flag", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error resolving risk flag"));
        }
    }

    /**
     * Get risk summary for a wallet
     * GET /api/v1/risk/wallets/{walletId}/summary
     */
    @GetMapping("/wallets/{walletId}/summary")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRiskSummary(
            @PathVariable Long walletId) {

        try {
            Map<String, Object> summary = riskEngineService.getWalletRiskStats(walletId);
            return ResponseEntity.ok(ApiResponse.success("Risk summary retrieved", summary));
        } catch (Exception e) {
            log.error("Error fetching risk summary", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error fetching risk summary"));
        }
    }

    /**
     * Get rule statistics
     * GET /api/v1/risk/rules/stats
     */
    @GetMapping("/rules/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRuleStats() {
        try {
            Map<String, Object> stats = riskRuleService.getRuleStats();
            return ResponseEntity.ok(ApiResponse.success("Rule statistics retrieved", stats));
        } catch (Exception e) {
            log.error("Error fetching rule stats", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error fetching rule statistics"));
        }
    }

    /**
     * Get engine configuration
     * GET /api/v1/risk/config
     */
    @GetMapping("/config")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getEngineConfig() {
        try {
            Map<String, Object> config = riskEngineService.getEngineConfig();
            return ResponseEntity.ok(ApiResponse.success("Engine configuration retrieved", config));
        } catch (Exception e) {
            log.error("Error fetching engine config", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error fetching engine configuration"));
        }
    }

    /**
     * Check if wallet has critical flags
     * GET /api/v1/risk/wallets/{walletId}/critical
     */
    @GetMapping("/wallets/{walletId}/critical")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkCriticalFlags(
            @PathVariable Long walletId) {

        try {
            boolean hasCritical = riskFlagService.hasCriticalFlags(walletId);
            boolean hasHighRisk = riskFlagService.hasHighRiskActivity(walletId);

            Map<String, Object> result = Map.of(
                "walletId", walletId,
                "hasCriticalFlags", hasCritical,
                "hasHighRiskActivity", hasHighRisk,
                "shouldInvestigate", hasCritical || hasHighRisk
            );

            return ResponseEntity.ok(ApiResponse.success("Critical flags check completed", result));
        } catch (Exception e) {
            log.error("Error checking critical flags", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error checking critical flags"));
        }
    }
}
