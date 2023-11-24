package com.laboratory.airlinebackend.repository;

import com.laboratory.airlinebackend.model.Role;
import com.laboratory.airlinebackend.model.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    // Find RolePermission entities by their associated Role
    List<RolePermission> findByRole(Role role);
}
