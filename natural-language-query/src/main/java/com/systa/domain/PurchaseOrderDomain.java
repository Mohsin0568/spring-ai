package com.systa.domain;

import java.time.LocalDateTime;
import java.util.List;

public record PurchaseOrderDomain(
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
