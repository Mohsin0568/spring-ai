package com.systa.domain;

import org.springframework.lang.Nullable;

public record CustomerOrderSearchRequest(
        @Nullable String orderId,

        // Customer
        @Nullable String customerId,
        @Nullable String customerName,

        // Contact Details
        @Nullable String email,
        @Nullable String phone,

        // Delivery Address
        @Nullable String postCode,
        @Nullable String city,
        @Nullable String country,

        // Order Items
        @Nullable String productName,
        @Nullable String productId,
        @Nullable Integer minQuantity,
        @Nullable Integer maxQuantity,

        // Status
        @Nullable String orderStatus,

        // Date filters
        @Nullable String deliveryDateFrom,
        @Nullable String deliveryDateTo,
        @Nullable String orderPlacementFrom,
        @Nullable String orderPlacementTo,

        // Optional controls
        @Nullable Integer limit,
        @Nullable String sortBy,
        @Nullable String sortDirection

) {
}
