package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.WalletBalanceDTO;
import com.mytech.virtualcourse.dtos.WalletDTO;
import com.mytech.virtualcourse.entities.Wallet;
import com.mytech.virtualcourse.enums.StatusWallet;
import com.mytech.virtualcourse.mappers.WalletMapper;
import com.mytech.virtualcourse.repositories.WalletRepository;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    // Lấy thông tin ví dựa theo instructorId
    public WalletDTO getWalletByInstructorId(Long instructorId) {
        Wallet wallet = walletRepository.findByInstructorId(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for instructorId: " + instructorId));

        return WalletMapper.INSTANCE.walletToWalletDTO(wallet);
    }

    // Cập nhật số dư ví
    @Transactional
    public WalletBalanceDTO updateBalance(Long instructorId, BigDecimal amount) {
        Wallet wallet = walletRepository.findByInstructorId(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for instructorId: " + instructorId));

        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.setLastUpdated(Timestamp.from(Instant.now()));

        walletRepository.save(wallet);
        return WalletMapper.INSTANCE.toWalletBalanceDTO(wallet);
    }

    // Xử lý yêu cầu rút tiền
    @Transactional
    public WalletBalanceDTO withdraw(Long instructorId, BigDecimal amount) {
        Wallet wallet = walletRepository.findByInstructorId(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for instructorId: " + instructorId));

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        if (wallet.getMinLimit() != null && amount.compareTo(wallet.getMinLimit()) < 0) {
            throw new IllegalArgumentException("Withdrawal amount is below minimum limit");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        wallet.setLastUpdated(Timestamp.from(Instant.now()));

        walletRepository.save(wallet);
        return WalletMapper.INSTANCE.toWalletBalanceDTO(wallet);
    }
}
