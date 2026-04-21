package com.systa.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "purchase_orders")
public record PurchaseOrder(
                     @Id
                     String id,
                     String entityIdentificationNumber,
                     String poNumber,
                     String poId,
                     String bookingNumber,
                     Integer vehicleNumber,
                     Integer chamberNumber,
                     LocalDateTime deliveryDateTime,
                     LocalDateTime orderPlacementDate,
                     Supplier supplier,
                     ShipToLocation shipToLocation,
                     List<OrderLine> orderLines,
                     String vehicleDepotIdentifier) {
}
