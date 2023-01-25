package com.app.estore.OrderService.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CreateOrderDTO {
    @NotBlank
    private String productId;
    @NotNull
    private Integer quantity;
    @NotBlank
    private String addressId;
}
