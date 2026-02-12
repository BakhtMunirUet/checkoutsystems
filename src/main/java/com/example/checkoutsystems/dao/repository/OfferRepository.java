package com.example.checkoutsystems.dao.repository;

import com.example.checkoutsystems.dao.entity.OfferEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface OfferRepository extends JpaRepository<OfferEntity, Long> {

    Optional<OfferEntity> findByItemId(Long itemId);
}
