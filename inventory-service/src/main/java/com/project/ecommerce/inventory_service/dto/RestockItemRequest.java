package com.project.ecommerce.inventory_service.dto;

import lombok.Data;

@Data
public class RestockItemRequest {
    private Long productId;
    private Integer quantity;
}
