// src/main/java/com/mytech/virtualcourse/services/WalletService.java

package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.WalletDTO;
import com.mytech.virtualcourse.entities.Wallet;
import com.mytech.virtualcourse.enums.StatusWallet;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.mappers.WalletMapper;
import com.mytech.virtualcourse.repositories.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Transactional
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletMapper walletMapper;

    /**
     * Tạo mới một Wallet.
     *
     * @param dto Dữ liệu Wallet.
     * @return WalletDTO đã được lưu.
     */
    public WalletDTO createWallet(WalletDTO dto) {
        Wallet wallet = walletMapper.walletDTOToWallet(dto);
        // Thiết lập các trường mặc định nếu cần
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setStatusWallet(StatusWallet.ACTIVE);
        wallet.setLastUpdated(new java.sql.Timestamp(System.currentTimeMillis()));

        Wallet savedWallet = walletRepository.save(wallet);
        return walletMapper.walletToWalletDTO(savedWallet);
    }

    /**
     * Lấy thông tin Wallet theo ID.
     *
     * @param walletId ID của Wallet.
     * @return WalletDTO.
     */
    public WalletDTO getWalletById(Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id: " + walletId));
        return walletMapper.walletToWalletDTO(wallet);
    }

    /**
     * Cập nhật số dư Wallet (nạp/rút tiền).
     *
     * @param walletId  ID của Wallet.
     * @param amount    Số tiền.
     * @param isDeposit Flag xác định loại giao dịch.
     * @return WalletDTO đã được cập nhật.
     */
    public WalletDTO updateBalance(Long walletId, BigDecimal amount, boolean isDeposit) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id: " + walletId));

        if (isDeposit) {
            wallet.setBalance(wallet.getBalance().add(amount));
        } else {
            if (wallet.getBalance().compareTo(amount) < 0) {
                throw new IllegalArgumentException("Insufficient balance in wallet.");
            }
            wallet.setBalance(wallet.getBalance().subtract(amount));
        }

        wallet.setLastUpdated(new java.sql.Timestamp(System.currentTimeMillis()));
        walletRepository.save(wallet);
        return walletMapper.walletToWalletDTO(wallet);
    }

     /**
     * Fetch the wallet for a given instructor.
     *
     * @param instructorId ID of the instructor.
     * @return WalletDTO.
     */
     public WalletDTO getWalletByInstructorId(Long instructorId) {
         Wallet wallet = walletRepository.findByInstructorId(instructorId)
                 .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for instructor with id: " + instructorId));
         return walletMapper.walletToWalletDTO(wallet);
     }


    // /**
    //  * Lấy Wallet của người dùng dựa trên userId.
    //  *
    //  * @param userId ID của người dùng.
    //  * @return WalletDTO.
    //  */
    // @Transactional(readOnly = true)
    // public WalletDTO fetchWalletByStudentId(Long studentId) {
    //     Wallet wallet = walletRepository.findByStudentId(studentId)
    //             .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user with id: " + studentId));
    //     return walletMapper.walletToWalletDTO(wallet);
    // }

    /**
     * Cập nhật trạng thái ví.
     *
     * @param walletId  ID của Wallet.
     * @param newStatus Trạng thái mới.
     * @return WalletDTO đã được cập nhật.
     */
    public WalletDTO updateWalletStatus(Long walletId, StatusWallet newStatus) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id: " + walletId));

        wallet.setStatusWallet(newStatus);
        wallet.setLastUpdated(new java.sql.Timestamp(System.currentTimeMillis()));
        walletRepository.save(wallet);

        return walletMapper.walletToWalletDTO(wallet);
    }

    /**
     * Đặt giới hạn tối đa cho Wallet.
     *
     * @param walletId ID của Wallet.
     * @param maxLimit Giới hạn tối đa.
     * @return WalletDTO đã được cập nhật.
     */
    public WalletDTO setMaxLimit(Long walletId, BigDecimal maxLimit) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id: " + walletId));

        if (maxLimit.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Max limit cannot be negative.");
        }

        wallet.setMaxLimit(maxLimit);
        wallet.setLastUpdated(new java.sql.Timestamp(System.currentTimeMillis()));
        walletRepository.save(wallet);

        return walletMapper.walletToWalletDTO(wallet);
    }
}
