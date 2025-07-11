package com.example.exam_portal.domain;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PaymentRequest {
    private String amount;
    private String orderId;
    private String orderInfo;
    private String requestId;
    private String extraData;
}
