package com.healthcare.payment.service;

import com.healthcare.payment.entity.Payment;
import com.healthcare.payment.entity.PaymentStatus;
import com.healthcare.payment.kafka.PaymentEventProducer;
import com.healthcare.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentEventProducer paymentEventProducer;

    @Transactional
    public Map<String, String> initiatePayment(Map<String, Object> request) {
        Long appointmentId = Long.valueOf(request.get("appointmentId").toString());
        Long patientId = Long.valueOf(request.get("patientId").toString());
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        String gateway = request.get("gateway").toString();

        Payment payment = Payment.builder()
                .appointmentId(appointmentId)
                .patientId(patientId)
                .amount(amount)
                .status(PaymentStatus.INITIATED)
                .gateway(gateway)
                .build();
        
        payment = paymentRepository.save(payment);

        // We assume payment is successful immediately for this demo
        payment.setStatus(PaymentStatus.COMPLETED);
        payment = paymentRepository.save(payment);
        
        // Publish events
        paymentEventProducer.publishEvent("payment.initiated", payment.getId(), appointmentId, "INITIATED");
        paymentEventProducer.publishEvent("payment.completed", payment.getId(), appointmentId, "COMPLETED");

        return Map.of("id", String.valueOf(payment.getId()), "status", payment.getStatus().name());
    }

    public Payment getPayment(Long id) {
        return paymentRepository.findById(id).orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    @Transactional
    public Map<String, String> refundPayment(Long id) {
        Payment payment = getPayment(id);
        payment.setStatus(PaymentStatus.REFUNDED);
        paymentRepository.save(payment);
        paymentEventProducer.publishEvent("payment.completed", payment.getId(), payment.getAppointmentId(), "REFUNDED");
        return Map.of("id", String.valueOf(payment.getId()), "status", payment.getStatus().name());
    }
}
