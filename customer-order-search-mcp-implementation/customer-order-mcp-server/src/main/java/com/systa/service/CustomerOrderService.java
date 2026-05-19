package com.systa.service;

import com.systa.domain.CustomerOrderDomain;
import com.systa.domain.CustomerOrderSearchRequest;

import java.util.List;

public interface CustomerOrderService {
        List<CustomerOrderDomain> getCustomerOrderDetails(final CustomerOrderSearchRequest searchRequest);
}
