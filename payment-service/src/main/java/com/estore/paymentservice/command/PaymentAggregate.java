package com.estore.paymentservice.command;

import com.estore.core.commands.ProcessPaymentCommand;
import com.estore.core.details.PaymentDetails;
import com.estore.core.events.PaymentProcessedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
public class PaymentAggregate {
    @AggregateIdentifier
    private String paymentId;
    private String orderId;

    @CommandHandler
    public PaymentAggregate(ProcessPaymentCommand processPaymentCommand){
        PaymentProcessedEvent paymentProcessedEvent = new PaymentProcessedEvent(processPaymentCommand.getPaymentId(), processPaymentCommand.getOrderId());
        //validate command
        if (processPaymentCommand.getPaymentId() == null || processPaymentCommand.getOrderId() == null || processPaymentCommand.getPaymentDetails() == null){
            throw new IllegalArgumentException("payment or orderId null");
        }

        AggregateLifecycle.apply(paymentProcessedEvent);
    }
    @EventSourcingHandler
    public void on(PaymentProcessedEvent paymentProcessedEvent){
        this.paymentId = paymentProcessedEvent.getPaymentId();
        this.orderId = paymentProcessedEvent.getOrderId();
    }
}
