// src/main/java/com/mytech/virtualcourse/dtos/MaxLimitUpdateRequest.java
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
public class MaxLimitUpdateRequest {
    
    @NotNull(message = "Max limit is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Max limit must be greater than zero")
    private BigDecimal maxLimit;
}
