package com.example.checkoutsystems.dto;

import jakarta.validation.Valid;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record CheckoutDto(
        Long id,
        BigDecimal totalPrice,

        BigDecimal totalDiscount,
        @Valid List<CheckoutItemDto> items) {
}
