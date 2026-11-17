package com.camaja.catapp.repository;

import com.camaja.catapp.models.AccessLevel;
import com.camaja.catapp.models.BackofficePage;
import com.camaja.catapp.models.RolePagePermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RolePagePermissionRepository extends JpaRepository<RolePagePermission, Long> {
    List<RolePagePermission> findByAccessLevel(AccessLevel accessLevel);
    List<RolePagePermission> findByAccessLevelIn(List<AccessLevel> accessLevels);
    Optional<RolePagePermission> findByAccessLevelAndBackofficePage(AccessLevel accessLevel, BackofficePage backofficePage);
}
