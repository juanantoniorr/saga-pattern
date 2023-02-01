package com.app.estore.ProductService.query;

import com.app.estore.ProductService.entity.ProductEntity;
import com.app.estore.ProductService.event.ProductCreatedEvent;
import com.app.estore.ProductService.repo.ProductRepository;
import com.estore.core.events.ProductReservationCancelledEvent;
import com.estore.core.events.ProductReservedEvent;
import lombok.extern.java.Log;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Log
@Component
@ProcessingGroup("product-group") //same group for events handlers
public class ProductsEventHandler {
    //Injected by constructor
    private final ProductRepository productRepository;

    //Manage exceptions from event handler in this class
    @ExceptionHandler(resultType = IllegalArgumentException.class)
    public void handleException(IllegalArgumentException exception){
        //Propagates the exception
        //lets rethrow the exception so Axon will handle it in ProductServiceErrorHandling class
        throw exception;

    }
    @ExceptionHandler(resultType = Exception.class)
    public void handleException(Exception exception) throws Exception {
    throw exception;
    }

    public ProductsEventHandler(ProductRepository productRepository){
        this.productRepository = productRepository;
    }
    @EventHandler // receives the event that will be handled
    public void on(ProductCreatedEvent productCreatedEvent) throws Exception{
        ProductEntity product = new ProductEntity();
        BeanUtils.copyProperties(productCreatedEvent, product);
        productRepository.save(product);
        //calls this method: handleException in this class
        //Forcing exception to see transaction behaviour, if error nothing is persisted
       //if (true){throw new Exception("Forced exception in event to rollback");}
    }

    //New event handler for Product reserved event
    @EventHandler
    public void on (ProductReservedEvent productReservedEvent){
       ProductEntity product = productRepository.findByProductId(productReservedEvent.getProductId());
       log.info("Current quantity " + productReservedEvent.getQuantity());
       if (product.getQuantity()<productReservedEvent.getQuantity()){
           throw new IllegalArgumentException("Not enough product in stock");
       }
       product.setQuantity(product.getQuantity() - productReservedEvent.getQuantity());
        log.info("New quantity " + product.getQuantity());
       productRepository.save(product);
       log.info("Product reserved event was called ");
    }

    @EventHandler
    //ToDo Event is called twice when rolling back
    public void on (ProductReservationCancelledEvent productReservationCancelledEvent){
    ProductEntity product = productRepository.findByProductId(productReservationCancelledEvent.getProductId());
    log.info("Product reservation cancelled event: quantity " + productReservationCancelledEvent.getQuantity());
    int newQuantity = product.getQuantity() + productReservationCancelledEvent.getQuantity();
    product.setQuantity(newQuantity);
        log.info("Product reservation cancelled event: rolled back: new quantity " + product.getQuantity());
    productRepository.save(product);
    }
}
