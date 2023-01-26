package com.app.estore.OrderService.saga;

import com.app.estore.OrderService.event.OrderCreatedEvent;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

@Saga
public class OrderSaga {
    @Autowired
    //transient because saga is serialized so we do not want to serialize our dependency
    private transient CommandGateway commandGateway;

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId") //it could be any property from orderCreatedEvent
    public void handle(OrderCreatedEvent orderCreatedEvent){

    }
}
