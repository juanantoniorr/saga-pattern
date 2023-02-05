package com.app.estore.OrderService.saga;

import com.app.estore.OrderService.command.ApprovedOrderCommand;
import com.app.estore.OrderService.command.RejectOrderCommand;
import com.app.estore.OrderService.dto.OrderSummaryDTO;
import com.app.estore.OrderService.event.OrderApprovedEvent;
import com.app.estore.OrderService.event.OrderCreatedEvent;
import com.app.estore.OrderService.event.OrderRejectedEvent;
import com.app.estore.OrderService.query.FindOrderQuery;
import com.estore.core.commands.CancelProductReservationCommand;
import com.estore.core.commands.ProcessPaymentCommand;
import com.estore.core.commands.ReserveProductCommand;
import com.estore.core.details.User;
import com.estore.core.events.PaymentProcessedEvent;
import com.estore.core.events.ProductReservationCancelledEvent;
import com.estore.core.events.ProductReservedEvent;
import com.estore.core.query.FetchUserPaymentDetailQuery;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.annotation.DeadlineHandler;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
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

    @Autowired
    public transient DeadlineManager deadlineManager;

    //Inform to queries subscriptors about errors and updates.
    @Autowired
    public transient QueryUpdateEmitter queryUpdateEmitter;

    private static final String DEADLINE_PAYMENT_NAME = "productReservedDeadline";

    private String scheduleId;

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
                RejectOrderCommand rejectOrderCommand = new RejectOrderCommand(orderCreatedEvent.getOrderId(),resultMessage.exceptionResult().getMessage());
                commandGateway.send(rejectOrderCommand);

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
            cancelProductReservation(productReservedEvent,exception.getMessage());
            return;
        }
        if (user==null){
            //Start compensating transaction
            cancelProductReservation(productReservedEvent,"Could not fetch user details");
            return;}
        log.info("Successfully fetched user payment details for user " + user.getUserId());

        //We can set 10 seconds in order to test the deadline and uncomment line 98
        scheduleId = deadlineManager.schedule(Duration.of(120, ChronoUnit.SECONDS), DEADLINE_PAYMENT_NAME, productReservedEvent);
        //forcing deadline to show up
       // if (true){return;}
        //Empieza proceso de pago (Comando)
        ProcessPaymentCommand processPaymentCommand = ProcessPaymentCommand.builder()
                .paymentId(UUID.randomUUID().toString())
                .paymentDetails(user.getPaymentDetails())
                .orderId(productReservedEvent.getOrderId())
                .build();
        String response = null;
        try {
            //Envia comando y espera resultado
            response = commandGateway.sendAndWait(processPaymentCommand);
        } catch (Exception exception){
            //Start compensating transaction
            cancelProductReservation(productReservedEvent,exception.getMessage());
        }
        if (response==null){
            log.info("The comand result was null");
            //Starting compensating transaction
            cancelProductReservation(productReservedEvent,"The comand result was null");
        }


    }

    private void cancelProductReservation(ProductReservedEvent event, String reason){
        cancelDeadline(DEADLINE_PAYMENT_NAME);

        CancelProductReservationCommand cancelProductReservationCommand =
                CancelProductReservationCommand.builder()
                        .productId(event.getProductId())
                        .quantity(event.getQuantity())
                        .orderId(event.getOrderId())
                        .reason(reason)
                        .userId(event.getUserId())
                        .build();

        commandGateway.send(cancelProductReservationCommand);


    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(PaymentProcessedEvent paymentProcessedEvent){
        log.info("Approving order");
        //if payment is success we need to cancel the deadline
        cancelDeadline(DEADLINE_PAYMENT_NAME);
        //Send an approved command
        ApprovedOrderCommand approvedOrderCommand = new ApprovedOrderCommand(paymentProcessedEvent.getOrderId());
        //Send to new command handler in order aggregate class
        commandGateway.send(approvedOrderCommand);
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle (OrderApprovedEvent orderApprovedEvent){
        log.info("Order Approved, Saga completed for id " + orderApprovedEvent.getOrderId());
        //Emmiting update that the order was completed
        queryUpdateEmitter.emit(FindOrderQuery.class,query -> true, new OrderSummaryDTO(orderApprovedEvent.getOrderId(), orderApprovedEvent.getOrderStatus(), "Order completed"));

    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderRejectedEvent orderRejectedEvent){

        log.info("Order succesfully cancelled ");
        //Emmiting update that the order was rejected
        queryUpdateEmitter.emit(FindOrderQuery.class,query -> true, new OrderSummaryDTO(orderRejectedEvent.getOrderId(), orderRejectedEvent.getOrderStatus(), orderRejectedEvent.getReason()));

    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservationCancelledEvent productReservationCancelledEvent){
        RejectOrderCommand rejectOrderCommand = new RejectOrderCommand(productReservationCancelledEvent.getOrderId(),productReservationCancelledEvent.getReason());
        commandGateway.send(rejectOrderCommand);
    }

    //Deadline handler method
    @DeadlineHandler(deadlineName = DEADLINE_PAYMENT_NAME)
    public void handlePaymentDeadline(ProductReservedEvent productReservedEvent){
        log.info("Deadline triggered");
        cancelProductReservation(productReservedEvent, "Payment timeout");

    }

    private void cancelDeadline(String deadlineName){
        if (scheduleId!=null){
            deadlineManager.cancelSchedule(deadlineName,scheduleId);
            scheduleId = null;
        }
    }
}
