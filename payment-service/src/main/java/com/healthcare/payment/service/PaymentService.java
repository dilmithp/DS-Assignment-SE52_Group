package com.healthcare.payment.service;

import com.healthcare.payment.dto.PaymentCompletedEvent;
import com.healthcare.payment.dto.PaymentRequest;
import com.healthcare.payment.dto.PaymentResponse;
import com.healthcare.payment.kafka.PaymentEventPublisher;
import com.healthcare.payment.model.Payment;
import com.healthcare.payment.repository.PaymentRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentEventPublisher paymentEventPublisher;

    @Transactional
    public PaymentResponse initiatePayment(PaymentRequest request) {
        log.info("Starting initiatePayment for appointmentId={} and patientId={}", request.getAppointmentId(), request.getPatientId());

        BigDecimal normalizedAmount = request.getAmount().setScale(2, RoundingMode.HALF_UP);
        long amountInCents = normalizedAmount.movePointRight(2).longValueExact();
        String currency = normalizeCurrency(request.getCurrency());

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency(currency)
                .putMetadata("appointmentId", request.getAppointmentId().toString())
                .putMetadata("patientId", request.getPatientId())
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .build();

        PaymentIntent paymentIntent;
        try {
            paymentIntent = PaymentIntent.create(params);
        } catch (StripeException e) {
            log.error("Failed to create Stripe PaymentIntent for appointmentId={} and patientId={}", request.getAppointmentId(), request.getPatientId(), e);
            throw new RuntimeException("Failed to create Stripe PaymentIntent", e);
        }

        Payment payment = Payment.builder()
                .appointmentId(request.getAppointmentId())
                .patientId(request.getPatientId())
                .amount(normalizedAmount)
                .currency(currency)
                .status(Payment.PaymentStatus.PENDING)
                .stripePaymentIntentId(paymentIntent.getId())
                .stripeClientSecret(paymentIntent.getClientSecret())
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        return toResponse(savedPayment);
    }

    @Transactional(noRollbackFor = RuntimeException.class)
    public PaymentResponse confirmPayment(String intentId) {
        log.info("Starting confirmPayment for intentId={}", intentId);

        Payment payment = paymentRepository.findByStripePaymentIntentId(intentId)
                .orElseThrow(() -> new RuntimeException("Payment not found for Stripe PaymentIntent ID: " + intentId));

        PaymentIntent paymentIntent;
        try {
            paymentIntent = PaymentIntent.retrieve(intentId);
        } catch (StripeException e) {
            log.error("Failed to retrieve Stripe PaymentIntent for intentId={}", intentId, e);
            throw new RuntimeException("Failed to retrieve Stripe PaymentIntent", e);
        }

        if ("succeeded".equals(paymentIntent.getStatus())) {
            payment.setStatus(Payment.PaymentStatus.COMPLETED);
            payment = paymentRepository.save(payment);
            paymentEventPublisher.publishPaymentCompletedEvent(PaymentCompletedEvent.builder()
                    .paymentId(payment.getId())
                    .appointmentId(payment.getAppointmentId())
                    .patientId(payment.getPatientId())
                    .amount(payment.getAmount())
                    .currency(payment.getCurrency())
                    .completedAt(LocalDateTime.now())
                    .build());
            return toResponse(payment);
        }

        payment.setStatus(Payment.PaymentStatus.FAILED);
        payment = paymentRepository.save(payment);
        throw new RuntimeException("Stripe PaymentIntent did not succeed. Current status: " + paymentIntent.getStatus());
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPayment(Long id) {
        log.info("Starting getPayment for id={}", id);
        return toResponse(findPaymentById(id));
    }

    @Transactional
    public PaymentResponse refundPayment(Long id) {
        log.info("Starting refundPayment for id={}", id);

        Payment payment = findPaymentById(id);
        if (payment.getStatus() != Payment.PaymentStatus.COMPLETED) {
            throw new RuntimeException("Only completed payments can be refunded");
        }

        RefundCreateParams params = RefundCreateParams.builder()
                .setPaymentIntent(payment.getStripePaymentIntentId())
                .build();

        try {
            Refund.create(params);
        } catch (StripeException e) {
            log.error("Failed to create Stripe refund for paymentId={}", id, e);
            throw new RuntimeException("Failed to create Stripe refund", e);
        }

        payment.setStatus(Payment.PaymentStatus.REFUNDED);
        payment = paymentRepository.save(payment);
        return toResponse(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByPatient(String patientId) {
        log.info("Starting getPaymentsByPatient for patientId={}", patientId);
        return paymentRepository.findByPatientIdOrderByCreatedAtDesc(patientId).stream()
                .map(this::toResponse)
                .toList();
    }

    private Payment findPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
    }

    private PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .appointmentId(payment.getAppointmentId())
                .patientId(payment.getPatientId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus().name())
                .stripePaymentIntentId(payment.getStripePaymentIntentId())
                .stripeClientSecret(payment.getStripeClientSecret())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }

    private String normalizeCurrency(String currency) {
        if (currency == null || currency.isBlank()) {
            return "usd";
        }
        return currency.toLowerCase(Locale.ROOT);
    }
}
