package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.TransactionDTO;
import com.mytech.virtualcourse.enums.PaymentMethod;
import com.mytech.virtualcourse.services.InstructorTransactionService;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/instructor-transaction")
public class InstructorTransactionController {

    @Autowired
    private InstructorTransactionService instructorTransactionService;


    @PostMapping("/request")
    public Map<String, String> requestWithdrawal(
            @RequestParam BigDecimal amount,
            @RequestParam String recipientEmail,
            @RequestParam PaymentMethod paymentMethod
    ) {
        Map<String, String> response = new HashMap<>();
        try {
            String payoutBatchId = instructorTransactionService.initiateWithdrawal(amount, recipientEmail, paymentMethod);
            response.put("status", "success");
            response.put("message", "Withdrawal successful");
            response.put("payoutBatchId", payoutBatchId);
        } catch (PayPalRESTException e) {
            response.put("status", "error");
            response.put("message", "Withdrawal failed: " + e.getMessage());
        }
        return response;
    }

    // API lấy lịch sử rút tiền của một instructor
    @GetMapping("/all")
    public List<TransactionDTO> getAllTransactions() {
        return instructorTransactionService.getAllInstructorTransactions();
    }

    // API lấy tất cả các giao dịch withdrawal
    @GetMapping("/withdrawals")
    public List<TransactionDTO> getInstructorWithdrawals() {
        return instructorTransactionService.getInstructorWithdrawals();
    }

    // API lấy tất cả các giao dịch deposit
    @GetMapping("/deposits")
    public List<TransactionDTO> getInstructorDeposits() {
        return instructorTransactionService.getInstructorDeposits();
    }

    @PostMapping("/deposit")
    public ResponseEntity<Map<String, String>> depositToInstructor(
            @RequestBody Map<String, String> requestData
    ) {
        Map<String, String> response = new HashMap<>();
        try {
            BigDecimal depositAmount = new BigDecimal(requestData.get("amount"));
            String instructorEmail = requestData.get("instructorEmail");

            String payoutBatchId = instructorTransactionService.depositToInstructor(depositAmount, instructorEmail);
            response.put("status", "success");
            response.put("message", "Deposit successful");
            response.put("payoutBatchId", payoutBatchId);
            return ResponseEntity.ok(response);
        } catch (PayPalRESTException e) {
            response.put("status", "error");
            response.put("message", "Deposit failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
