//package com.mytech.virtualcourse.controllers;
//
//import com.paypal.api.payments.Payment;
//import com.paypal.api.payments.Links;
//import com.paypal.base.rest.PayPalRESTException;
//import com.mytech.virtualcourse.services.PayPalService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/paypal")
//@CrossOrigin(origins = "*")
//public class PayPalController {
//
//    private final PayPalService payPalService;
//
//    @Autowired
//    public PayPalController(PayPalService payPalService) {
//        this.payPalService = payPalService;
//    }
//
//    /**
//     * Create a PayPal payment and return the approval URL
//     *
//     * @param total the total amount for the payment
//     * @return the approval URL or an error message
//     */
//    @PostMapping("/create-payment")
//    public ResponseEntity<String> createPayment(@RequestParam Double total) {
//        try {
//            String cancelUrl = "http://localhost:3000/cancel";
//            String successUrl = "http://localhost:3000/success";
//
//            Payment payment = payPalService.createPayment(total, "USD", "paypal", "sale",
//                    "Payment for courses", cancelUrl, successUrl);
//
//            // Find and return the approval URL
//            for (Links link : payment.getLinks()) {
//                if ("approval_url".equals(link.getRel())) {
//                    return ResponseEntity.ok(link.getHref());
//                }
//            }
//            return ResponseEntity.status(500).body("Approval URL not found in PayPal response.");
//        } catch (PayPalRESTException e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500).body("Error creating PayPal payment: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Execute a PayPal payment after the user approves it
//     *
//     * @param paymentId the ID of the payment to execute
//     * @param payerId   the ID of the payer
//     * @param studentId the ID of the student
//     * @param courseId  the ID of the course
//     * @return the executed Payment object or an error message
//     */
//    @PostMapping("/execute-payment")
//    public ResponseEntity<?> executePayment(@RequestParam String paymentId,
//                                            @RequestParam String payerId,
//                                            @RequestParam Long studentId,
//                                            @RequestParam Long courseId) {
//        try {
//            Payment payment = payPalService.executePayment(paymentId, payerId, studentId, courseId);
//            return ResponseEntity.ok(payment);
//        } catch (PayPalRESTException e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500).body("Error executing PayPal payment: " + e.getMessage());
//        }
//    }
//}
