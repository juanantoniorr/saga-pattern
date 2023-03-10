package com.app.estore.ProductService.errorhandling;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@AllArgsConstructor
@Data
public class ProductError {
    private final String message;
    private final Date timestamp;
}
