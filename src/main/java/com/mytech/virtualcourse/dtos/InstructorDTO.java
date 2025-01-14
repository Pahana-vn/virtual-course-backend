// src/main/java/com/mytech/virtualcourse/dtos/InstructorDTO.java

package com.mytech.virtualcourse.dtos;
import com.mytech.virtualcourse.entities.Account;
import com.mytech.virtualcourse.enums.EAccountStatus;
import jakarta.validation.constraints.NotNull;

import com.mytech.virtualcourse.enums.Gender;
import lombok.Data;

@Data
public class InstructorDTO {
    private Long id;
    @NotNull(message = "First name is required")
    private String firstName;

    @NotNull(message = "Last name is required")
    private String lastName;

    @NotNull(message = "Gender is required")
    private Gender gender;
    @NotNull(message = "Status is required")
    private AccountDTO status;
    private String address;
    private String phone;
    private String bio;
    private boolean verifiedPhone;
    private String photo;
    private String title;
    private String workplace;
    @NotNull(message = "Account ID is required")
    private Long accountId; // Thêm trường này
    // Các trường khác nếu cần
    private Long walletId; // Thêm trường này

}
