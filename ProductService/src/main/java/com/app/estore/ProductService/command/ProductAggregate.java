package com.app.estore.ProductService.command;

import com.app.estore.ProductService.event.ProductCreatedEvent;
import com.estore.core.commands.CancelProductReservationCommand;
import com.estore.core.commands.ReserveProductCommand;
import com.estore.core.events.ProductReservationCancelledEvent;
import com.estore.core.events.ProductReservedEvent;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

@Aggregate
@NoArgsConstructor
public class ProductAggregate {
    @AggregateIdentifier
    private String productId;
    private String title;
    private BigDecimal price;
    private Integer quantity;

    @CommandHandler
    public ProductAggregate(CreateProductCommand productCommand){
    /// Validate command just example
        if (productCommand.getTitle()==null || productCommand.getTitle().isBlank()){
            throw new IllegalArgumentException("Title cannot be null");
        }
        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent();
        BeanUtils.copyProperties(productCommand,productCreatedEvent);

        //Publish event to all eventHandlers (on-> bottom method)
        AggregateLifecycle.apply(productCreatedEvent);
        //if exception happens here all transaction will rollback cause apply() method above does not execute immediately
    }

    //Handle reserve product command from project core
    @CommandHandler
    public void handle (ReserveProductCommand reserveProductCommand){
        if (quantity< reserveProductCommand.getQuantity()){
            throw new IllegalArgumentException("Not enought product in stock");
        }
        //Event from core, command from core as a parameter
        ProductReservedEvent productReservedEvent = ProductReservedEvent.builder()
                .productId(reserveProductCommand.getProductId())
                .orderId(reserveProductCommand.getOrderId())
                .userId(reserveProductCommand.getUserId())
                .quantity(reserveProductCommand.getQuantity())
                .build();
        AggregateLifecycle.apply(productReservedEvent);
    }

    ///Only update the aggregate state
    @EventSourcingHandler
    public void on(ProductCreatedEvent productCreatedEvent){
        this.productId = productCreatedEvent.getProductId();
        this.price = productCreatedEvent.getPrice();
        this.quantity = productCreatedEvent.getQuantity();
        this.title = productCreatedEvent.getTitle();

    }

    //Lets handle reserve event from core
    @EventSourcingHandler
    public void on (ProductReservedEvent productReservedEvent){
        //just substract the quantity to be up to date all other fields are the same
        this.quantity-= productReservedEvent.getQuantity();
    }

    @CommandHandler
    public void handle(CancelProductReservationCommand cancelProductReservationCommand){
        ProductReservationCancelledEvent productReservationCancelledEvent = ProductReservationCancelledEvent
                .builder()
                .productId(cancelProductReservationCommand.getProductId())
                .orderId(cancelProductReservationCommand.getOrderId())
                .userId(cancelProductReservationCommand.getUserId())
                .reason(cancelProductReservationCommand.getReason())
                .quantity(cancelProductReservationCommand.getQuantity())
                .build();

        AggregateLifecycle.apply(productReservationCancelledEvent);

    }

   @EventSourcingHandler
    public void on (ProductReservationCancelledEvent productReservationCancelledEvent){
        this.quantity+= productReservationCancelledEvent.getQuantity();
    }

}
