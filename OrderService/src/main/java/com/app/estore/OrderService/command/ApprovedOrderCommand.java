package com.app.estore.OrderService.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@AllArgsConstructor
public class ApprovedOrderCommand {
    @TargetAggregateIdentifier
    private final String  orderId;
}
