package com.example.checkoutsystems.dao.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@SuperBuilder(toBuilder = true)
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "TBL_CHECKOUT_ITEM")
public class CheckoutItemEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "ITEM_ID")
    private Long itemId;

    @Column(name = "ITEM_NAME")
    private String itemName;

    @Column(name = "QUANTITY")
    private Long quantity;

    @Column(name = "UNIT_PRICE")
    private BigDecimal unitPrice;

    @Column(name = "TOTAL_PRICE")
    private BigDecimal totalPrice;

    @Column(name = "ORIGINAL_TOTAL_PRICE")
    private BigDecimal originalTotalPrice;


    @Column(name = "DISCOUNT_APPLIED")
    private BigDecimal discountApplied;

    @Column(name = "TIMES_OFFER_APPLIED")
    private Long timesOfferApplied;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHECKOUT_ID")
    private CheckoutEntity checkout;
}
