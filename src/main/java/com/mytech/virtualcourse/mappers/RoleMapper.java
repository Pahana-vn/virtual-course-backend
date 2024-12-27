// src/main/java/com/mytech/virtualcourse/mappers/RoleMapper.java

package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.RoleDTO;
import com.mytech.virtualcourse.entities.Role;
import com.mytech.virtualcourse.enums.RoleName;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface RoleMapper {
    RoleDTO roleToRoleDTO(Role role);
    Role roleDTOToRole(RoleDTO roleDTO);
}
