package com.systa.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "customer_orders")
public record CustomerOrder(
                     @Id
                     String id,
                     String orderId,
                     LocalDateTime deliveryDateTime,
                     LocalDateTime orderPlacementDateTime,
                     Customer customer,
                     ContactDetails contactDetails,
                     DeliveryAddress deliveryAddress,
                     List<OrderItem> orderItems,
                     String orderStatus) {
}
