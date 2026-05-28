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
        log.info("SMS sender not configured. Skipping SMS delivery to {}", phoneNumber);
    }
}
