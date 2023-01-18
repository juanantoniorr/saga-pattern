package com.app.estore.ProductService.rest;

import com.app.estore.ProductService.dto.ProductDTO;
import com.app.estore.ProductService.query.FindProductsQuery;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductsQueryController {
    @Autowired
    QueryGateway queryGateway;
    @GetMapping
    public List<ProductDTO> getProducts(){
        //We can represent query with empty class cause no param required
        FindProductsQuery findProductsQuery = new FindProductsQuery();
        //ResponseTypes.multipleInstancesOf returns a completableFuture .join return the result when data is ready
        //Receives empty class, this method calls ProductQueryHandler
        List<ProductDTO> products =  queryGateway.query(findProductsQuery, ResponseTypes.multipleInstancesOf(ProductDTO.class)).join();
        return products;
    }

}
