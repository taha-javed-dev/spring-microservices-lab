package com.project.ecommerce.inventory_service.service;

import com.project.ecommerce.inventory_service.dto.OrderRequestDto;
import com.project.ecommerce.inventory_service.dto.ProductDto;
import com.project.ecommerce.inventory_service.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    public List<ProductDto> getAllInventory();
    public ProductDto getProductById(Long id);

    Double reduceStock(OrderRequestDto orderRequestDto);
}
