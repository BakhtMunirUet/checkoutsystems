package com.example.checkoutsystems.dao.repository;

import com.example.checkoutsystems.dao.entity.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository  extends JpaRepository<ItemEntity, Long> {
}
