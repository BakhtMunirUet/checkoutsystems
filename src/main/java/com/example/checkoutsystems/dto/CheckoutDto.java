package com.example.checkoutsystems.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record CheckoutDto(
        Long id,
        BigDecimal totalPrice,

        BigDecimal totalDiscount,

        @NotNull(message = "Item List can not be null")
        @NotEmpty(message = "Item List can not be empty")
        @Valid
        List<CheckoutItemDto> items) {
}
