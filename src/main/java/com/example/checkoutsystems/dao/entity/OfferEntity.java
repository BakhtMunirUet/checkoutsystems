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
@Table(name = "TBL_OFFER")
public class OfferEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ITEM_ID")
    private ItemEntity item;

    @Column(name = "QUANTITY", nullable = false)
    private Long quantity;

    @Column(name = "OFFER_PRICE", nullable = false)
    private BigDecimal offerPrice;


}
