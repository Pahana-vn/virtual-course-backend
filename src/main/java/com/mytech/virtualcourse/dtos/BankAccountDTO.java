// package com.mytech.virtualcourse.dtos;

// import com.mytech.virtualcourse.enums.BankName;

// import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.Pattern;
// import lombok.Data;

// @Data
// public class BankAccountDTO {
//     private Long id;
//     @NotBlank(message = "Bank name cannot be blank")

//     private BankName bankName;
//     @NotBlank(message = "Account number is required")
//     @Pattern(regexp = "\\d{10,12}", message = "Invalid account number")
//     private String accountNumber;
//     @NotBlank(message = "Account holder name is required")
//     private String accountHolderName;
//     private Long instructorId; // ID của Instructor liên kết
// }
