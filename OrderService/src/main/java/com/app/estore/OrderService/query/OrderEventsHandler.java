package com.app.estore.OrderService.query;

import com.app.estore.OrderService.entity.Order;
import com.app.estore.OrderService.event.OrderApprovedEvent;
import com.app.estore.OrderService.event.OrderCreatedEvent;
import com.app.estore.OrderService.event.OrderRejectedEvent;
import com.app.estore.OrderService.repository.OrderRepository;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
    @EventHandler
    public void on(OrderApprovedEvent orderApprovedEvent){
       Optional<Order> optionalOrder =  orderRepository.findById(orderApprovedEvent.getOrderId());
       if (!optionalOrder.isPresent()){return;} //log message
        Order order = optionalOrder.get();
       order.setOrderStatus(orderApprovedEvent.getOrderStatus());
       orderRepository.save(order);

    }

    @EventHandler
    public void on (OrderRejectedEvent orderRejectedEvent){
        Optional<Order> optionalOrder = orderRepository.findById(orderRejectedEvent.getOrderId());
        if (optionalOrder.isPresent()){
            Order order = optionalOrder.get();
            order.setOrderStatus(orderRejectedEvent.getOrderStatus());
            orderRepository.save(order);
        }
    }
}
