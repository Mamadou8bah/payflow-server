package com.mamadou.payflow.notification.service;

public interface SmsSender {
    void send(String phoneNumber, String message);
}
