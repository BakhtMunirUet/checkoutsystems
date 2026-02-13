package com.example.checkoutsystems.mapper;


import com.example.checkoutsystems.dao.entity.CheckoutEntity;
import com.example.checkoutsystems.dao.entity.CheckoutItemEntity;
import com.example.checkoutsystems.dto.CheckoutDto;
import com.example.checkoutsystems.dto.CheckoutItemDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CheckoutMapper {

    CheckoutItemDto entToDto(CheckoutItemEntity entity);
    List<CheckoutItemDto> entityToDto(List<CheckoutItemEntity> entityList);

    CheckoutDto checkoutToDo(CheckoutEntity entity);
}
