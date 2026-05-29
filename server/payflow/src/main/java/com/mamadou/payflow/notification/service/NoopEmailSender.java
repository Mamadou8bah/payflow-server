package com.mamadou.payflow.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NoopEmailSender implements EmailSender {

    @Value("${payflow.notifications.email.from:no-reply@payflow.local}")
    private String fromAddress;

    @Override
    public void send(String email, String subject, String body) {
        log.info("Email sender not configured. Skipping email delivery from {} to {}", fromAddress, email);
    }
}
