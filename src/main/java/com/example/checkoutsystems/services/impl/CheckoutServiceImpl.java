package com.example.checkoutsystems.services.impl;


import com.example.checkoutsystems.dao.entity.ItemEntity;
import com.example.checkoutsystems.dao.entity.OfferEntity;
import com.example.checkoutsystems.dao.repository.ItemRepository;
import com.example.checkoutsystems.dao.repository.OfferRepository;
import com.example.checkoutsystems.dto.CartItemDto;
import com.example.checkoutsystems.dto.CheckoutDto;
import com.example.checkoutsystems.services.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private OfferRepository offerRepository;


    @Override
    public CheckoutDto checkout(CheckoutDto dto) {

        BigDecimal total = BigDecimal.ZERO;

        for (CartItemDto cartItem : dto.cartItemDtoList()) {

            ItemEntity itemEntity = itemRepository.findById(cartItem.itemId())
                    .orElseThrow(() -> new RuntimeException("Item not found: " + cartItem.itemId()));

            Long quantity = cartItem.quantity();
            BigDecimal itemPrice = itemEntity.getPrice();

//             Check if an offer exists for the item
            var offerEntity = offerRepository.findByItemId(itemEntity.getId());

            if (offerEntity.isPresent()) {
                OfferEntity offer = offerEntity.get();
                Long offerQty = offer.getQuantity();
                BigDecimal offerPrice = offer.getOfferPrice();

//                // How many times the offer applies
                Long offerAppliedTimes = quantity / offerQty;
                Long remainingQty = quantity % offerQty;

                total = total.add(offerPrice.multiply(BigDecimal.valueOf(offerAppliedTimes)));
                total = total.add(itemPrice.multiply(BigDecimal.valueOf(remainingQty)));
            } else {
                total = total.add(itemPrice.multiply(BigDecimal.valueOf(quantity)));
            }
        }

//        return total;

        return null;
    }
}
