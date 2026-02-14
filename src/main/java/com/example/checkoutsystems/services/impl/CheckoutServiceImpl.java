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
import com.example.checkoutsystems.exception.InputValidationException;
import com.example.checkoutsystems.mapper.CheckoutMapper;
import com.example.checkoutsystems.services.CheckoutService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

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
    @Transactional
    public CheckoutDto checkout(CheckoutDto dto) {
        LocalDateTime now = LocalDateTime.now();
        CheckoutEntity checkoutEntity = new CheckoutEntity();
        checkoutEntity.setItems(new ArrayList<>());

        BigDecimal total = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;

        for (CheckoutItemDto cartItem : dto.items()) {

            ItemEntity item = findItem(cartItem.itemId());
            OfferEntity offer = findValidOffer(item.getId(), now);

            ItemCalculation calc = calculateItemTotals(item, offer, cartItem.quantity());

            total = total.add(calc.itemTotal());
            totalDiscount = totalDiscount.add(calc.discount());

            checkoutEntity.getItems().add(buildCheckoutItemEntity(
                    checkoutEntity, item, cartItem.quantity(), calc
            ));
        }

        checkoutEntity.setCreatedDate(now);
        checkoutEntity.setTotalPrice(total);
        checkoutEntity.setTotalDiscount(totalDiscount);

        return checkoutMapper.checkoutToDo(checkoutRepository.save(checkoutEntity));

    }


    private ItemEntity findItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new InputValidationException("Item not found: " + itemId));
    }

    private OfferEntity findValidOffer(Long itemId, LocalDateTime now) {
        return offerRepository.findByItemId(itemId)
                .filter(o -> !now.isBefore(o.getStartDate()) && !now.isAfter(o.getEndDate()))
                .orElse(null);
    }

    private ItemCalculation calculateItemTotals(ItemEntity item, OfferEntity offer, long qty) {

        BigDecimal unitPrice = item.getPrice();
        BigDecimal originalTotal = unitPrice.multiply(BigDecimal.valueOf(qty));

        if (offer == null) {
            return new ItemCalculation(originalTotal, BigDecimal.ZERO, 0, originalTotal);
        }

        long offerQty = offer.getQuantity();
        long offerTimes = qty / offerQty;
        long remainder = qty % offerQty;

        BigDecimal offerPricePart =
                offer.getOfferPrice().multiply(BigDecimal.valueOf(offerTimes));

        BigDecimal normalPart =
                unitPrice.multiply(BigDecimal.valueOf(remainder));

        BigDecimal itemTotal = offerPricePart.add(normalPart);
        BigDecimal discount = originalTotal.subtract(itemTotal);

        return new ItemCalculation(itemTotal, discount, offerTimes, originalTotal);
    }

    private CheckoutItemEntity buildCheckoutItemEntity(
            CheckoutEntity checkout,
            ItemEntity item,
            long qty,
            ItemCalculation calc) {

        return CheckoutItemEntity.builder()
                .itemId(item.getId())
                .itemName(item.getName())
                .quantity(qty)
                .unitPrice(item.getPrice())
                .totalPrice(calc.itemTotal())
                .originalTotalPrice(calc.originalTotal())
                .discountApplied(calc.discount())
                .timesOfferApplied(calc.offerTimes())
                .createdDate(LocalDateTime.now())
                .checkout(checkout)
                .build();
    }

    private record ItemCalculation(
            BigDecimal itemTotal,
            BigDecimal discount,
            long offerTimes,
            BigDecimal originalTotal
    ) {}
}
