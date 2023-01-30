package com.estore.paymentservice.event;

import com.estore.core.events.PaymentProcessedEvent;
import com.estore.paymentservice.entity.Payment;
import com.estore.paymentservice.repository.PaymentRepository;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ProcessingGroup("payment-group")
public class PaymentEventsHandler {
    @Autowired
    private PaymentRepository paymentRepository;

    @EventHandler
    public void on (PaymentProcessedEvent paymentProcessedEvent){
        Payment payment = new Payment();
        BeanUtils.copyProperties(paymentProcessedEvent, payment);
        paymentRepository.save(payment);
    }

}
