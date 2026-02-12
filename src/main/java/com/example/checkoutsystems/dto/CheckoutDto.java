package com.example.checkoutsystems.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record CheckoutDto(List<CartItemDto> cartItemDtoList) {
}
