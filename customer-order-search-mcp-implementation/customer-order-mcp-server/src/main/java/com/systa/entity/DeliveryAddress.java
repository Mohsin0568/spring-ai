package com.systa.entity;

public record DeliveryAddress(
        String postCode,
        String city,
        String country) {
}
