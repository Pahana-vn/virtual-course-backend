package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.TransactionDTO;
import com.mytech.virtualcourse.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    // Nạp tiền vào ví
    @PostMapping("/deposit")
    public ResponseEntity<TransactionDTO> deposit(@RequestParam Long walletId, @RequestParam BigDecimal amount) {
        TransactionDTO transaction = transactionService.depositToWallet(walletId, amount);
        return ResponseEntity.ok(transaction);
    }

    // Rút tiền từ ví
    @PostMapping("/withdraw")
    public ResponseEntity<TransactionDTO> withdraw(@RequestParam Long walletId, @RequestParam BigDecimal amount) {
        TransactionDTO transaction = transactionService.withdrawFromWallet(walletId, amount);
        return ResponseEntity.ok(transaction);
    }

    // Xem lịch sử giao dịch
    @GetMapping("/history/{walletId}")
    public ResponseEntity<List<TransactionDTO>> getTransactionHistory(@PathVariable Long walletId) {
        List<TransactionDTO> history = transactionService.getTransactionHistory(walletId);
        return ResponseEntity.ok(history);
    }

    //  /**
    //  * GET /api/transactions/instructor/{instructorId}
    //  * Fetch all transactions for a specific instructor.
    //  *
    //  * @param instructorId ID of the instructor.
    //  * @return List of TransactionDTO.
    //  */
    // @GetMapping("/instructor/{instructorId}")
    // public ResponseEntity<List<TransactionDTO>> getTransactionsByInstructor(@PathVariable Long instructorId) {
    //     List<TransactionDTO> transactions = transactionService.getTransactionsByInstructorId(instructorId);
    //     return ResponseEntity.ok(transactions);
    // }

    // Xử lý hoàn tiền
    @PostMapping("/refund")
    public ResponseEntity<TransactionDTO> refund(@RequestParam Long paymentId, @RequestParam BigDecimal amount) {
        TransactionDTO transaction = transactionService.refundPayment(paymentId, amount);
        return ResponseEntity.ok(transaction);
    }

}
