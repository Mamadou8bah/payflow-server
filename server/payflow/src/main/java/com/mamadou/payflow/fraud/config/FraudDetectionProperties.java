package com.mamadou.payflow.fraud.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "payflow.fraud-detection")
public class FraudDetectionProperties {

    private boolean enabled = true;
    private String baseUrl = "http://localhost:8001";
    private int timeoutMs = 2000;
    /** When true, allow transactions if the fraud API is unreachable */
    private boolean failOpen = true;
    private boolean blockOnReview = false;
    private String defaultCountry = "GM";
    private String defaultChannel = "api";
    /** Shared secret sent as X-Api-Key to the fraud service */
    private String apiKey = "";
}
