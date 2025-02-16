package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.dtos.TransactionHistoryDTO;
import com.mytech.virtualcourse.services.TransactionHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class TransactionHistoryController {

    @Autowired
    private TransactionHistoryService transactionHistoryService;

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/history/{studentId}")
    public List<TransactionHistoryDTO> getTransactionHistory(@PathVariable Long studentId) {
        return transactionHistoryService.getStudentTransactionHistory(studentId);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/history/details/{transactionId}")
    public TransactionHistoryDTO getTransactionDetails(@PathVariable Long transactionId) {
        return transactionHistoryService.getTransactionById(transactionId);
    }

}
