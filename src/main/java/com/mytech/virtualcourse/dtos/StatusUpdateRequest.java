// src/main/java/com/mytech/virtualcourse/dtos/StatusUpdateRequest.java
package com.mytech.virtualcourse.dtos;

import com.mytech.virtualcourse.enums.StatusWallet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatusUpdateRequest {
    
    @NotNull(message = "Status cannot be null")
    private StatusWallet newStatus;
}
