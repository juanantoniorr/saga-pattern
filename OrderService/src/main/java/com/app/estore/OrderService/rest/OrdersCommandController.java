package com.app.estore.OrderService.rest;

import com.app.estore.OrderService.command.CreateOrderCommand;
import com.app.estore.OrderService.constants.OrderStatus;
import com.app.estore.OrderService.dto.CreateOrderDTO;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.http.ResponseEntity;
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

    public OrdersCommandController(CommandGateway commandGateway){
        this.commandGateway = commandGateway;
    }

    @PostMapping
    public String createOrder(@Valid @RequestBody CreateOrderDTO orderDTO){
        CreateOrderCommand createOrderCommand = CreateOrderCommand.builder().orderId(UUID.randomUUID().toString())
                .quantity(orderDTO.getQuantity())
                .productId(orderDTO.getProductId())
                .addressId(orderDTO.getAddressId())
                .orderStatus(OrderStatus.CREATED)
                .userId("27b95829-4f3f-4ddf-8983-151ba010e35b")
                .build();
        String callback = commandGateway.sendAndWait(createOrderCommand);
        return callback;
    }
}
