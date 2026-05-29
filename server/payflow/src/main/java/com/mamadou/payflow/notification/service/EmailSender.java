package com.mamadou.payflow.notification.service;

public interface EmailSender {
    void send(String email, String subject, String body);
}
