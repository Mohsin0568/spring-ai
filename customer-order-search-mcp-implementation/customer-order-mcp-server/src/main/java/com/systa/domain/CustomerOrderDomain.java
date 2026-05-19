package com.systa.domain;

import java.time.LocalDateTime;
import java.util.List;

public record CustomerOrderDomain(
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
