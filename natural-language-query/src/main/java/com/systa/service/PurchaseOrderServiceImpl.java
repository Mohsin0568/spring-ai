package com.systa.service;

import com.systa.domain.PurchaseOrderDomain;
import com.systa.domain.PurchaseOrderSearchRequest;
import com.systa.entity.PurchaseOrder;
import com.systa.util.PurchaseOrderMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<PurchaseOrderDomain> getPurchaseOrderDetails(final PurchaseOrderSearchRequest searchRequest) {

        Query query = new Query();

        // Top-level
        if (searchRequest.poNumber() != null) {
            query.addCriteria(Criteria.where("poNumber").is(searchRequest.poNumber()));
        }

        if (searchRequest.poId() != null) {
            query.addCriteria(Criteria.where("poId").is(searchRequest.poId()));
        }

        if (searchRequest.bookingNumber() != null) {
            query.addCriteria(caseInsensitiveExact("bookingNumber", searchRequest.bookingNumber()));
        }

        if (searchRequest.vehicleNumber() != null) {
            query.addCriteria(Criteria.where("vehicleNumber").is(searchRequest.vehicleNumber()));
        }

        // Supplier
        if (searchRequest.supplierName() != null) {
            query.addCriteria(caseInsensitiveExact("supplier.name", searchRequest.supplierName()));
        }

        if (searchRequest.supplierLegacyId() != null) {
            query.addCriteria(Criteria.where("supplier.legacyId").is(searchRequest.supplierLegacyId()));
        }

        // Location
        if (searchRequest.shipToLocationLegacyId() != null) {
            query.addCriteria(Criteria.where("shipToLocation.legacyId")
                    .is(searchRequest.shipToLocationLegacyId()));
        }

        // Product (array)
        if (searchRequest.productName() != null) {
            query.addCriteria(caseInsensitiveExact("orderLines.productName", searchRequest.productName()));
        }

        // Quantity range
        if (searchRequest.minQuantity() != null || searchRequest.maxQuantity() != null) {
            Criteria qtyCriteria = Criteria.where("orderLines.quantity");

            if (searchRequest.minQuantity() != null) {
                qtyCriteria.gte(searchRequest.minQuantity());
            }

            if (searchRequest.maxQuantity() != null) {
                qtyCriteria.lte(searchRequest.maxQuantity());
            }

            query.addCriteria(qtyCriteria);
        }

        // Date range
        if (searchRequest.deliveryDateFrom() != null || searchRequest.deliveryDateTo() != null) {

            Criteria dateCriteria = Criteria.where("deliveryDateTime");

            if (searchRequest.deliveryDateFrom() != null) {
                dateCriteria.gte(searchRequest.deliveryDateFrom());
            }

            if (searchRequest.deliveryDateTo() != null) {
                dateCriteria.lte(searchRequest.deliveryDateTo());
            }

            query.addCriteria(dateCriteria);
        }

        // Sorting
        if (searchRequest.sortBy() != null) {
            Sort.Direction dir = "desc".equalsIgnoreCase(searchRequest.sortDirection())
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;

            query.with(Sort.by(dir, searchRequest.sortBy()));
        }

        // Limit
        if (searchRequest.limit() != null) {
            query.limit(searchRequest.limit());
        }

        final List<PurchaseOrder> purchaseOrders = mongoTemplate.find(query, PurchaseOrder.class);
        return PurchaseOrderMapper.toDomain(purchaseOrders);
    }

    private Criteria caseInsensitiveExact(String field, String value) {
        return Criteria.where(field)
                .regex("^" + Pattern.quote(value) + "$", "i");
    }
}
