package com.example.checkoutsystems.controller;


import com.example.checkoutsystems.dto.CheckoutDto;
import com.example.checkoutsystems.dto.CheckoutItemDto;
import com.example.checkoutsystems.services.CheckoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;




@WebMvcTest(CheckoutController.class)
public class CheckoutControllerTest{
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CheckoutService checkoutService;

    private CheckoutItemDto itemDto;
    private CheckoutDto responseDto;

    @BeforeEach
    void setup() {
        itemDto = CheckoutItemDto.builder()
                .id(1L)
                .itemId(1L)
                .itemName("apple")
                .quantity(2L)
                .unitPrice(BigDecimal.valueOf(0.15))
                .totalPrice(BigDecimal.valueOf(0.30))
                .originalTotalPrice(BigDecimal.valueOf(0.30))
                .discountApplied(BigDecimal.ZERO)
                .timesOfferApplied(0L)
                .build();

        responseDto = CheckoutDto.builder()
                .id(1L)
                .totalPrice(BigDecimal.valueOf(0.30))
                .totalDiscount(BigDecimal.ZERO)
                .items(List.of(itemDto))
                .build();
    }

    @Test
    void givenValidCheckoutRequest_whenCheckout_thenReturnCheckoutDto() throws Exception {
        when(checkoutService.checkout(any())).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "items": [
                                    {
                                      "id": 1,
                                      "itemId": 1,
                                      "itemName": "apple",
                                      "quantity": 2,
                                      "unitPrice": 0.15,
                                      "totalPrice": 0.30,
                                      "originalTotalPrice": 0.30,
                                      "discountApplied": 0.00,
                                      "timesOfferApplied": 0
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.totalPrice").value(0.30))
                .andExpect(jsonPath("$.totalDiscount").value(0.00))
                .andExpect(jsonPath("$.items[0].itemName").value("apple"))
                .andExpect(jsonPath("$.items[0].quantity").value(2));
    }

    @Test
    void givenEmptyItemList_whenCheckout_thenReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "items": []
                                }
                                """))
                .andExpect(status().isBadRequest());


    }

    @Test
    void givenItemWithNullQuantity_whenCheckout_thenReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "items": [
                                    {
                                      "id": 1,
                                      "itemId": 1,
                                      "itemName": "apple",
                                      "quantity": null
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenItemWithNullItemId_whenCheckout_thenReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "items": [
                                    {
                                      "id": 1,
                                      "itemId": null,
                                      "itemName": "apple",
                                      "quantity": 3
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

}
