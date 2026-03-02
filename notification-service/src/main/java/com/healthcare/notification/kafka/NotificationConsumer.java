package com.healthcare.notification.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationConsumer {

    @KafkaListener(topics = "appointment.booked", groupId = "notification-group")
    public void handleAppointmentBooked(String message) {
        log.info("Received appointment.booked event: {}", message);
        sendEmailNotification(message);
        sendSmsNotification(message);
    }

    @KafkaListener(topics = "payment.completed", groupId = "notification-group")
    public void handlePaymentCompleted(String message) {
        log.info("Received payment.completed event: {}", message);
        sendEmailNotification(message);
    }

    @KafkaListener(topics = "consultation.ended", groupId = "notification-group")
    public void handleConsultationEnded(String message) {
        log.info("Received consultation.ended event: {}", message);
        sendEmailNotification(message);
    }

    private void sendEmailNotification(String message) {
        // TODO: Integrate with SendGrid
        log.info("Simulating Email sent for event: {}", message);
    }

    private void sendSmsNotification(String message) {
        // TODO: Integrate with Twilio
        log.info("Simulating SMS sent for event: {}", message);
    }
}
