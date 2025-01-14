// src/main/java/com/mytech/virtualcourse/dtos/BalanceUpdateRequest.java
package com.mytech.virtualcourse.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BalanceUpdateRequest {
    
    @NotNull(message = "Wallet ID cannot be null")
    private Long walletId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than zero")
    private BigDecimal amount;
    
    @NotNull(message = "isDeposit flag is required")
    private Boolean isDeposit; // true: nạp tiền, false: rút tiền
}
