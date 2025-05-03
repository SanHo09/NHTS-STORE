package com.nhom4.nhtsstore.entities.audit;


import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(CustomAuditingEntityListener.class)
public class AbstractAuditEntity {

    @CreationTimestamp
    public ZonedDateTime createdOn;

    @CreatedBy
    public String createdBy;

    @UpdateTimestamp
    public ZonedDateTime lastModifiedOn;

    @LastModifiedBy
    public String lastModifiedBy;
}
