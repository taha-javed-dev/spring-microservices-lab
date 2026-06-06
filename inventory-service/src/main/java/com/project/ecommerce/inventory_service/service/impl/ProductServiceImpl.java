package com.project.ecommerce.inventory_service.service.impl;


import com.project.ecommerce.inventory_service.dto.OrderRequestDto;
import com.project.ecommerce.inventory_service.dto.OrderRequestItemDto;
import com.project.ecommerce.inventory_service.dto.ProductDto;
import com.project.ecommerce.inventory_service.dto.RestockItemRequest;
import com.project.ecommerce.inventory_service.entity.Product;
import com.project.ecommerce.inventory_service.repository.ProductRepository;
import com.project.ecommerce.inventory_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {


    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;


    @Override
    public List<ProductDto> getAllInventory() {
       log.info("Fetching all inventory items");
       List<Product> inventories = productRepository.findAll();
       return inventories.stream()
               .map(product -> modelMapper.map(product,ProductDto.class))
               .toList();
    }



    @Override
    public ProductDto getProductById(Long id) {
        log.info("Fetching Product with ID: {}", id);
        Optional<Product> inventory = productRepository.findById(id);
        return inventory.map(item -> modelMapper.map(item, ProductDto.class))
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
    }

    @Override
    @Transactional
    public Double reduceStock(OrderRequestDto orderRequestDto) {
        log.info("Reduce the stocks");
        Double totalPrice = 0.0;
        for(OrderRequestItemDto orderRequestItemDto: orderRequestDto.getItems()) {
            Long productId = orderRequestItemDto.getProductId();
            Integer quantity = orderRequestItemDto.getQuantity();

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found with id : "+ productId));

            if (product.getStock() < quantity) {
                throw new RuntimeException("Product cannnot be fulfilled for given quantity");
            }

            product.setStock(product.getStock() - quantity);
            productRepository.save(product);
            totalPrice += quantity * product.getPrice();
        }
        return totalPrice;
    }

    @Override
    public void restoreStock(List<RestockItemRequest> restockItemRequests) {

        for (RestockItemRequest item: restockItemRequests) {
            Long productId = item.getProductId();
            Integer productQuantity = item.getQuantity();
            Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product does not exists"));
            product.setStock(product.getStock() + productQuantity);
            productRepository.save(product);
        }
    }
}
