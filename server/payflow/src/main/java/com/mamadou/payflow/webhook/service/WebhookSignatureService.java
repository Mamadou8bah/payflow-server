package com.mamadou.payflow.webhook.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Service for verifying Modem Pay webhook signatures
 * Ensures webhooks are legitimate and not tampered with
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookSignatureService {

    @Value("${payflow.webhooks.modempay.signing-secret}")
    private String webhookSecret;

    /**
     * Verify webhook signature using HMAC-SHA256
     * 
     * @param payload Raw webhook payload
     * @param signature Signature from webhook header
     * @return true if signature is valid, false otherwise
     */
    public boolean verifySignature(String payload, String signature) {
        try {
            String expectedSignature = generateSignature(payload);
            boolean isValid = constantTimeEquals(expectedSignature, signature);
            
            if (!isValid) {
                log.warn("Webhook signature verification failed");
            }
            return isValid;
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Error verifying webhook signature", e);
            return false;
        }
    }

    /**
     * Generate HMAC-SHA256 signature for payload
     */
    private String generateSignature(String payload) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(
                webhookSecret.getBytes(StandardCharsets.UTF_8),
                0,
                webhookSecret.getBytes(StandardCharsets.UTF_8).length,
                "HmacSHA256"
        );
        mac.init(secretKey);
        byte[] rawHmac = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(rawHmac);
    }

    /**
     * Constant-time comparison to prevent timing attacks
     */
    private boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        
        byte[] aBytes = a.getBytes(StandardCharsets.UTF_8);
        byte[] bBytes = b.getBytes(StandardCharsets.UTF_8);
        
        if (aBytes.length != bBytes.length) {
            return false;
        }
        
        int result = 0;
        for (int i = 0; i < aBytes.length; i++) {
            result |= aBytes[i] ^ bBytes[i];
        }
        return result == 0;
    }
}
