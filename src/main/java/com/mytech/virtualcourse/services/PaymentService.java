// src/main/java/com/mytech/virtualcourse/services/PaymentService.java

package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.PaymentDTO;
import com.mytech.virtualcourse.entities.Payment;
import com.mytech.virtualcourse.entities.Student;
import com.mytech.virtualcourse.enums.TransactionStatus;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.mappers.PaymentMapper;
import com.mytech.virtualcourse.repositories.PaymentRepository;
import com.mytech.virtualcourse.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PaymentMapper paymentMapper;

    /**
     * Tạo mới một thanh toán.
     *
     * @param dto Dữ liệu thanh toán.
     * @return PaymentDTO đã được lưu.
     */
    public PaymentDTO createPayment(PaymentDTO dto) {
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + dto.getStudentId()));

        Payment payment = paymentMapper.toEntity(dto);
        payment.setStatus(TransactionStatus.PENDING);
        payment.setPaymentDate(new Timestamp(System.currentTimeMillis()));
        payment.setStudent(student);

        Payment savedPayment = paymentRepository.save(payment);
        return paymentMapper.toDTO(savedPayment);
    }

    /**
     * Lấy danh sách thanh toán của một người dùng.
     *
     * @param userId ID của người dùng.
     * @return Danh sách PaymentDTO.
     */
    public List<PaymentDTO> getPaymentsByUser(Long userId) {
        List<Payment> payments = paymentRepository.findByUserId(userId);
        return payments.stream()
                .map(paymentMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Cập nhật trạng thái của một thanh toán.
     *
     * @param paymentId ID của thanh toán.
     * @param status    Trạng thái mới.
     * @return PaymentDTO đã được cập nhật.
     */
    public PaymentDTO updatePaymentStatus(Long paymentId, TransactionStatus status) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        payment.setStatus(status);
        Payment updatedPayment = paymentRepository.save(payment);
        return paymentMapper.toDTO(updatedPayment);
    }

    public List<PaymentDTO> getPaymentsByStudent(Long studentId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPaymentsByStudent'");
    }
}
