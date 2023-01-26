package com.estore.core.commands;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

//This class (command) will be handled by ProductAggregate class
@Data
@Builder
public class ReserveProductCommand {
    //help axon find the right aggregate
    @TargetAggregateIdentifier
    private final String productId;
    private final int quantity;
    private final String orderId;
    private final String userId;
}
