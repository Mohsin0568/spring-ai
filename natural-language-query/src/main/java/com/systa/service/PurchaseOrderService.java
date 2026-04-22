package com.systa.service;

import com.systa.domain.PurchaseOrderDomain;
import com.systa.domain.PurchaseOrderSearchRequest;

import java.util.List;

public interface PurchaseOrderService {
        List<PurchaseOrderDomain> getPurchaseOrderDetails(final PurchaseOrderSearchRequest searchRequest);
}
