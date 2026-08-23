package com.mamadou.payflow.fraud.client;

import com.mamadou.payflow.fraud.config.FraudDetectionProperties;
import com.mamadou.payflow.fraud.dto.FraudDecisionResponse;
import com.mamadou.payflow.fraud.dto.TransactionEventRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class FraudDetectionClient {

    private final FraudDetectionProperties properties;
    private final RestTemplate fraudDetectionRestTemplate;

    public FraudDecisionResponse score(TransactionEventRequest event) {
        String url = properties.getBaseUrl().replaceAll("/+$", "") + "/v1/score";
        HttpEntity<TransactionEventRequest> entity = new HttpEntity<>(event, authHeaders());
        return fraudDetectionRestTemplate.postForObject(url, entity, FraudDecisionResponse.class);
    }

    public void labelRecord(String transactionReference, boolean isFraud) {
        try {
            String url = properties.getBaseUrl().replaceAll("/+$", "")
                    + "/v1/records/" + transactionReference + "/label";
            Map<String, Object> body = Map.of("is_fraud", isFraud, "label_source", "java-admin");
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, authHeaders());
            fraudDetectionRestTemplate.put(url, entity);
        } catch (RestClientException ex) {
            log.warn("Failed to propagate fraud label to Python service: {}", ex.getMessage());
        }
    }

    public boolean isHealthy() {
        try {
            String url = properties.getBaseUrl().replaceAll("/+$", "") + "/health";
            Map<?, ?> response = fraudDetectionRestTemplate.getForObject(url, Map.class);
            return response != null && "ok".equals(response.get("status"));
        } catch (RestClientException ex) {
            log.warn("Fraud detection health check failed: {}", ex.getMessage());
            return false;
        }
    }

    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String apiKey = properties.getApiKey();
        if (apiKey != null && !apiKey.isBlank()) {
            headers.set("X-Api-Key", apiKey);
        }
        return headers;
    }
}
