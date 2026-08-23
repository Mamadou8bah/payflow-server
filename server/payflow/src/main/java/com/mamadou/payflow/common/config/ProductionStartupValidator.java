package com.mamadou.payflow.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Warns on production misconfiguration that would silently break security or notifications.
 */
@Component
@Profile("prod")
@Slf4j
public class ProductionStartupValidator implements ApplicationRunner {

    @Value("${payflow.notifications.sms.provider:noop}")
    private String smsProvider;

    @Value("${payflow.notifications.email.provider:noop}")
    private String emailProvider;

    @Value("${jwt.secret:}")
    private String jwtSecret;

    @Override
    public void run(ApplicationArguments args) {
        if (jwtSecret == null || jwtSecret.isBlank()) {
            throw new IllegalStateException("JWT_SECRET must be set in production");
        }

        if ("noop".equalsIgnoreCase(smsProvider)) {
            log.warn(
                    "PAYFLOW_SMS_PROVIDER is 'noop' — SMS OTP delivery is disabled. "
                            + "Configure Twilio before enabling 2FA for merchants/admins."
            );
        }

        if ("noop".equalsIgnoreCase(emailProvider)) {
            log.warn(
                    "PAYFLOW_EMAIL_PROVIDER is 'noop' — email OTP delivery is disabled. "
                            + "Configure a real email provider before enabling email-based 2FA."
            );
        }
    }
}
