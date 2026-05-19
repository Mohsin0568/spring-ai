package com.systa.domain;

public record CustomerOrderSearchRequest(
         String orderId,

        // Customer
         String customerId,
         String customerName,

        // Contact Details
         String email,
         String phone,

        // Delivery Address
         String postCode,
         String city,
         String country,

        // Order Items
         String productName,
         String productId,
         Integer minQuantity,
         Integer maxQuantity,

        // Status
         String orderStatus,

        // Date filters
         String deliveryDateFrom,
         String deliveryDateTo,
         String orderPlacementFrom,
         String orderPlacementTo,

        // Optional controls
         Integer limit,
         String sortBy,
         String sortDirection

) {
}
