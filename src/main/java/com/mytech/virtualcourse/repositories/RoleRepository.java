// src/main/java/com/mytech/virtualcourse/repositories/RoleRepository.java
package com.mytech.virtualcourse.repositories;

import com.mytech.virtualcourse.entities.Role;
import com.mytech.virtualcourse.enums.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
    boolean existsByName(ERole name);
}
