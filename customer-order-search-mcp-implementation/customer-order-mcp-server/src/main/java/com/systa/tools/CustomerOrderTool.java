package com.systa.tools;

import com.systa.domain.CustomerOrderDomain;
import com.systa.domain.CustomerOrderSearchRequest;
import com.systa.service.CustomerOrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@AllArgsConstructor
public class CustomerOrderTool {

    private final CustomerOrderService customerOrderService;

    @Tool(
            name = "search_customer_orders",
            description = "Search customer orders using structured filters like customer, product, dates, delivery address and contact details",
            returnDirect = true
    )
    public List<CustomerOrderDomain> searchCustomerOrders(CustomerOrderSearchRequest request) {
        log.info("Received search request: {}", request);
        return customerOrderService.getCustomerOrderDetails(request);
    }
}
