package com.systa.domain;

public record OrderLine(

         Integer lineNumber,
         String productName,
         String legacyProductId,
         String strategicProductId,
         Integer packSize,
         Integer quantity) {
}
