package com.example.checkoutsystems.dao.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@MappedSuperclass
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Column(name = "IS_ACTIVE")
    Boolean isActive;
    @Column(name = "CREATED_BY")
    Long createdBy;
    @Column(name = "CREATED_DATE")
    LocalDateTime createdDate;
    @Column(name = "MODIFIED_BY")
    @LastModifiedBy
    Long modifiedBy;
    @Column(name = "MODIFIED_DATE")
    LocalDateTime modifiedDate;
}
