package com.systa.util;

import com.systa.domain.OrderLine;
import com.systa.domain.PurchaseOrderDomain;
import com.systa.domain.ShipToLocation;
import com.systa.domain.Supplier;
import com.systa.entity.PurchaseOrder;

import java.util.List;
import java.util.stream.Collectors;

public class PurchaseOrderMapper {

    public static PurchaseOrderDomain toDomain(final PurchaseOrder entity) {
        return new PurchaseOrderDomain(
                entity.id(),
                entity.entityIdentificationNumber(),
                entity.poNumber(),
                entity.poId(),
                entity.bookingNumber(),
                entity.vehicleNumber(),
                entity.chamberNumber(),
                entity.deliveryDateTime(),
                entity.orderPlacementDate(),
                toDomain(entity.supplier()),
                toDomain(entity.shipToLocation()),
                entity.orderLines().stream().map(PurchaseOrderMapper::toDomain).collect(Collectors.toList()),
                entity.vehicleDepotIdentifier()
        );
    }

    public static List<PurchaseOrderDomain> toDomain(final List<PurchaseOrder> entities) {
        return entities.stream().map(PurchaseOrderMapper::toDomain).collect(Collectors.toList());
    }

    private static Supplier toDomain(com.systa.entity.Supplier entity) {
        return new Supplier(
                entity.legacyId(),
                entity.strategicId(),
                entity.name()
        );
    }

    private static ShipToLocation toDomain(com.systa.entity.ShipToLocation entity) {
        return new ShipToLocation(
                entity.legacyId(),
                entity.strategicId()
        );
    }

    private static OrderLine toDomain(com.systa.entity.OrderLine entity) {
        return new OrderLine(
                entity.lineNumber(),
                entity.productName(),
                entity.legacyProductId(),
                entity.strategicProductId(),
                entity.packSize(),
                entity.quantity()
        );
    }
}
