package com.healthcare.orchestrator.service;

import com.healthcare.orchestrator.client.DoctorServiceClient;
import com.healthcare.orchestrator.client.PaymentServiceClient;
import com.healthcare.orchestrator.kafka.NotificationProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SagaService {

    private final PaymentServiceClient paymentServiceClient;
    private final DoctorServiceClient doctorServiceClient;
    private final NotificationProducer notificationProducer;

    public void startBookingSaga(Long appointmentId, Long patientId, Long doctorId) {
        log.info("Starting booking saga for appointment: {}", appointmentId);
        
        Long paymentId = null;
        try {
            // Step 1: Initiate Payment
            Map<String, Object> paymentRequest = Map.of(
                    "appointmentId", appointmentId,
                    "patientId", patientId,
                    "amount", 100.00,
                    "gateway", "STRIPE"
            );
            Map<String, String> paymentResponse = paymentServiceClient.initiatePayment(paymentRequest);
            paymentId = Long.parseLong(paymentResponse.get("id"));

            // Step 2: Confirm Slot with Doctor
            doctorServiceClient.acceptAppointment(doctorId, appointmentId);

            // Step 3: Publish Event
            notificationProducer.publishAppointmentBooked(appointmentId, patientId, doctorId);
            
            log.info("Booking saga completed successfully for appointment: {}", appointmentId);

        } catch (Exception e) {
            log.error("Saga failed, initiating compensation. Error: {}", e.getMessage());
            compensate(appointmentId, paymentId, doctorId);
        }
    }

    private void compensate(Long appointmentId, Long paymentId, Long doctorId) {
        if (paymentId != null) {
            try {
                paymentServiceClient.refundPayment(paymentId);
                log.info("Refunded payment: {}", paymentId);
            } catch (Exception e) {
                log.error("Failed to refund payment: {}", paymentId, e);
            }
        }
        // In a real system, we'd also reverse the doctor slot if it was confirmed,
        // and tell appointment-service to cancel the appointment.
    }
}
