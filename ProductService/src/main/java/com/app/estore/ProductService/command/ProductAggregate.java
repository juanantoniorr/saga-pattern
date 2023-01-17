package com.app.estore.ProductService.command;

import com.app.estore.ProductService.event.ProductCreatedEvent;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.EventHandler;
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
    }

    ///Only update the aggregate state
    @EventSourcingHandler
    public void on(ProductCreatedEvent productCreatedEvent){
        this.productId = productCreatedEvent.getProductId();
        this.price = productCreatedEvent.getPrice();
        this.quantity = productCreatedEvent.getQuantity();
        this.title = productCreatedEvent.getTitle();

    }

}
