package com.app.estore.ProductService.dto;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class ProductDTO {
    private String productId;
    private String title;
    private BigDecimal price;
    private Integer quantity;
}
