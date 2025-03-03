package com.mytech.virtualcourse.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mytech.virtualcourse.entities.Role;
import com.mytech.virtualcourse.enums.AuthenticationType;
import com.mytech.virtualcourse.enums.Gender;
import com.mytech.virtualcourse.enums.StatusWallet;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class InstructorRegistrationDTO {
    // Account Info
    private String username;
    private String email;
    private String password;
    private AuthenticationType authenticationType;
    private Integer version;
    private List<Role> roles;

    // Instructor Info
    private String firstName;
    private String lastName;
    private Gender gender;
    private String address;
    private String phone;
    private String bio;
    private String photo;
    private String title;
    private String workplace;

    //Wallet Info
    private BigDecimal balance;
    private BigDecimal minLimit;
    private StatusWallet statusWallet;
    private Timestamp lastUpdated;

    // Social Info
    private String facebookUrl;
    private String googleUrl;
    private String instagramUrl;
    private String linkedinUrl;
}
