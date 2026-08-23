package com.mamadou.payflow.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "payflow.notifications.sms", name = "provider", havingValue = "noop", matchIfMissing = true)
public class NoopSmsSender implements SmsSender {

    @Override
    public void send(String phoneNumber, String message) {
        // Never log message bodies — they may contain OTPs or other secrets.
        log.warn("SMS provider is noop; message not delivered to {}", maskPhone(phoneNumber));
    }

    private static String maskPhone(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 4) {
            return "***";
        }
        return "***" + phoneNumber.substring(phoneNumber.length() - 4);
    }
}
