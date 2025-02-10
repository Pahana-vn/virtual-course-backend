package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.TransactionHistoryDTO;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.mappers.TransactionHistoryMapper;
import com.mytech.virtualcourse.repositories.TransactionHistoryRepository;
import com.mytech.virtualcourse.entities.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionHistoryService {

    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository;

    @Autowired
    private TransactionHistoryMapper transactionHistoryMapper;

    public List<TransactionHistoryDTO> getStudentTransactionHistory(Long studentId) {
        List<Payment> payments = transactionHistoryRepository.findByStudentIdOrderByPaymentDateDesc(studentId);
        return payments.stream()
                .map(transactionHistoryMapper::toTransactionHistoryDTO)
                .collect(Collectors.toList());
    }

    public TransactionHistoryDTO getTransactionById(Long transactionId) {
        Payment payment = transactionHistoryRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + transactionId));

        return transactionHistoryMapper.toTransactionHistoryDTO(payment);
    }


}
