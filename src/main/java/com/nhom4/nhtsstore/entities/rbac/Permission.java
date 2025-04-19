package com.nhom4.nhtsstore.entities.rbac;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nhom4.nhtsstore.entities.audit.AbstractAuditEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class Permission  {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer permissionId;

    @Column(unique = true, nullable = false, length = 50)
    private String permissionName;
    @Column
    @Nationalized
    private String description;

    @OneToMany(mappedBy = "permission",fetch = FetchType.EAGER)
    private Set<RoleHasPermission> permissions = new HashSet<>();
}
