package com.healthcare.notification.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SmsService {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.from-number}")
    private String fromNumber;

    @PostConstruct
    public void initTwilio() {
        try {
            Twilio.init(accountSid, authToken);
            log.info("Twilio SDK initialized successfully.");
        } catch (Exception e) {
            log.error("Failed to initialize Twilio SDK: {}", e.getMessage(), e);
        }
    }

    public void sendSms(String toNumber, String messageBody) {
        if (!StringUtils.hasText(toNumber)) {
            log.warn("Skipping SMS notification because recipient phone is missing.");
            return;
        }

        try {
            Message message = Message.creator(
                    new PhoneNumber(toNumber),
                    new PhoneNumber(fromNumber),
                    messageBody
            ).create();

            log.info("SMS sent to {} via Twilio. SID={}", toNumber, message.getSid());
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", toNumber, e.getMessage(), e);
        }
    }

    public void sendAppointmentConfirmationToPatient(String patientPhone, String doctorName, String appointmentTime) {
        String message = "Your appointment with Dr. " + safe(doctorName) + " at " + safe(appointmentTime)
                + " is confirmed. - Healthcare Platform";
        sendSms(patientPhone, trimTo160(message));
    }

    public void sendNewAppointmentToDoctor(String doctorPhone, String patientName, String appointmentTime) {
        String message = "New appointment: Patient " + safe(patientName) + " at " + safe(appointmentTime)
                + ". - Healthcare Platform";
        sendSms(doctorPhone, trimTo160(message));
    }

    public void sendAppointmentCancellationToPatient(String patientPhone, Object appointmentId) {
        String message = "Appointment #" + safe(appointmentId) + " has been cancelled. - Healthcare Platform";
        sendSms(patientPhone, trimTo160(message));
    }

    private String safe(Object value) {
        return value == null ? "N/A" : String.valueOf(value);
    }

    private String trimTo160(String message) {
        if (message.length() <= 160) {
            return message;
        }
        return message.substring(0, 160);
    }
}
