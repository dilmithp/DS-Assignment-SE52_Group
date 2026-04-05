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
    private final AppointmentServiceClient appointmentServiceClient;
    private final NotificationProducer notificationProducer;

    public void startBookingSaga(Long appointmentId, Long patientId, Long doctorId) {
        log.info("Starting booking saga for appointment: {}", appointmentId);
        
        Long paymentId = null;
        boolean doctorAccepted = false;

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
            doctorAccepted = true;

            // Step 3: Complete Workflow (Mark status CONFIRMED)
            appointmentServiceClient.updateAppointmentStatus(appointmentId, "CONFIRMED");

            // Step 4: Publish Async Event
            notificationProducer.publishAppointmentBooked(appointmentId, patientId, doctorId);
            
            log.info("Booking saga completed successfully for appointment: {}", appointmentId);

        } catch (Exception e) {
            log.error("Saga failed, initiating compensation. Error: {}", e.getMessage());
            compensate(appointmentId, paymentId, doctorId, doctorAccepted);
        }
    }

    private void compensate(Long appointmentId, Long paymentId, Long doctorId, boolean doctorAccepted) {
        // Reverse Payment if it was processed
        if (paymentId != null) {
            try {
                paymentServiceClient.refundPayment(paymentId);
                log.info("Refunded payment: {}", paymentId);
            } catch (Exception e) {
                log.error("Failed to refund payment: {}", paymentId, e);
            }
        }
        
        // Reverse Doctor Schedule Slot if it was accepted
        if (doctorAccepted) {
            try {
                doctorServiceClient.cancelAppointment(doctorId, appointmentId);
                log.info("Released doctor slot for appointment: {}", appointmentId);
            } catch (Exception e) {
                log.error("Failed to release doctor slot for appointment: {}", appointmentId, e);
            }
        }

        // Notify Appointment service of failure
        try {
            appointmentServiceClient.updateAppointmentStatus(appointmentId, "FAILED");
            log.info("Marked appointment {} as FAILED", appointmentId);
        } catch (Exception e) {
            log.error("Failed to mark appointment {} as FAILED", appointmentId, e);
        }
    }
}
