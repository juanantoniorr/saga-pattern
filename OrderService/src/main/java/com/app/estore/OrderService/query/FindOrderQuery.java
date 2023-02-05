package com.app.estore.OrderService.query;

import lombok.Value;

@Value
public class FindOrderQuery {
    private final String orderId;
}
