package com.app.estore.OrderService.dto;

import com.app.estore.OrderService.constants.OrderStatus;
import lombok.Value;

@Value
public class OrderSummaryDTO {
    private final String orderId;
    private final OrderStatus orderStatus;
    private final String message;
}
