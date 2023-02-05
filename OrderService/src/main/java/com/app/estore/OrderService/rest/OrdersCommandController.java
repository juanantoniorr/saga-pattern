package com.app.estore.OrderService.rest;

import com.app.estore.OrderService.command.CreateOrderCommand;
import com.app.estore.OrderService.constants.OrderStatus;
import com.app.estore.OrderService.dto.CreateOrderDTO;
import com.app.estore.OrderService.dto.OrderSummaryDTO;
import com.app.estore.OrderService.query.FindOrderQuery;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrdersCommandController {
    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    public OrdersCommandController(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @PostMapping
    public OrderSummaryDTO createOrder(@Valid @RequestBody CreateOrderDTO orderDTO) {
        String orderId = UUID.randomUUID().toString();
        CreateOrderCommand createOrderCommand = CreateOrderCommand.builder().orderId(UUID.randomUUID().toString())
                .quantity(orderDTO.getQuantity())
                .productId(orderDTO.getProductId())
                .addressId(orderDTO.getAddressId())
                .orderStatus(OrderStatus.CREATED)
                .userId("27b95829-4f3f-4ddf-8983-151ba010e35b")
                .build();
        //query, initial response type, updated response type
        // de aqui va a OrderQueryHandler que regresa un orderSummaryDTO
        //Subscription extends from autoclosable so we can use try with resources
        try(SubscriptionQueryResult<OrderSummaryDTO, OrderSummaryDTO> subscriptionQueryResult = queryGateway.subscriptionQuery(new FindOrderQuery(orderId),
                ResponseTypes.instanceOf(OrderSummaryDTO.class),
                ResponseTypes.instanceOf(OrderSummaryDTO.class));){
           commandGateway.sendAndWait(createOrderCommand);
            return subscriptionQueryResult.updates().blockFirst();
        }
    }
}
