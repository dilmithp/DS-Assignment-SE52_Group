package com.healthcare.orchestrator.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void publishAppointmentBooked(Long appointmentId, Long patientId, Long doctorId) {
        String message = String.format("{\"appointmentId\": %d, \"patientId\": %d, \"doctorId\": %d}", 
                                       appointmentId, patientId, doctorId);
        kafkaTemplate.send("appointment.booked", message);
    }
}
