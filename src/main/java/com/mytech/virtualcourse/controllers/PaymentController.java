package com.mytech.virtualcourse.controllers;

import com.mytech.virtualcourse.entities.Payment;
import com.mytech.virtualcourse.services.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // PayPal
    @PostMapping("/create-paypal-payment")
    public ResponseEntity<?> createPaypalPayment(
            @RequestParam Long courseId,
            @RequestParam(required = false) String platform,
            HttpServletRequest request
    ) {
        try {
            String approvalUrl = paymentService.initiatePaypalPayment(courseId, platform, request);
            return ResponseEntity.ok(approvalUrl);
        } catch (Exception e) {
            e.printStackTrace(); // Hoặc log.error("Error createPayPal", e);
            return ResponseEntity.badRequest().body(Map.of("message","An error occurred: " + e));
        }
    }

    @PostMapping("/execute-paypal-payment")
    public Payment executePaypalPayment(@RequestParam String paymentId, @RequestParam String payerId) throws Exception {
        return paymentService.completePaypalPayment(paymentId, payerId);
    }

    @PostMapping("/create-paypal-payment-multiple")
    public String createPaypalPaymentMultiple(@RequestBody List<Long> courseIds, HttpServletRequest request) throws Exception {
        return paymentService.initiatePaypalPaymentForMultipleCourses(courseIds, request);
    }

    // VNPAY
    @PostMapping("/create-vnpay-payment")
    public String createVnpayPayment(@RequestParam Long courseId, HttpServletRequest request) throws Exception {
        return paymentService.initiateVnPayPayment(courseId, request);
    }

    @PostMapping("/create-vnpay-payment-multiple")
    public String createVnpayPaymentMultiple(@RequestBody List<Long> courseIds, HttpServletRequest request) throws Exception {
        return paymentService.initiateVnPayPaymentForMultipleCourses(courseIds, request);
    }

    @GetMapping("/vnpay-return")
    public void handleVnpayReturnGet(HttpServletRequest request, HttpServletResponse response) {
        String queryString = request.getQueryString();
        Map<String, String> params = parseQueryString(queryString);
        try {
            paymentService.handleVnpayReturn(params);

            String transactionStatus = params.get("vnp_TransactionStatus");
            String txnRef = params.get("vnp_TxnRef");

            if ("00".equals(transactionStatus)) {
                String successUrl = "http://localhost:3000/success-vnpay?vnp_TxnRef=" + txnRef + "&vnp_TransactionStatus=" + transactionStatus;
                response.sendRedirect(successUrl);
            } else {
                String failUrl = "http://localhost:3000/fail";
                response.sendRedirect(failUrl);
            }
        } catch (Exception e) {
            try {
                String failUrl = "http://localhost:3000/fail";
                response.sendRedirect(failUrl);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    // Hàm parse query string mà không decode

    private Map<String, String> parseQueryString(String queryString) {
        Map<String, String> params = new HashMap<>();
        if (queryString != null && !queryString.isEmpty()) {
            // Tách theo '&'
            String[] pairs = queryString.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                if (idx != -1) {
                    String key = pair.substring(0, idx);
                    // Value giữ nguyên, không decode
                    String value = pair.substring(idx + 1);
                    params.put(key, value);
                }
            }
        }
        return params;
    }
}

