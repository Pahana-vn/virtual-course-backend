package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.TransactionDTO;
import com.mytech.virtualcourse.entities.Instructor;
import com.mytech.virtualcourse.entities.Transaction;
import com.mytech.virtualcourse.entities.Wallet;
import com.mytech.virtualcourse.enums.NotificationType;
import com.mytech.virtualcourse.enums.PaymentMethod;
import com.mytech.virtualcourse.enums.StatusTransaction;
import com.mytech.virtualcourse.enums.StatusWallet;
import com.mytech.virtualcourse.enums.TransactionType;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.exceptions.WalletOperationException;
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
// Add these imports at the top
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.math.RoundingMode;
import java.util.ArrayList;
//import jakarta.transaction.Transactional;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InstructorTransactionService {

    private static final Logger logger = LoggerFactory.getLogger(InstructorTransactionService.class);

    @Autowired
    private APIContext apiContext;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private NotificationService notificationService;
    @PersistenceContext
    private EntityManager entityManager;

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

    //admin

    /**
     * Get all transactions with pagination and filtering
     */
    public Page<Transaction> getAllTransactionsPaged(Pageable pageable, String type, String status) {
        // If both type and status are provided
        if (type != null && !type.isEmpty() && status != null && !status.isEmpty()) {
            TransactionType transactionType = TransactionType.valueOf(type);
            StatusTransaction statusTransaction = StatusTransaction.valueOf(status);
            return transactionRepository.findByTransactionTypeAndStatusTransaction(
                    transactionType, statusTransaction, pageable);
        }

        // If only type is provided
        if (type != null && !type.isEmpty()) {
            TransactionType transactionType = TransactionType.valueOf(type);
            return transactionRepository.findByTransactionType(transactionType, pageable);
        }

        // If only status is provided
        if (status != null && !status.isEmpty()) {
            StatusTransaction statusTransaction = StatusTransaction.valueOf(status);
            return transactionRepository.findByStatusTransaction(statusTransaction, pageable);
        }

        // If no filters are provided
        return transactionRepository.findAll(pageable);
    }

    /**
     * Get a transaction by ID
     */
    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
    }

