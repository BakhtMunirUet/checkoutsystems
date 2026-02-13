package com.example.checkoutsystems.dto;


import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CheckoutItemDto(

        Long id,
        Long itemId,
        String itemName,
        Long quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice,

        BigDecimal originalTotalPrice,
        BigDecimal discountApplied,
        Long timesOfferApplied

        ) { }
