package com.app.estore.OrderService.query;

import com.app.estore.OrderService.entity.Order;
import com.app.estore.OrderService.event.OrderCreatedEvent;
import com.app.estore.OrderService.repository.OrderRepository;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@ProcessingGroup("order-group") //same group for events handlers
public class OrderEventsHandler {
    private final OrderRepository orderRepository;

    public OrderEventsHandler(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
    }
    @EventHandler
    public void on (OrderCreatedEvent orderCreatedEvent){
        Order order = new Order();
        BeanUtils.copyProperties(orderCreatedEvent, order);
        orderRepository.save(order);
    }
}