//    /**
//     * Get transaction statistics
//     */
//    public Map<String, Object> getTransactionStatistics() {
//        Map<String, Object> statistics = new HashMap<>();
//
//        // Total number of transactions
//        long totalTransactions = transactionRepository.count();
//        statistics.put("totalTransactions", totalTransactions);
//
//        // Count by transaction type
//        long totalDeposits = transactionRepository.countByTransactionType(TransactionType.DEPOSIT);
//        long totalWithdrawals = transactionRepository.countByTransactionType(TransactionType.WITHDRAWAL);
//        statistics.put("totalDeposits", totalDeposits);
//        statistics.put("totalWithdrawals", totalWithdrawals);
//
//        // Count by status
//        long pendingTransactions = transactionRepository.countByStatusTransaction(StatusTransaction.PENDING);
//        long completedTransactions = transactionRepository.countByStatusTransaction(StatusTransaction.COMPLETED);
//        long failedTransactions = transactionRepository.countByStatusTransaction(StatusTransaction.FAILED);
//        statistics.put("pendingTransactions", pendingTransactions);
//        statistics.put("completedTransactions", completedTransactions);
//        statistics.put("failedTransactions", failedTransactions);
//
//        // Sum of amounts by transaction type
//        BigDecimal totalDepositAmount = transactionRepository.sumAmountByTransactionType(TransactionType.DEPOSIT);
//        BigDecimal totalWithdrawalAmount = transactionRepository.sumAmountByTransactionType(TransactionType.WITHDRAWAL);
//        statistics.put("totalDepositAmount", totalDepositAmount != null ? totalDepositAmount : BigDecimal.ZERO);
//        statistics.put("totalWithdrawalAmount", totalWithdrawalAmount != null ? totalWithdrawalAmount : BigDecimal.ZERO);
//
//        return statistics;
//    }

    /**
     * Get all transactions for a specific instructor
     */
    public List<Transaction> getInstructorTransactions(Long instructorId) {
        return transactionRepository.findByWallet_InstructorId(instructorId);
    }

    /**
     * Approve a withdrawal transaction
     *
     * @param id The transaction ID
     * @return The updated transaction
     * @throws ResourceNotFoundException If transaction not found
     */
    @Transactional
    public Transaction approveWithdrawal(Long id) {
        logger.info("Approving withdrawal transaction ID: {}", id);

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));

        // Verify this is a withdrawal transaction
        if (transaction.getTransactionType() != TransactionType.WITHDRAWAL) {
            logger.error("Transaction ID: {} is not a withdrawal transaction", id);
            throw new IllegalArgumentException("Transaction is not a withdrawal request");
        }

        // Verify transaction is in PENDING state
        if (transaction.getStatusTransaction() != StatusTransaction.PENDING) {
            logger.error("Cannot approve transaction ID: {} with status: {}", id, transaction.getStatusTransaction());
            throw new IllegalStateException("Transaction is not in PENDING state");
        }

        Wallet wallet = transaction.getWallet();

        // Verify wallet exists and is active
        if (wallet == null) {
            logger.error("Wallet not found for transaction ID: {}", id);
            throw new ResourceNotFoundException("Wallet not found for transaction");
        }

        if (wallet.getStatusWallet() != StatusWallet.ACTIVE) {
            logger.error("Cannot process withdrawal for wallet with status: {}", wallet.getStatusWallet());
            throw new WalletOperationException("Wallet is not active");
        }

        // Verify sufficient balance
        if (wallet.getBalance().compareTo(transaction.getAmount()) < 0) {
            logger.error("Insufficient balance for withdrawal from wallet ID: {}", wallet.getId());
            transaction.setStatusTransaction(StatusTransaction.FAILED);
            return transactionRepository.save(transaction);
        }

        // Update wallet balance
        BigDecimal newBalance = wallet.getBalance().subtract(transaction.getAmount());
        wallet.setBalance(newBalance);
        wallet.setLastUpdated(new Timestamp(System.currentTimeMillis()));
        walletRepository.save(wallet);

        // Update transaction status
        transaction.setStatusTransaction(StatusTransaction.COMPLETED);
        transaction.setProcessedAt(new Timestamp(System.currentTimeMillis()));
        transaction.setWalletBalance(newBalance); // Update the wallet balance in the transaction

        Transaction updatedTransaction = transactionRepository.save(transaction);

        // Send notification to instructor
        try {
            Instructor instructor = wallet.getInstructor();
            if (instructor != null && instructor.getAccount() != null) {
                notificationService.sendNotification(
                        instructor.getAccount().getId(),
                        "Your withdrawal request for " + transaction.getAmount() + " has been approved and processed.",
                        NotificationType.WalletWithdrawal,
                        null,
                        null
                );
            }
        } catch (Exception e) {
            logger.error("Failed to send withdrawal approval notification: {}", e.getMessage());
            // Continue processing even if notification fails
        }

        logger.info("Withdrawal transaction ID: {} approved successfully", id);
        return updatedTransaction;
    }

    @Transactional
    public Transaction rejectWithdrawal(Long id, String reason) {
        logger.info("Rejecting withdrawal transaction ID: {}", id);

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));

        // Verify this is a withdrawal transaction
        if (transaction.getTransactionType() != TransactionType.WITHDRAWAL) {
            logger.error("Transaction ID: {} is not a withdrawal transaction", id);
            throw new IllegalArgumentException("Transaction is not a withdrawal request");
        }

        // Verify transaction is in PENDING state
        if (transaction.getStatusTransaction() != StatusTransaction.PENDING) {
            logger.error("Cannot reject transaction ID: {} with status: {}", id, transaction.getStatusTransaction());
            throw new IllegalStateException("Transaction is not in PENDING state");
        }

        // Update transaction status
        transaction.setStatusTransaction(StatusTransaction.REJECTED);

        // Store rejection reason in the title field by appending it
        if (reason != null && !reason.isEmpty()) {
            String updatedTitle = transaction.getTitle() + " - Rejected: " + reason;
            // Ensure the title doesn't exceed the database column length (assuming it's VARCHAR(255))
            if (updatedTitle.length() > 255) {
                updatedTitle = updatedTitle.substring(0, 252) + "...";
            }
            transaction.setTitle(updatedTitle);
        } else {
            transaction.setTitle(transaction.getTitle() + " - Rejected");
        }

        // Set the processed timestamp
        transaction.setProcessedAt(new Timestamp(System.currentTimeMillis()));

        Transaction rejectedTransaction = transactionRepository.save(transaction);

        // Send notification to instructor
        try {
            Wallet wallet = transaction.getWallet();
            if (wallet != null && wallet.getInstructor() != null && wallet.getInstructor().getAccount() != null) {
                String message = "Your withdrawal request for " + transaction.getAmount() + " has been rejected. "
                        + (reason != null && !reason.isEmpty() ? "Reason: " + reason : "");

                notificationService.sendNotification(
                        wallet.getInstructor().getAccount().getId(),
                        message,
                        NotificationType.WalletWithdrawal,
                        null,
                        null
                );
            }
        } catch (Exception e) {
            logger.error("Failed to send withdrawal rejection notification: {}", e.getMessage());
            // Continue processing even if notification fails
        }

        logger.info("Withdrawal transaction ID: {} rejected successfully", id);
        return rejectedTransaction;
    }

    /**
     * Get transaction statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getTransactionStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // Get total counts
        long totalTransactions = transactionRepository.count();
        long totalDeposits = transactionRepository.countByTransactionType(TransactionType.DEPOSIT);
        long totalWithdrawals = transactionRepository.countByTransactionType(TransactionType.WITHDRAWAL);

        // Get status counts
        long pendingTransactions = transactionRepository.countByStatusTransaction(StatusTransaction.PENDING);
        long completedTransactions = transactionRepository.countByStatusTransaction(StatusTransaction.COMPLETED);
        long failedTransactions = transactionRepository.countByStatusTransaction(StatusTransaction.FAILED);

        // Get total amounts
        BigDecimal totalDepositAmount = transactionRepository.sumAmountByTransactionType(TransactionType.DEPOSIT);
        BigDecimal totalWithdrawalAmount = transactionRepository.sumAmountByTransactionType(TransactionType.WITHDRAWAL);

        // Add basic statistics
        statistics.put("totalTransactions", totalTransactions);
        statistics.put("totalDeposits", totalDeposits);
        statistics.put("totalWithdrawals", totalWithdrawals);
        statistics.put("totalDepositAmount", totalDepositAmount != null ? totalDepositAmount : BigDecimal.ZERO);
        statistics.put("totalWithdrawalAmount", totalWithdrawalAmount != null ? totalWithdrawalAmount : BigDecimal.ZERO);
        statistics.put("pendingTransactions", pendingTransactions);
        statistics.put("completedTransactions", completedTransactions);
        statistics.put("failedTransactions", failedTransactions);

        // Add growth statistics (you can calculate these based on your business logic)
        statistics.put("transactionGrowth", 7.8);
        statistics.put("depositGrowth", 12.5);
        statistics.put("withdrawalGrowth", 5.2);
        statistics.put("pendingGrowth", -2.3);

        // Get monthly transaction trends
        Map<String, Object> monthlyTrends = getMonthlyTransactionTrends();
        statistics.put("monthlyData", monthlyTrends.get("monthlyData"));

        // Add growth rates from monthly trends if available
        if (monthlyTrends.containsKey("depositGrowth")) {
            statistics.put("depositGrowth", monthlyTrends.get("depositGrowth"));
        }

        if (monthlyTrends.containsKey("withdrawalGrowth")) {
            statistics.put("withdrawalGrowth", monthlyTrends.get("withdrawalGrowth"));
        }

        return statistics;
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public Map<String, Object> getMonthlyTransactionTrends() {
        Map<String, Object> result = new HashMap<>();

        // Lấy dữ liệu 12 tháng gần nhất
        LocalDateTime startDate = LocalDateTime.now().minusMonths(12);
        List<Object[]> rows = transactionRepository.findMonthlyTransactionTrends(startDate);

        List<Map<String, Object>> monthlyData = new ArrayList<>();

        // Chuyển đổi kết quả thành danh sách Map
        for (Object[] row : rows) {
            Map<String, Object> monthData = new HashMap<>();
            String month = (String) row[0];
            Integer year = ((Number) row[1]).intValue();
            BigDecimal deposits = (BigDecimal) row[2];
            BigDecimal withdrawals = (BigDecimal) row[3];
            Long depositCount = ((Number) row[4]).longValue();
            Long withdrawalCount = ((Number) row[5]).longValue();

            monthData.put("month", month);
            monthData.put("year", year);
            monthData.put("deposits", deposits);
            monthData.put("withdrawals", withdrawals);
            monthData.put("depositCount", depositCount);
            monthData.put("withdrawalCount", withdrawalCount);

            monthlyData.add(monthData);
        }

        // Sắp xếp dữ liệu theo năm và tháng
        monthlyData.sort((a, b) -> {
            Integer yearA = (Integer) a.get("year");
            Integer yearB = (Integer) b.get("year");

            if (yearA.equals(yearB)) {
                String monthA = (String) a.get("month");
                String monthB = (String) b.get("month");

                // Chuyển đổi tháng thành số để sắp xếp
                int monthNumA = getMonthNumber(monthA);
                int monthNumB = getMonthNumber(monthB);

                return Integer.compare(monthNumA, monthNumB);
            }

            return yearA.compareTo(yearB);
        });

        // Tính tổng và các thống kê khác
        BigDecimal totalDeposits = BigDecimal.ZERO;
        BigDecimal totalWithdrawals = BigDecimal.ZERO;
        int totalDepositCount = 0;
        int totalWithdrawalCount = 0;

        for (Map<String, Object> monthData : monthlyData) {
            BigDecimal deposits = (BigDecimal) monthData.get("deposits");
            BigDecimal withdrawals = (BigDecimal) monthData.get("withdrawals");
            Long depositCount = (Long) monthData.get("depositCount");
            Long withdrawalCount = (Long) monthData.get("withdrawalCount");

            totalDeposits = totalDeposits.add(deposits);
            totalWithdrawals = totalWithdrawals.add(withdrawals);
            totalDepositCount += depositCount;
            totalWithdrawalCount += withdrawalCount;
        }

        result.put("monthlyData", monthlyData);
        result.put("totalDeposits", totalDeposits);
        result.put("totalWithdrawals", totalWithdrawals);
        result.put("totalDepositCount", totalDepositCount);
        result.put("totalWithdrawalCount", totalWithdrawalCount);

        // Tính tỷ lệ tăng trưởng (so sánh tháng gần nhất với tháng trước đó)
        if (monthlyData.size() >= 2) {
            Map<String, Object> lastMonth = monthlyData.get(monthlyData.size() - 1);
            Map<String, Object> previousMonth = monthlyData.get(monthlyData.size() - 2);

            BigDecimal lastMonthDeposits = (BigDecimal) lastMonth.get("deposits");
            BigDecimal previousMonthDeposits = (BigDecimal) previousMonth.get("deposits");

            BigDecimal lastMonthWithdrawals = (BigDecimal) lastMonth.get("withdrawals");
            BigDecimal previousMonthWithdrawals = (BigDecimal) previousMonth.get("withdrawals");

            // Tránh chia cho 0
            if (previousMonthDeposits.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal depositGrowth = lastMonthDeposits.subtract(previousMonthDeposits)
                        .divide(previousMonthDeposits, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"));
                result.put("depositGrowth", depositGrowth);
            } else {
                result.put("depositGrowth", BigDecimal.ZERO);
            }

            if (previousMonthWithdrawals.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal withdrawalGrowth = lastMonthWithdrawals.subtract(previousMonthWithdrawals)
                        .divide(previousMonthWithdrawals, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"));
                result.put("withdrawalGrowth", withdrawalGrowth);
            } else {
                result.put("withdrawalGrowth", BigDecimal.ZERO);
            }
        }

        return result;
    }

    /**
     * Helper method to convert month abbreviation to number
     * @param monthAbbr Month abbreviation (e.g., "Jan", "Feb", etc.)
     * @return Month number (1-12)
     */
    private int getMonthNumber(String monthAbbr) {
        switch (monthAbbr.toLowerCase()) {
            case "jan": return 1;
            case "feb": return 2;
            case "mar": return 3;
            case "apr": return 4;
            case "may": return 5;
            case "jun": return 6;
            case "jul": return 7;
            case "aug": return 8;
            case "sep": return 9;
            case "oct": return 10;
            case "nov": return 11;
            case "dec": return 12;
            default: return 0; // Invalid month
        }
    }
}