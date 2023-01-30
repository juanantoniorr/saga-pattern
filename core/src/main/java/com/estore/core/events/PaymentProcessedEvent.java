package com.estore.core.events;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentProcessedEvent {
    private  String paymentId;
    private  String orderId;
}
