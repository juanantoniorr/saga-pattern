package com.app.estore.ProductService.dto;

import lombok.Data;


import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
public class CreateProductRestModel {
    //Not null and not blank
    @NotBlank(message = "Field is required")
    private String title;
    @Min(value = 1, message = "Price cannot be lower than 1")
    private BigDecimal price;
    @Max(value = 5, message = "Quantity cannot be more than quantity")
    private Integer quantity;
}
