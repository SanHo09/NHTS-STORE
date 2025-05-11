package com.nhom4.nhtsstore.entities.rbac;


import com.nhom4.nhtsstore.entities.GenericEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "roles")
@Getter
@Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class Role extends GenericEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @Column(unique = true, nullable = false, length = 50)
    private String roleName;
    @Column
    @Nationalized
    private String description;

    @OneToMany(mappedBy = "role",fetch = FetchType.EAGER,cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<RoleHasPermission> rolePermissions = new HashSet<>();

    @Override
    public Long getId() {
        return roleId;
    }
    @Override
    public Object getFieldValueByIndex(int index) {
        switch (index) {
            case 0: return roleName;
            case 1: return description;
            case 2: return isActive() ? "Active" : "Inactive";
            default: return null;
        }
    }
}

