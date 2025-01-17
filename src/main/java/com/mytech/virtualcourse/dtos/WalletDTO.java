package com.mytech.virtualcourse.dtos;

import com.mytech.virtualcourse.enums.StatusWallet;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WalletDTO {
    private Long id; // ID của Wallet
    private BigDecimal balance; // Số dư
    private StatusWallet statusWallet; // Trạng thái ví (ACTIVE, SUSPENDED, CLOSED)
    private BigDecimal maxLimit; // Giới hạn tối đa của ví
    private Timestamp lastUpdated; // Thời gian cập nhật cuối cùng
    private Long instructorId; // ID của Instructor (nếu ví thuộc về giảng viên)
}
