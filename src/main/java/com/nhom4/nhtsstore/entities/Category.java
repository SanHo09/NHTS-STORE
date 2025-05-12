package com.nhom4.nhtsstore.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.format.DateTimeFormatter;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.util.List;

@Entity
@Table
@Getter
@Setter
public class Category extends GenericEntity {
    @Id
    @Column
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Nationalized
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "category")
    private List<Product> products;
    
    @Override
    public String toString() {
        return this.name;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return id != null && id.equals(category.id);
    }
            
    @Override
    public Long getId() {
        return id;
    }
    
    @Override
    public Object getFieldValueByIndex(int index) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        switch (index) {
            case 0: return name;
            case 1: return isActive() ? "Visible" : "Hidden";
            case 2: return lastModifiedOn != null ? lastModifiedOn.format(formatter) : null;
            case 3: return lastModifiedBy;
            default: return null;
        }
    }
}
