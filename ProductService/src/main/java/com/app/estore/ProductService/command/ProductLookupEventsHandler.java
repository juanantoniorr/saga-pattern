package com.app.estore.ProductService.command;

import com.app.estore.ProductService.entity.ProductLookupEntity;
import com.app.estore.ProductService.event.ProductCreatedEvent;
import com.app.estore.ProductService.repo.ProductLookupRepository;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ResetHandler;
import org.springframework.stereotype.Component;

@Component
@ProcessingGroup("product-group")
public class ProductLookupEventsHandler {
    private final ProductLookupRepository productLookupRepository;

    public ProductLookupEventsHandler(ProductLookupRepository productLookupRepository){
        this.productLookupRepository = productLookupRepository;
    }
    @EventHandler
    public void on (ProductCreatedEvent productCreatedEvent){
        ProductLookupEntity productLookupEntity = new ProductLookupEntity(productCreatedEvent.getProductId(),
                productCreatedEvent.getTitle());
        productLookupRepository.save(productLookupEntity);
    }

    @ResetHandler
    public void reset(){
        productLookupRepository.deleteAll();
    }

}
