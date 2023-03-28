package com.example.orderservice.service;


import com.example.orderservice.OrderServiceApplication;
import com.example.orderservice.dto.InventoryResponse;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.OrderlineItemsDto;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderLineItems;
import com.example.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {


    private final OrderRepository orderRepository;
    private final WebClient webClient;

    public void placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        
    List<OrderLineItems> orderLineItems= orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

    order.setOrderLineItemsList(orderLineItems);

    List<String> skuCodes = order.getOrderLineItemsList().stream()
            .map(OrderLineItems::getSkuCode)
            .toList();

   InventoryResponse[] inventoryResponseArray =  webClient.get()
            .uri("http://localhost:8082/api/inventory",uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                    .retrieve()
                            .bodyToMono(InventoryResponse[].class)
           .block();

   boolean allProductsInStock = Arrays.stream(inventoryResponseArray)
                .allMatch(InventoryResponse::isInStock);

    if(allProductsInStock){
        orderRepository.save(order);
    }else {
        throw new IllegalArgumentException("Product is not in stock, please try again");
    }


    }

    private OrderLineItems mapToDto(OrderlineItemsDto orderlineItemsDto) {

        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderlineItemsDto.getPrice());
        orderLineItems.setQuantity(orderlineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderlineItemsDto.getSkuCode());
        return orderLineItems;



    }

}
