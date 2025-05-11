package com.nhom4.nhtsstore.entities;


import com.nhom4.nhtsstore.entities.audit.AbstractAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 *
 * @author NamDang
 */
@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class GenericEntity extends AbstractAuditEntity {
    @Column(name = "is_active")
    private boolean active = true;

    @Transient
    private boolean selected;

    public abstract Long getId();
    
    public Object getFieldValueByIndex(int index) {
        return null;
    }
}
