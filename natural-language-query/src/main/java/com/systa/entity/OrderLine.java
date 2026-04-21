package com.systa.entity;

public record OrderLine(
        
         Integer lineNumber,
         String productName,
         String legacyProductId,
         String strategicProductId,
         Integer packSize,
         Integer quantity) {
}
