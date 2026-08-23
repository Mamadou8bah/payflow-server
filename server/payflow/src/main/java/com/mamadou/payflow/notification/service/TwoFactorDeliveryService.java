package com.mamadou.payflow.notification.service;

import com.mamadou.payflow.common.Exception.InvalidTokenException;
import com.mamadou.payflow.user.entity.User;
import com.mamadou.payflow.user.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TwoFactorDeliveryService {

    private final SmsSender smsSender;
    private final EmailSender emailSender;

    public void sendCode(User user, String code) {
        if (user.getRole() == Role.MERCHANT || user.getRole() == Role.DEVELOPER || user.getRole() == Role.ADMIN) {
            sendEmailCode(user, code);
            return;
        }
        sendSmsCode(user, code);
    }

    private void sendSmsCode(User user, String code) {
        if (user.getPhoneNumber() == null || user.getPhoneNumber().isBlank()) {
            throw new InvalidTokenException("Customer phone number is required for 2FA");
        }
        smsSender.send(user.getPhoneNumber(), "Your Payflow verification code is " + code + ". It expires in 5 minutes.");
    }

    private void sendEmailCode(User user, String code) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new InvalidTokenException("Merchant email is required for 2FA");
        }
        emailSender.send(
                user.getEmail(),
                "Your Payflow verification code",
                "Your Payflow verification code is " + code + ". It expires in 5 minutes."
        );
    }
}
