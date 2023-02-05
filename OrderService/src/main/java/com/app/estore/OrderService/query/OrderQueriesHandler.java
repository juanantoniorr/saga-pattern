package com.app.estore.OrderService.query;

import com.app.estore.OrderService.dto.OrderSummaryDTO;
import com.app.estore.OrderService.entity.Order;
import com.app.estore.OrderService.repository.OrderRepository;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OrderQueriesHandler {
    @Autowired
    OrderRepository orderRepository;

    @QueryHandler
    public OrderSummaryDTO findOrder(FindOrderQuery findOrderQuery) {
        Optional<Order> order = orderRepository.findById(findOrderQuery.getOrderId());
        if (order.isPresent()) return new OrderSummaryDTO(order.get().getOrderId(), order.get().getOrderStatus(), "");
        return null;
    }


}
