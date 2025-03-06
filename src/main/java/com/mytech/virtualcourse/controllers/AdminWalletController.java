package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.WalletBalanceDTO;
import com.mytech.virtualcourse.dtos.WalletDTO;
import com.mytech.virtualcourse.entities.Wallet;
import com.mytech.virtualcourse.enums.StatusWallet;
import com.mytech.virtualcourse.mappers.WalletMapper;
import com.mytech.virtualcourse.services.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/wallets")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AdminWalletController {

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletMapper walletMapper;

    @GetMapping
    public ResponseEntity<List<WalletDTO>> getAllWallets() {
        List<Wallet> wallets = walletService.getAllWallets();
        List<WalletDTO> walletDTOs = wallets.stream()
                .map(walletMapper::walletToWalletDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(walletDTOs);
    }

    @GetMapping("/detail/{instructorId}")
    public ResponseEntity<WalletDTO> getWalletByInstructorId(@PathVariable Long instructorId) {
        WalletDTO wallet = walletService.getWalletByInstructorId(instructorId);
        return ResponseEntity.ok(wallet);
    }

    @PutMapping("/update-status/{walletId}")
    public ResponseEntity<WalletDTO> updateWalletStatus(
            @PathVariable Long walletId,
            @RequestBody Map<String, String> payload) {

        StatusWallet status = StatusWallet.valueOf(payload.get("status"));
        Wallet wallet = walletService.updateWalletStatus(walletId, status);
        return ResponseEntity.ok(walletMapper.walletToWalletDTO(wallet));
    }

    @PutMapping("/update-balance/{instructorId}")
    public ResponseEntity<WalletBalanceDTO> updateWalletBalance(
            @PathVariable Long instructorId,
            @RequestBody Map<String, BigDecimal> payload) {

        BigDecimal amount = payload.get("amount");
        WalletBalanceDTO updatedBalance = walletService.updateBalance(instructorId, amount);
        return ResponseEntity.ok(updatedBalance);
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getWalletStatistics() {
        // This would need to be implemented in your service
        Map<String, Object> statistics = walletService.getWalletStatistics();
        return ResponseEntity.ok(statistics);
    }
}