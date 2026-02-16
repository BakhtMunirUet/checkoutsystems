package com.example.checkoutsystems.services.impl;

import com.example.checkoutsystems.dao.entity.CheckoutEntity;
import com.example.checkoutsystems.dao.entity.ItemEntity;
import com.example.checkoutsystems.dao.entity.OfferEntity;
import com.example.checkoutsystems.dao.repository.CheckoutRepository;
import com.example.checkoutsystems.dao.repository.ItemRepository;
import com.example.checkoutsystems.dao.repository.OfferRepository;
import com.example.checkoutsystems.dto.CheckoutDto;
import com.example.checkoutsystems.dto.CheckoutItemDto;
import com.example.checkoutsystems.mapper.CheckoutMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private CheckoutRepository checkoutRepository;

    @Mock
    private CheckoutMapper checkoutMapper;

    @InjectMocks
    private CheckoutServiceImpl checkoutService;

    private ItemEntity apple;
    private OfferEntity appleOffer;
    private LocalDateTime now;
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this); // Initialize mocks

        now = LocalDateTime.now();

        apple = ItemEntity.builder()
                .id(1L)
                .name("Apple")
                .price(BigDecimal.valueOf(0.30))
                .build();

        appleOffer = OfferEntity.builder()
                .id(1L)
                .item(apple)
                .quantity(2L)
                .offerPrice(BigDecimal.valueOf(0.45))
                .startDate(now.minusDays(1))
                .endDate(now.plusDays(1))
                .build();
    }

    @Test
    void givenItemWithoutOffer_whenCheckout_thenReturnFullPrice() {
        CheckoutItemDto itemDto = CheckoutItemDto.builder()
                .id(1L)
                .itemId(1L)
                .itemName("apple")
                .quantity(2L)
                .build();


        when(itemRepository.findById(1L)).thenReturn(Optional.of(apple));
        when(offerRepository.findByItemId(1L)).thenReturn(Optional.empty());
        when(checkoutRepository.save(any(CheckoutEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(checkoutMapper.checkoutToDo(any()))
                .thenReturn(new CheckoutDto(1L, BigDecimal.valueOf(0.30), BigDecimal.ZERO, List.of(itemDto)));

        CheckoutDto result = checkoutService.checkout(
                CheckoutDto.builder()
                        .items(List.of(itemDto))
                        .build()
        );

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(0.30), result.totalPrice());
        assertEquals(BigDecimal.ZERO, result.totalDiscount());
    }


    @Test
    void givenItemWithActiveOffer_whenCheckout_thenApplyDiscount() {
        CheckoutItemDto itemDto = CheckoutItemDto.builder()
                .id(1L)
                .itemId(1L)
                .itemName("apple")
                .quantity(2L)
                .build();

        when(itemRepository.findById(1L)).thenReturn(Optional.of(apple));
        when(offerRepository.findByItemId(1L)).thenReturn(Optional.of(appleOffer));
        when(checkoutRepository.save(any(CheckoutEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(checkoutMapper.checkoutToDo(any()))
                .thenReturn(new CheckoutDto(1L, BigDecimal.valueOf(0.45), BigDecimal.valueOf(0.15), List.of(itemDto)));

        CheckoutDto result = checkoutService.checkout(
                CheckoutDto.builder()
                        .items(List.of(itemDto))
                        .build()
        );

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(0.45), result.totalPrice());
        assertEquals(BigDecimal.valueOf(0.15), result.totalDiscount());
    }

    @Test
    void givenEmptyCart_whenCheckout_thenReturnZeroTotals() {
        CheckoutDto input = CheckoutDto.builder()
                .items(List.of())
                .build();

        when(checkoutMapper.checkoutToDo(any()))
                .thenReturn(CheckoutDto.builder()
                        .items(List.of())
                        .totalDiscount(BigDecimal.valueOf(0.0))
                        .totalPrice(BigDecimal.valueOf(0.0))
                        .build());


        CheckoutDto result = checkoutService.checkout(input);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(0.0), result.totalPrice());
        assertEquals(BigDecimal.valueOf(0.0), result.totalDiscount());
        assertTrue(result.items().isEmpty());
    }

    @Test
    void givenNonExistentItem_whenCheckout_thenThrowException() {
        CheckoutItemDto itemDto = CheckoutItemDto.builder()
                .id(1L)
                .itemId(1L)
                .itemName("apple")
                .quantity(2L)
                .build();

        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                checkoutService.checkout(
                        CheckoutDto.builder().items(List.of(itemDto)).build()
                )
        );
    }

    @Test
    void givenExpiredOffer_whenCheckout_thenIgnoreDiscount() {
        CheckoutItemDto itemDto = CheckoutItemDto.builder()
                .id(1L)
                .itemId(1L)
                .itemName("apple")
                .quantity(2L)
                .build();

        OfferEntity expiredOffer = OfferEntity.builder()
                .id(1L)
                .item(apple)
                .quantity(2L)
                .offerPrice(BigDecimal.valueOf(0.45))
                .startDate(now.minusDays(5))
                .endDate(now.minusDays(1))
                .build();

        when(itemRepository.findById(1L)).thenReturn(Optional.of(apple));
        when(offerRepository.findByItemId(1L)).thenReturn(Optional.of(expiredOffer));
        when(checkoutRepository.save(any(CheckoutEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(checkoutMapper.checkoutToDo(any()))
                .thenReturn(new CheckoutDto(1L, BigDecimal.valueOf(0.60), BigDecimal.valueOf(0.0), List.of(itemDto)));

        CheckoutDto result = checkoutService.checkout(
                CheckoutDto.builder()
                        .items(List.of(itemDto))
                        .build()
        );

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(0.60), result.totalPrice());
        assertEquals(BigDecimal.valueOf(0.0), result.totalDiscount());
    }

    @Test
    void givenMultipleItemsWithMixedOffers_whenCheckout_thenCalculateTotalsCorrectly() {
        ItemEntity banana = ItemEntity.builder()
                .id(2L)
                .name("Banana")
                .price(BigDecimal.valueOf(0.20))
                .build();

        OfferEntity bananaOffer = OfferEntity.builder()
                .id(2L)
                .item(banana)
                .quantity(3L)
                .offerPrice(BigDecimal.valueOf(0.50))
                .startDate(now.minusDays(1))
                .endDate(now.plusDays(1))
                .build();

        CheckoutItemDto appleDto = CheckoutItemDto.builder()
                .id(1L).itemId(1L).itemName("apple").quantity(2L).build();

        CheckoutItemDto bananaDto = CheckoutItemDto.builder()
                .id(2L).itemId(2L).itemName("banana").quantity(3L).build();

        when(itemRepository.findById(1L)).thenReturn(Optional.of(apple));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(banana));
        when(offerRepository.findByItemId(1L)).thenReturn(Optional.empty());
        when(offerRepository.findByItemId(2L)).thenReturn(Optional.of(bananaOffer));
        when(checkoutRepository.save(any(CheckoutEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(checkoutMapper.checkoutToDo(any()))
                .thenReturn(new CheckoutDto(1L, BigDecimal.valueOf(1.10), BigDecimal.valueOf(0.10), List.of(appleDto, bananaDto)));

        CheckoutDto result = checkoutService.checkout(
                CheckoutDto.builder().items(List.of(appleDto, bananaDto)).build()
        );

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(1.10), result.totalPrice());
        assertEquals(BigDecimal.valueOf(0.10), result.totalDiscount());
    }

    @Test
    void givenItemQuantityBelowOffer_whenCheckout_thenNoDiscountApplied() {
        CheckoutItemDto itemDto = CheckoutItemDto.builder()
                .id(1L)
                .itemId(1L)
                .itemName("apple")
                .quantity(1L) // offer requires 2
                .build();

        when(itemRepository.findById(1L)).thenReturn(Optional.of(apple));
        when(offerRepository.findByItemId(1L)).thenReturn(Optional.of(appleOffer));
        when(checkoutRepository.save(any(CheckoutEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(checkoutMapper.checkoutToDo(any()))
                .thenReturn(new CheckoutDto(1L, BigDecimal.valueOf(0.30), BigDecimal.ZERO, List.of(itemDto)));

        CheckoutDto result = checkoutService.checkout(
                CheckoutDto.builder().items(List.of(itemDto)).build()
        );

        assertEquals(BigDecimal.valueOf(0.30), result.totalPrice());
        assertEquals(BigDecimal.ZERO, result.totalDiscount());
    }




}