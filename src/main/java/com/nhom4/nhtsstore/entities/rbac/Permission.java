package com.nhom4.nhtsstore.entities.rbac;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nhom4.nhtsstore.entities.GenericEntity;
import com.nhom4.nhtsstore.entities.audit.AbstractAuditEntity;
import jakarta.persistence.*;
import java.time.format.DateTimeFormatter;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class Permission extends GenericEntity {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long permissionId;

    @Column(unique = true, nullable = false, length = 50)
    private String permissionName;
    @Column
    @Nationalized
    private String description;

    @OneToMany(mappedBy = "permission",fetch = FetchType.EAGER)
    private Set<RoleHasPermission> permissions = new HashSet<>();

    @Override
    public Long getId() {
        return permissionId;
    }
    @Override
    public Object getFieldValueByIndex(int index) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        switch (index) {
            case 0: return permissionName;
            case 1: return description;
            case 2: return isActive() ? "Visible" : "Hidden";
            case 3: return lastModifiedOn != null ? lastModifiedOn.format(formatter) : null;
            case 4: return lastModifiedBy;
            default: return null;
        }
    }
}
