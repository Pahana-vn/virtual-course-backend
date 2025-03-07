package com.mytech.virtualcourse.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class RejectWithdrawalRequest {

    @NotBlank(message = "Reason is required")
    @Size(min = 5, max = 200, message = "Reason must be between 5 and 200 characters")
    private String reason;

    public RejectWithdrawalRequest() {
    }

    public RejectWithdrawalRequest(String reason) {
        this.reason = reason;
    }

}