package com.app.estore.ProductService.rest;

import com.app.estore.ProductService.command.CreateProductCommand;
import com.app.estore.ProductService.dto.CreateProductRestModel;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductsCommandController {
    private final Environment environment;
    private final CommandGateway commandGateway;

    //Autowired can be omitted if 1 constructor
    public ProductsCommandController(Environment environment, CommandGateway commandGateway){
        this.environment = environment;
        this.commandGateway = commandGateway;
    }

    @PostMapping
    //RequestBody only mandatory fields in order to create product
    public String createProduct(@Valid @RequestBody  CreateProductRestModel product){
      CreateProductCommand productCommand=  CreateProductCommand.builder().price(product.getPrice())
                .quantity(product.getQuantity())
                .title(product.getTitle())
                .productId(UUID.randomUUID().toString())
                .build();

      //send command object to gateway and wait for response
        String callback = commandGateway.sendAndWait(productCommand);

        return callback;
    }

    @PutMapping
    public String updateProduct(){
        return "HTTP Put";
    }

    @DeleteMapping
    public String deleteProduct(){
        return "HTTP Put";
    }
}
