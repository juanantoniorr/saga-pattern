package com.app.estore.ProductService.command.interceptors;

import com.app.estore.ProductService.command.CreateProductCommand;
import com.app.estore.ProductService.entity.ProductLookupEntity;
import com.app.estore.ProductService.repo.ProductLookupRepository;
import lombok.extern.java.Log;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

//This interceptor class is triggered at the beginning of every request of creation
@Component
@Log
public class CreateProductCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    private final ProductLookupRepository productLookupRepository;
    public CreateProductCommandInterceptor(ProductLookupRepository productLookupRepository){
        this.productLookupRepository = productLookupRepository;
    }

    @Nonnull
    @Override
    //Regresas una funcion que recibe un integer, un commandMessage y regresas un commandMessage
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(@Nonnull List<? extends CommandMessage<?>> messages) {
        log.info("Entering to message dispatcher filter");
        return (index,command) -> {
            //Validate just to intercept creation
            if (CreateProductCommand.class.equals(command.getPayloadType()) ){
                //Cast payload cause we know is a CreateCommand type
                CreateProductCommand createProductCommand = (CreateProductCommand) command.getPayload();
                /// Validate command just example
                if (createProductCommand.getTitle()==null || createProductCommand.getTitle().isBlank()){
                    throw new IllegalArgumentException("Title cannot be null");
                }
                //Validate id and title in the lookup table
               Optional<ProductLookupEntity> productLookupEntity = productLookupRepository.findByProductIdOrTitle(createProductCommand.getProductId(), createProductCommand.getTitle());
                if (productLookupEntity.isPresent()){
                    //After throwing exception ProductServiceErrorHandling class will be called
                throw new IllegalArgumentException("Title or Id already in the database");
                }
            }
            return command;
        };
    }
}
