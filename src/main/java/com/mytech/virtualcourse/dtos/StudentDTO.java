// src/main/java/com/mytech/virtualcourse/dtos/StudentDTO.java
package com.mytech.virtualcourse.dtos;

import com.mytech.virtualcourse.enums.Gender;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StudentDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private Date dob;
    @NotNull(message = "Gender is required")
    private Gender gender;
    private String address;
    private String phone;
    private String avatar;
    private boolean verifiedPhone;
    private String categoryPrefer;
    private String statusStudent;
    private Long accountId; // Thêm trường này
}
