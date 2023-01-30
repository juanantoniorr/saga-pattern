package com.app.estore.OrderService.command;

import com.app.estore.OrderService.constants.OrderStatus;
import com.app.estore.OrderService.event.OrderApprovedEvent;
import com.app.estore.OrderService.event.OrderCreatedEvent;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@Aggregate
@NoArgsConstructor
public class OrderAggregate {
    @AggregateIdentifier //toDo
    private String orderId;
    private String userId;
    private String productId;
    private int quantity;
    private String addressId;
    private OrderStatus orderStatus;

    //Aqui llega el gateway
    @CommandHandler
    public OrderAggregate(CreateOrderCommand createOrderCommand){
        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent();
        BeanUtils.copyProperties(createOrderCommand, orderCreatedEvent);
        AggregateLifecycle.apply(orderCreatedEvent);
    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent orderCreatedEvent){
        this.orderId = orderCreatedEvent.getOrderId();
        this.userId = orderCreatedEvent.getUserId();
        this.productId = orderCreatedEvent.getProductId();
        this.quantity = orderCreatedEvent.getQuantity();
        this.addressId = orderCreatedEvent.getAddressId();
        this.orderStatus = orderCreatedEvent.getOrderStatus();
    }

    //Handle approved order command from OrderSaga class
    @CommandHandler
    public void handle(ApprovedOrderCommand approvedOrderCommand){
        OrderApprovedEvent orderApprovedEvent = new OrderApprovedEvent(approvedOrderCommand.getOrderId());
        //Schedule event
        AggregateLifecycle.apply(orderApprovedEvent);
    }

    @EventSourcingHandler
    protected void on(OrderApprovedEvent orderApprovedEvent){
        this.orderStatus = orderApprovedEvent.getOrderStatus();
    }

}
