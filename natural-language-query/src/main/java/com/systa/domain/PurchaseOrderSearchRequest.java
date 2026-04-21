package com.systa.domain;

public record PurchaseOrderSearchRequest(
         String poNumber,
         String poId,
         String bookingNumber,
         Integer vehicleNumber,
         Integer chamberNumber,
         String vehicleDepotIdentifier,

        // Supplier
         String supplierName,
         String supplierLegacyId,
         String supplierStrategicId,

        // Location
         String shipToLocationLegacyId,
         String shipToLocationStrategicId,

        // Order lines
         String productName,
         Integer minQuantity,
         Integer maxQuantity,

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
