package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.TransactionDTO;
import com.mytech.virtualcourse.entities.Transaction;
import com.mytech.virtualcourse.entities.Wallet;
import com.mytech.virtualcourse.enums.PaymentMethod;
import com.mytech.virtualcourse.enums.StatusTransaction;
import com.mytech.virtualcourse.enums.TransactionType;
import com.mytech.virtualcourse.mappers.TransactionMapper;
import com.mytech.virtualcourse.repositories.TransactionRepository;
import com.mytech.virtualcourse.repositories.WalletRepository;
import com.mytech.virtualcourse.security.SecurityUtils;
import com.paypal.api.payments.PayoutBatch;
import com.paypal.api.payments.Payout;
import com.paypal.api.payments.PayoutItem;
import com.paypal.api.payments.PayoutSenderBatchHeader;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InstructorTransactionService {
    @Autowired
    private APIContext apiContext;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionMapper transactionMapper;

    public String initiateWithdrawal(BigDecimal amount, String recipientEmail, PaymentMethod paymentMethod) throws PayPalRESTException {
        // Kiểm tra số dư ví
        Long instructorId = SecurityUtils.getLoggedInInstructorId();
        Wallet wallet = walletRepository.findByInstructorId(instructorId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        // Lấy số dư ví hiện tại
        BigDecimal walletBalance = wallet.getBalance();

        if (walletBalance.compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        // Tính số dư còn lại sau giao dịch
        BigDecimal remainingBalance = walletBalance.subtract(amount);

        // Tạo tiêu đề cho giao dịch
        String transactionTitle = "Withdrawal from " + paymentMethod;

        // Tạo giao dịch withdrawal
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTitle(transactionTitle);
        transaction.setTransactionType(TransactionType.WITHDRAWAL);
        transaction.setStatusTransaction(StatusTransaction.PENDING);
        transaction.setPaymentMethod(paymentMethod);
        transaction.setWallet(wallet);
        transaction.setWalletBalance(remainingBalance);
        transactionRepository.save(transaction);

        // Gửi yêu cầu rút tiền qua PayPal Payouts API
        Payout payout = new Payout();
        PayoutSenderBatchHeader senderBatchHeader = new PayoutSenderBatchHeader();
        senderBatchHeader.setSenderBatchId(UUID.randomUUID().toString());
        senderBatchHeader.setEmailSubject("You have a payout!");

        PayoutItem payoutItem = new PayoutItem();
        payoutItem.setRecipientType("EMAIL");
        payoutItem.setReceiver(recipientEmail);
        payoutItem.setAmount(new com.paypal.api.payments.Currency("USD", amount.toString()));
        payoutItem.setNote("Withdrawal request");

        payout.setSenderBatchHeader(senderBatchHeader);
        payout.setItems(List.of(payoutItem));

        PayoutBatch payoutBatch = payout.create(apiContext, new HashMap<>());

        // Cập nhật trạng thái giao dịch
        transaction.setWalletBalance(remainingBalance);
        transaction.setStatusTransaction(StatusTransaction.COMPLETED);
        transaction.setProcessedAt(Timestamp.valueOf(LocalDateTime.now()));
        transaction.setPaypalPayoutId(payoutBatch.getBatchHeader().getPayoutBatchId());
        transactionRepository.save(transaction);

        // 🟢 Cập nhật số dư ví instructor sau khi rút tiền
        wallet.setBalance(remainingBalance);
        walletRepository.save(wallet);

        return payoutBatch.getBatchHeader().getPayoutBatchId();
    }

    public List<TransactionDTO> getAllInstructorTransactions() {
        Long instructorId = SecurityUtils.getLoggedInInstructorId();
        List<Transaction> transactions = transactionRepository.findByWallet_InstructorId(instructorId);

        // Sử dụng Mapper để chuyển entity thành DTO
        return transactions.stream()
                .map(transaction -> {

                    // Tính toán số dư còn lại sau giao dịch
//                    BigDecimal remainingBalance = transaction.getWallet().getBalance();
//                    if (transaction.getTransactionType() == TransactionType.WITHDRAWAL) {
//                        remainingBalance = remainingBalance.subtract(transaction.getAmount());
//                    } else if (transaction.getTransactionType() == TransactionType.DEPOSIT) {
//                        remainingBalance = remainingBalance.add(transaction.getAmount());
//                    }

//                    dto.setWalletBalance(remainingBalance);
                    return transactionMapper.toDTO(transaction);
                })
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> getInstructorWithdrawals() {
        Long instructorId = SecurityUtils.getLoggedInInstructorId();
        List<Transaction> transactions = transactionRepository.findByWallet_InstructorIdAndTransactionType(instructorId, TransactionType.WITHDRAWAL);

        // Sử dụng Mapper để chuyển entity thành DTO
        return transactions.stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> getInstructorDeposits() {
        Long instructorId = SecurityUtils.getLoggedInInstructorId();
        List<Transaction> transactions = transactionRepository.findByWallet_InstructorIdAndTransactionType(instructorId, TransactionType.DEPOSIT);

        // Sử dụng Mapper để chuyển entity thành DTO
        return transactions.stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public String depositToInstructor(BigDecimal amount, String instructorEmail) throws PayPalRESTException {
        // Kiểm tra xem ví của instructor có tồn tại không
        Long instructorId = SecurityUtils.getLoggedInInstructorId();
        Wallet wallet = walletRepository.findByInstructorId(instructorId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        // Lấy số dư ví hiện tại
        BigDecimal walletBalance = wallet.getBalance();

        BigDecimal remainingBalance = walletBalance.add(amount);

        // Tạo giao dịch Deposit
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTitle("Deposit from Admin by PayPal");
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setStatusTransaction(StatusTransaction.COMPLETED);
        transaction.setPaymentMethod(PaymentMethod.PAYPAL);
        transaction.setWallet(wallet);
        transaction.setWalletBalance(remainingBalance);

        // Tạo PayPal Payout để gửi tiền từ Admin (Business) đến Instructor
        Payout payout = new Payout();
        PayoutSenderBatchHeader senderBatchHeader = new PayoutSenderBatchHeader();
        senderBatchHeader.setSenderBatchId(UUID.randomUUID().toString());
        senderBatchHeader.setEmailSubject("Admin has sent you money!");

        PayoutItem payoutItem = new PayoutItem();
        payoutItem.setRecipientType("EMAIL");
        payoutItem.setReceiver(instructorEmail); // Email Instructor trên Sandbox
        payoutItem.setAmount(new com.paypal.api.payments.Currency("USD", amount.toString()));
        payoutItem.setNote("Admin deposit to instructor account");

        payout.setSenderBatchHeader(senderBatchHeader);
        payout.setItems(List.of(payoutItem));

        // Thực hiện giao dịch
        PayoutBatch payoutBatch = payout.create(apiContext, new HashMap<>());

        // Cập nhật giao dịch & số dư ví
        transaction.setPaypalPayoutId(payoutBatch.getBatchHeader().getPayoutBatchId());
        transactionRepository.save(transaction);

        wallet.setBalance(wallet.getBalance().add(amount)); // Cộng số dư vào ví Instructor
        walletRepository.save(wallet);

        return payoutBatch.getBatchHeader().getPayoutBatchId();
    }
}
