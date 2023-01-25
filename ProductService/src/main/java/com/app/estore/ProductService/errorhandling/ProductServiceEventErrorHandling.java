package com.app.estore.ProductService.errorhandling;

import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.EventMessageHandler;
import org.axonframework.eventhandling.ListenerInvocationErrorHandler;

import javax.annotation.Nonnull;
//Class that handle exceptions thrown in event handlers methods
public class ProductServiceEventErrorHandling implements ListenerInvocationErrorHandler {
    @Override
    public void onError(@Nonnull Exception exception, @Nonnull EventMessage<?> event, @Nonnull EventMessageHandler eventHandler) throws Exception {
    throw exception;
    }
}
