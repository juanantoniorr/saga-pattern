package com.app.estore.ProductService.query;

import com.app.estore.ProductService.dto.ProductDTO;
import com.app.estore.ProductService.entity.ProductEntity;
import com.app.estore.ProductService.repo.ProductRepository;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProductsQueryHandler {
    ProductRepository productRepository;

    public ProductsQueryHandler(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    @QueryHandler
    //Returning DTO instead of entities, receives empty class as query
    public List<ProductDTO> findProducts(FindProductsQuery findProductsQuery){
       List<ProductEntity> products =  productRepository.findAll();
       List<ProductDTO> productDTOS = new ArrayList<>();
     products.stream().forEach(e -> {
        ProductDTO productDTO = new ProductDTO();
        BeanUtils.copyProperties(e,productDTO);
        productDTOS.add(productDTO);
    });
     return productDTOS;
    }
}
