package com.nhom4.nhtsstore.entities.rbac;


import com.nhom4.nhtsstore.entities.audit.AbstractAuditEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class User extends AbstractAuditEntity implements Serializable, UserDetails {

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


}
