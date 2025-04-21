package com.nhom4.nhtsstore.entities.rbac;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nhom4.nhtsstore.entities.audit.AbstractAuditEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@Getter
@Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class Role  {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roleId;

    @Column(unique = true, nullable = false, length = 50)
    private String roleName;
    @Column
    @Nationalized
    private String description;


    @OneToMany(mappedBy = "role",fetch = FetchType.EAGER)
    private Set<RoleHasPermission> rolePermissions = new HashSet<>();
}

