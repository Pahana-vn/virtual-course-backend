package com.mytech.virtualcourse.mappers;

import com.mytech.virtualcourse.dtos.AccountDTO;
import com.mytech.virtualcourse.entities.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    AccountDTO toAccountDTO(Account account);
    Account toAccount(AccountDTO accountDTO);
}
