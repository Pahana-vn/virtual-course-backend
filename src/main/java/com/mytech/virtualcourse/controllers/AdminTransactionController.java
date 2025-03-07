package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.TransactionDTO;
import com.mytech.virtualcourse.dtos.TransactionHistoryDTO;
import com.mytech.virtualcourse.entities.Transaction;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.exceptions.WalletOperationException;
import com.mytech.virtualcourse.mappers.TransactionMapper;
import com.mytech.virtualcourse.services.InstructorTransactionService;
import com.mytech.virtualcourse.services.TransactionHistoryService;
import io.swagger.v3.oas.models.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/transactions")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AdminTransactionController {

    private static final Logger logger = LoggerFactory.getLogger(AdminTransactionController.class);

    @Autowired
    private InstructorTransactionService instructorTransactionService;

    @Autowired
    private TransactionHistoryService transactionHistoryService;

    @Autowired
    private TransactionMapper transactionMapper;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status) {

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Transaction> transactionsPage = instructorTransactionService.getAllTransactionsPaged(pageable, type, status);

            List<TransactionDTO> content = transactionsPage.getContent().stream()
                    .map(transactionMapper::toDTO)
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("content", content);
            response.put("currentPage", transactionsPage.getNumber());
            response.put("totalItems", transactionsPage.getTotalElements());
            response.put("totalPages", transactionsPage.getTotalPages());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching transactions: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch transactions: " + e.getMessage()));
        }
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable Long id) {
        try {
            Transaction transaction = instructorTransactionService.getTransactionById(id);
            return ResponseEntity.ok(transactionMapper.toDTO(transaction));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error fetching transaction details: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch transaction details: " + e.getMessage()));
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<?> getTransactionStatistics() {
        try {
            Map<String, Object> statistics = instructorTransactionService.getTransactionStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            logger.error("Error fetching transaction statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch transaction statistics: " + e.getMessage()));
        }
    }

    /**
     * Get monthly transaction trends
     * Provides data for transaction trend charts and analysis
     */
    @GetMapping("/trends/monthly")
    public ResponseEntity<?> getMonthlyTransactionTrends() {
        try {
            Map<String, Object> trends = instructorTransactionService.getMonthlyTransactionTrends();
            return ResponseEntity.ok(trends);
        } catch (Exception e) {
            logger.error("Error fetching monthly transaction trends: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse());
        }
    }

    /**
     * Approve a withdrawal request
     * This will update the transaction status, adjust the wallet balance,
     * and send a notification to the instructor
     */
    @PutMapping("/approve-withdrawal/{id}")
    public ResponseEntity<?> approveWithdrawal(@PathVariable @NotNull Long id) {
        try {
            logger.info("Approving withdrawal with ID: {}", id);

            // Get transaction details before approval for notification
            Transaction transaction = instructorTransactionService.getTransactionById(id);

            // Process the approval
            Transaction updatedTransaction = instructorTransactionService.approveWithdrawal(id);

            // The notification is already sent in the service layer, but we can log it here
            logger.info("Notification sent to instructor about withdrawal approval for transaction ID: {}", id);

            Map<String, Object> response = new HashMap<>();
            response.put("transaction", transactionMapper.toDTO(updatedTransaction));
            response.put("message", "Withdrawal approved successfully");
            response.put("notificationSent", true);

            logger.info("Successfully approved withdrawal with ID: {}", id);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            logger.warn("Transaction not found with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            logger.warn("Invalid transaction state for approval. ID: {}, Error: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (WalletOperationException e) {
            logger.warn("Wallet operation error during approval. ID: {}, Error: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error approving withdrawal with ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to approve withdrawal: " + e.getMessage()));
        }
    }

    /**
     * Reject a withdrawal request
     * This will update the transaction status and send a notification to the instructor
     */
    @PutMapping("/reject-withdrawal/{id}")
    public ResponseEntity<?> rejectWithdrawal(
            @PathVariable @Valid @NotNull(message = "Transaction ID is required") Long id,
            @RequestBody @Valid RejectWithdrawalRequest payload) {
        try {
            logger.info("Rejecting withdrawal with ID: {}, Reason: {}", id, payload.getReason());

            // Get transaction details before rejection for notification
            Transaction transaction = instructorTransactionService.getTransactionById(id);

            // Process the rejection
            Transaction rejectedTransaction = instructorTransactionService.rejectWithdrawal(id, payload.getReason());

            // The notification is already sent in the service layer, but we can log it here
            logger.info("Notification sent to instructor about withdrawal rejection for transaction ID: {}", id);

            Map<String, Object> response = new HashMap<>();
            response.put("transaction", transactionMapper.toDTO(rejectedTransaction));
            response.put("message", "Withdrawal rejected successfully");
            response.put("reason", payload.getReason());
            response.put("notificationSent", true);

            logger.info("Successfully rejected withdrawal with ID: {}", id);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            logger.warn("Transaction not found with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            logger.warn("Invalid transaction state for rejection. ID: {}, Error: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error rejecting withdrawal with ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to reject withdrawal: " + e.getMessage()));
        }
    }

    @GetMapping("/student-history/{studentId}")
    public ResponseEntity<?> getStudentTransactionHistory(@PathVariable Long studentId) {
        try {
            List<TransactionHistoryDTO> history = transactionHistoryService.getStudentTransactionHistory(studentId);
            return ResponseEntity.ok(history);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error fetching student transaction history: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch student transaction history: " + e.getMessage()));
        }
    }

    @GetMapping("/instructor-transactions/{instructorId}")
    public ResponseEntity<?> getInstructorTransactions(@PathVariable Long instructorId) {
        try {
            List<Transaction> transactions = instructorTransactionService.getInstructorTransactions(instructorId);
            List<TransactionDTO> transactionDTOs = transactions.stream()
                    .map(transactionMapper::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(transactionDTOs);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error fetching instructor transactions: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch instructor transactions: " + e.getMessage()));
        }
    }
    /**
     * Request class for withdrawal rejection
     */
    @Getter
    @Setter
    public static class RejectWithdrawalRequest {
        @NotBlank(message = "Reason is required")
        private String reason;

    }

}