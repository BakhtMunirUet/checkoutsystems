package com.example.checkoutsystems.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CheckoutItemDto(

        Long id,
        @NotNull(message = "Item id should not be null")
        Long itemId,
        String itemName,
        @NotNull(message = "Quantity should not be null")
        Long quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice,

        BigDecimal originalTotalPrice,
        BigDecimal discountApplied,
        Long timesOfferApplied

        ) { }
