package com.nhom4.nhtsstore.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

@Entity
@Table
@Getter
@Setter
public class SupplierCategory extends GenericEntity {
   @Id
   @Column
   @GeneratedValue(strategy= GenerationType.IDENTITY)
   private Long id;
   
   @Column(nullable = false)
   @Nationalized
   private String name;

   @JsonIgnore
   @OneToMany(mappedBy = "supplierCategory")
   private List<Supplier> suppliers;
   
   @Override
   public String toString() {
       return this.name;
   }
   
   @Override
   public boolean equals(Object o) {
       if (this == o) return true;
       if (o == null || getClass() != o.getClass()) return false;
       SupplierCategory supplierCategory = (SupplierCategory) o;
       return id != null && id.equals(supplierCategory.id);
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
