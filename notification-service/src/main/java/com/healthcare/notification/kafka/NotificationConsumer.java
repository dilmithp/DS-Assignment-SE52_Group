package com.healthcare.notification.kafka;

import java.util.Map;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.healthcare.notification.service.EmailService;
import com.healthcare.notification.service.SmsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final EmailService emailService;
    private final SmsService smsService;

    @KafkaListener(topics = "appointment.booked", groupId = "notification-group")
    public void onAppointmentBooked(Map<String, Object> event) {
        try {
            log.info("Received event from topic appointment.booked: appointmentId={}, patientId={}, doctorId={}, appointmentTime={}",
                    event.get("appointmentId"), event.get("patientId"), event.get("doctorId"), event.get("appointmentTime"));

            String patientEmail = asString(event.get("patientEmail"));
            String patientPhone = asString(event.get("patientPhone"));
            String doctorEmail = asString(event.get("doctorEmail"));
            String doctorPhone = asString(event.get("doctorPhone"));
            String doctorName = asString(event.get("doctorName"));
            String patientName = asString(event.get("patientName"));
            String appointmentTime = asString(event.get("appointmentTime"));

            if (!StringUtils.hasText(patientEmail)) {
                log.warn("Skipping patient appointment confirmation email because patientEmail is missing. appointmentId={}",
                        event.get("appointmentId"));
            } else {
                emailService.sendAppointmentConfirmationToPatient(patientEmail, doctorName, appointmentTime);
            }

            if (!StringUtils.hasText(patientPhone)) {
                log.warn("Skipping patient appointment confirmation SMS because patientPhone is missing. appointmentId={}",
                        event.get("appointmentId"));
            } else {
                smsService.sendAppointmentConfirmationToPatient(patientPhone, doctorName, appointmentTime);
            }

            emailService.sendNewAppointmentToDoctor(doctorEmail, patientName, appointmentTime);
            smsService.sendNewAppointmentToDoctor(doctorPhone, patientName, appointmentTime);
        } catch (Exception e) {
            log.error("Error processing appointment.booked event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "payment.completed", groupId = "notification-group")
    public void onPaymentCompleted(Map<String, Object> event) {
        try {
            log.info("Received event from topic payment.completed: paymentId={}, appointmentId={}, patientId={}, completedAt={}",
                    event.get("paymentId"), event.get("appointmentId"), event.get("patientId"), event.get("completedAt"));

            String patientEmail = asString(event.get("patientEmail"));
            Object amount = event.get("amount");
            Object currency = event.get("currency");
            Object appointmentId = event.get("appointmentId");

            if (!StringUtils.hasText(patientEmail)) {
                log.warn("Skipping payment receipt email because patientEmail is missing. paymentId={}", event.get("paymentId"));
            } else {
                emailService.sendPaymentReceiptToPatient(patientEmail, amount, currency, appointmentId);
            }
        } catch (Exception e) {
            log.error("Error processing payment.completed event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "consultation.ended", groupId = "notification-group")
    public void onConsultationEnded(Map<String, Object> event) {
        try {
            log.info("Received event from topic consultation.ended: sessionId={}, appointmentId={}, patientId={}, doctorId={}, endedAt={}",
                    event.get("sessionId"), event.get("appointmentId"), event.get("patientId"), event.get("doctorId"), event.get("endedAt"));

            String patientEmail = asString(event.get("patientEmail"));
            String doctorName = asString(event.get("doctorName"));

            if (!StringUtils.hasText(patientEmail)) {
                log.warn("Skipping consultation follow-up email because patientEmail is missing. sessionId={}",
                        event.get("sessionId"));
            } else {
                emailService.sendConsultationFollowUpToPatient(patientEmail, doctorName);
            }
        } catch (Exception e) {
            log.error("Error processing consultation.ended event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "appointment.cancelled", groupId = "notification-group")
    public void onAppointmentCancelled(Map<String, Object> event) {
        try {
            log.info("Received event from topic appointment.cancelled: appointmentId={}, patientId={}, reason={}",
                    event.get("appointmentId"), event.get("patientId"), event.get("reason"));

            String patientEmail = asString(event.get("patientEmail"));
            String patientPhone = asString(event.get("patientPhone"));
            Object appointmentId = event.get("appointmentId");

            if (!StringUtils.hasText(patientEmail)) {
                log.warn("Skipping appointment cancellation email because patientEmail is missing. appointmentId={}",
                        appointmentId);
            } else {
                emailService.sendAppointmentCancellationToPatient(patientEmail, appointmentId);
            }

            if (!StringUtils.hasText(patientPhone)) {
                log.warn("Skipping appointment cancellation SMS because patientPhone is missing. appointmentId={}",
                        appointmentId);
            } else {
                smsService.sendAppointmentCancellationToPatient(patientPhone, appointmentId);
            }
        } catch (Exception e) {
            log.error("Error processing appointment.cancelled event: {}", e.getMessage(), e);
        }
    }

    private String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }
}
