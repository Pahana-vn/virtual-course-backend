// src/main/java/com/mytech/virtualcourse/mappers/AccountMapper.java

package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.AccountDTO;
import com.mytech.virtualcourse.entities.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring")
public interface AccountMapper {
    AccountDTO toAccountDTO(Account account);
    Account toAccount(AccountDTO accountDTO);
}
