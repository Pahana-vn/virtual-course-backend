package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.entities.Payment;
import com.mytech.virtualcourse.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin("*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create-paypal-payment")
    public String createPaypalPayment(@RequestParam Long courseId) throws Exception {
        return paymentService.initiatePaypalPayment(courseId);
    }

    @PostMapping("/execute-paypal-payment")
    public Payment executePaypalPayment(@RequestParam String paymentId, @RequestParam String payerId) throws Exception {
        return paymentService.completePaypalPayment(paymentId, payerId);
    }

    @PostMapping("/create-paypal-payment-multiple")
    public String createPaypalPaymentMultiple(@RequestBody List<Long> courseIds) throws Exception {
        return paymentService.initiatePaypalPaymentForMultipleCourses(courseIds);
    }

}
