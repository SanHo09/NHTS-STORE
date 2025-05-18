package com.nhom4.nhtsstore.entities;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import java.time.format.DateTimeFormatter;

import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.util.List;

@Entity
@Table
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Customer extends GenericEntity {
    @Id
    @Column
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Nationalized
    private String name;

    @Column(nullable = false)
    @Email
    private String email;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String address;

    @OneToMany(mappedBy = "customer")
    private List<Order> order;

    @OneToMany(mappedBy = "customer")
    private List<Invoice> invoice;
    
    @Override
    public String toString() {
        return this.name;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return id != null && id.equals(customer.id);
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
            case 1: return email;
            case 2: return phoneNumber;
            case 3: return address;
            case 4: return isActive() ? "Visible" : "Hidden";
            case 5: return lastModifiedOn != null ? lastModifiedOn.format(formatter) : null;
            case 6: return lastModifiedBy;
            default: return null;
        }
    }
}
