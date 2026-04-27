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
        if (searchRequest.orderId() != null) {
            query.addCriteria(Criteria.where("orderId").is(searchRequest.orderId()));
        }

        // Customer
        if (searchRequest.customerName() != null) {
            query.addCriteria(caseInsensitiveExact("customer.name", searchRequest.customerName()));
        }

        if (searchRequest.customerId() != null) {
            query.addCriteria(Criteria.where("customer.customerId").is(searchRequest.customerId()));
        }

        // Contact Details
        if (searchRequest.email() != null) {
            query.addCriteria(caseInsensitiveExact("contactDetails.email", searchRequest.email()));
        }

        if (searchRequest.phone() != null) {
            query.addCriteria(Criteria.where("contactDetails.phone").is(searchRequest.phone()));
        }

        // Delivery Address
        if (searchRequest.postCode() != null) {
            query.addCriteria(Criteria.where("deliveryAddress.postCode").is(searchRequest.postCode()));
        }

        if (searchRequest.city() != null) {
            query.addCriteria(caseInsensitiveExact("deliveryAddress.city", searchRequest.city()));
        }

        if (searchRequest.country() != null) {
            query.addCriteria(caseInsensitiveExact("deliveryAddress.country", searchRequest.country()));
        }

        // Order Status
        if (searchRequest.orderStatus() != null) {
            query.addCriteria(caseInsensitiveExact("orderStatus", searchRequest.orderStatus()));
        }

        // Product (array)
        if (searchRequest.productName() != null) {
            query.addCriteria(caseInsensitiveExact("orderItems.productName", searchRequest.productName()));
        }

        if (searchRequest.productId() != null) {
            query.addCriteria(Criteria.where("orderItems.productId").is(searchRequest.productId()));
        }

        // Quantity range
        if (searchRequest.minQuantity() != null || searchRequest.maxQuantity() != null) {
            Criteria qtyCriteria = Criteria.where("orderItems.quantity");

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

        if (searchRequest.orderPlacementFrom() != null || searchRequest.orderPlacementTo() != null) {

            Criteria dateCriteria = Criteria.where("orderPlacementDateTime");

            if (searchRequest.orderPlacementFrom() != null) {
                dateCriteria.gte(searchRequest.orderPlacementFrom());
            }

            if (searchRequest.orderPlacementTo() != null) {
                dateCriteria.lte(searchRequest.orderPlacementTo());
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

        final List<CustomerOrder> customerOrders = mongoTemplate.find(query, CustomerOrder.class);
        return CustomerOrderMapper.toDomain(customerOrders);
    }

    private Criteria caseInsensitiveExact(String field, String value) {
        return Criteria.where(field)
                .regex("^" + Pattern.quote(value) + "$", "i");
    }
}
