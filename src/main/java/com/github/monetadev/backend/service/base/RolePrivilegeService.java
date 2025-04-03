package com.github.monetadev.backend.service.base;

import com.github.monetadev.backend.model.Role;

import java.util.UUID;

public interface RolePrivilegeService {
    Role assignPrivilegeToRole(UUID roleId, UUID privilegeId);

    Role removePrivilegeFromRole(UUID roleId, UUID privilegeId);
}
