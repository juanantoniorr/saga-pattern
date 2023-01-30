package com.app.estore.OrderService.saga;

import com.app.estore.OrderService.command.ApprovedOrderCommand;
import com.app.estore.OrderService.event.OrderApprovedEvent;
import com.app.estore.OrderService.event.OrderCreatedEvent;
import com.estore.core.commands.ProcessPaymentCommand;
import com.estore.core.commands.ReserveProductCommand;
import com.estore.core.details.User;
import com.estore.core.events.PaymentProcessedEvent;
import com.estore.core.events.ProductReservedEvent;
import com.estore.core.query.FetchUserPaymentDetailQuery;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

@Log
@Saga
public class OrderSaga {
    @Autowired
    //transient because saga is serialized so we do not want to serialize our dependency
    private transient CommandGateway commandGateway;

    @Autowired
    private transient QueryGateway queryGateway;

    //first we create the order
    //then we reserve the quantity (products microservice)
    @StartSaga
    @SagaEventHandler(associationProperty = "orderId") //it could be any property from orderCreatedEvent
    public void handle(OrderCreatedEvent orderCreatedEvent){
        ReserveProductCommand reserveProductCommand = ReserveProductCommand.builder()
                .productId(orderCreatedEvent.getProductId())
                .orderId(orderCreatedEvent.getOrderId())
                .quantity(orderCreatedEvent.getQuantity())
                .userId(orderCreatedEvent.getUserId())
                .build();
        log.info("OrderCreatedEvent handled for order  " + reserveProductCommand.getOrderId());
    //If there is an exception (like there is not enough product in stock we can handle it here)
        CommandCallback<ReserveProductCommand, Object> callback = (commandMessage,resultMessage) -> {
            if (resultMessage.isExceptional()){
                log.info(resultMessage.exceptionResult().getMessage());
            }
        };
        commandGateway.send(reserveProductCommand,callback);


    }
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservedEvent productReservedEvent){
        FetchUserPaymentDetailQuery fetchUserPaymentDetailQuery = new FetchUserPaymentDetailQuery(productReservedEvent.getUserId());
        log.info("Product reserved for product  " + productReservedEvent.getProductId());
        User user = null;
        try {
            user = queryGateway.query(fetchUserPaymentDetailQuery, ResponseTypes.instanceOf(User.class)).join();
        } catch (Exception exception){
            log.info(exception.getMessage());
            //Start compensating transaction
            return;
        }
        if (user==null){
            //Start compensating transaction
            return;}
        log.info("Successfully fetched user payment details for user " + user.getUserId());
        //Empieza proceso de pago (Comando)
        ProcessPaymentCommand processPaymentCommand = ProcessPaymentCommand.builder()
                .paymentId(UUID.randomUUID().toString())
                .paymentDetails(user.getPaymentDetails())
                .orderId(productReservedEvent.getOrderId())
                .build();
        String response = null;
        try {
            //Envia comando y espera resultado
            response = commandGateway.sendAndWait(processPaymentCommand,30, TimeUnit.SECONDS);
        } catch (Exception exception){
            //Start compensating transaction
        }
        if (response==null){
            log.info("The comand result was null");
            //Starting compensating transaction
        }


    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(PaymentProcessedEvent paymentProcessedEvent){
        log.info("Approving order");

        //Send an approved command
        ApprovedOrderCommand approvedOrderCommand = new ApprovedOrderCommand(paymentProcessedEvent.getOrderId());
        //Send to new command handler in order aggregate class
        commandGateway.send(approvedOrderCommand);
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle (OrderApprovedEvent orderApprovedEvent){
        log.info("Order Approved, Saga completed for id " + orderApprovedEvent.getOrderId());
        SagaLifecycle.end();

    }
}
