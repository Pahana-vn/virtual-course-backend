// src/main/java/com/mytech/virtualcourse/mappers/AccountMapper.java
package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.AccountDTO;
import com.mytech.virtualcourse.dtos.RegisterDTO;
import com.mytech.virtualcourse.dtos.UpdateAccountDTO;
import com.mytech.virtualcourse.entities.Account;
import com.mytech.virtualcourse.entities.Role;
import com.mytech.virtualcourse.enums.ERole;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    // Chuyển đổi từ Account sang AccountDTO
    AccountDTO toAccount(Account account);

    // Chuyển đổi từ AccountDTO sang Account
    @Mapping(target = "roles", ignore = true) // Vai trò được xử lý trong Service
    Account toEntity(AccountDTO accountDTO);

    // Chuyển đổi từ RegisterDTO sang Account
    @Mapping(target = "roles", ignore = true) // Vai trò được xử lý trong Service
    Account registerDTOToAccount(RegisterDTO registerDTO);

    // Chuyển đổi từ Account sang UpdateAccountDTO
    @Mapping(target = "roles", ignore = true) // Vai trò được xử lý trong Service
    UpdateAccountDTO toUpdateAccountDTO(Account account);

    // Chuyển đổi từ UpdateAccountDTO sang Account
    @Mapping(target = "roles", ignore = true) // Vai trò được xử lý trong Service
    Account toAccount(UpdateAccountDTO updateAccountDTO);

    // Phương thức chuyển đổi từ List<Role> sang List<ERole>
    @Named("mapRoles")
    default List<ERole> mapRoles(List<Role> roles) {
        return roles.stream()
                .map(Role::getName) // Trả về String
                .map(name -> {
                    try {
                        return ERole.valueOf(name);
                    } catch (IllegalArgumentException e) {
                        throw new ResourceNotFoundException("Invalid role name: " + name);
                    }
                })
                .collect(Collectors.toList());
    }


    // Phương thức chuyển đổi từ List<Role> sang List<String> cho AccountDTO
    default List<String> mapRolesToStrings(List<Role> roles) {
        return roles.stream()
                .map(Role::getName) // Trả về trực tiếp String
                .collect(Collectors.toList());
    }
}
