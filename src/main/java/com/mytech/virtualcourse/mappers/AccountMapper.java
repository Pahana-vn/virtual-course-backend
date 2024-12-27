// src/main/java/com/mytech/virtualcourse/mappers/AccountMapper.java

package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.AccountDTO;
import com.mytech.virtualcourse.entities.Account;
import com.mytech.virtualcourse.entities.Role;
import com.mytech.virtualcourse.enums.RoleName;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {InstructorMapper.class, StudentMapper.class, RoleMapper.class}
)
public interface AccountMapper {

    /**
     * Chuyển đổi Account Entity thành AccountDTO.
     */
    @Mapping(target = "roles", expression = "java(mapRoles(account.getRoles()))")
    @Mapping(target = "instructorId", source = "instructor.id")
    @Mapping(target = "studentId", source = "student.id")
    AccountDTO accountToAccountDTO(Account account);

    /**
     * Chuyển đổi AccountDTO thành Account Entity.
     * Các liên kết với Instructor và Student sẽ được xử lý trong service.
     */
    @Mapping(target = "instructor", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "roles", ignore = true)
    Account accountDTOToAccount(AccountDTO accountDTO);

    /**
     * Helper method để map roles từ Set<Role> sang Set<RoleName>.
     */
    default Set<RoleName> mapRoles(Set<Role> roles) {
        if (roles == null) {
            return null;
        }
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }
}
