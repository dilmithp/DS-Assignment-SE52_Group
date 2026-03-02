package com.healthcare.payment.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentEventProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void publishEvent(String topic, Long paymentId, Long appointmentId, String status) {
        String message = String.format("{\"paymentId\": %d, \"appointmentId\": %d, \"status\": \"%s\"}", 
                                       paymentId, appointmentId, status);
        kafkaTemplate.send(topic, message);
    }
}
