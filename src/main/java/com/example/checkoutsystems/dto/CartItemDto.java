package com.example.checkoutsystems.dto;


import lombok.Builder;

@Builder
public record CartItemDto(Long itemId, String name, Long quantity) {
}
