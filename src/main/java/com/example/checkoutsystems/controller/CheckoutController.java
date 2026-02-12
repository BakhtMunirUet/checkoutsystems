package com.example.checkoutsystems.controller;

import com.example.checkoutsystems.dto.CartItemDto;
import com.example.checkoutsystems.dto.CheckoutDto;
import com.example.checkoutsystems.dto.CheckoutResponseDto;
import com.example.checkoutsystems.services.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    @Autowired
    private CheckoutService checkoutService;

    @PostMapping
    public ResponseEntity<CheckoutDto> checkout(@RequestBody CheckoutDto dto){
        checkoutService.checkout(dto);
        return  ResponseEntity.ok(dto);

    }
}
