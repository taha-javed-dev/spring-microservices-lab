package com.project.ecommerce.order_service.service;

import com.project.ecommerce.order_service.client.InventoryOpenFeignClient;
import com.project.ecommerce.order_service.dto.OrderRequestDto;
import com.project.ecommerce.order_service.dto.RestockItemRequest;
import com.project.ecommerce.order_service.entity.OrderItem;
import com.project.ecommerce.order_service.entity.OrderStatus;
import com.project.ecommerce.order_service.entity.Orders;
import com.project.ecommerce.order_service.repository.OrdersRepository;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
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
public class OrderService {

    private final OrdersRepository ordersRepository;
    private final InventoryOpenFeignClient inventoryOpenFeignClient;
    private final ModelMapper modelMapper;

    public List<OrderRequestDto> getAllOrders() {
        log.info("Fetching all orders");
        List<Orders> orders = ordersRepository.findAll();
        return orders.stream().map(order -> modelMapper.map(order, OrderRequestDto.class)).toList();

    }

    public OrderRequestDto getOrderById(Long id) {
        log.info("Fetching order with ID: {}", id);
        Orders order = ordersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return modelMapper.map(order, OrderRequestDto.class);
    }

    @Retry(name = "inventoryRetry", fallbackMethod = "createOrderFallback")
    @RateLimiter(name = "inventoryRateLimiter", fallbackMethod = "createOrderFallback")
    public OrderRequestDto createOrder(OrderRequestDto orderRequestDto) {
        log.info("Calling the createOrder method");
        Double totalPrice = inventoryOpenFeignClient.reduceStocks(orderRequestDto);

        Orders orders = modelMapper.map(orderRequestDto, Orders.class);
        for(OrderItem orderItem: orders.getItems()) {
            orderItem.setOrder(orders);
        }
        orders.setTotalPrice(totalPrice);
        orders.setOrderStatus(OrderStatus.CONFIRMED);

        Orders savedOrder = ordersRepository.save(orders);

        return modelMapper.map(savedOrder, OrderRequestDto.class);
    }

    public OrderRequestDto createOrderFallback(OrderRequestDto orderRequestDto, Throwable throwable) {
        log.error("Fallback occurred due to : {}", throwable.getMessage());
        return new OrderRequestDto();
    }

    public void cancelOrder(Long id) {
        Orders orders = ordersRepository.findById(id).orElseThrow(() -> new RuntimeException("Order does not exists"));
        if (orders.getOrderStatus() == OrderStatus.CANCELLED || orders.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("Order is already cancelled");
        }

        List<OrderItem> items = orders.getItems();
        List<RestockItemRequest> restockItemRequests = items.stream()
                .map((item) -> new RestockItemRequest(
                        item.getProductId(),
                        item.getQuantity()))
                        .toList();
        inventoryOpenFeignClient.restoreStock(restockItemRequests);
        orders.setOrderStatus(OrderStatus.CANCELLED);
        ordersRepository.save(orders);
    }
}
