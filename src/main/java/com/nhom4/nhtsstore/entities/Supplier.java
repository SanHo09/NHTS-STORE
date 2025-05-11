package com.nhom4.nhtsstore.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import java.time.format.DateTimeFormatter;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.util.List;

@Entity
@Table
@Getter
@Setter
public class Supplier extends GenericEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Nationalized
    private String name;

    @Column(nullable = false)
    @Nationalized
    private String address;
    
    @Column(nullable = true)
    @Email
    private String email;

    @Column(nullable = true)
    private String phoneNumber;
    
    @JsonIgnore
    @OneToMany(mappedBy = "supplier")
    private List<Product> products;
    
   @ManyToOne
   @JoinColumn(name = "supplier_category_id", nullable = false)
   private SupplierCategory supplierCategory;
    
    @Override
    public String toString() {
        return this.name;
    }
        
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Supplier supplier = (Supplier) o;
        return id != null && id.equals(supplier.id);
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
           case 1: return supplierCategory.getName();
           case 2: return email;
           case 3: return address;
           case 4: return phoneNumber;
           case 5: return isActive() ? "Visible" : "Hidden";
           case 6: return lastModifiedOn != null ? lastModifiedOn.format(formatter) : null;
           case 7: return lastModifiedBy;
           default: return null;
       }
   }
}
