package com.app.estore.ProductService.query;

import com.app.estore.ProductService.entity.ProductEntity;
import com.app.estore.ProductService.event.ProductCreatedEvent;
import com.app.estore.ProductService.repo.ProductRepository;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class ProductsEventHandler {
    //Injected by constructor
    private final ProductRepository productRepository;

    public ProductsEventHandler(ProductRepository productRepository){
        this.productRepository = productRepository;
    }
    @EventHandler // receives the event that will be handled
    public void on(ProductCreatedEvent productCreatedEvent){
        ProductEntity product = new ProductEntity();
        BeanUtils.copyProperties(productCreatedEvent, product);
        productRepository.save(product);
    }
}
