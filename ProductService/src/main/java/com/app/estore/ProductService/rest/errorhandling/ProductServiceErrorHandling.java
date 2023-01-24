package com.app.estore.ProductService.rest.errorhandling;

import com.app.estore.ProductService.entity.ProductLookupEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

///Class in charge of handling all exceptions from the controller
@ControllerAdvice
public class ProductServiceErrorHandling {
    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException exception, WebRequest request){
        ProductError productError = new ProductError(exception.getMessage(), new Date());
    return new ResponseEntity<>(productError,new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<?> handleAllException(Exception exception, WebRequest request){
        ProductError productError = new ProductError(exception.getMessage(), new Date());
        return new ResponseEntity<>(productError,new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
