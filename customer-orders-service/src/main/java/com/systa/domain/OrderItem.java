package com.systa.domain;

public record OrderItem(

         Integer lineNumber,
         String productName,
         String productId,
         Integer quantity) {
}
