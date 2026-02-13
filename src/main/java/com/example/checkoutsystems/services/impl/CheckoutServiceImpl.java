package com.example.checkoutsystems.services.impl;


import com.example.checkoutsystems.dao.entity.CheckoutEntity;
import com.example.checkoutsystems.dao.entity.CheckoutItemEntity;
import com.example.checkoutsystems.dao.entity.ItemEntity;
import com.example.checkoutsystems.dao.entity.OfferEntity;
import com.example.checkoutsystems.dao.repository.CheckoutRepository;
import com.example.checkoutsystems.dao.repository.ItemRepository;
import com.example.checkoutsystems.dao.repository.OfferRepository;
import com.example.checkoutsystems.dto.CheckoutItemDto;
import com.example.checkoutsystems.dto.CheckoutDto;
import com.example.checkoutsystems.mapper.CheckoutMapper;
import com.example.checkoutsystems.services.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private CheckoutMapper checkoutMapper;

    @Autowired
    private CheckoutRepository checkoutRepository;


    @Override
    public CheckoutDto checkout(CheckoutDto dto) {

        BigDecimal total = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        CheckoutEntity checkoutEntity = new CheckoutEntity();
        checkoutEntity.setItems(new ArrayList<>());

        for (CheckoutItemDto cartItem : dto.items()) {

            ItemEntity itemEntity = itemRepository.findById(cartItem.itemId())
                    .orElseThrow(() -> new RuntimeException("Item not found: " + cartItem.itemId()));

            Long quantity = cartItem.quantity();
            BigDecimal itemPrice = itemEntity.getPrice();

            LocalDateTime now = LocalDateTime.now();

            var offerEntity = offerRepository.findByItemId(itemEntity.getId())
                    .filter(offer -> !now.isBefore(offer.getStartDate()) && !now.isAfter(offer.getEndDate()));

            BigDecimal itemTotal;
            BigDecimal discountApplied = BigDecimal.ZERO;
            long offerAppliedTimes = 0;
            BigDecimal originalTotalPrice = BigDecimal.ZERO;


            /**
             * Check here if the item have price and it is still valid
             */

            if (offerEntity.isPresent()) {
                OfferEntity offer = offerEntity.get();
                Long offerQty = offer.getQuantity();
                BigDecimal offerPrice = offer.getOfferPrice();

                /**
                 * Calculation the offer price base on quantity
                 * how many times offer need to applied offerAppliedTimes = quantity / offerQty;
                 * How many times not to applied Long remainingQty = quantity % offerQty;
                 */
                offerAppliedTimes = quantity / offerQty;
                Long remainingQty = quantity % offerQty;

                /**
                 * Offer Price Calculation and Total Price calculation
                 */
                BigDecimal offerPart = offerPrice.multiply(BigDecimal.valueOf(offerAppliedTimes));
                BigDecimal normalPart =
                        itemPrice.multiply(BigDecimal.valueOf(remainingQty));



                itemTotal = offerPart.add(normalPart);
                originalTotalPrice = itemPrice.multiply(BigDecimal.valueOf(quantity));

                discountApplied = originalTotalPrice.subtract(itemTotal);

                totalDiscount = totalDiscount.add(discountApplied);

            } else {
                itemTotal = itemPrice.multiply(BigDecimal.valueOf(quantity));
            }

            total = total.add(itemTotal);


            checkoutEntity.getItems().add(
                    CheckoutItemEntity.builder()
                            .itemId(itemEntity.getId())
                            .itemName(itemEntity.getName())
                            .quantity(cartItem.quantity())
                            .unitPrice(itemPrice)
                            .totalPrice(itemTotal)
                            .originalTotalPrice(originalTotalPrice)
                            .discountApplied(discountApplied)
                            .timesOfferApplied(offerAppliedTimes)
                            .createdDate(LocalDateTime.now())
                            .checkout(checkoutEntity)
                            .build()
            );


        }

        checkoutEntity.setTotalPrice(total);
        checkoutEntity.setTotalDiscount(totalDiscount);
        checkoutEntity.setCreatedDate(LocalDateTime.now());

        return checkoutMapper.checkoutToDo(checkoutRepository.save(checkoutEntity));

    }
}
