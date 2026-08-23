package com.mamadou.payflow.fraud.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FraudDecisionResponse(
    String transaction_id,
    String decision,
    double score,
    List<String> reasons,
    double latency_ms,
    String rule_triggered,
    Map<String, Object> features
) {}
