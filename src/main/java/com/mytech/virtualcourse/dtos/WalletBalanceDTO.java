package com.mytech.virtualcourse.dtos;

import com.mytech.virtualcourse.enums.StatusWallet;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WalletBalanceDTO {
    private Long id;
    private BigDecimal balance;
    private StatusWallet statusWallet;
    private Timestamp lastUpdated;
}
