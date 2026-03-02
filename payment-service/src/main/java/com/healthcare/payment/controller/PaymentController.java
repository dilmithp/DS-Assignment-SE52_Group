package com.healthcare.payment.controller;

import com.healthcare.payment.entity.Payment;
import com.healthcare.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/initiate")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR')")
    public Map<String, String> initiatePayment(@RequestBody Map<String, Object> request) {
        return paymentService.initiatePayment(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR') or hasRole('ADMIN')")
    public Payment getPayment(@PathVariable Long id) {
        return paymentService.getPayment(id);
    }

    @PostMapping("/{id}/refund")
    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN')")
    public Map<String, String> refundPayment(@PathVariable Long id) {
        return paymentService.refundPayment(id);
    }
}
