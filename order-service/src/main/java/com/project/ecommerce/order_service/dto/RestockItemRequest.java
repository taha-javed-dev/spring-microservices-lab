package com.project.ecommerce.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RestockItemRequest {
    private Long productId;
    private Integer quantity;
}
