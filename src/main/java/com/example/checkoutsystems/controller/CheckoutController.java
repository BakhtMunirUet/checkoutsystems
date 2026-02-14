package com.example.checkoutsystems.controller;

import com.example.checkoutsystems.dto.CheckoutDto;
import com.example.checkoutsystems.services.CheckoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api/v1/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    @Autowired
    private CheckoutService checkoutService;

    @PostMapping
    public ResponseEntity<CheckoutDto> checkout(@Valid @RequestBody CheckoutDto dto){
        return  ResponseEntity.ok(checkoutService.checkout(dto));
    }
}
