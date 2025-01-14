// package com.mytech.virtualcourse.services;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.transaction.annotation.Transactional;

// import com.mytech.virtualcourse.dtos.PaymentDTO;
// import com.mytech.virtualcourse.enums.PaymentStatus;
// import com.mytech.virtualcourse.enums.StatusTransaction;
// import com.mytech.virtualcourse.mappers.PaymentMapper;
// import com.mytech.virtualcourse.repositories.PaymentRepository;

// public class PaymentGatewayService  {
    
//     @Autowired
//     private PaymentRepository paymentRepository;

//     @Autowired
//     private PaymentMapper paymentMapper;
//     @Autowired
//     private PaymentService paymentService;
//     @Autowired
//     private TransactionService transactionService;

//     // Giả sử bạn sử dụng Momo SDK hoặc API
//     public PaymentDTO initiatePayment(PaymentDTO paymentDTO) {
//         // Gọi API Momo để tạo thanh toán
//         // Lưu Payment vào DB với trạng thái PENDING
//         paymentDTO.setStatus(StatusTransaction.PENDING);
//         PaymentDTO savedPayment = paymentService.createPayment(paymentDTO);
//         // Gửi yêu cầu thanh toán tới Momo và nhận URL thanh toán
//         String paymentUrl = momoApi.createPayment(savedPayment);
//         savedPayment.setPaymentUrl(paymentUrl);
//         return savedPayment;
//     }

//     @Transactional
//     public void handlePaymentCallback(String paymentId, PaymentStatus status) {
//         Payment payment = paymentRepository.findById(paymentId)
//                 .orElseThrow(() -> new PaymentProcessingException("Payment not found: " + paymentId));

//         if (status == StatusTransaction.SUCCESS) {
//             payment.setStatus(StatusTransaction.SUCCESS);
//             paymentRepository.save(payment);
//             transactionService.depositToWallet(payment.getStudent().getWallet().getId(), payment.getAmount());
//         } else {
//             payment.setStatus(StatusTransaction.FAILED);
//             paymentRepository.save(payment);
//         }
//     }
// }
