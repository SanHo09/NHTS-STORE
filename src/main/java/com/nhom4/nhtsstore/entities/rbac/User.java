package com.nhom4.nhtsstore.entities.rbac;


import com.nhom4.nhtsstore.entities.GenericEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class User extends GenericEntity implements Serializable, UserDetails {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 256)
    private String password;

    @Column( length = 100)
    private String email;

    @Column(length = 100)
    @Nationalized
    private String fullName;

    @Column
    private byte[] avatar;
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = true)
    private Role role;
    @Column(name = "is_active")
    private boolean active = true;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == null) {
            return Set.of();
        }
        Set<RoleHasPermission> roleHasPermissionList = role.getRolePermissions();
        Set<Permission> permissionList = roleHasPermissionList.stream().map(RoleHasPermission::getPermission).collect(Collectors.toSet());
        return permissionList.stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermissionName()))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return this.active;
    }

    @Override
    public Long getId() {
        return userId;
    }
    @Override
    public Object getFieldValueByIndex(int index) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        switch (index) {
            case 0: return fullName;
            case 1: return role.getRoleName();
            case 2: return isActive() ? "Active" : "Inactive";
            case 3: return lastModifiedOn != null ? lastModifiedOn.format(formatter) : null;
            case 4: return lastModifiedBy;
            default: return null;
        }
    }
}
