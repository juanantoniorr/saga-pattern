package com.app.estore.ProductService;

import com.app.estore.ProductService.command.interceptors.CreateProductCommandInterceptor;
import com.app.estore.ProductService.errorhandling.ProductServiceEventErrorHandling;
import com.estore.core.config.AxonConfig;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.config.EventProcessingConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

@EnableDiscoveryClient
@SpringBootApplication
@Import({ AxonConfig.class })
public class ProductServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductServiceApplication.class, args);
	}

	//Register interceptor
	@Autowired
	public void registerCreateCommandInterceptor(ApplicationContext applicationContext, CommandBus commandBus){
	commandBus.registerDispatchInterceptor(applicationContext.getBean(CreateProductCommandInterceptor.class));
	}

	@Autowired
	public void configure(EventProcessingConfigurer eventProcessingConfigurer){
		//product group defined in ProductsEventHandler, config = Class that will handle errors created by me
		eventProcessingConfigurer.registerListenerInvocationErrorHandler("product-group",
				config -> new ProductServiceEventErrorHandling());
		//This if i dont want to use a custom class, just use axon class
		//config -> new PropagatingErrorHandler.instance();
	}

}
