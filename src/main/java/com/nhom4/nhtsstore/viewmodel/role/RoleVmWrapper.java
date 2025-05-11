package com.nhom4.nhtsstore.viewmodel.role;

import java.util.Objects;

public class RoleVmWrapper {
    private final RoleVm roleVm;

    public RoleVmWrapper(RoleVm roleVm) {
        this.roleVm = roleVm;
    }

    public RoleVm getRoleVm() {
        return roleVm;
    }

    @Override
    public String toString() {
        return roleVm.getRoleName();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RoleVmWrapper that = (RoleVmWrapper) obj;
        return Objects.equals(roleVm.getRoleId(), that.roleVm.getRoleId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleVm.getRoleId());
    }
}
