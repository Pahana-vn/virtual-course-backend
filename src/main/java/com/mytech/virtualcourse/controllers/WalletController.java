package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.WalletBalanceDTO;
import com.mytech.virtualcourse.dtos.WalletDTO;
import com.mytech.virtualcourse.entities.Wallet;
import com.mytech.virtualcourse.services.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;

    // Lấy số dư của giảng viên
    @GetMapping("/{instructorId}")
    public ResponseEntity<WalletDTO> getWallet(@PathVariable Long instructorId) {
        return ResponseEntity.ok(walletService.getWalletByInstructorId(instructorId));
    }

    // Cập nhật số dư (ví dụ: nạp tiền vào ví)
    @PutMapping("/{instructorId}/update-balance")
    public ResponseEntity<WalletBalanceDTO> updateBalance(
            @PathVariable Long instructorId, @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(walletService.updateBalance(instructorId, amount));
    }

    // Xử lý yêu cầu rút tiền
    @PostMapping("/{instructorId}/withdraw")
    public ResponseEntity<WalletBalanceDTO> withdraw(
            @PathVariable Long instructorId, @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(walletService.withdraw(instructorId, amount));
    }
}
