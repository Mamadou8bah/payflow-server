package com.mamadou.payflow.webhook.client;

import lombok.Data;

@Data
public class CreateChargeResponse {
    private String id; // external charge id
    private String status;
    private String paymentUrl; // hosted checkout link
    private Object raw; // catch-all for response
}
