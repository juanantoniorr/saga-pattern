package com.app.estore.ProductService.command.interceptors;

import com.app.estore.ProductService.command.CreateProductCommand;
import lombok.extern.java.Log;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.BiFunction;

@Component
@Log
public class CreateProductCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    @Nonnull
    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(@Nonnull List<? extends CommandMessage<?>> messages) {
        log.info("Entering to message dispatcher filter");
        return (index,command) -> {
            //Validate just to intercept creation
            if (command.getPayloadType().isInstance(CreateProductCommand.class) ){
                //Cast payload cause we know is a CreateCommand type
                CreateProductCommand createProductCommand = (CreateProductCommand) command.getPayload();
                /// Validate command just example
                if (createProductCommand.getTitle()==null || createProductCommand.getTitle().isBlank()){
                    throw new IllegalArgumentException("Title cannot be null");
                }
            }
            return command;
        };
    }
}
