package com.app.estore.ProductService.rest;

import com.app.estore.ProductService.command.CreateProductCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductsController {
    private final Environment environment;
    private final CommandGateway commandGateway;

    //Autowired can be omitted if 1 constructor
    public ProductsController(Environment environment, CommandGateway commandGateway){
        this.environment = environment;
        this.commandGateway = commandGateway;
    }

    @PostMapping
    public String createProduct(@RequestBody CreateProductRestModel product){
      CreateProductCommand productCommand=  CreateProductCommand.builder().price(product.getPrice())
                .quantity(product.getQuantity())
                .title(product.getTitle())
                .productId(UUID.randomUUID().toString())
                .build();

      //send command object to gateway and wait for response
        String callback;
        try {
            callback = commandGateway.sendAndWait(productCommand);
        } catch (Exception exception){
            callback = exception.getLocalizedMessage();
        }


        return callback;
    }

    @GetMapping
    public String getProduct(){
        return environment.getProperty("local.server.port");
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
