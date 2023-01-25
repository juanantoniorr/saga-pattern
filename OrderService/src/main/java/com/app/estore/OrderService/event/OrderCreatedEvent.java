package com.app.estore.OrderService.event;

import com.app.estore.OrderService.constants.OrderStatus;
import lombok.Data;

@Data
public class OrderCreatedEvent {
    private String orderId;
    private String userId;
    private String productId;
    private int quantity;
    private String addressId;
    private OrderStatus orderStatus;
}
