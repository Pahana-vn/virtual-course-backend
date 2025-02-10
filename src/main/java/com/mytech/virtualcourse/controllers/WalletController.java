package com.mytech.virtualcourse.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mytech.virtualcourse.dtos.BalanceUpdateRequest;
import com.mytech.virtualcourse.dtos.MaxLimitUpdateRequest;
import com.mytech.virtualcourse.dtos.StatusUpdateRequest;
import com.mytech.virtualcourse.dtos.WalletDTO;
import com.mytech.virtualcourse.services.WalletService;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {
    
    @Autowired
    private WalletService walletService;

    // Tạo Wallet (nếu cần)
    @PostMapping
    public ResponseEntity<WalletDTO> createWallet(@RequestBody WalletDTO walletDTO) {
        WalletDTO createdWallet = walletService.createWallet(walletDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdWallet);
    }

    // Lấy Wallet theo ID
    @GetMapping("/{walletId}")
    public ResponseEntity<WalletDTO> getWalletById(@PathVariable Long walletId) {
        WalletDTO walletDTO = walletService.getWalletById(walletId);
        return ResponseEntity.ok(walletDTO);
    }

    // /**
    //  * GET /api/wallets/instructor/{instructorId}
    //  * Fetch the wallet for a specific instructor.
    //  *
    //  * @param instructorId ID of the instructor.
    //  * @return WalletDTO.
    //  */
    // @GetMapping("/instructor/{instructorId}")
    // public ResponseEntity<WalletDTO> getWalletByInstructor(@PathVariable Long instructorId) {
    //     WalletDTO wallet = walletService.getWalletByInstructorId(instructorId);
    //     return ResponseEntity.ok(wallet);
    // }   

    // Cập nhật Wallet balance (nạp/rút tiền)
    @PutMapping("/{walletId}/balance")
    public ResponseEntity<WalletDTO> updateBalance(@PathVariable Long walletId,
                                                   @RequestBody BalanceUpdateRequest request) {
        WalletDTO updatedWallet = walletService.updateBalance(walletId, request.getAmount(), request.getIsDeposit());
        return ResponseEntity.ok(updatedWallet);
    }

    // Cập nhật status của Wallet
    @PutMapping("/{walletId}/status")
    public ResponseEntity<WalletDTO> updateWalletStatus(@PathVariable Long walletId,
                                                        @RequestBody StatusUpdateRequest request) {
        WalletDTO updatedWallet = walletService.updateWalletStatus(walletId, request.getNewStatus());
        return ResponseEntity.ok(updatedWallet);
    }

    // Đặt giới hạn tối đa cho Wallet
    @PutMapping("/{walletId}/max-limit")
    public ResponseEntity<WalletDTO> setMaxLimit(@PathVariable Long walletId,
                                                @RequestBody MaxLimitUpdateRequest request) {
        WalletDTO updatedWallet = walletService.setMaxLimit(walletId, request.getMaxLimit());
        return ResponseEntity.ok(updatedWallet);
    }

    // Các endpoint khác nếu cần...
}
