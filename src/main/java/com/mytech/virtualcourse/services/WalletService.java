package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.WalletBalanceDTO;
import com.mytech.virtualcourse.dtos.WalletDTO;
import com.mytech.virtualcourse.entities.Instructor;
import com.mytech.virtualcourse.entities.Transaction;
import com.mytech.virtualcourse.entities.Wallet;
import com.mytech.virtualcourse.enums.NotificationType;
import com.mytech.virtualcourse.enums.StatusTransaction;
import com.mytech.virtualcourse.enums.StatusWallet;
import com.mytech.virtualcourse.enums.TransactionType;
import com.mytech.virtualcourse.exceptions.InsufficientBalanceException;
import com.mytech.virtualcourse.exceptions.WalletOperationException;
import com.mytech.virtualcourse.mappers.WalletMapper;
import com.mytech.virtualcourse.repositories.InstructorRepository;
import com.mytech.virtualcourse.repositories.TransactionRepository;
import com.mytech.virtualcourse.repositories.WalletRepository;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class WalletService {
    private static final Logger logger = LoggerFactory.getLogger(WalletService.class);

    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private NotificationService notificationService;

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

    @Transactional
    public Wallet createWalletForInstructor(Instructor instructor) {
        logger.info("Creating wallet for instructor ID: {}", instructor.getId());

        // Kiểm tra xem instructor đã có ví chưa
        if (instructor.getWallet() != null) {
            logger.info("Wallet already exists for instructor ID: {}", instructor.getId());
            return instructor.getWallet();
        }

        // Tạo ví mới
        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setStatusWallet(StatusWallet.ACTIVE);
        wallet.setMaxLimit(new BigDecimal("10000.00")); // Giới hạn mặc định
        wallet.setLastUpdated(new Timestamp(System.currentTimeMillis()));
        wallet.setInstructor(instructor);

        // Lưu ví và cập nhật instructor
        Wallet savedWallet = walletRepository.save(wallet);
        instructor.setWallet(savedWallet);
        instructorRepository.save(instructor);

        logger.info("Wallet created successfully for instructor ID: {}, wallet ID: {}",
                instructor.getId(), savedWallet.getId());
        return savedWallet;
    }

    /**
     * Lấy thông tin ví của instructor
     *
     * @param instructorId ID của instructor
     * @return Đối tượng Wallet
     * @throws ResourceNotFoundException Nếu không tìm thấy ví
     */
    public Wallet getInstructorWallet(Long instructorId) {
        logger.debug("Getting wallet for instructor ID: {}", instructorId);

        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> {
                    logger.error("Instructor not found with ID: {}", instructorId);
                    return new ResourceNotFoundException("Instructor not found with id: " + instructorId);
                });

        if (instructor.getWallet() == null) {
            logger.error("Wallet not found for instructor ID: {}", instructorId);
            throw new ResourceNotFoundException("Wallet not found for instructor with id: " + instructorId);
        }

        return instructor.getWallet();
    }

    /**
     * Cập nhật số dư ví
     *
     * @param walletId ID của ví
     * @param amount Số tiền thay đổi
     * @param isCredit true nếu là nạp tiền, false nếu là trừ tiền
     * @return Đối tượng Wallet đã cập nhật
     * @throws ResourceNotFoundException Nếu không tìm thấy ví
     * @throws InsufficientBalanceException Nếu số dư không đủ khi rút tiền
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Wallet updateBalance(Long walletId, BigDecimal amount, boolean isCredit) {
        logger.info("Updating balance for wallet ID: {}, amount: {}, isCredit: {}",
                walletId, amount, isCredit);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> {
                    logger.error("Wallet not found with ID: {}", walletId);
                    return new ResourceNotFoundException("Wallet not found with id: " + walletId);
                });

        // Kiểm tra trạng thái ví
        if (wallet.getStatusWallet() != StatusWallet.ACTIVE) {
            logger.error("Cannot update balance for wallet ID: {} with status: {}",
                    walletId, wallet.getStatusWallet());
            throw new WalletOperationException("Wallet is not active");
        }

        BigDecimal newBalance;
        if (isCredit) {
            // Thêm tiền vào ví
            newBalance = wallet.getBalance().add(amount);

            // Kiểm tra giới hạn tối đa
            if (wallet.getMaxLimit() != null && newBalance.compareTo(wallet.getMaxLimit()) > 0) {
                logger.warn("New balance exceeds max limit for wallet ID: {}", walletId);
                throw new WalletOperationException("Transaction would exceed wallet maximum limit");
            }
        } else {
            // Trừ tiền từ ví
            if (wallet.getBalance().compareTo(amount) < 0) {
                logger.error("Insufficient balance for wallet ID: {}", walletId);
                throw new InsufficientBalanceException("Insufficient balance");
            }
            newBalance = wallet.getBalance().subtract(amount);
        }

        wallet.setBalance(newBalance);
        wallet.setLastUpdated(new Timestamp(System.currentTimeMillis()));
        Wallet updatedWallet = walletRepository.save(wallet);

        logger.info("Balance updated successfully for wallet ID: {}, new balance: {}",
                walletId, newBalance);
        return updatedWallet;
    }

    /**
     * Cập nhật trạng thái ví
     *
     * @param walletId ID của ví
     * @param status Trạng thái mới
     * @return Đối tượng Wallet đã cập nhật
     * @throws ResourceNotFoundException Nếu không tìm thấy ví
     */
    @Transactional
    public Wallet updateWalletStatus(Long walletId, StatusWallet status) {
        logger.info("Updating status for wallet ID: {} to {}", walletId, status);

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> {
                    logger.error("Wallet not found with ID: {}", walletId);
                    return new ResourceNotFoundException("Wallet not found with id: " + walletId);
                });

        wallet.setStatusWallet(status);
        wallet.setLastUpdated(new Timestamp(System.currentTimeMillis()));
        Wallet updatedWallet = walletRepository.save(wallet);

        logger.info("Status updated successfully for wallet ID: {}", walletId);
        return updatedWallet;
    }

    /**
     * Tạo giao dịch mới
     *
     * @param wallet Đối tượng Wallet
     * @param amount Số tiền giao dịch
     * @param transactionType Loại giao dịch (CREDIT/DEBIT)
     * @param description Mô tả giao dịch
     * @param referenceId ID tham chiếu (nếu có)
     * @return Đối tượng Transaction đã tạo
     */
    @Transactional
    public Transaction createTransaction(Wallet wallet, BigDecimal amount,
                                         TransactionType transactionType, String description, String referenceId) {
        logger.info("Creating transaction for wallet ID: {}, amount: {}, type: {}",
                wallet.getId(), amount, transactionType);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amount must be greater than zero");
        }

        // Kiểm tra trạng thái ví
        if (wallet.getStatusWallet() != StatusWallet.ACTIVE) {
            logger.error("Cannot create transaction for wallet ID: {} with status: {}",
                    wallet.getId(), wallet.getStatusWallet());
            throw new WalletOperationException("Wallet is not active");
        }

        // Tạo ID tham chiếu nếu không được cung cấp
        if (referenceId == null || referenceId.isEmpty()) {
            referenceId = "TXN-" + UUID.randomUUID().toString().substring(0, 8);
        }

        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setAmount(amount);
        transaction.setTransactionType(transactionType);
        transaction.setStatusTransaction(StatusTransaction.PENDING);
        // Lưu mô tả vào một trường phù hợp hoặc tạo thêm trường nếu cần

        Transaction savedTransaction = transactionRepository.save(transaction);

        logger.info("Transaction created successfully with ID: {}", savedTransaction.getId());
        return savedTransaction;
    }

    /**
     * Xử lý giao dịch
     *
     * @param transactionId ID của giao dịch
     * @return Đối tượng Transaction đã xử lý
     * @throws ResourceNotFoundException Nếu không tìm thấy giao dịch
     * @throws IllegalStateException Nếu giao dịch không ở trạng thái PENDING
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Transaction processTransaction(Long transactionId) {
        logger.info("Processing transaction with ID: {}", transactionId);

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> {
                    logger.error("Transaction not found with ID: {}", transactionId);
                    return new ResourceNotFoundException("Transaction not found with id: " + transactionId);
                });

        if (transaction.getStatusTransaction() != StatusTransaction.PENDING) {
            logger.error("Cannot process transaction ID: {} with status: {}",
                    transactionId, transaction.getStatusTransaction());
            throw new IllegalStateException("Transaction is not in PENDING state");
        }

        try {
            Wallet wallet = transaction.getWallet();

            // Kiểm tra trạng thái ví
            if (wallet.getStatusWallet() != StatusWallet.ACTIVE) {
                logger.error("Cannot process transaction for inactive wallet ID: {}", wallet.getId());
                transaction.setStatusTransaction(StatusTransaction.FAILED);
                return transactionRepository.save(transaction);
            }

            if (transaction.getTransactionType() == TransactionType.CREDIT) {
                // Nạp tiền vào ví
                BigDecimal newBalance = wallet.getBalance().add(transaction.getAmount());

                // Kiểm tra giới hạn tối đa
                if (wallet.getMaxLimit() != null && newBalance.compareTo(wallet.getMaxLimit()) > 0) {
                    logger.warn("Transaction would exceed wallet maximum limit");
                    transaction.setStatusTransaction(StatusTransaction.FAILED);
                    return transactionRepository.save(transaction);
                }

                wallet.setBalance(newBalance);
            } else if (transaction.getTransactionType() == TransactionType.DEBIT) {
                // Trừ tiền từ ví
                if (wallet.getBalance().compareTo(transaction.getAmount()) < 0) {
                    logger.error("Insufficient balance for transaction ID: {}", transactionId);
                    transaction.setStatusTransaction(StatusTransaction.FAILED);
                    return transactionRepository.save(transaction);
                }

                wallet.setBalance(wallet.getBalance().subtract(transaction.getAmount()));
            } else {
                logger.error("Invalid transaction type: {}", transaction.getTransactionType());
                transaction.setStatusTransaction(StatusTransaction.FAILED);
                return transactionRepository.save(transaction);
            }

            wallet.setLastUpdated(new Timestamp(System.currentTimeMillis()));
            walletRepository.save(wallet);

            transaction.setStatusTransaction(StatusTransaction.SUCCESS);
            transaction.setProcessedAt(new Timestamp(System.currentTimeMillis()));
            Transaction completedTransaction = transactionRepository.save(transaction);

            logger.info("Transaction ID: {} processed successfully. New wallet balance: {}",
                    transactionId, wallet.getBalance());

            // Gửi thông báo cho người dùng
            try {
                Instructor instructor = wallet.getInstructor();
                if (instructor != null && instructor.getAccount() != null) {
                    String message = transaction.getTransactionType() == TransactionType.CREDIT ?
                            "Your wallet has been credited with " + transaction.getAmount() :
                            "Your wallet has been debited with " + transaction.getAmount();

                    notificationService.sendNotification(
                            instructor.getAccount().getId(),
                            message,
                            transaction.getTransactionType() == TransactionType.CREDIT ?
                                    NotificationType.WalletCredit : NotificationType.WalletDebit,
                            null,
                            null
                    );
                }
            } catch (Exception e) {
                logger.error("Failed to send transaction notification: {}", e.getMessage());
                // Không throw exception để không ảnh hưởng đến giao dịch
            }

            return completedTransaction;
        } catch (Exception e) {
            logger.error("Error processing transaction ID: {}: {}", transactionId, e.getMessage());
            transaction.setStatusTransaction(StatusTransaction.FAILED);
            return transactionRepository.save(transaction);
        }
    }

    /**
     * Lấy danh sách giao dịch của ví
     *
     * @param walletId ID của ví
     * @return Danh sách giao dịch
     */
    public List<Transaction> getTransactionsByWalletId(Long walletId) {
        logger.debug("Getting transactions for wallet ID: {}", walletId);

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id: " + walletId));

        return wallet.getTransactions();
    }

    /**
     * Lấy danh sách giao dịch của ví có phân trang
     *
     * @param walletId ID của ví
     * @param pageable Thông tin phân trang
     * @return Trang giao dịch
     */
    public Page<Transaction> getTransactionsByWalletId(Long walletId, Pageable pageable) {
        logger.debug("Getting paginated transactions for wallet ID: {}", walletId);
        return transactionRepository.findByWalletIdOrderByCreatedAtDesc(walletId, pageable);
    }

    /**
     * Lấy danh sách giao dịch theo trạng thái
     *
     * @param walletId ID của ví
     * @param status Trạng thái giao dịch
     * @return Danh sách giao dịch
     */
    public List<Transaction> getTransactionsByStatus(Long walletId, StatusTransaction status) {
        logger.debug("Getting transactions for wallet ID: {} with status: {}", walletId, status);
        return transactionRepository.findByWalletIdAndStatusTransactionOrderByCreatedAtDesc(walletId, status);
    }

    /**
     * Yêu cầu rút tiền
     *
     * @param walletId ID của ví
     * @param amount Số tiền rút
     * @param bankInfo Thông tin ngân hàng (số tài khoản, tên ngân hàng)
     * @return Đối tượng Transaction đã tạo
     * @throws ResourceNotFoundException Nếu không tìm thấy ví
     * @throws InsufficientBalanceException Nếu số dư không đủ
     */
    @Transactional
    public Transaction requestWithdrawal(Long walletId, BigDecimal amount, String bankInfo) {
        logger.info("Processing withdrawal request for wallet ID: {}, amount: {}", walletId, amount);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero");
        }

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> {
                    logger.error("Wallet not found with ID: {}", walletId);
                    return new ResourceNotFoundException("Wallet not found with id: " + walletId);
                });

        // Kiểm tra trạng thái ví
        if (wallet.getStatusWallet() != StatusWallet.ACTIVE) {
            logger.error("Cannot process withdrawal for wallet ID: {} with status: {}",
                    walletId, wallet.getStatusWallet());
            throw new WalletOperationException("Wallet is not active");
        }

        // Kiểm tra số dư
        if (wallet.getBalance().compareTo(amount) < 0) {
            logger.error("Insufficient balance for withdrawal from wallet ID: {}", walletId);
            throw new InsufficientBalanceException("Insufficient balance for withdrawal");
        }

        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.WITHDRAWAL);
        transaction.setStatusTransaction(StatusTransaction.PENDING);
        // Lưu thông tin ngân hàng vào trường phù hợp hoặc tạo thêm trường nếu cần

        Transaction savedTransaction = transactionRepository.save(transaction);

        logger.info("Withdrawal request created with transaction ID: {}", savedTransaction.getId());

        // Gửi thông báo cho người dùng
        try {
            Instructor instructor = wallet.getInstructor();
            if (instructor != null && instructor.getAccount() != null) {
                notificationService.sendNotification(
                        instructor.getAccount().getId(),
                        "Your withdrawal request for " + amount + " has been submitted and is pending approval.",
                        NotificationType.WalletWithdrawal,
                        null,
                        null
                );
            }
        } catch (Exception e) {
            logger.error("Failed to send withdrawal notification: {}", e.getMessage());
            // Không throw exception để không ảnh hưởng đến giao dịch
        }

        return savedTransaction;
    }

    /**
     * Phê duyệt yêu cầu rút tiền
     *
     * @param transactionId ID của giao dịch rút tiền
     * @return Đối tượng Transaction đã xử lý
     * @throws ResourceNotFoundException Nếu không tìm thấy giao dịch
     */
    @Transactional
    public Transaction approveWithdrawal(Long transactionId) {
        logger.info("Approving withdrawal transaction ID: {}", transactionId);

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> {
                    logger.error("Transaction not found with ID: {}", transactionId);
                    return new ResourceNotFoundException("Transaction not found with id: " + transactionId);
                });

        if (transaction.getStatusTransaction() != StatusTransaction.PENDING) {
            logger.error("Cannot approve transaction ID: {} with status: {}",
                    transactionId, transaction.getStatusTransaction());
            throw new IllegalStateException("Transaction is not in PENDING state");
        }

        if (transaction.getTransactionType() != TransactionType.WITHDRAWAL) {
            logger.error("Transaction ID: {} is not a withdrawal transaction", transactionId);
            throw new IllegalArgumentException("Transaction is not a withdrawal request");
        }

        // Xử lý giao dịch rút tiền
        Wallet wallet = transaction.getWallet();

        // Kiểm tra số dư
        if (wallet.getBalance().compareTo(transaction.getAmount()) < 0) {
            logger.error("Insufficient balance for withdrawal from wallet ID: {}", wallet.getId());
            transaction.setStatusTransaction(StatusTransaction.FAILED);
            return transactionRepository.save(transaction);
        }

        // Trừ tiền từ ví
        wallet.setBalance(wallet.getBalance().subtract(transaction.getAmount()));
        wallet.setLastUpdated(new Timestamp(System.currentTimeMillis()));
        walletRepository.save(wallet);

        // Cập nhật trạng thái giao dịch
        transaction.setStatusTransaction(StatusTransaction.SUCCESS);
        transaction.setProcessedAt(new Timestamp(System.currentTimeMillis()));
        Transaction processedTransaction = transactionRepository.save(transaction);

        // Gửi thông báo cho người dùng
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
        }

        logger.info("Withdrawal transaction ID: {} processed successfully", transactionId);
        return processedTransaction;
    }

    /**
     * Từ chối yêu cầu rút tiền
     *
     * @param transactionId ID của giao dịch rút tiền
     * @param reason Lý do từ chối
     * @return Đối tượng Transaction đã cập nhật
     * @throws ResourceNotFoundException Nếu không tìm thấy giao dịch
     */
    @Transactional
    public Transaction rejectWithdrawal(Long transactionId, String reason) {
        logger.info("Rejecting withdrawal transaction ID: {}", transactionId);

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> {
                    logger.error("Transaction not found with ID: {}", transactionId);
                    return new ResourceNotFoundException("Transaction not found with id: " + transactionId);
                });

        if (transaction.getStatusTransaction() != StatusTransaction.PENDING) {
            logger.error("Cannot reject transaction ID: {} with status: {}",
                    transactionId, transaction.getStatusTransaction());
            throw new IllegalStateException("Transaction is not in PENDING state");
        }

        if (transaction.getTransactionType() != TransactionType.WITHDRAWAL) {
            logger.error("Transaction ID: {} is not a withdrawal transaction", transactionId);
            throw new IllegalArgumentException("Transaction is not a withdrawal request");
        }

        // Cập nhật trạng thái
        transaction.setStatusTransaction(StatusTransaction.REJECTED);
        Transaction rejectedTransaction = transactionRepository.save(transaction);

        logger.info("Withdrawal transaction ID: {} rejected", transactionId);

        // Gửi thông báo cho người dùng
        try {
            Wallet wallet = transaction.getWallet();
            Instructor instructor = wallet.getInstructor();
            if (instructor != null && instructor.getAccount() != null) {
                String message = "Your withdrawal request for " + transaction.getAmount() +
                        " has been rejected. " +
                        (reason != null && !reason.isEmpty() ? "Reason: " + reason : "");

                notificationService.sendNotification(
                        instructor.getAccount().getId(),
                        message,
                        NotificationType.WalletWithdrawal,
                        null,
                        null
                );
            }
        } catch (Exception e) {
            logger.error("Failed to send withdrawal rejection notification: {}", e.getMessage());
        }

        return rejectedTransaction;
    }
}
