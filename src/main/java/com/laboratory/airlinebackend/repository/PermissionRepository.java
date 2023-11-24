package com.laboratory.airlinebackend.repository;

import com.laboratory.airlinebackend.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

}
