package com.systa.domain;

public record DeliveryAddress(
        String postCode,
        String city,
        String country) {
}
