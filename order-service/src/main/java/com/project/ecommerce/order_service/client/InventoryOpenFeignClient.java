package com.project.ecommerce.order_service.client;

import com.project.ecommerce.order_service.dto.OrderRequestDto;
import com.project.ecommerce.order_service.dto.RestockItemRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "inventory-service", path = "inventory")
public interface InventoryOpenFeignClient {

    @PutMapping("/products/reduce-stocks")
    Double reduceStocks(@RequestBody OrderRequestDto orderRequestDto);

    @PutMapping("/products/restore-stocks")
    Void restoreStock(@RequestBody List<RestockItemRequest> restockItemRequests);
}
