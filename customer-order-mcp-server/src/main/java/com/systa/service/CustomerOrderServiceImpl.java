package com.systa.service;

import com.systa.domain.CustomerOrderDomain;
import com.systa.domain.CustomerOrderSearchRequest;
import com.systa.entity.CustomerOrder;
import com.systa.util.CustomerOrderMapper;
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
public class CustomerOrderServiceImpl implements CustomerOrderService {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<CustomerOrderDomain> getCustomerOrderDetails(final CustomerOrderSearchRequest searchRequest) {

        Query query = new Query();

        // Top-level
        if (isNotEmpty(searchRequest.orderId())) {
            query.addCriteria(Criteria.where("orderId").is(searchRequest.orderId()));
        }

        // Customer
        if (isNotEmpty(searchRequest.customerName())) {
            query.addCriteria(caseInsensitiveContains("customer.name", searchRequest.customerName()));
        }

        if (isNotEmpty(searchRequest.customerId())) {
            query.addCriteria(Criteria.where("customer.customerId").is(searchRequest.customerId()));
        }

        // Contact Details
        if (isNotEmpty(searchRequest.email())) {
            query.addCriteria(caseInsensitiveExact("contactDetails.email", searchRequest.email()));
        }

        if (isNotEmpty(searchRequest.phone())) {
            query.addCriteria(Criteria.where("contactDetails.phone").is(searchRequest.phone()));
        }

        // Delivery Address
        if (isNotEmpty(searchRequest.postCode())) {
            query.addCriteria(Criteria.where("deliveryAddress.postCode").is(searchRequest.postCode()));
        }

        if (isNotEmpty(searchRequest.city())) {
            query.addCriteria(caseInsensitiveExact("deliveryAddress.city", searchRequest.city()));
        }

        if (isNotEmpty(searchRequest.country())) {
            query.addCriteria(caseInsensitiveExact("deliveryAddress.country", searchRequest.country()));
        }

        // Order Status
        if (isNotEmpty(searchRequest.orderStatus())) {
            query.addCriteria(caseInsensitiveExact("orderStatus", searchRequest.orderStatus()));
        }

        // Product (array)
        if (isNotEmpty(searchRequest.productName())) {
            query.addCriteria(caseInsensitiveExact("orderItems.productName", searchRequest.productName()));
        }

        if (isNotEmpty(searchRequest.productId())) {
            query.addCriteria(Criteria.where("orderItems.productId").is(searchRequest.productId()));
        }

        // Quantity range
        if (isValidQuantity(searchRequest.minQuantity()) || isValidQuantity(searchRequest.maxQuantity())) {
            Criteria qtyCriteria = Criteria.where("orderItems.quantity");

            if (isValidQuantity(searchRequest.minQuantity())) {
                qtyCriteria.gte(searchRequest.minQuantity());
            }

            if (isValidQuantity(searchRequest.maxQuantity())) {
                qtyCriteria.lte(searchRequest.maxQuantity());
            }

            query.addCriteria(qtyCriteria);
        }

        // Date range
        if (isNotEmpty(searchRequest.deliveryDateFrom()) || isNotEmpty(searchRequest.deliveryDateTo())) {

            Criteria dateCriteria = Criteria.where("deliveryDateTime");

            if (isNotEmpty(searchRequest.deliveryDateFrom())) {
                dateCriteria.gte(searchRequest.deliveryDateFrom());
            }

            if (isNotEmpty(searchRequest.deliveryDateTo())) {
                dateCriteria.lte(searchRequest.deliveryDateTo());
            }

            query.addCriteria(dateCriteria);
        }

        if (isNotEmpty(searchRequest.orderPlacementFrom()) || isNotEmpty(searchRequest.orderPlacementTo())) {

            Criteria dateCriteria = Criteria.where("orderPlacementDateTime");

            if (isNotEmpty(searchRequest.orderPlacementFrom())) {
                dateCriteria.gte(searchRequest.orderPlacementFrom());
            }

            if (isNotEmpty(searchRequest.orderPlacementTo())) {
                dateCriteria.lte(searchRequest.orderPlacementTo());
            }

            query.addCriteria(dateCriteria);
        }

        // Sorting
        if (isNotEmpty(searchRequest.sortBy())) {
            Sort.Direction dir = "desc".equalsIgnoreCase(searchRequest.sortDirection())
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;

            query.with(Sort.by(dir, searchRequest.sortBy()));
        }

        // Limit
        if (searchRequest.limit() != null) {
            query.limit(searchRequest.limit());
        }

        final List<CustomerOrder> customerOrders = mongoTemplate.find(query, CustomerOrder.class);
        return CustomerOrderMapper.toDomain(customerOrders);
    }

    private boolean isNotEmpty(final String text){
        return text != null && !text.isEmpty();
    }

    private boolean isValidQuantity(final Integer quantity) {
        return quantity != null && quantity != 0;
    }

    private Criteria caseInsensitiveExact(String field, String value) {
        return Criteria.where(field)
                .regex("^" + Pattern.quote(value) + "$", "i");
    }

    private Criteria caseInsensitiveContains(String field, String value) {
        String safe = Pattern.quote(value);
        return Criteria.where(field).regex(".*" + safe + ".*", "i");
    }
}
