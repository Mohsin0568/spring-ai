package com.systa.tools;

import com.systa.domain.PurchaseOrderSearchRequest;
import com.systa.service.PurchaseOrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@AllArgsConstructor
public class PurchaseOrderTool {

    private final PurchaseOrderService purchaseOrderService;

    @Tool(
            name = "search_purchase_orders",
            description = "Search purchase orders using structured filters like supplier, product, dates, and vehicle details",
            returnDirect = true
    )
    public List<PurchaseOrderDomain> searchPurchaseOrders(PurchaseOrderSearchRequest request) {
        log.info("Received search request: {}", request);
        return List.of(
                new PurchaseOrderDomain("1", "PO-12345"),
                new PurchaseOrderDomain("2", "PO-67890")
        );
    }
}
