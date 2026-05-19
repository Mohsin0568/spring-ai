package com.systa.entity;

public record OrderItem(
        
         Integer lineNumber,
         String productName,
         String productId,
         Integer quantity) {
}
