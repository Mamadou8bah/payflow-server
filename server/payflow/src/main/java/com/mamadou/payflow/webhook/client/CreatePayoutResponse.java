package com.mamadou.payflow.webhook.client;

import lombok.Data;

@Data
public class CreatePayoutResponse {
    private String id;
    private String status;
    private Object raw;
}
