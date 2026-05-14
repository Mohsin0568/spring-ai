package com.systa.util;

import com.systa.domain.ContactDetails;
import com.systa.domain.Customer;
import com.systa.domain.CustomerOrderDomain;
import com.systa.domain.DeliveryAddress;
import com.systa.domain.OrderItem;
import com.systa.entity.CustomerOrder;

import java.util.List;
import java.util.stream.Collectors;

public class CustomerOrderMapper {

    public static CustomerOrderDomain toDomain(final CustomerOrder entity) {
        return new CustomerOrderDomain(
                entity.id(),
                entity.orderId(),
                entity.deliveryDateTime(),
                entity.orderPlacementDateTime(),
                toDomain(entity.customer()),
                toDomain(entity.contactDetails()),
                toDomain(entity.deliveryAddress()),
                entity.orderItems().stream().map(CustomerOrderMapper::toDomain).collect(Collectors.toList()),
                entity.orderStatus()
        );
    }

    public static List<CustomerOrderDomain> toDomain(final List<CustomerOrder> entities) {
        return entities.stream().map(CustomerOrderMapper::toDomain).collect(Collectors.toList());
    }

    private static Customer toDomain(com.systa.entity.Customer entity) {
        return new Customer(
                entity.customerId(),
                entity.name()
        );
    }

    private static DeliveryAddress toDomain(com.systa.entity.DeliveryAddress entity) {
        return new DeliveryAddress(
                entity.postCode(),
                entity.city(),
                entity.country()
        );
    }

    private static ContactDetails toDomain(com.systa.entity.ContactDetails entity) {
        return new ContactDetails(
                entity.email(),
                entity.phone()
        );
    }

    private static OrderItem toDomain(com.systa.entity.OrderItem entity) {
        return new OrderItem(
                entity.lineNumber(),
                entity.productName(),
                entity.productId(),
                entity.quantity()
        );
    }
}
