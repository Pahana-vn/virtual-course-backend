// src/main/java/com/mytech/virtualcourse/services/TransactionService.java

package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.TransactionDTO;
import com.mytech.virtualcourse.entities.Payment;
import com.mytech.virtualcourse.entities.Student;
import com.mytech.virtualcourse.entities.Transaction;
import com.mytech.virtualcourse.entities.Wallet;
import com.mytech.virtualcourse.enums.TransactionStatus;
import com.mytech.virtualcourse.enums.TransactionType;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.mappers.TransactionMapper;
import com.mytech.virtualcourse.repositories.PaymentRepository;
import com.mytech.virtualcourse.repositories.TransactionRepository;
import com.mytech.virtualcourse.repositories.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PaymentRepository paymentRepository;

    /**
     * Tìm Wallet theo ID.
     *
     * @param walletId ID của Wallet.
     * @return Wallet.
     */
    private Wallet findWalletById(Long walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id: " + walletId));
    }

    /**
     * Gửi thông báo cho chủ sở hữu Wallet.
     *
     * @param wallet        Wallet liên quan.
     * @param message       Nội dung thông báo.
     * @param transactionId ID của giao dịch.
     */
    private void notifyWalletOwner(Wallet wallet, String message, Long transactionId) {
        Long ownerId;
        if (wallet.getStudent() != null) {
            ownerId = wallet.getStudent().getAccount().getId();
        } else if (wallet.getInstructor() != null) {
            ownerId = wallet.getInstructor().getAccount().getId();
        } else {
            throw new ResourceNotFoundException("User not found for wallet id: " + wallet.getId());
        }

        notificationService.sendNotification(
                ownerId,
                message,
                com.mytech.virtualcourse.enums.NotificationType.TRANSACTION,
                null,
                transactionId
        );
    }

    /**
     * Nạp tiền vào Wallet.
     *
     * @param walletId ID của Wallet.
     * @param amount   Số tiền.
     * @return TransactionDTO.
     */
    public TransactionDTO depositToWallet(Long walletId, BigDecimal amount) {
        Wallet wallet = findWalletById(walletId);
        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.setLastUpdated(new Timestamp(System.currentTimeMillis()));
        walletRepository.save(wallet);

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setTransactionStatus(TransactionStatus.SUCCESS);
        transaction.setProcessedAt(new Timestamp(System.currentTimeMillis()));
        transaction.setWallet(wallet);

        Transaction savedTransaction = transactionRepository.save(transaction);
        notifyWalletOwner(wallet, "Bạn đã nạp " + amount + " vào ví.", savedTransaction.getId());

        return transactionMapper.toDTO(savedTransaction);
    }

    /**
     * Rút tiền từ Wallet.
     *
     * @param walletId ID của Wallet.
     * @param amount   Số tiền.
     * @return TransactionDTO.
     */
    public TransactionDTO withdrawFromWallet(Long walletId, BigDecimal amount) {
        Wallet wallet = findWalletById(walletId);

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance in wallet.");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        wallet.setLastUpdated(new Timestamp(System.currentTimeMillis()));
        walletRepository.save(wallet);

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.WITHDRAWAL);
        transaction.setTransactionStatus(TransactionStatus.SUCCESS);
        transaction.setProcessedAt(new Timestamp(System.currentTimeMillis()));
        transaction.setWallet(wallet);

        Transaction savedTransaction = transactionRepository.save(transaction);
        notifyWalletOwner(wallet, "Bạn đã rút " + amount + " từ ví.", savedTransaction.getId());

        return transactionMapper.toDTO(savedTransaction);
    }

    /**
     * Lấy lịch sử giao dịch của Wallet.
     *
     * @param walletId ID của Wallet.
     * @return Danh sách TransactionDTO.
     */
    public List<TransactionDTO> getTransactionHistory(Long walletId) {
        List<Transaction> transactions = transactionRepository.findByWalletId(walletId);
        return transactions.stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Hoàn tiền thanh toán.
     *
     * @param paymentId ID của thanh toán.
     * @param amount    Số tiền hoàn tiền.
     * @return TransactionDTO.
     */
    public TransactionDTO refundPayment(Long paymentId, BigDecimal amount) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        // Giả sử Wallet liên kết với Student thông qua Payment
        Student student = payment.getStudent();

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.REFUND);
        transaction.setTransactionStatus(TransactionStatus.SUCCESS);
        transaction.setProcessedAt(new Timestamp(System.currentTimeMillis()));
        transaction.setPayment(payment); // Sửa dòng này để gán đối tượng Payment

        Transaction savedTransaction = transactionRepository.save(transaction);

        return transactionMapper.toDTO(savedTransaction);
    }
    // /**
    //  * Fetch all transactions for a given instructor.
    //  *
    //  * @param instructorId ID of the instructor.
    //  * @return List of TransactionDTO.
    //  */
    // public List<TransactionDTO> getTransactionsByInstructorId(Long instructorId) {
    //     List<Transaction> transactions = transactionRepository.findByWalletInstructorId(instructorId);
    //     if (transactions.isEmpty()) {
    //         throw new ResourceNotFoundException("No transactions found for instructor with id: " + instructorId);
    //     }
    //     return transactions.stream()
    //             .map(transactionMapper::toDTO)
    //             .collect(Collectors.toList());
    // }
}
